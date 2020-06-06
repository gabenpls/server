package clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.ws.StandaloneWSRequest;
import play.libs.ws.StandaloneWSResponse;
import play.libs.ws.WSRequestExecutor;

import java.util.concurrent.CompletionStage;

public class WSRequestExecutorLogging implements WSRequestExecutor {

    private final Logger log = LoggerFactory.getLogger("access");
    private final WSRequestExecutor executor;

    public WSRequestExecutorLogging(WSRequestExecutor executor) {

        this.executor = executor;
    }

    @Override
    public CompletionStage<StandaloneWSResponse> apply(StandaloneWSRequest request) {
        long start = System.currentTimeMillis();
        return executor.apply(request).whenComplete((response, error) -> {
            int status = response != null ? response.getStatus() : 500;
            long duration = System.currentTimeMillis() - start;
            log.info("[OUTCOMMING] [{}] [{}] [{}] [{}ms] ", request.getUrl(), request.getMethod(), status, duration);
        });
    }
}
