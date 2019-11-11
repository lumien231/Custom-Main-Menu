package lumien.custommainmenu.configuration.elements;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;

import lumien.custommainmenu.configuration.Config;
import lumien.custommainmenu.configuration.GuiConfig;
import lumien.custommainmenu.lib.textures.ITexture;
import lumien.custommainmenu.lib.textures.TextureResourceLocation;

public class Slideshow extends Element
{
	public ITexture[] ressources;
	public int displayDuration;

	private int counter;
	public int fadeDuration;
	private boolean fading = false;

	public Slideshow(GuiConfig parent, String[] images)
	{
		super(parent);
		ressources = new ITexture[images.length];

		this.displayDuration = 60;
		this.counter = 0;
		this.fadeDuration = 20;

		for (int i = 0; i < images.length; i++)
		{
			ressources[i] = GuiConfig.getWantedTexture(images[i]);
		}
	}

	public void shuffle()
	{
		List<ITexture> list = Arrays.asList(ressources);
		Collections.shuffle(list);
		ressources = (ITexture[]) list.toArray();
	}

	public void update()
	{
		counter++;

		fading = (counter % (displayDuration + fadeDuration)) > displayDuration;
	}

	public boolean fading()
	{
		return fading;
	}

	public float getAlphaFade(float partial)
	{
		float counterProgress = ((counter + partial) % (displayDuration + fadeDuration)) - displayDuration;

		float durationTeiler = 1F / fadeDuration;
		float alpha = durationTeiler * counterProgress;
		return alpha;
	}

	public ITexture getCurrentResource1()
	{
		int index = counter / ((displayDuration + fadeDuration)) % ressources.length;
		return ressources[index];
	}

	public ITexture getCurrentResource2()
	{
		if (fading)
		{
			int index = (counter + fadeDuration) / ((displayDuration + fadeDuration)) % ressources.length;
			return ressources[index];
		}
		return null;
	}
}
