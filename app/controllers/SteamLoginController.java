package controllers;

import play.libs.openid.OpenIdClient;
import play.libs.openid.UserInfo;
import play.mvc.*;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class SteamLoginController extends Controller {

    public static final String STEAM_ID_NAME = "steam_id";


    @Inject
    OpenIdClient openIdClient;

    private String parseSteamId(String userUrl) {
        return userUrl.replace("https://steamcommunity.com/openid/id/", "");
    }

    public CompletionStage<Result> login(Http.Request request) {

        CompletionStage<String> redirectUrlPromise =
                openIdClient.redirectURL(
                        "https://steamcommunity.com/openid", routes.SteamLoginController.callBack().absoluteURL(request));

        return redirectUrlPromise
                .thenApply(Controller::redirect);
//                .exceptionally(throwable -> badRequest(views.html.login.render(throwable.getMessage())));
    }

    public Result logout(Http.Request request) {
        return redirect("/").removingFromSession(request, STEAM_ID_NAME);
    }


    public CompletionStage<Result> callBack(Http.Request request) {
        return openIdClient.verifiedId(request)
                .thenApply(userInfo -> {
                    String steamId = parseSteamId(userInfo.id());
                    Result response = redirect("/");
                    response = response.addingToSession(request, STEAM_ID_NAME, steamId);
                    return response;

                })
                .exceptionally(throwable -> badRequest(views.html.hello.render(throwable.getMessage())));
    }

}
