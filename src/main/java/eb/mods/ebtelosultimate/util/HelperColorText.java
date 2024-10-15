package eb.mods.ebtelosultimate.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

public class HelperColorText {

    public static MutableText colorText(String text) {
        /**
         * dark_red
         * red
         * gold
         * yellow
         * dark_green
         * green
         * aqua
         * dark_aqua
         * dark_blue
         * blue
         * light_purple
         * dark_purple
         * white
         * gray
         * dark_gray
         * black
         **/
        MutableText mutableText = Text.empty();
        StringBuilder tempString = new StringBuilder();
        String currentColor = "";

        while (!text.isBlank()) {
            String firstChar = text.substring(0, 1);
            String beforeFirstChar = "";

            if (firstChar.equals("{") && (text.length() > 1 && text.charAt(1) != '\\')) {
                // Append any previous tempString with the current color
                if (!tempString.isEmpty()) {
                    if (currentColor.isEmpty()) {
                        mutableText.append(Text.of(tempString.toString()));
                    } else {
                        mutableText.append(Text.literal(tempString.toString()).fillStyle(Style.EMPTY.withColor(Formatting.byName(currentColor.toUpperCase(Locale.ENGLISH)))));
                    }
                    tempString = new StringBuilder();
                }

                // Extract the color between { and }
                text = text.substring(1);  // Remove "{"
                StringBuilder colorName = new StringBuilder();
                firstChar = text.substring(0, 1);

                while (!firstChar.equals("}") && !beforeFirstChar.equals("\\")) {
                    colorName.append(firstChar);
                    text = text.substring(1);  // Move forward
                    if (text.isBlank()) break;
                    beforeFirstChar = firstChar;
                    firstChar = text.substring(0, 1);
                }

                // Set the current color
                currentColor = colorName.toString();
                if (!text.isBlank()) {
                    text = text.substring(1);  // Remove "}"
                }
            } else {
                // Regular text, append it to tempString
                tempString.append(firstChar);
                text = text.substring(1);  // Move forward
            }
        }

        // Append any remaining text with the current color
        if (!tempString.isEmpty()) {
            if (currentColor.isEmpty()) {
                mutableText.append(Text.of(tempString.toString()));
            } else {
                mutableText.append(Text.literal(tempString.toString()).fillStyle(Style.EMPTY.withColor(Formatting.byName(currentColor.toUpperCase(Locale.ENGLISH)))));
            }
        }

        return mutableText;
    }
}
