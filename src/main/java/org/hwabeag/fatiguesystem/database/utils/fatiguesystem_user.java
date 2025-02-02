package org.hwabeag.fatiguesystem.database.utils;

public class fatiguesystem_user {
    private int player_point;
    private String player_uuid;

    public String getPlayerUuid() {
        return player_uuid;
    }

    public void setPlayerUuid(String PlayerUuid) {
        this.player_uuid = PlayerUuid;
    }

    public int getPlayerPoint() {
        return player_point;
    }

    public void setPlayerPoint(int PlayerPoint) {
        this.player_point = PlayerPoint;
    }
}
