package controllers.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

public class LoggingAction extends play.mvc.Action.Simple {
    Logger log = LoggerFactory.getLogger("access");

    public CompletionStage<Result> call(Http.Request req) {
        log.info("request [{}] received", req.uri());
        long start = System.currentTimeMillis();

        return delegate.call(req).whenComplete((ok, error) -> {
            long duration = System.currentTimeMillis() - start;
            if (ok != null) {
                log.info("request [{}] processed in [{}ms] result is [ok]", req.uri(), duration);
            } else {
                log.error("request [{}] processed in [{}ms] result is [error]", req.uri(), duration);
            }
        });
    }
}
