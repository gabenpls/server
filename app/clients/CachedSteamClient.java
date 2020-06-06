package clients;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import model.Achievement;
import model.Game;
import model.GameSchema;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class CachedSteamClient extends SteamClient {


    Cache<Integer, GameSchema> schemaCache = Caffeine.newBuilder()
            .expireAfterWrite(1L, TimeUnit.DAYS)
            .maximumSize(1000)
            .build();

    Cache<String, List<Game>> playersGamesCache = Caffeine.newBuilder()
            .expireAfterWrite(5L, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    Cache<Integer, List<Achievement>> achievementPercentCache = Caffeine.newBuilder()
            .expireAfterWrite(1L, TimeUnit.HOURS)
            .maximumSize(1000)
            .build();


    @Inject
    public CachedSteamClient(WSClient ws) {
        super(ws);
    }

    @Override
    public CompletionStage<GameSchema> getSchemaForGame(Integer gameId) {
        GameSchema cachedSchema = schemaCache.getIfPresent(gameId);
        if (cachedSchema != null) {
            log.info("schema cache hit [{}]", gameId);
            return CompletableFuture.completedFuture(cachedSchema);
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
        List<Game> cachedList = playersGamesCache.getIfPresent(steamId);
        if (cachedList != null) {
            log.info("playerGames cache hit [{}]", steamId);
            return CompletableFuture.completedFuture(cachedList);
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
        List<Achievement> cachedList = achievementPercentCache.getIfPresent(gameId);
        if (cachedList != null) {
            log.info("achievementPercent cache hit [{}]", gameId);
            return CompletableFuture.completedFuture(cachedList);
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
