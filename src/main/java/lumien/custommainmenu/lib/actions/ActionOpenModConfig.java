package lumien.custommainmenu.lib.actions;

import com.google.common.base.Strings;

import lumien.custommainmenu.gui.GuiCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class ActionOpenModConfig implements IAction
{
	String modid;

	public ActionOpenModConfig(String modid)
	{
		this.modid = modid;
	}

	@Override
	public void perform(Object source, GuiCustom parent)
	{
		for (ModContainer mod : Loader.instance().getModList())
		{
			if (mod.getModId().equals(modid))
			{
				IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(mod);

				if (guiFactory != null)
				{
					GuiScreen newScreen = guiFactory.createConfigGui(parent);
					Minecraft.getMinecraft().displayGuiScreen(newScreen);
				}
			}
		}
	}

}
