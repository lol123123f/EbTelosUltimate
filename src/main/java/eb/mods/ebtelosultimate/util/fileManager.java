package eb.mods.ebtelosultimate.util;

import eb.mods.ebtelosultimate.client.EbTelosUltimateClient;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class fileManager {
    // should create a file inside folder with the name of the modid inside the Minecraft directory
    public static boolean createFile(String fileName) {
        File gameDir = MinecraftClient.getInstance().runDirectory;
        File modDir = new File(gameDir, EbTelosUltimateClient.modid);
        if (!modDir.exists()) {
            modDir.mkdirs();  // Create the mod folder if it doesn't exist
        }
        File file = new File(modDir, fileName); // Create the file
        try {
            file.createNewFile();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isFileExist(String fileName) {
        File gameDir = MinecraftClient.getInstance().runDirectory;
        File modDir = new File(gameDir, EbTelosUltimateClient.modid);
        if (!modDir.exists()) {
            return false;
        }
        File file = new File(modDir, fileName);
        return file.exists();
    }

    public static String getFileDataAsString(String fileName) {
        File gameDir = MinecraftClient.getInstance().runDirectory;
        File modDir = new File(gameDir, EbTelosUltimateClient.modid);
        if (!modDir.exists()) {
            return null;
        }
        File file = new File(modDir, fileName);
        if (!file.exists()) {
            return null;
        }
        try {
            Scanner r = new Scanner(file);
            if (!r.hasNextLine()) {
                r.close();
                return "";
            }
            String data = r.nextLine();
            while (r.hasNextLine()) {
                data += "\n" + r.nextLine();
            }
            r.close();
            return data;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static boolean writeToEndOfFile(String fileName, String data) {
        File gameDir = MinecraftClient.getInstance().runDirectory;
        File modDir = new File(gameDir, EbTelosUltimateClient.modid);
        if (!modDir.exists()) {
            return false;
        }

        File file = new File(modDir, fileName);
        if (!file.exists()) {
            return false;
        }

        try (FileWriter writer = new FileWriter(file, true)) {
            // Appends the new data to the file
            writer.write("\n" + data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public static boolean reWriteFile(String fileName, String data) {
        File gameDir = MinecraftClient.getInstance().runDirectory;
        File modDir = new File(gameDir, EbTelosUltimateClient.modid);
        if (!modDir.exists()) {
            return false;
        }

        File file = new File(modDir, fileName);
        if (!file.exists()) {
            return false;
        }

        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
