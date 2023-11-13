package io.greitan.mineserv.network;

import java.util.List;

public class Payloads {
    public static class LoginPacket {
        public int type = 0;
        public String loginKey = "";
    }

    public static class BindingPacket {
        public int type = 1;
        public String loginKey = "";
        public int playerId = 0;
        public String playerKey = "";
        public String gamertag = "";
    }

    public static class UpdatePlayersPacket {
        public int type = 2;
        public String loginKey = "";
        public List<PlayerData> players;
    }

    public static class UpdateSettingsPacket {
        public int type = 3;
        public String loginKey = "";
        public ServerSettings settings = new ServerSettings();
    }

    public static class GetSettingsPacket {
        public int type = 4;
        public String loginKey = "";
    }

    public static class DisconnectPlayerPacket {
        public int type = 5;
        public String loginKey = "";
        public int playerId = 0;
    }

    public static class ServerSettings {
        public int proximityDistance = 30;
        public boolean proximityToggle = true;
        public boolean voiceEffects = true;
    }

    public static class PlayerData {
        public int PlayerId = 0;
        public String DimensionId = "";
        public LocationData Location = new LocationData();
        public double Rotation = 0.0;
        public double CaveDensity = 0.0;
        public boolean IsDead = false;
        public boolean InWater = false;
    }

    public static class LocationData {
        public double x = 0;
        public double y = 0;
        public double z = 0;
    }
}
