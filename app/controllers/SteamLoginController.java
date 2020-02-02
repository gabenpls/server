package controllers;

import play.libs.openid.OpenIdClient;
import play.libs.openid.UserInfo;
import play.mvc.*;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class SteamLoginController extends Controller {

    @Inject
    OpenIdClient openIdClient;

    public CompletionStage<Result> login(Http.Request request) {

        CompletionStage<String> redirectUrlPromise =
                openIdClient.redirectURL(
                        "https://steamcommunity.com/openid", routes.SteamLoginController.callBack().absoluteURL(request));

        return redirectUrlPromise
                .thenApply(Controller::redirect);
//                .exceptionally(throwable -> badRequest(views.html.login.render(throwable.getMessage())));
    }

    public CompletionStage<Result> callBack(Http.Request request) {
        return openIdClient.verifiedId(request)
                .thenApply(userInfo -> ok(views.html.hello.render(userInfo.id())))
                .exceptionally(throwable -> badRequest(views.html.hello.render(throwable.getMessage())));
    }
}
