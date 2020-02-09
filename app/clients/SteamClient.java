package clients;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.*;
import play.libs.ws.*;

import java.util.concurrent.CompletionStage;

public class SteamClient implements WSBodyReadables, WSBodyWritables {
    private final WSClient ws;
    private static final String STEAM_KEY = "A9F0A04C59611FF92C1005BB076AD5C6";

    @Inject
    public SteamClient(WSClient ws) {
        this.ws = ws;
    }

    public CompletionStage<String> getAvatar(String steamId) {
        WSRequest request = ws.url("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/")
                .addQueryParameter("key", STEAM_KEY)
                .addQueryParameter("steamids", steamId);
        CompletionStage<WSResponse> responsePromise = request.get();
        CompletionStage<String> avatarResp = responsePromise.thenApply(response -> {
            JsonNode jsBody = response.getBody(json());
            return jsBody.get("response").get("players").get(0).get("avatarfull").asText();
        });

        return avatarResp;
    }
}

