package eb.mods.ebtelosultimate.command.Friends;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eb.mods.ebtelosultimate.util.ClientPlayerEntityEb;
import eb.mods.ebtelosultimate.util.fileManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static eb.mods.ebtelosultimate.command.Friends.Friends.getFriendsList;

public class SubCommands {

    static int add(CommandContext<FabricClientCommandSource> context) {
        final String playerName = StringArgumentType.getString(context, "Player name");
        ClientPlayerEntity player = context.getSource().getPlayer();
        List<String> friendsList = getFriendsList();
        if (friendsList == null) {
            player.sendMessage(Text.literal("Error reading friends.txt").fillStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            return 0;
        }
        if (friendsList.contains(playerName)) {
            player.sendMessage(Text.literal(playerName + " is already in your friends list").fillStyle(Style.EMPTY.withColor(Formatting.YELLOW)), false);
            return 0;
        }
        fileManager.writeToEndOfFile("friends.txt", playerName);
        player.sendMessage(Text.literal(playerName + " has been added to your friends list").fillStyle(Style.EMPTY.withColor(Formatting.GREEN)), false);
        return 1;
    }

    public static int remove(CommandContext<FabricClientCommandSource> context) {
        final String playerName = StringArgumentType.getString(context, "Player name");
        ClientPlayerEntity player = context.getSource().getPlayer();

        List<String> friendsList = getFriendsList();
        if (friendsList == null) {
            player.sendMessage(Text.literal("Error reading friends.txt").fillStyle(Style.EMPTY.withColor(Formatting.DARK_RED)), false);
            return 0;
        }

        if (!friendsList.contains(playerName)) {
            player.sendMessage(Text.literal(playerName + " is not in your friends list").fillStyle(Style.EMPTY.withColor(Formatting.YELLOW)), false);
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
            player.sendMessage(Text.literal(playerName + " has been removed from your friends list").fillStyle(Style.EMPTY.withColor(Formatting.GREEN)), false);
            return 1;
        } else {
            player.sendMessage(Text.literal("Error removing " + playerName + " from your friends list").fillStyle(Style.EMPTY.withColor(Formatting.DARK_RED)), false);
            return 0;
        }
    }

    public static int tp(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity player = context.getSource().getPlayer();
        player.sendMessage(Text.literal("As rolo (the owner of the server) asked me to rework this command it temporarily down, Soon it will come back").formatted(Formatting.GOLD), false);
        return 1;
    }
}
