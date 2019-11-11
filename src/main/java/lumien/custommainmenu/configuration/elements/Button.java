package lumien.custommainmenu.configuration.elements;

import lumien.custommainmenu.configuration.Alignment;
import lumien.custommainmenu.configuration.GuiConfig;
import lumien.custommainmenu.lib.actions.IAction;
import lumien.custommainmenu.lib.texts.IText;
import lumien.custommainmenu.lib.textures.ITexture;

public class Button extends Element
{
	public String name;
	public IText text;
	public IText hoverText;
	public IAction action;
	public IText tooltip;
	
	public Alignment alignment;
	public int posX;
	public int posY;
	public int width;
	public int height;
	public int imageWidth;
	public int imageHeight;
	
	public ITexture texture;
	
	public int normalTextColor;
	public int hoverTextColor;
	public boolean shadow;
	
	public String pressSound;
	public String hoverSound;
	
	public int textOffsetX;
	public int textOffsetY;
	
	public int wrappedButtonID;

	public Button(GuiConfig parent,IText text, int posX, int posY, int width, int height, Alignment alignment)
	{
		super(parent);
		this.text = text;

		this.posX = posX;
		this.posY = posY;
		this.width = this.imageWidth = width;
		this.height = this.imageHeight = height;
		this.alignment = alignment;
		this.texture = null;
		this.normalTextColor = 14737632;
		this.hoverTextColor = 16777120;
		this.shadow = true;
		this.wrappedButtonID = -1;
		this.action = null;
		this.tooltip = null;
		this.hoverText = text;
		
		this.textOffsetX = 0;
		this.textOffsetY = 0;

		if (this.alignment == null)
		{
			this.alignment = parent.getAlignment("button");
		}
	}

	public Button(GuiConfig parent,IText text, int posX, int posY, int width, int height)
	{
		this(parent,text, posX, posY, width, height, parent.getAlignment("button"));
	}
	
	public void setShadow(boolean shadow)
	{
		this.shadow = shadow;
	}
	
	public void setPressSound(String pressSound)
	{
		this.pressSound = pressSound;
	}
	
	public void setHoverSound(String hoverSound)
	{
		this.hoverSound = hoverSound;
	}

	public void setStringAlignment(String stringAlignment)
	{
		this.alignment = parent.getAlignment(stringAlignment);
	}

	public Button setTexture(ITexture texture)
	{
		this.texture = texture;
		return this;
	}
	
	public void setWrappedButton(int wrappedButtonID)
	{
		this.wrappedButtonID = wrappedButtonID;
	}
}
