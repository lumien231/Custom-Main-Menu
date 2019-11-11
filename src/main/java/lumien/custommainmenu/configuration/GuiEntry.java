package lumien.custommainmenu.configuration;

import net.minecraft.client.Minecraft;
import lumien.custommainmenu.gui.GuiCustom;

public class GuiEntry
{
	GuiCustom standard;

	GuiCustom auto;
	GuiCustom small;
	GuiCustom normal;
	GuiCustom large;

	public GuiCustom getCurrentGUI()
	{
		int guiScale = Minecraft.getMinecraft().gameSettings.guiScale;
		
		if (guiScale == 0 && auto != null)
		{
			return auto;
		}
		
		if (guiScale == 1 && small != null)
		{
			return small;
		}
		
		if (guiScale == 2 && normal != null)
		{
			return normal;
		}
		
		if (guiScale == 3 && large != null)
		{
			return large;
		}
		
		return standard;
	}
}
