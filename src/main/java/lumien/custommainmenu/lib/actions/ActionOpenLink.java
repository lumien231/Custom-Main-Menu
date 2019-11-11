package lumien.custommainmenu.lib.actions;

import lumien.custommainmenu.gui.GuiCustom;
import lumien.custommainmenu.gui.GuiCustomConfirmOpenLink;
import lumien.custommainmenu.lib.StringReplacer;
import net.minecraft.client.Minecraft;

public class ActionOpenLink implements IAction
{
	String link;
	
	public ActionOpenLink(String link)
	{
		this.link = link;
	}

	@Override
	public void perform(Object source,GuiCustom menu)
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiCustomConfirmOpenLink(menu, getLink(), -1, false));
		menu.beingChecked = source;
	}

	public String getLink()
	{
		return StringReplacer.replacePlaceholders(link);
	}
}
