package de.jonahd345.extendedeconomy.util;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    /**
     * Saves the provided FileConfiguration to the specified file.
     *
     * @param fileConfiguration the FileConfiguration to save
     * @param file the file to save the YamlConfiguration to
     */
    public static void saveFile(FileConfiguration fileConfiguration, File file) {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
