package lumien.custommainmenu.gui;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.configuration.elements.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiCustomWrappedButton extends GuiCustomButton
{
	GuiButton wrappedButton;
	public int wrappedButtonID;

	public GuiCustomWrappedButton(int buttonId, int wrappedButtonID, Button b)
	{
		super(buttonId, b);

		this.wrappedButtonID = wrappedButtonID;
	}

	@Override
	public void func_191745_a(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		if (wrappedButton != null)
		{
			this.visible = this.enabled = wrappedButton.visible && wrappedButton.enabled;
		}
		else
		{
			this.visible = this.enabled = false;
		}
		super.func_191745_a(mc, mouseX, mouseY, partialTicks);
	}

	public void init(GuiButton wrappedButton)
	{
		this.wrappedButton = wrappedButton;
		if (wrappedButton == null)
		{
			this.visible = this.enabled = false;
		}
	}

	public GuiButton getWrappedButton()
	{
		return wrappedButton;
	}
}
