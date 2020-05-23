package controllers;

import clients.SteamClient;
import play.libs.openid.OpenIdClient;
import play.libs.openid.UserInfo;
import play.mvc.*;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class SteamLoginController extends Controller {

    public static final String STEAM_ID_NAME = "steam_id";
    public static final String STEAM_AVATAR_URL_NAME = "avatar_url";


    @Inject
    OpenIdClient openIdClient;

    @Inject
    SteamClient steamClient;

    private String parseSteamId(String userUrl) {
        return userUrl.replace("https://steamcommunity.com/openid/id/", "");
    }

    public CompletionStage<Result> login(Http.Request request) {

        CompletionStage<String> redirectUrlPromise = openIdClient
                .redirectURL(
                        "https://steamcommunity.com/openid",
                        routes.SteamLoginController.callBack().absoluteURL(request)
                );

        return redirectUrlPromise.thenApply(Controller::redirect);
    }

    public Result logout(Http.Request request) {
        return redirect("/").removingFromSession(request, STEAM_ID_NAME);
    }


    public CompletionStage<Result> callBack(Http.Request request) {
        return openIdClient.verifiedId(request)
                .thenCompose(userInfo -> {
                    String steamId = parseSteamId(userInfo.id());
                    return steamClient.getPlayerSummaries(steamId).thenApply(playerSummaries -> {
                        String avatarUrl = playerSummaries.getAvatarUrl();
                        Result response = redirect("/achievements");
                        response = response.addingToSession(request, STEAM_ID_NAME, steamId);
                        response = response.addingToSession(request, STEAM_AVATAR_URL_NAME, avatarUrl);
                        return response;
                    });
                });
    }

}
