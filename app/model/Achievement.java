package model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Achievement {
    private final String iconUrl;
    private final String title;
    private final String description;
    private final String apiName;
    private final boolean isAchieved;
    private final String iconUrlGray;
    private final double percent;

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

    public boolean isAchieved() {
        return isAchieved;
    }

    public double getPercent() {
        return percent;
    }

    public Achievement(String title, String iconUrl, String description, String apiName, String iconUrlGray) {
        this.iconUrl = iconUrl;
        this.title = title;
        this.description = description;
        this.apiName = apiName;
        this.iconUrlGray = iconUrlGray;

        this.isAchieved = false;
        this.percent = 0;

    }

    public Achievement(boolean isAchieved, String apiName) {
        this.isAchieved = isAchieved;
        this.apiName = apiName;

        this.iconUrl = null;
        this.title = null;
        this.description = null;
        this.iconUrlGray = null;
        this.percent = 1;
    }

    public Achievement(String iconUrl, String title, String description, String apiName, boolean isAchieved, String iconUrlGray) {
        this.iconUrl = iconUrl;
        this.title = title;
        this.description = description;
        this.apiName = apiName;
        this.isAchieved = isAchieved;
        this.iconUrlGray = iconUrlGray;
        this.percent = 2;
    }

    public Achievement(String apiName, double percent) {
        this.apiName = apiName;
        this.percent = percent;

        this.iconUrl = null;
        this.title = null;
        this.description = null;
        this.isAchieved = false;
        this.iconUrlGray = null;
    }

    public Achievement(String iconUrl, String title, String description, String apiName, boolean isAchieved, String iconUrlGray, double percent) {
        this.iconUrl = iconUrl;
        this.title = title;
        this.description = description;
        this.apiName = apiName;
        this.isAchieved = isAchieved;
        this.iconUrlGray = iconUrlGray;
        this.percent = percent;
    }

    public static Achievement parseFromGameSchema(JsonNode json) {
        String iconUrl = json.get("icon").asText();
        String title = json.get("displayName").asText();
        String description = json.get("description").asText();
        String apiName = json.get("name").asText();
        String iconUrlGray = json.get("icongray").asText();
        return new Achievement(title, iconUrl, description, apiName, iconUrlGray);
    }

    public static List<Achievement> parseListFromPlayersAchievements(JsonNode json) {
        List<Achievement> result = new ArrayList<>();
        json.get("playerstats").get("achievements").forEach(elem -> {
            result.add(Achievement.parseFromPlayersAchievements(elem));
        });
        return result;
    }

    public static Achievement parseFromPlayersAchievements(JsonNode json) {
        String apiName = json.get("apiname").asText();
        boolean achieved = json.get("achieved").asBoolean();
        return new Achievement(achieved, apiName);

    }

    public static List<Achievement> parseListPercentFrom(JsonNode json) {
        List<Achievement> result = new ArrayList<>();
        json.get("achievementpercentages").get("achievements").forEach(elem -> {
            result.add(new Achievement(elem.get("name").asText(), elem.get("percent").asDouble()));
        });
        return result;
    }

    public static List<Achievement> sortByPercent(List<Achievement> achievementList) {
        List<Achievement> sortedList = new ArrayList<>();
        Achievement tmp;
        int index = 0;
        for (int i = 0; i < achievementList.size(); i++) {
            tmp = achievementList.get(i);
            for (Achievement elem : achievementList) {
                if (elem.getPercent() > tmp.getPercent()) {
                    tmp = elem;
                    index = achievementList.indexOf(elem);
                } else {
                    index = 0;
                }
            }
            sortedList.add(tmp);
            achievementList.remove(index);
        }
        return sortedList;
    }
}
