package controllers;

import clients.SteamClient;
import com.fasterxml.jackson.databind.JsonNode;
import model.Achievement;
import model.GameSchema;
import play.mvc.*;

import javax.inject.Inject;
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

        CompletionStage<JsonNode> achievementsPromise = steamClient.getPlayerAchievements(optSteamId.get(), gameId);
        CompletionStage<GameSchema> gameSchemaPromise = steamClient.getSchemaForGame(gameId);


        return achievementsPromise.thenCombine(gameSchemaPromise, (achievements, schema) -> {
            String achievId = achievements.get("playerstats").get("achievements").get(0).get("apiname").asText();

            Achievement correct = schema.getAchievementList().stream()
                    .filter(elem -> elem.getApiName().equals(achievId))
                    .findFirst()
                    .orElse(null);

            return ok(views.html.achievements.render(correct));
        });

    }
}
