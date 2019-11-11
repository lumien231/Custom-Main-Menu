package lumien.custommainmenu.lib.actions;

import lumien.custommainmenu.gui.GuiCustom;

public class ActionQuit implements IAction
{
	@Override
	public void perform(Object source, GuiCustom menu)
	{
		menu.mc.shutdown();
	}
}
