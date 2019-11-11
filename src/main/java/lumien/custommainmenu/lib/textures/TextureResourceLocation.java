package lumien.custommainmenu.lib.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class TextureResourceLocation extends ResourceLocation implements ITexture
{
	public TextureResourceLocation(String resourceString)
	{
		super(resourceString);
	}

	@Override
	public void bind()
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(this);
	}
}
