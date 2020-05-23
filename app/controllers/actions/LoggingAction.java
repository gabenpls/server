package controllers.actions;

import controllers.SteamLoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class LoggingAction extends play.mvc.Action.Simple {
    Logger log = LoggerFactory.getLogger("access");

    public CompletionStage<Result> call(Http.Request req) {
        long start = System.currentTimeMillis();
        String steamId = req.session().get(SteamLoginController.STEAM_ID_NAME).orElse("-");
        String ip = req.remoteAddress();


        return delegate.call(req).whenComplete((response, error) -> {
            int status = response != null ? response.status() : 500;
            long duration = System.currentTimeMillis() - start;
            log.info("[{}] [{}] [{}] [{} {}] [{}] [{}ms] ", req.id(), ip, steamId, req.method(), req.uri(), status, duration);
        });
    }
}
