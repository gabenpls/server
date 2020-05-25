package controllers;

import clients.SteamClient;
import controllers.actions.LoggingAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.*;

import javax.inject.Inject;
import javax.management.StandardEmitterMBean;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
@With(LoggingAction.class)
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
        Optional<String> optSteamId = request.session().get(SteamLoginController.STEAM_ID_NAME);
        CompletionStage<Boolean> isVisible;

        if (optSteamId.isPresent()) {
            isVisible = steamClient.isVisible(optSteamId.get());
        } else return CompletableFuture.completedFuture(ok(views.html.index.render()));

        return isVisible.thenApply(iv -> {
            if (iv) {
                return redirect("/achievements");
            } else {
                return redirect("/changePrivacySettings");
            }
        });
    }

    public Result privacyChangingPage(Http.Request request) {
        return ok(views.html.changePrivacySettings.render());
    }
}
