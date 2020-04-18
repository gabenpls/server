package model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final int id;

    private final String name;

    private final String iconUrl;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Game(int id, String name, String iconUrl) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public static Game parseFrom(JsonNode json) {
        int id = json.get("appid").asInt();
        String iconHash = json.get("img_icon_url").asText();
        String iconUrl = String.format("http://media.steampowered.com/steamcommunity/public/images/apps/%d/%s.jpg", id, iconHash);
        String name = json.get("name").asText();
        return new Game(id, name, iconUrl);
    }

    public static List<Game> parseListFrom(JsonNode json) {
        List<Game> result = new ArrayList<Game>();

        json.get("response").get("games").forEach(nodeElem -> {
            result.add(Game.parseFrom(nodeElem));
        });
        return result;
    }
}
