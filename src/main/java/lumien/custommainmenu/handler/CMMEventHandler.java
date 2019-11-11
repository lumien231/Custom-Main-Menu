package lumien.custommainmenu.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.Level;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;
import lumien.custommainmenu.gui.GuiCustomButton;
import lumien.custommainmenu.gui.GuiCustomWrappedButton;
import lumien.custommainmenu.gui.GuiFakeMain;
import lumien.custommainmenu.lib.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CMMEventHandler
{
	public long displayMs = -1;

	Field guiField;

	public CMMEventHandler()
	{
		try
		{
			guiField = GuiScreenEvent.class.getDeclaredField("gui");
			guiField.setAccessible(true);
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void openGui(GuiOpenEvent event)
	{
		if (event.getGui() instanceof GuiMainMenu)
		{
			GuiCustom customMainMenu = CustomMainMenu.INSTANCE.config.getGUI("mainmenu");
			if (customMainMenu != null)
			{
				event.setGui(customMainMenu);
			}
		}
		else if (event.getGui() instanceof GuiCustom)
		{
			GuiCustom custom = (GuiCustom) event.getGui();

			GuiCustom target = CustomMainMenu.INSTANCE.config.getGUI(custom.guiConfig.name);
			if (target != custom)
			{
				event.setGui(target);
			}
		}
	}

	GuiCustom actualGui;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void initGuiPostEarly(InitGuiEvent.Post event)
	{
		if (event.getGui() instanceof GuiCustom)
		{
			GuiCustom custom = (GuiCustom) event.getGui();
			if (custom.guiConfig.name.equals("mainmenu"))
			{
				event.setButtonList(new ArrayList<GuiButton>());
				actualGui = custom;
				try
				{
					guiField.set(event, new GuiFakeMain());
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void initGuiPost(InitGuiEvent.Post event)
	{
		if (event.getGui() instanceof GuiFakeMain)
		{
			GuiFakeMain fake = (GuiFakeMain) event.getGui();

			HashMap<Integer, GuiButton> removedButtons = new HashMap<Integer, GuiButton>();

			Iterator<GuiButton> iterator = event.getButtonList().iterator();
			while (iterator.hasNext())
			{
				Object o = iterator.next();
				GuiButton b = (GuiButton) o;
				if (!(b instanceof GuiCustomButton))
				{
					iterator.remove();
					removedButtons.put(b.id, b);
					if (b.id == Reference.OPEN_EYE_BUTTON && Loader.isModLoaded(Reference.OPEN_EYE_MODID))
					{
						// OpenEye
						CustomMainMenu.INSTANCE.logger.log(Level.DEBUG, "Found OpenEye button, use a wrapped button to config this. (" + b.id + ")");
					}
					else if (b.id == Reference.VERSION_CHECKER_BUTTON && Loader.isModLoaded(Reference.VERSION_CHECKER_MODID))
					{
						// VersionChecker
						CustomMainMenu.INSTANCE.logger.log(Level.DEBUG, "Found VersionChecker button, use a wrapped button to config this. (" + b.id + ")");
					}
					else
					{
						// Others
						CustomMainMenu.INSTANCE.logger.log(Level.DEBUG, "Found unsupported button, use a wrapped button to config this. (" + b.id + ")");
					}
				}
			}

			for (GuiButton o : actualGui.getButtonList())
			{
				if (o instanceof GuiCustomWrappedButton)
				{
					GuiCustomWrappedButton b = (GuiCustomWrappedButton) o;
					CustomMainMenu.INSTANCE.logger.log(Level.DEBUG, "Initiating Wrapped Button " + b.wrappedButtonID + " with " + removedButtons.get(b.wrappedButtonID));
					b.init(removedButtons.get(b.wrappedButtonID));
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void renderScreenPost(DrawScreenEvent.Post event)
	{
		if (displayMs != -1)
		{
			if (System.currentTimeMillis() - displayMs < 5000)
			{
				Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Error loading config file, see console for more information", 0, 80, 16711680);
			}
			else
			{
				displayMs = -1;
			}
		}
	}

	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END)
		{
			if (CustomMainMenu.INSTANCE.config != null)
				CustomMainMenu.INSTANCE.config.tick();
		}
	}
}
