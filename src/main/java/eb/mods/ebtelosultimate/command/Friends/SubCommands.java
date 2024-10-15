package eb.mods.ebtelosultimate.command.Friends;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eb.mods.ebtelosultimate.util.ChatHelper;
import eb.mods.ebtelosultimate.util.ClientPlayerEntityEb;
import eb.mods.ebtelosultimate.util.HelperColorText;
import eb.mods.ebtelosultimate.util.fileManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static eb.mods.ebtelosultimate.command.Friends.Friends.getFriendsList;

public class SubCommands {

    public static Boolean isWaitingForResponse = false;
    public static List<String> waitingForResponse = new ArrayList<>();

    static int add(CommandContext<FabricClientCommandSource> context) {
        final String playerName = StringArgumentType.getString(context, "Player name");
        ClientPlayerEntity player = context.getSource().getPlayer();
        List<String> friendsList = getFriendsList();
        if (friendsList == null) {
            player.sendMessage(HelperColorText.colorText("{red}Error reading friends.txt"), false);
            return 0;
        }
        if (friendsList.contains(playerName)) {
            player.sendMessage(HelperColorText.colorText("{Green}" + playerName + "{Yellow} is already in your friends list"), false);
            return 0;
        }
        isWaitingForResponse = true;
        if (!waitingForResponse.contains(playerName.toLowerCase(Locale.ENGLISH))) {
            player.networkHandler.sendChatCommand("msg " + playerName + " You got a friend request from this player! Accepting lets him see your realm and teleport to you from the hub. Do you accept? (Reply Yes/No) No response in 5 min = No. | NOTE: This is an automated message from the EbTelosUltimate mod.");
            waitingForResponse.add(playerName.toLowerCase(Locale.ENGLISH));
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(1000 * 60 * 5);
                    if (waitingForResponse.contains(playerName.toLowerCase(Locale.ENGLISH))) {
                        waitingForResponse.remove(playerName.toLowerCase(Locale.ENGLISH));
                        if (waitingForResponse.isEmpty()) {
                            isWaitingForResponse = false;
                        }
                        player.sendMessage(HelperColorText.colorText("{Yellow}No response from {red}" + playerName + "{Yellow}. Request has been canceled"), false);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else {
            player.sendMessage(HelperColorText.colorText("{Yellow}You have already sent a friend request to {red}" + playerName), false);
            return 0;
        }
        return 1;
    }

    public static int remove(CommandContext<FabricClientCommandSource> context) {
        final String playerName = StringArgumentType.getString(context, "Player name");
        ClientPlayerEntity player = context.getSource().getPlayer();

        List<String> friendsList = getFriendsList();
        if (friendsList == null) {
            player.sendMessage(HelperColorText.colorText("{red}Error reading friends.txt"), false);
            return 0;
        }

        if (!friendsList.contains(playerName)) {
            player.sendMessage(HelperColorText.colorText("{Green}" + playerName + "{Yellow} is not in your friends list"), false);
            return 0;
        }
        List<String> newFriendsList = new ArrayList<>(friendsList);
        newFriendsList.remove(playerName);
        StringBuilder newFriendsListString = new StringBuilder(newFriendsList.removeFirst());
        if (!newFriendsList.isEmpty()) {
            for (String friend : newFriendsList) {
                newFriendsListString.append("\n").append(friend);
            }
        }
        if (fileManager.reWriteFile("friends.txt", newFriendsListString.toString())){
            player.sendMessage(HelperColorText.colorText("{Green}" + playerName + "{Yellow} has been removed from your friends list"), false);
            return 1;
        } else {
            player.sendMessage(HelperColorText.colorText("{dark_red}Error removing {green}" + playerName + "{dark_red} from your friends list"), false);
            return 0;
        }
    }

    public static int tp(CommandContext<FabricClientCommandSource> context) {
        final String tpTo = StringArgumentType.getString(context, "Player name");
        ClientPlayerEntity player = context.getSource().getPlayer();
        ClientPlayerEntityEb ebPlayer = new ClientPlayerEntityEb(context.getSource().getPlayer());

        player.sendMessage(HelperColorText.colorText("{yellow}Teleporting to {dark_aqua}" + tpTo), false);
        CompletableFuture.runAsync(() -> {
            ClientPlayerEntityEb targetPlayer = new ClientPlayerEntityEb(tpTo);
            Map.Entry<String, Integer> playerServer = targetPlayer.getPlayerServer().entrySet().iterator().next();
            if (playerServer.getValue() == 0) {
                player.sendMessage(HelperColorText.colorText("{red}" + playerServer.getKey()), false);
                return;
            }
            int waitTime = 250;
            player.networkHandler.sendChatCommand("joinq " + playerServer.getKey());
            while (!ebPlayer.getPlayerServer().entrySet().iterator().next().getKey().equals(playerServer.getKey())) {
                try {
                    Thread.sleep(100);
                    waitTime --;
                    if (waitTime == 0) {
                        player.sendMessage(HelperColorText.colorText("{red}Wait time exceeded. Error teleporting to {dark_aqua}" + tpTo), false);
                        return;
                    }
                } catch (InterruptedException e) {
                    player.sendMessage(HelperColorText.colorText("{red}Thread error teleporting to {dark_aqua}" + tpTo).fillStyle(Style.EMPTY.withColor(Formatting.RED)), false);
                    Thread.currentThread().interrupt();
                }
            }
            player.networkHandler.sendChatCommand("tp " + tpTo);
        });
        return 1;
    }
}
