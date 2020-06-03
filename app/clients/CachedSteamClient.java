package clients;

import cache.Cache;
import model.GameSchema;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class CachedSteamClient extends SteamClient {

    Cache<Integer, GameSchema> schemaCache = new Cache<>(1000L * 10);

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
}
