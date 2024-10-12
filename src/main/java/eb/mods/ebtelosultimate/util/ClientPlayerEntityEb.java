package eb.mods.ebtelosultimate.util;

import com.google.gson.Gson;
import net.minecraft.client.network.ClientPlayerEntity;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.IdentityHashMap;
import java.util.Map;


public class ClientPlayerEntityEb {
    private String playerName;
    public ClientPlayerEntityEb(ClientPlayerEntity clientPlayerEntity) {
        this.playerName = clientPlayerEntity.getName().getString();
    }

    public ClientPlayerEntityEb(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Map<String, Integer> getPlayerServer() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.telosrealms.com/lookup/player/" + playerName))
                .header("accept", "application/json").build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Map json = new Gson().fromJson(response.body(), Map.class);
                if (json.get("data") != null) {
                    Map data = (Map) json.get("data");
                    if (data.get("currentServer") != null) {
                        String serverName = data.get("currentServer").toString().split(":")[1];
                        return new IdentityHashMap<>(Map.of(serverName, 1));
                    } else {
                        if (data.get("lastPlayed") != null) {
                            return new IdentityHashMap<>(Map.of("Player " + playerName + " is offline", 0));
                        }
                    }
                }
            }

        } catch (IOException | InterruptedException e) {}
        return new IdentityHashMap<>(Map.of("Error teleporting to " + playerName, 0));
    }

}
