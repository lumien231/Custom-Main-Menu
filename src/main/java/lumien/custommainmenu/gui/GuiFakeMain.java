package lumien.custommainmenu.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;

public class GuiFakeMain extends GuiMainMenu
{
	public GuiFakeMain()
	{
		this.mc = Minecraft.getMinecraft();
	}
	
	public void initGui()
    {
		
    }
	
	public List<GuiButton> getButtonList()
	{
		return this.buttonList;
	}
}
