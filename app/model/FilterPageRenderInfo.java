package model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;

public class FilterPageRenderInfo {

    private final List<Achievement> achievements;
    private final List<Game> games;

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public List<Game> getGames() {
        return games;
    }


    public FilterPageRenderInfo(List<Achievement> achievements, List<Game> games) {
        this.achievements = achievements;
        this.games = games;
    }

    public ObjectNode asJson() {
        ObjectNode result = Json.newObject();
        ArrayNode achievementsJson = Json.newArray();
        ArrayNode gamesJson = Json.newArray();

        for (Achievement ach : this.getAchievements()) {
            achievementsJson.add(ach.asJson());
        }
        for (Game game : this.getGames()) {
            gamesJson.add(game.asJson());
        }

        result.set("achievements", achievementsJson);
//        result.set("games", gamesJson);

        return result;
    }

    public FilterPageRenderInfo page(int number, int size) {

        List<Achievement> currentPage = this.achievements.subList(number * size, number * size + size);

        return new FilterPageRenderInfo(currentPage, this.games);
    }
}
