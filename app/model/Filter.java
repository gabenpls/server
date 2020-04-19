package model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class Filter {

    private final List<Integer> gameIds;


    public List<Integer> getGameIds() {
        return gameIds;
    }

    public Filter(List<Integer> gameIds) {
        this.gameIds = gameIds;
    }

    public static Filter parseFrom(JsonNode json) {
        List<Integer> gameIds = new ArrayList<>();
        json.get("games").forEach(e -> {
            gameIds.add(e.asInt());
        });
        return new Filter(gameIds);
    }

}
