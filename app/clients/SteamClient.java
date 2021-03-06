package clients;

import javax.inject.Inject;

import akka.event.LoggingFilter;
import com.fasterxml.jackson.databind.JsonNode;
import model.Achievement;
import model.Game;
import model.GameSchema;
import model.PlayerSummaries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.ws.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SteamClient implements WSBodyReadables, WSBodyWritables {
    private final WSClient ws;
    private static final String STEAM_KEY = "00A01E3C408E32DE20C045C5FCCD944E";
    private final LoggingWSFilter loggingFilter = new LoggingWSFilter();
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    public SteamClient(WSClient ws) {
        this.ws = ws;
    }

    public CompletionStage<PlayerSummaries> getPlayerSummaries(String steamId) {
        WSRequest request = ws.url("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/")
                .addQueryParameter("key", STEAM_KEY)
                .addQueryParameter("steamids", steamId)
                .setRequestFilter(loggingFilter);
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
                .addQueryParameter("steamid", steamId)
                .setRequestFilter(loggingFilter);
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
                .addQueryParameter("key", STEAM_KEY)
                .setRequestFilter(loggingFilter);
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
                .addQueryParameter("include_appinfo", "1")
                .setRequestFilter(loggingFilter);
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
                .addQueryParameter("format", "json")
                .setRequestFilter(loggingFilter);

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

    public CompletionStage<Boolean> isVisible(String steamId) {
        WSRequest statusRequest = ws.url("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/")
                .addQueryParameter("key", STEAM_KEY)
                .addQueryParameter("steamids", steamId)
                .setRequestFilter(loggingFilter);
        CompletionStage<WSResponse> statusPromise = statusRequest.get();
        return statusPromise.thenApply(response -> {
            if (response.getStatus() != 200) {
                error(response);
            }
            int visibleParam = response.getBody(json())
                    .get("response")
                    .get("players")
                    .get(0)
                    .get("communityvisibilitystate").asInt();
            return (visibleParam == 3);
        });

    }
}

