package lumien.custommainmenu.lib.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.handler.LoadTextureURL;
import lumien.custommainmenu.lib.StringReplacer;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;

public class TextureURL implements ITexture
{
	URL url;
	int textureID;
	private BufferedImage bi;

	public TextureURL(String url)
	{
		this.textureID = -1;
		try
		{
			this.url = new URL(StringReplacer.replacePlaceholders(url));
		}
		catch (MalformedURLException e)
		{
			CustomMainMenu.INSTANCE.logger.log(Level.ERROR, "Invalid URL: " + url);
			e.printStackTrace();
		}

		new LoadTextureURL(this).start();
	}

	@Override
	public void bind()
	{
		if (this.textureID != -1)
		{
			GlStateManager.bindTexture(this.textureID);
		}
		else
		{
			if (bi != null)
			{
				setTextureID(TextureUtil.uploadTextureImageAllocate(GL11.glGenTextures(), bi, false, false));
				bind();
				return;
			}
			CustomMainMenu.bindTransparent();
		}
	}

	public void finishLoading(BufferedImage bi)
	{
		this.bi = bi;
	}

	public URL getURL()
	{
		return url;
	}

	public void setTextureID(int textureID)
	{
		this.textureID = textureID;
	}

}
