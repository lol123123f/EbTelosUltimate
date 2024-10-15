package eb.mods.ebtelosultimate.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatHelper{
    /**
     * Get the player and the message from a chat message
     * @param msg The chat message
     *            Example: "[Player] ┅ Hello!"
     *            Example: "[Player] ┅ Hello! How are you?"
     *            Example: "[Player] ┅ Hello! How are you? I am fine."
     * @return A list with the player name and the message
     *        Example: ["Player1", "Hello World!"]
     * **/
    public static String[] getDMPlayer(String msg , boolean To){
        if (!msg.contains("From ") && !To) {
            return null;
        }
        if (!msg.contains("To ") && To) {
            return null;
        }
        if (!msg.contains(" ┅ ")) {
            return null;
        }
        String mainPart = msg.replace("From ", "");
        String[] splittedMainPart = mainPart.split(" ┅ ");

        //                     Player Name     ,     message
        return new String[]{splittedMainPart[0], splittedMainPart[1]};
    }

    public static String[] getDMPlayer(String msg ){
        return getDMPlayer(msg, false);
    }
}
