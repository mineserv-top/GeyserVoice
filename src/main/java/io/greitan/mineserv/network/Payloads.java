package io.greitan.mineserv.network;

import java.util.List;

public class Payloads {
    public static class MCCommPacket {
        public int PacketType = 0;
        public Object PacketData = null;
    }

    public static class LoginPacket {
        public String LoginKey = "";
    }

    public static class BindingPacket {
        public String LoginKey = "";
        public int PlayerId = 0;
        public String PlayerKey = "";
        public String Gamertag = "";
    }

    public static class UpdatePlayersPacket {
        public String LoginKey = "";
        public List<PlayerData> Players;
    }

    public static class UpdateSettingsPacket {
        public String LoginKey = "";
        public int ProximityDistance = 30;
        public boolean ProximityToggle = true;
        public boolean VoiceEffects = true;
    }

    public static class GetSettingsPacket {
        public String LoginKey = "";
    }

    public static class DisconnectPlayerPacket {
        public String LoginKey = "";
        public int PlayerId = 0;
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
