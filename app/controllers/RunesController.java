package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class RunesController extends Controller {

    public Result runesPage(Http.Request request) {
        return ok(views.html.runesPage.render());
    }


}
