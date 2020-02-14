package controllers;

import clients.SteamClient;
import com.fasterxml.jackson.databind.JsonNode;
import model.Achievement;
import play.mvc.*;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AchievementsController extends Controller {

    @Inject
    SteamClient steamClient;

    public CompletionStage<Result> getAchievements(Http.Request request) {
        Optional<String> optSteamId = request.session()
                .get(SteamLoginController.STEAM_ID_NAME);
        String gameId = request.queryString("game_id").get();

        CompletionStage<JsonNode> achievementsPromise = steamClient.getPlayerAchievements(optSteamId.get(), gameId);
        CompletionStage<JsonNode> gameSchemaPromise = steamClient.getSchemaForGame(gameId);


        return achievementsPromise.thenCombine(gameSchemaPromise, (achievements, schema) -> {
            String iconUrl = "https://memepedia.ru/wp-content/uploads/2019/09/kto-nahuy-orig-768x558.jpg";
            String achievName = "notFound";

            String achievId = achievements.get("playerstats").get("achievements").get(0).get("apiname").asText();

            JsonNode achievementsList = schema.get("game").get("availableGameStats").get("achievements");
            for (JsonNode element : achievementsList) {
                if (element.get("name").asText().equals(achievId)) {
                    iconUrl = element.get("icon").asText();
                    achievName = element.get("displayName").asText();
                }
            }
            Achievement a = new Achievement(achievName, iconUrl);
            return ok(views.html.achievements.render(a));
        });

    }
}
