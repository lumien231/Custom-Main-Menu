package lumien.custommainmenu.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;

import org.apache.commons.io.IOUtils;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class ConfigurationLoader
{
	Config config;

	public ConfigurationLoader(Config config)
	{
		this.config = config;
	}

	public void load() throws Exception
	{
		JsonParser jsonParser = new JsonParser();

		File configFolder = new File(CustomMainMenu.INSTANCE.configFolder, "CustomMainMenu");
		if (!configFolder.exists())
		{
			configFolder.mkdir();
		}

		File mainmenuConfig = new File(configFolder, "mainmenu.json");
		if (!mainmenuConfig.exists())
		{
			InputStream input = null;

			OutputStream output = null;
			try
			{
				output = new FileOutputStream(mainmenuConfig);
				input = getClass().getResourceAsStream("/assets/custommainmenu/mainmenu_default.json");
				ByteStreams.copy(input, output);
			}
			catch (FileNotFoundException e1)
			{
				e1.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				IOUtils.closeQuietly(output);
				IOUtils.closeQuietly(input);
			}
		}

		File[] jsonFiles = configFolder.listFiles();

		
		// Preload Main Menu so that other menus can rely on it
		
		for (File guiFile : jsonFiles)
		{
			if (guiFile.getName().equals("mainmenu.json"))
			{
				GuiConfig guiConfig = new GuiConfig();
				String name = guiFile.getName().replace(".json", "");

				JsonReader reader = null;
				try
				{
					reader = new JsonReader(new FileReader(guiFile));
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				try
				{
					JsonElement jsonElement = jsonParser.parse(reader);
					JsonObject jsonObject = jsonElement.getAsJsonObject();

					guiConfig.load(name, jsonObject);
				}
				catch (Exception e)
				{
					try
					{
						reader.close();
					}
					catch (IOException io)
					{
						io.printStackTrace();
					}
					throw e;
				}

				try
				{
					reader.close();
				}
				catch (IOException io)
				{
					io.printStackTrace();
				}

				this.config.addGui(guiConfig.name, new GuiCustom(guiConfig));
			}
		}

		for (File guiFile : jsonFiles)
		{
			if (!guiFile.getName().equals("mainmenu.json") && guiFile.getName().endsWith(".json"))
			{
				GuiConfig guiConfig = new GuiConfig();
				String name = guiFile.getName().replace(".json", "");

				JsonReader reader = null;
				try
				{
					reader = new JsonReader(new FileReader(guiFile));
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				try
				{
					JsonElement jsonElement = jsonParser.parse(reader);
					JsonObject jsonObject = jsonElement.getAsJsonObject();

					guiConfig.load(name, jsonObject);
				}
				catch (Exception e)
				{
					try
					{
						reader.close();
					}
					catch (IOException io)
					{
						io.printStackTrace();
					}
					throw e;
				}

				try
				{
					reader.close();
				}
				catch (IOException io)
				{
					io.printStackTrace();
				}

				this.config.addGui(guiConfig.name, new GuiCustom(guiConfig));
			}
		}
	}
}
