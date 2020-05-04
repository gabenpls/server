package controllers;

import clients.SteamClient;
import com.fasterxml.jackson.databind.JsonNode;
import logic.AchievementUtils;
import logic.ListUtils;
import model.*;
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


    public CompletionStage<Result> mainPage(Http.Request request) {
        Optional<String> optSteamId = request.session().get(SteamLoginController.STEAM_ID_NAME);
        Optional<String> optAvatar = request.session().get(SteamLoginController.STEAM_AVATAR_URL_NAME);
        if (optSteamId.isEmpty()) {
            return CompletableFuture.completedFuture(redirect("/"));
        }
        String steamId = optSteamId.get();

        return getAllPlayerAchievements(steamId)
                .thenApply(FilterPageRenderInfo::getAchievements)
                .thenApply(achievements -> {
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
                                                elem2.getIconUrlGray(),
                                                null,
                                                elem.getUnlockTime(),
                                                null
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

    public CompletionStage<Result> filterPage(Http.Request request) {
        Optional<String> optSteamId = request.session().get(SteamLoginController.STEAM_ID_NAME);
        Optional<String> optAvatar = request.session().get(SteamLoginController.STEAM_AVATAR_URL_NAME);
        if (optSteamId.isEmpty()) {
            return CompletableFuture.completedFuture(redirect("/"));
        }
        String steamId = optSteamId.get();

        CompletionStage<FilterPageRenderInfo> info = getAllPlayerAchievements(steamId);


        return info
                .thenApply(i -> i.page(0, 10))
                .thenApply(i -> ok(views.html.filter_page.render(optAvatar.orElse(null), i.getAchievements(), i.getGames())));

    }

    public CompletionStage<Result> filter(Http.Request request) {
        Optional<String> optSteamId = request.session().get(SteamLoginController.STEAM_ID_NAME);
        Optional<String> optAvatar = request.session().get(SteamLoginController.STEAM_AVATAR_URL_NAME);
        if (optSteamId.isEmpty()) {
            return CompletableFuture.completedFuture(redirect("/"));
        }
        String steamId = optSteamId.get();

        CompletionStage<FilterPageRenderInfo> pageInfoPromise = this.getAllPlayerAchievements(steamId);

        JsonNode node = request.body().asJson();
        Filter filter = Filter.parseFrom(node);

        return pageInfoPromise.thenApply(info -> {
            List<Achievement> filteredAch = ListUtils.filter(info.getAchievements(), ach -> {
                return filter.getGameIds().indexOf(ach.getGame().getId()) >= 0;
            });
            FilterPageRenderInfo filteredInfo = new FilterPageRenderInfo(filteredAch, info.getGames());
            return ok(filteredInfo.page(0, 20).asJson());
        });
    }

    private CompletionStage<FilterPageRenderInfo> getAllPlayerAchievements(String steamId) {

        CompletionStage<List<Game>> ownedGamesPromise = steamClient.getPlayerGames(steamId);
        CompletionStage<List<Achievement>> achievementListPromise = ownedGamesPromise.thenCompose(games -> {

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
        return achievementListPromise.thenCombine(ownedGamesPromise, FilterPageRenderInfo::new);
    }
}

