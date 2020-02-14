package model;

import com.fasterxml.jackson.databind.JsonNode;

public class Achievement {
    private final String iconUrl;
    private final String title;
    private final String description;
    private final String apiName;

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

    public Achievement(String title, String iconUrl, String description, String apiName) {
        this.iconUrl = iconUrl;
        this.title = title;
        this.description = description;
        this.apiName = apiName;
    }

    public static Achievement parseFrom(JsonNode json) {
        String iconUrl = json.get("icon").asText();
        String title = json.get("displayName").asText();
        String description = json.get("description").asText();
        String apiName = json.get("name").asText();
        return new Achievement(title, iconUrl, description, apiName);
    }

}
