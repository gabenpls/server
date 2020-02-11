package controllers;

import clients.SteamClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.*;

import javax.inject.Inject;
import javax.management.StandardEmitterMBean;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    @Inject
    SteamClient steamClient;
    final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public CompletionStage<Result> index(Http.Request request) {
        Optional<String> optSteamId = request.session()
                .get(SteamLoginController.STEAM_ID_NAME);
        if (optSteamId.isPresent()) {
            log.info(optSteamId.toString());
            return steamClient.getAvatar(optSteamId.get()).thenApply(avatarUrl -> {
                return ok(views.html.hello.render(avatarUrl));
            });

        } else {
            return CompletableFuture.completedFuture(ok(views.html.index.render()));
        }
    }


}
