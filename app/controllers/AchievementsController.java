package controllers;

import clients.SteamClient;
import com.fasterxml.jackson.databind.JsonNode;
import model.Achievement;
import model.GameSchema;
import play.mvc.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class AchievementsController extends Controller {

    @Inject
    SteamClient steamClient;

    public CompletionStage<Result> getAchievements(Http.Request request) {
        Optional<String> optSteamId = request.session()
                .get(SteamLoginController.STEAM_ID_NAME);
        String gameId = request.queryString("game_id").get();

        CompletionStage<List<Achievement>> achievementsPromise = steamClient.getPlayerAchievements(optSteamId.get(), gameId);
        CompletionStage<GameSchema> gameSchemaPromise = steamClient.getSchemaForGame(gameId);
        CompletionStage<List<Achievement>> achievementPercentList = steamClient.getAchievementsPercent(gameId);


        CompletionStage<List<Achievement>> achievementsList = achievementsPromise.thenCombine(gameSchemaPromise, (achievements, schema) -> {
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
            
            return ok(views.html.achievements.render(achievementList));
        });


    }
}
