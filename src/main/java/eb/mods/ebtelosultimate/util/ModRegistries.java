package eb.mods.ebtelosultimate.util;

import eb.mods.ebtelosultimate.command.ClassView.ClassView;
import eb.mods.ebtelosultimate.command.Friends.Friends;
import eb.mods.ebtelosultimate.events.chatMessageProcess;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

public class ModRegistries {

    public static void registerMod(){
        registerCommands();
    }

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(Friends::register);
        ClientCommandRegistrationCallback.EVENT.register(ClassView::register);

        ClientReceiveMessageEvents.MODIFY_GAME.register(new chatMessageProcess());
    }

}
