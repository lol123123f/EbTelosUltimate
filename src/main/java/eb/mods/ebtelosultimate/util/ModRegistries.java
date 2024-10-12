package eb.mods.ebtelosultimate.util;

import eb.mods.ebtelosultimate.command.ClassView.ClassView;
import eb.mods.ebtelosultimate.command.Friends.Friends;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class ModRegistries {

    public static void registerMod(){
        registerCommands();
    }

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(Friends::register);
        ClientCommandRegistrationCallback.EVENT.register(ClassView::register);
    }

}
