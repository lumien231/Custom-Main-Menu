package lumien.custommainmenu.lib.actions;

import com.google.common.util.concurrent.Runnables;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiCustomizeSkin;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiSnooper;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.realms.RealmsBridge;
import net.minecraftforge.fml.client.GuiModList;

public class ActionOpenGUI implements IAction
{
	String guiName;

	public ActionOpenGUI(String guiName)
	{
		this.guiName = guiName;
	}

	@Override
	public void perform(Object source, GuiCustom menu)
	{
		GuiScreen gui = null;

		if (guiName.startsWith("custom."))
		{
			String customName = guiName.substring(7);

			gui = CustomMainMenu.INSTANCE.config.getGUI(customName);
		}
		else
		{
			if (guiName.equalsIgnoreCase("mods"))
			{
				gui = new GuiModList(menu);
			}
			else if (guiName.equalsIgnoreCase("singleplayer"))
			{
				gui = new GuiWorldSelection(menu);
			}
			else if (guiName.equalsIgnoreCase("singleplayer.createworld"))
			{
				gui = new GuiCreateWorld(menu);
			}
			else if (guiName.equalsIgnoreCase("multiplayer"))
			{
				gui = new GuiMultiplayer(menu);
			}
			else if (guiName.equalsIgnoreCase("options"))
			{
				gui = new GuiOptions(menu, menu.mc.gameSettings);
			}
			else if (guiName.equalsIgnoreCase("languages"))
			{
				gui = new GuiLanguage(menu, menu.mc.gameSettings, menu.mc.getLanguageManager());
			}
			else if (guiName.equalsIgnoreCase("options.ressourcepacks"))
			{
				gui = new GuiScreenResourcePacks(menu);
			}
			else if (guiName.equalsIgnoreCase("options.snooper"))
			{
				gui = new GuiSnooper(menu, menu.mc.gameSettings);
			}
			else if (guiName.equalsIgnoreCase("options.sounds"))
			{
				gui = new GuiScreenOptionsSounds(menu, menu.mc.gameSettings);
			}
			else if (guiName.equalsIgnoreCase("options.skinsettings"))
			{
				gui = new GuiCustomizeSkin(menu);
			}
			else if (guiName.equalsIgnoreCase("options.video"))
			{
				gui = new GuiVideoSettings(menu, menu.mc.gameSettings);
			}
			else if (guiName.equalsIgnoreCase("options.controls"))
			{
				gui = new GuiControls(menu, menu.mc.gameSettings);
			}
			else if (guiName.equalsIgnoreCase("options.multiplayer"))
			{
				gui = new ScreenChatOptions(menu, menu.mc.gameSettings);
			}
			else if (guiName.equalsIgnoreCase("mainmenu"))
			{
				gui = new GuiMainMenu();
			}
			else if (guiName.equalsIgnoreCase("realms"))
			{
		        RealmsBridge realmsbridge = new RealmsBridge();
		        realmsbridge.switchToRealms(Minecraft.getMinecraft().currentScreen);
			}
			else if (guiName.equalsIgnoreCase("credits"))
			{
				gui = new GuiWinGame(false, Runnables.doNothing());
			}
		}

		if (gui != null)
		{
			Minecraft.getMinecraft().displayGuiScreen(gui);
		}

	}

}
