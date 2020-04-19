package clients;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.internal.cglib.reflect.$FastMember;
import model.Achievement;
import model.Game;
import model.GameSchema;
import model.PlayerSummaries;
import play.libs.ws.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class SteamClient implements WSBodyReadables, WSBodyWritables {
    private final WSClient ws;
    private static final String STEAM_KEY = "00A01E3C408E32DE20C045C5FCCD944E";

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
            if (response.getStatus() != 200) {
                error(response);
            }
            JsonNode jsBody = response.getBody(json());
            return PlayerSummaries.parseFrom(jsBody);
        });
    }


    public CompletionStage<List<Achievement>> getPlayerAchievements(String steamId, Integer gameId) {
        WSRequest request = ws.url("http://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/")
                .addQueryParameter("appid", gameId.toString())
                .addQueryParameter("key", STEAM_KEY)
                .addQueryParameter("steamid", steamId);
        CompletionStage<WSResponse> responsePromise = request.get();

        return responsePromise.thenApply(response -> {
            if (response.getStatus() != 200) {
                error(response);
            }
            return Achievement.parseListFromPlayersAchievements(response.asJson());
        });
    }

    public CompletionStage<GameSchema> getSchemaForGame(Integer gameId) {
        WSRequest request = ws.url("http://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/")
                .addQueryParameter("appid", gameId.toString())
                .addQueryParameter("key", STEAM_KEY);
        CompletionStage<WSResponse> responsePromise = request.get();

        return responsePromise.thenApply(response -> {
            if (response.getStatus() != 200) {
                error(response);
            }
            return GameSchema.parseFrom(response.getBody(json()), gameId);
        });
    }

    public CompletionStage<List<Game>> getPlayerGames(String steamId) {
        WSRequest request = ws.url("http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/")
                .addQueryParameter("steamid", steamId)
                .addQueryParameter("key", STEAM_KEY)
                .addQueryParameter("include_played_free_games", "1")
                .addQueryParameter("include_appinfo", "1");

        CompletionStage<WSResponse> responsePromise = request.get();
        return responsePromise.thenApply(response -> {
            if (response.getStatus() != 200) {
                error(response);
            }
            return Game.parseListFrom(response.getBody(json()));
        });
    }

    public CompletionStage<List<Achievement>> getAchievementsPercent(Integer gameId) {
        WSRequest request = ws.url("http://api.steampowered.com/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v0002/")
                .addQueryParameter("gameid", gameId.toString())
                .addQueryParameter("format", "json");

        CompletionStage<WSResponse> responsePromise = request.get();
        return responsePromise.thenApply(response -> {
            if (response.getStatus() != 200) {
                error(response);
            }
            return Achievement.parseListPercentFrom(response.getBody(json()));
        });
    }

    private void error(WSResponse response) {
        String status = response.getStatusText();
        String body = response.getBody();
        StringBuilder errorText = new StringBuilder("Wrong status from steam api.\n");
        errorText.append("Status: ");
        errorText.append(status);
        errorText.append("\n");
        errorText.append("Body: ");
        errorText.append(body);
        errorText.append("\n");

        throw new IllegalStateException(errorText.toString());
    }
}

