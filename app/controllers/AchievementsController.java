package controllers;

import clients.SteamClient;
import logic.AchievementUtils;
import logic.ListUtils;
import model.Achievement;
import model.Game;
import model.GameSchema;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AchievementsController extends Controller {

    @Inject
    SteamClient steamClient;

    public CompletionStage<Result> getAchievements(Http.Request request) {
        Optional<String> optSteamId = request.session().get(SteamLoginController.STEAM_ID_NAME);
        Optional<String> optAvatar = request.session().get(SteamLoginController.STEAM_AVATAR_URL_NAME);
        if (optSteamId.isEmpty()) {
            return CompletableFuture.completedFuture(redirect("/"));
        }
        String steamId = optSteamId.get();

        CompletionStage<List<Game>> ownedGamesPromise = steamClient.getPlayerGames(steamId);
        CompletionStage<List<Achievement>> result = ownedGamesPromise.thenCompose(games -> {

            CompletionStage<List<Achievement>> finalList = CompletableFuture.completedFuture(new ArrayList<>());
            for (Game game : games) {
                CompletionStage<List<Achievement>> achievementReq = this.achievementsForGame(steamId, game.getId());

                finalList = finalList.thenCombine(achievementReq, (allAchievements, gameAchievements) -> {
                    for (Achievement a : gameAchievements) {
                        a.setGame(game);
                    }
                    allAchievements.addAll(gameAchievements);
                    return allAchievements;
                });
            }
            return finalList;
        });


        return result.thenApply(achievements -> {
            List<Achievement> rarestAchieved = AchievementUtils.rarestAchieved(achievements, 10);
            List<Achievement> mostCommonUnAchieved = AchievementUtils.mostCommonUnAchieved(achievements, 10);

            return ok(views.html.achievements.render(optAvatar.orElse(null), rarestAchieved, mostCommonUnAchieved));
        });
    }


    private CompletionStage<List<Achievement>> achievementsForGame(String steamId, Integer gameId) {

        CompletionStage<GameSchema> gameSchemaPromise = steamClient.getSchemaForGame(gameId);

        return gameSchemaPromise.thenCompose(schema -> {
            if (!schema.getAchievementList().isEmpty()) {
                CompletionStage<List<Achievement>> achievementsPromise = steamClient.getPlayerAchievements(steamId, gameId);
                CompletionStage<List<Achievement>> achievementPercentList = steamClient.getAchievementsPercent(gameId);


                CompletionStage<List<Achievement>> achievementsList = achievementsPromise.thenApply(achievements -> {
                    List<Achievement> achievementList = new ArrayList<>();

                    for (Achievement elem : achievements) {
                        for (Achievement elem2 : schema.getAchievementList()) {
                            if (elem.getApiName().equals(elem2.getApiName())) {
                                achievementList.add(
                                        new Achievement(
                                                elem2.getIconUrl(),
                                                elem2.getTitle(),
                                                elem2.getDescription(),
                                                elem2.getApiName(),
                                                elem.isAchieved(),
                                                elem2.getIconUrlGray()
                                        )
                                );
                            }
                        }
                    }

                    return achievementList;
                });

                return achievementPercentList.thenCombine(achievementsList, (percent, achievements) -> {
                    List<Achievement> achievementList = new ArrayList<>();

                    for (Achievement elem : percent) {
                        for (Achievement elem2 : achievements) {
                            if (elem.getApiName().equals(elem2.getApiName())) {
                                achievementList.add(
                                        new Achievement(
                                                elem2.getIconUrl(),
                                                elem2.getTitle(),
                                                elem2.getDescription(),
                                                elem2.getApiName(),
                                                elem2.isAchieved(),
                                                elem2.getIconUrlGray(),
                                                elem.getPercent()
                                        )
                                );
                            }
                        }
                    }

                    return achievementList;
                });
            } else {
                return CompletableFuture.completedFuture(schema.getAchievementList());
            }
        });
    }
}
