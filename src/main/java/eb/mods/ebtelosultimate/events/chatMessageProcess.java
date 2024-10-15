package eb.mods.ebtelosultimate.events;

import eb.mods.ebtelosultimate.client.EbTelosUltimateClient;
import eb.mods.ebtelosultimate.command.Friends.SubCommands;
import eb.mods.ebtelosultimate.util.ChatHelper;
import eb.mods.ebtelosultimate.util.HelperColorText;
import eb.mods.ebtelosultimate.util.fileManager;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public class chatMessageProcess implements ClientReceiveMessageEvents.ModifyGame {
    @Override
    public Text modifyReceivedGameMessage(Text message, boolean overlay) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || message.toString().contains("[EbTelosUltimateWaterMark]")) {
            if (message.toString().contains("[EbTelosUltimateWaterMark]")) {
                return Text.of(message.getString().replace("[EbTelosUltimateWaterMark]", ""));
            }
            return message;
        }
        if(SubCommands.isWaitingForResponse){
            if (message.toString().contains("┅")) {
                String[] messageData = ChatHelper.getDMPlayer(message.getString());
                if (messageData == null) {
                    if (message.toString().contains("You got a friend request from this player! Accepting lets him see your realm and teleport to you from the hub. Do you accept? (Reply Yes/No) No response in 5 min = No. | NOTE: This is an automated message from the EbTelosUltimate mod.")) {
                        return HelperColorText.colorText("{Yellow}You have sent a friend request to {green}" + Objects.requireNonNull(ChatHelper.getDMPlayer(message.getString(), true))[0]);
                    }
                    return message;
                }
                String PlayerName = messageData[0];
                if (SubCommands.waitingForResponse.contains(PlayerName.toLowerCase(Locale.ENGLISH))) {
                    if (messageData[1].equalsIgnoreCase("yes")) {
                        SubCommands.waitingForResponse.remove(PlayerName.toLowerCase(Locale.ENGLISH));
                        if (SubCommands.waitingForResponse.isEmpty()) {
                            SubCommands.isWaitingForResponse = false;
                        }
                        fileManager.writeToEndOfFile("friends.txt", messageData[0]);
                        return HelperColorText.colorText("{green}" + PlayerName + "{Yellow} Has accepted your request and was added your friends list");
                    } else if (messageData[1].equalsIgnoreCase("no")) {
                        SubCommands.waitingForResponse.remove(PlayerName.toLowerCase(Locale.ENGLISH));
                        if (SubCommands.waitingForResponse.isEmpty()) {
                            SubCommands.isWaitingForResponse = false;
                        }
                        return HelperColorText.colorText("{red}" + PlayerName + "{Yellow} has declined the friend request.");
                    }
                }
                return message;
            }
        }
        return message;
    }
}
