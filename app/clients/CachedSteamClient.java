package clients;

import cache.Cache;
import model.Achievement;
import model.Game;
import model.GameSchema;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class CachedSteamClient extends SteamClient {

    Cache<Integer, GameSchema> schemaCache = new Cache<>(1000L * 60);
    Cache<String, List<Game>> playersGamesCache = new Cache<>(1000L * 60);
    Cache<Integer, List<Achievement>> achievementPercentCache = new Cache<>(1000L * 60);

    @Inject
    public CachedSteamClient(WSClient ws) {
        super(ws);
    }

    @Override
    public CompletionStage<GameSchema> getSchemaForGame(Integer gameId) {

        if (schemaCache.contains(gameId)) {
            log.info("schema cache hit [{}]", gameId);

            return CompletableFuture.completedFuture(schemaCache.get(gameId));
        } else {
            log.info("schema cache miss [{}]", gameId);

            return super.getSchemaForGame(gameId).whenComplete((schema, error) -> {
                if (error == null) {
                    schemaCache.put(gameId, schema);
                }
            });
        }
    }

    @Override
    public CompletionStage<List<Game>> getPlayerGames(String steamId) {

        if (playersGamesCache.contains(steamId)) {
            log.info("playerGames cache hit [{}]", steamId);

            return CompletableFuture.completedFuture(playersGamesCache.get(steamId));
        } else {
            log.info("playerGames cache miss [{}]", steamId);

            return super.getPlayerGames(steamId).whenComplete((games, error) -> {
                if (error == null) {
                    playersGamesCache.put(steamId, games);
                }
            });
        }
    }

    @Override
    public CompletionStage<List<Achievement>> getAchievementsPercent(Integer gameId) {
        if (achievementPercentCache.contains(gameId)) {
            log.info("achievementPercent cache hit [{}]", gameId);

            return CompletableFuture.completedFuture(achievementPercentCache.get(gameId));
        } else {
            log.info("achievementPercent cache miss [{}]", gameId);

            return super.getAchievementsPercent(gameId).whenComplete((percent, error) -> {
                if (error == null) {
                    achievementPercentCache.put(gameId, percent);
                }
            });
        }

    }
}
