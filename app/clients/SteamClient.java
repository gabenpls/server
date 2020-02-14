package clients;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import model.GameSchema;
import model.PlayerSummaries;
import play.libs.ws.*;

import java.util.concurrent.CompletionStage;

public class SteamClient implements WSBodyReadables, WSBodyWritables {
    private final WSClient ws;
    private static final String STEAM_KEY = "A9F0A04C59611FF92C1005BB076AD5C6";

    @Inject
    public SteamClient(WSClient ws) {
        this.ws = ws;
    }

    public CompletionStage<PlayerSummaries> getPlayerSummaries(String steamId) {
        WSRequest request = ws.url("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/")
                .addQueryParameter("key", STEAM_KEY)
                .addQueryParameter("steamids", steamId);
        CompletionStage<WSResponse> responsePromise = request.get();

        return responsePromise.thenApply(response -> {
            JsonNode jsBody = response.getBody(json());
            return PlayerSummaries.parseFrom(jsBody);
        });
    }


    public CompletionStage<JsonNode> getPlayerAchievements(String steamId, String gameId) {
        WSRequest request = ws.url("http://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/")
                .addQueryParameter("appid", gameId)
                .addQueryParameter("key", STEAM_KEY)
                .addQueryParameter("steamid", steamId);
        CompletionStage<WSResponse> responsePromise = request.get();

        return responsePromise.thenApply(response -> {
            if (response.getStatus() != 200) {
                throw new IllegalStateException("wrong status from steamApi");
            }
            return response.getBody(json());
        });
    }

    public CompletionStage<GameSchema> getSchemaForGame(String gameId) {
        WSRequest request = ws.url("http://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/")
                .addQueryParameter("appid", gameId)
                .addQueryParameter("key", STEAM_KEY);
        CompletionStage<WSResponse> responsePromise = request.get();

        return responsePromise.thenApply(response -> {
            if (response.getStatus() != 200) {
                throw new IllegalStateException("wrong status from steamApi");
            }
            return GameSchema.parseFrom(response.getBody(json()), Integer.parseInt(gameId));
        });
    }

}

