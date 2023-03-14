package lumien.custommainmenu.configuration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigurationLoader {
    Config config;

    public ConfigurationLoader(Config config) {
        this.config = config;
    }

    public void load() {
        JsonParser jsonParser = new JsonParser();

        File configFolder = new File(CustomMainMenu.INSTANCE.configFolder, "CustomMainMenu");
        if (!configFolder.exists())
            if (!configFolder.mkdirs())
                throw new RuntimeException(new IOException("Can't create folder"));

        File mainConfig = new File(configFolder, "mainmenu.json");
        if (!mainConfig.exists()) {
            try (
                    InputStream input = getClass().getResourceAsStream("/assets/custommainmenu/mainmenu_default.json");
                    OutputStream output = Files.newOutputStream(mainConfig.toPath())
            ) {
                if (input == null)
                    throw new RuntimeException(new IOException("Default config not exist (/assets/custommainmenu/mainmenu_default.json)"));
                IOUtils.copy(input, output);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File[] jsonFiles = configFolder.listFiles();

        if (jsonFiles == null)
            return;
        for (File guiFile : jsonFiles) {
            if (!guiFile.getName().endsWith(".json"))
                continue;
            GuiConfig guiConfig = new GuiConfig();
            String name = guiFile.getName().replace(".json", "");

            try (
                    InputStream is = Files.newInputStream(guiFile.toPath());
                    InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                    JsonReader reader = new JsonReader(isr)
            ) {
                JsonElement jsonElement = jsonParser.parse(reader);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                guiConfig.load(name, jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.config.addGui(guiConfig.name, new GuiCustom(guiConfig));
        }
    }
}
