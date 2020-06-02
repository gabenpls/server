package clients;

import cache.Cache;
import cache.Key;
import model.GameSchema;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class CachedSteamClient extends SteamClient {

    Cache<GameSchema> schemaCache = new Cache<>();

    @Inject
    public CachedSteamClient(WSClient ws) {
        super(ws);
    }

    @Override
    public CompletionStage<GameSchema> getSchemaForGame(Integer gameId) {

        if (schemaCache.contains(new Key(gameId, 11111111L))) {
            log.info("schema cache hit [{}]", gameId);
            schemaCache.contains(new Key("lel", 11111L));

            return CompletableFuture.completedFuture(schemaCache.get(new Key(gameId, 1111L)));
        } else {
            log.info("schema cache miss [{}]", gameId);

            return super.getSchemaForGame(gameId).whenComplete((schema, error) -> {
                if (error == null) {
                    schemaCache.put(new Key(gameId, 86400000L), schema);
                }
            });
        }
    }
}
