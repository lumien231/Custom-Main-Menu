package lumien.custommainmenu.configuration.elements;

import net.minecraft.util.ResourceLocation;
import lumien.custommainmenu.configuration.Alignment;
import lumien.custommainmenu.configuration.GuiConfig;
import lumien.custommainmenu.lib.texts.IText;
import lumien.custommainmenu.lib.texts.TextResourceLocation;

public class SplashText extends Element
{
	public IText texts;
	public int posX;
	public int posY;
	public int color;
	public Alignment alignment;
	public boolean synced;

	public SplashText(GuiConfig parent, int posX, int posY, int color, Alignment alignment)
	{
		super(parent);
		this.posX = posX;
		this.posY = posY;
		this.alignment = alignment;
		this.color = color;
		this.texts = new TextResourceLocation("texts/splashes.txt");
		this.synced = false;

		if (this.alignment == null)
		{
			this.alignment = parent.getAlignment("button");
		}
	}

	public SplashText(GuiConfig parent, int posX, int posY, String alignment)
	{
		this(parent, posX, posY, -256, parent.getAlignment(alignment));
	}

	public SplashText(GuiConfig parent, int posX, int posY, int color, String alignment)
	{
		this(parent, posX, posY, color, parent.getAlignment(alignment));
	}
	
	public void setSplashTexts(IText texts)
	{
		this.texts = texts;
	}
}
