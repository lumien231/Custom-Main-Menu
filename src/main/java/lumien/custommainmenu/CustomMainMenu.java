package lumien.custommainmenu;

import java.io.File;

import lumien.custommainmenu.configuration.Config;
import lumien.custommainmenu.configuration.ConfigurationLoader;
import lumien.custommainmenu.handler.CMMEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@Mod(modid = CustomMainMenu.MOD_ID, name = CustomMainMenu.MOD_NAME, version = CustomMainMenu.MOD_VERSION, clientSideOnly = true)
public class CustomMainMenu
{
	public static final String MOD_ID = "custommainmenu";
	public static final String MOD_NAME = "Custom Main Menu";
	public static final String MOD_VERSION = "@VERSION@";

	@Instance(value = MOD_ID)
	public static CustomMainMenu INSTANCE;

	public static CMMEventHandler EVENT_HANDLER;

	private ConfigurationLoader configLoader;
	public Config config;

	public Logger logger;
	public File configFolder;

	static ResourceLocation transparentTexture;

	public static void bindTransparent()
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(transparentTexture);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		configFolder = event.getModConfigurationDirectory();
		config = new Config();
		// Load Transparent
		transparentTexture = new ResourceLocation("custommainmenu:textures/gui/transparent.png");

		EVENT_HANDLER = new CMMEventHandler();
		MinecraftForge.EVENT_BUS.register(EVENT_HANDLER);
		FMLCommonHandler.instance().bus().register(EVENT_HANDLER);

		logger = event.getModLog();

		configLoader = new ConfigurationLoader(config);

		try
		{
			configLoader.load();
		}
		catch (Exception e)
		{
			logger.log(Level.ERROR, "Error while loading config file. Will have to crash here :(.");
			throw new RuntimeException(e);
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{

	}

	public void reload()
	{
		Config backup = config;
		config = new Config();
		configLoader = new ConfigurationLoader(config);
		try
		{
			configLoader.load();
			EVENT_HANDLER.displayMs = -1;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			EVENT_HANDLER.displayMs = System.currentTimeMillis();
			logger.log(Level.ERROR, "Error while loading new config file, trying to keep the old one loaded.");
			config = backup;
		}
	}
}
