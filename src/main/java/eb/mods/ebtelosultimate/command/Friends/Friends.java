package eb.mods.ebtelosultimate.command.Friends;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eb.mods.ebtelosultimate.util.fileManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class Friends {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(ClientCommandManager.literal("friends").executes(Friends::run));
        dispatcher.register(ClientCommandManager.literal("friends").then(ClientCommandManager.literal("add").then(ClientCommandManager.argument("Player name", StringArgumentType.string()).executes(SubCommands::add))));
        dispatcher.register(ClientCommandManager.literal("friends").then(ClientCommandManager.literal("remove").then(ClientCommandManager.argument("Player name", StringArgumentType.string()).executes(SubCommands::remove))));
        dispatcher.register(ClientCommandManager.literal("friends").then(ClientCommandManager.literal("list").executes(Friends::run)));
        dispatcher.register(ClientCommandManager.literal("friends").then(ClientCommandManager.literal("tp").then(ClientCommandManager.argument("Player name", StringArgumentType.string()).executes(SubCommands::tp))));

        //Aliases
        dispatcher.register(ClientCommandManager.literal("fl").executes(Friends::run));
        dispatcher.register(ClientCommandManager.literal("ftp").then(ClientCommandManager.argument("Player name", StringArgumentType.string()).executes(SubCommands::tp)));
    }

    private static int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        ClientPlayerEntity player = context.getSource().getPlayer();
        List<String> friendsList = getFriendsList();
        if (friendsList == null) {
            player.sendMessage(Text.literal("Error reading friends.txt").fillStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            return 0;
        }
        if (friendsList.size() == 0) {
            player.sendMessage(Text.literal("You have no friends!").fillStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            return 0;
        }

        player.sendMessage(Text.literal("Checking friends... This may take a while").fillStyle(Style.EMPTY.withColor(Formatting.AQUA)), false);

        List<String> onlineFriends = new ArrayList<>();
        List<String> offlineFriends = new ArrayList<>();
        List<String> errorFriends = new ArrayList<>();

        CompletableFuture.runAsync(() -> {
            for (String friend : friendsList) {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.telosrealms.com/lookup/player/" + friend))
                        .header("accept", "application/json").build();
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        Map json = new Gson().fromJson(response.body(), Map.class); //Json parsing
                        if (json != null && json.containsKey("data")) {
                            if (json.get("data") == null) {
                                errorFriends.add(friend); continue;
                            }
                            Map<String, Object> data = (Map<String, Object>) json.get("data");
                            if (data.containsKey("currentServer") && data.get("currentServer") != null) {
                                String serverName = data.get("currentServer").toString().split(":")[1]; //For some reason the server name is in the format "something:serverName"
                                onlineFriends.add(friend + " - " + serverName); continue;
                            } else if (data.containsKey("lastPlayed")) {
                                DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                                df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
                                BigInteger lastPlayed = new BigInteger(df.format(data.get("lastPlayed")));
                                offlineFriends.add(friend + ":Last seen " + getString(lastPlayed)); continue;
                            }
                        }
                    }
                    errorFriends.add(friend);
                }
                catch (IOException | InterruptedException e) {
                    errorFriends.add(friend);
                }
            }
        }).thenAccept(result -> {
            MutableText friendsText = Text.empty();

            if (!onlineFriends.isEmpty()) {
                friendsText.append(Text.literal("Online Friends:").fillStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)));
                int i = 1;
                for (String onlineFriend : onlineFriends) {
                    friendsText.append(Text.literal("\n" + i + ". " + onlineFriend).fillStyle(Style.EMPTY.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends tp " + onlineFriend.split(" - ")[0])).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to teleport to " + onlineFriend.split(" - ")[0])))));
                    i++;
                }
            }
            if (!offlineFriends.isEmpty()) {
                friendsText.append(Text.literal("\n\nOffline Friends:").fillStyle(Style.EMPTY.withColor(Formatting.RED)));
                int i = 1;
                for (String offlineFriend : offlineFriends) {
                    String[] friendData = offlineFriend.split(":");
                    friendsText.append(Text.literal("\n" + i + ". " + friendData[0]).fillStyle(Style.EMPTY.withColor(Formatting.DARK_RED).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(friendData[1])))));
                    i++;
                }
            }
            if (!errorFriends.isEmpty()) {
                friendsText.append(Text.literal("\n\nError Friends:").fillStyle(Style.EMPTY.withColor(Formatting.RED)));
                int i = 1;
                for (String errorFriend : errorFriends) {
                    friendsText.append(Text.literal("\n" + i + ". " + errorFriend).fillStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
                    i++;
                }
            }
            if (friendsText.getString().isEmpty()) {
                friendsText = Text.literal("You have no friends!").fillStyle(Style.EMPTY.withColor(Formatting.AQUA));
            }
            player.sendMessage(friendsText);
        });
        return 1;
    }

    @NotNull
    private static String getString(BigInteger timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp.longValueExact());
        Duration duration = Duration.between(instant, Instant.now());
        long days = duration.toDays();
        long hours = duration.toHours();
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds();

        String timeAgo = "";

        if (days > 0) {
            timeAgo =  days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (hours > 0) {
            timeAgo =  hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (minutes > 0) {
            timeAgo =  minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (seconds > 0) {
            timeAgo =  seconds + " second" + (seconds > 1 ? "s" : "") + " ago";
        }
        if (timeAgo.isEmpty()) {
            timeAgo = "just now";
        }
        return timeAgo;
    }

    static List<String> getFriendsList() {
        if (!fileManager.isFileExist("friends.txt")) {
            if (!fileManager.createFile("friends.txt")) {
                return null;
            }
        }
        String data = fileManager.getFileDataAsString("friends.txt");
        if (data == null) {
            return null;
        }
        if (data.equals("")) {
            return List.of();
        }
        return List.of(data.split("\n"));
    }
}
