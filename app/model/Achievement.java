package model;

public class Achievement {
    private final String iconUrl;
    private final String title;

    public String getIconUrl() {
        return iconUrl;
    }

    public String getTitle() {
        return title;
    }


    public Achievement(String title, String iconUrl) {
        this.iconUrl = iconUrl;
        this.title = title;
    }

}
