package eb.mods.ebtelosultimate.client;

import net.fabricmc.api.ClientModInitializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static eb.mods.ebtelosultimate.util.ModRegistries.registerMod;




public class EbTelosUltimateClient implements ClientModInitializer {

    public static boolean chatFlag = false;
    public static String modid = "ebtelosultimate";

    public static final Log LOGGER = LogFactory.getLog(modid);

    @Override
    public void onInitializeClient() {
        registerMod();
    }
}
