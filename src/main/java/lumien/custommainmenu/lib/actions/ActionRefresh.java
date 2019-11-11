package lumien.custommainmenu.lib.actions;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;

import net.minecraft.client.gui.GuiMainMenu;

import org.lwjgl.input.Keyboard;

public class ActionRefresh implements IAction
{

	@Override
	public void perform(Object source, GuiCustom menu)
	{
		CustomMainMenu.INSTANCE.reload();

		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			menu.mc.refreshResources();
		}
		menu.mc.displayGuiScreen(new GuiMainMenu());
	}

}
