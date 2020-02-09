package controllers;

import play.mvc.*;

import java.util.Optional;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index(Http.Request request) {
        Optional<String> optSteamId = request.session()
                .get(SteamLoginController.STEAM_ID_NAME);
        System.out.println(optSteamId);
        if (optSteamId.isPresent()) {

            return ok(views.html.hello.render(optSteamId.get()));

        } else {
            return ok(views.html.index.render());
        }
    }

    public Result hello(String name) {
        return ok(views.html.hello.render(name));
    }

}
