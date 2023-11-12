package io.greitan.mineserv.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class Language {
    private static final Map<String, YamlConfiguration> languageConfigs = new HashMap<>();
    private static String defaultLanguage = "en";

    public static void init(Plugin plugin)
    {
        File languageFolder = new File(plugin.getDataFolder(), "locale");

        if (!languageFolder.exists())
        {
            languageFolder.mkdirs();
            copyResource(plugin, "locale/en.yml", new File(languageFolder, "en.yml"));
            copyResource(plugin, "locale/ru.yml", new File(languageFolder, "ru.yml"));
        }

        loadLanguages(languageFolder.getAbsolutePath());
    }

    private static void loadLanguages(String pluginFolder)
    {
        File languageFolder = new File(pluginFolder);

        if (languageFolder.exists() && languageFolder.isDirectory())
        {

            for (File file : languageFolder.listFiles())
            {

                if (file.getName().endsWith(".yml"))
                {
                    String language = file.getName().replace(".yml", "");
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    languageConfigs.put(language, config);
                }
            }
        }
    }

    private static void copyResource(Plugin plugin, String resourceName, File destination)
    {
        try (InputStream inputStream = plugin.getResource(resourceName))
        {
            if (inputStream != null)
            {
                Files.copy(inputStream, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String getMessage(String language, String key)
    {
        if (languageConfigs.containsKey(language))
        {
            YamlConfiguration config = languageConfigs.get(language);
            if (config.contains("messages." + key))
            {
                return config.getString("messages." + key);
            }
        }
        return languageConfigs.get(defaultLanguage).getString("messages." + key);
    }
}
