package lumien.custommainmenu.configuration;

import java.util.HashMap;

import lumien.custommainmenu.gui.GuiCustom;

public class Config
{
	HashMap<String, GuiEntry> guis;

	public Config()
	{
		guis = new HashMap<String, GuiEntry>();
	}

	public void addGui(String name, GuiCustom gc)
	{
		GuiEntry entry = guis.get(name);

		if (entry == null)
		{
			entry = new GuiEntry();
			guis.put(name, entry);
		}
		
		int scale = gc.guiConfig.guiScale;

		if (scale == -1)
		{
			entry.standard = gc;
		}
		else if (scale == 0)
		{
			entry.auto = gc;
		}
		else if (scale == 1)
		{
			entry.small = gc;
		}
		else if (scale == 2)
		{
			entry.normal = gc;
		}
		else if (scale == 3)
		{
			entry.large = gc;
		}
	}

	public GuiCustom getGUI(String name)
	{
		return guis.get(name).getCurrentGUI();
	}

	public void tick()
	{
		guis.values().forEach((ge) -> ge.getCurrentGUI().tick());
	}
}
