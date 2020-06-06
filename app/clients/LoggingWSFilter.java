package clients;

import play.libs.ws.StandaloneWSRequest;
import play.libs.ws.StandaloneWSResponse;
import play.libs.ws.WSRequestExecutor;
import play.libs.ws.WSRequestFilter;

import java.util.concurrent.CompletionStage;

public class LoggingWSFilter implements WSRequestFilter {

    @Override
    public WSRequestExecutor apply(WSRequestExecutor wsRequestExecutor) {
        return new WSRequestExecutorLogging(wsRequestExecutor);
    }
}
