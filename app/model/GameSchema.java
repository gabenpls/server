package model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class GameSchema {
    private final int id;
    private final String name;
    private final List<Achievement> achievementList;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Achievement> getAchievementList() {
        return achievementList;
    }

    public GameSchema(int id, String name, List<Achievement> achievementList) {
        this.id = id;
        this.name = name;
        this.achievementList = achievementList;
    }

    public static GameSchema parseFrom(JsonNode json, int id) {
        String name = json.get("game").get("gameName").asText();
        List<Achievement> achievementList = new ArrayList<>();
        JsonNode achievJsonArray = json.get("game").get("availableGameStats").get("achievements");
        for (JsonNode elem : achievJsonArray) {
            achievementList.add(Achievement.parseFromGameSchema(elem));
        }
        return new GameSchema(id, name, achievementList);
    }

}
