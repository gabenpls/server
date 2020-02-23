package model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final int id;

    public int getId() {
        return id;
    }

    public Game(int id) {
        this.id = id;
    }

    public static Game parseFrom(JsonNode json) {
        int id = json.get("appid").asInt();
        return new Game(id);
    }

    public static List<Game> parseListFrom(JsonNode json) {
        List<Game> result = new ArrayList<Game>();
        json.get("response").get("games").forEach(nodeElem -> {
            result.add(Game.parseFrom(nodeElem));
        });
        return result;
    }
}
