package model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import logic.NumericUtils;
import play.libs.Json;

import java.lang.reflect.Field;
import java.util.*;

public class Achievement {
    private final String iconUrl;
    private final String title;
    private final String description;
    private final String apiName;
    private final Boolean isAchieved;
    private final String iconUrlGray;
    private final Double percent;
    private final Game game;
    private final Integer unlockTime;

    public Boolean getAchieved() {
        return isAchieved;
    }

    public Integer getUnlockTime() {
        return unlockTime;
    }

    public Game getGame() {
        return game;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getApiName() {
        return apiName;
    }

    public String getIconUrlGray() {
        return iconUrlGray;
    }

    public Boolean isAchieved() {
        return isAchieved;
    }

    public Double getPercent() {
        return percent;
    }

    public Achievement(String iconUrl, String title, String description, String apiName, Boolean isAchieved
            , String iconUrlGray, Double percent, Integer unlockTime, Game game) {
        this.iconUrl = iconUrl;
        this.title = title;
        this.description = description;
        this.apiName = apiName;
        this.isAchieved = isAchieved;
        this.iconUrlGray = iconUrlGray;
        this.percent = percent;
        this.unlockTime = unlockTime;
        this.game = game;
    }

    public Achievement mergeAch(Achievement ach) {
        return new Achievement(
                this.getIconUrl() != null ? this.getIconUrl() : ach.getIconUrl(),
                this.getTitle() != null ? this.getTitle() : ach.getTitle(),
                this.getDescription() != null ? this.getDescription() : ach.getDescription(),
                this.getApiName() != null ? this.getApiName() : ach.getApiName(),
                this.isAchieved() != null ? this.isAchieved() : ach.isAchieved(),
                this.getIconUrlGray() != null ? this.getIconUrlGray() : ach.getIconUrlGray(),
                this.getPercent() != null ? this.getPercent() : ach.getPercent(),
                this.getUnlockTime() != null ? this.getUnlockTime() : ach.getUnlockTime(),
                null
        );
    }


    public Achievement mergeAch(Game game) {
        return new Achievement(
                this.getIconUrl(),
                this.getTitle(),
                this.getDescription(),
                this.getApiName(),
                this.isAchieved(),
                this.getIconUrlGray(),
                this.getPercent(),
                this.getUnlockTime(),
                game
        );
    }


    public static Achievement parseFromGameSchema(JsonNode json) {
        String iconUrl = json.get("icon").asText();
        String title = json.get("displayName").asText();
        String description = "";
        if (json.get("description") != null) {
            description = json.get("description").asText();
        }
        String apiName = json.get("name").asText();
        String iconUrlGray = json.get("icongray").asText();
        return new Achievement(
                iconUrl,
                title,
                description,
                apiName,
                null,
                iconUrlGray,
                null,
                null,
                null);
    }

    public static List<Achievement> parseListFromPlayersAchievements(JsonNode json) {
        List<Achievement> result = new ArrayList<>();
        json.get("playerstats").get("achievements").forEach(elem -> {
            result.add(Achievement.parseFromPlayersAchievements(elem));
        });
        return result;
    }

    public static Achievement parseFromPlayersAchievements(JsonNode json) {
//        String iconUrl = null;
//        String title = null;
//        String description = null;
        String apiName = json.get("apiname").asText();
        Boolean isAchieved = json.get("achieved").asBoolean();
//        String iconUrlGray = null;
//        Integer unlockTime = null;

        return new Achievement(
                null,
                null,
                null,
                apiName,
                isAchieved,
                null,
                null,
                null,
                null);

    }

    public static List<Achievement> parseListPercentFrom(JsonNode json) {
        List<Achievement> result = new ArrayList<>();
        json.get("achievementpercentages").get("achievements").forEach(elem -> {
            double percent = elem.get("percent").asDouble();
            result.add(new Achievement(
                    null,
                    null,
                    null,
                    elem.get("name").asText(),
                    null,
                    null,
                    NumericUtils.round(percent, 2),
                    null,
                    null));
        });
        return result;
    }

    public ObjectNode asJson() {
        ObjectNode result = Json.newObject();

        result.put("iconUrl", this.iconUrl);
        result.put("title", this.title);
        result.put("description", this.description);
        result.put("apiName", this.apiName);
        result.put("iconUrlGray", this.iconUrlGray);
        result.put("isAchieved", this.isAchieved);
        result.put("percent", this.percent);
        result.set("game", this.game.asJson());

        return result;
    }


}
