package model;

import com.fasterxml.jackson.databind.JsonNode;

public class PlayerSummaries {
    private final String avatarUrl;
    private final String nickName;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public PlayerSummaries(String avatarUrl, String nickName) {
        this.avatarUrl = avatarUrl;
        this.nickName = nickName;
    }

    public static PlayerSummaries parseFrom(JsonNode json) {
        JsonNode playerInfo = json.get("response").get("players").get(0);

        String avatarUrl = playerInfo.get("avatarfull").asText();
        String nickName = playerInfo.get("personaname").asText();

        return new PlayerSummaries(avatarUrl, nickName);
    }

}
