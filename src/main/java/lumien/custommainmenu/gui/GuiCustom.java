package lumien.custommainmenu.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_HEIGHT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WIDTH;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGetTexLevelParameteri;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.configuration.Alignment;
import lumien.custommainmenu.configuration.Config;
import lumien.custommainmenu.configuration.GuiConfig;
import lumien.custommainmenu.configuration.elements.Background;
import lumien.custommainmenu.configuration.elements.Button;
import lumien.custommainmenu.configuration.elements.Image;
import lumien.custommainmenu.configuration.elements.Label;
import lumien.custommainmenu.configuration.elements.Panorama;
import lumien.custommainmenu.configuration.elements.SplashText;
import lumien.custommainmenu.lib.MODE;
import lumien.custommainmenu.lib.actions.ActionOpenLink;
import lumien.custommainmenu.lib.textures.ITexture;
import lumien.custommainmenu.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustom extends GuiScreen implements GuiYesNoCallback
{
	public static Config config;

	int buttonCounter;
	float panoramaTimer;

	ArrayList<GuiCustomLabel> textLabels;

	private ITexture[] titlePanoramaPaths;

	public Object beingChecked;

	public GuiConfig guiConfig;

	Random rand;

	// Main Menu Features
	protected DynamicTexture viewportTexture;
	protected ResourceLocation field_110351_G;
	protected String splashText;
	protected int field_92024_r;
	protected int field_92023_s;
	protected int field_92022_t;
	protected int field_92021_u;
	protected int field_92020_v;
	protected int field_92019_w;

	private boolean loadedSplashText;

	public GuiCustom(GuiConfig guiConfig)
	{
		super();

		this.guiConfig = guiConfig;
		this.rand = new Random();
		this.loadedSplashText = false;
	}

	public List<GuiButton> getButtonList()
	{
		return this.buttonList;
	}

	private void loadSplashTexts()
	{
		if (guiConfig.splashText != null)
		{
			String texts = guiConfig.splashText.texts.get();

			String[] seperateLines = texts.split("\n");

			this.splashText = (String) seperateLines[(rand.nextInt(seperateLines.length))];
		}
	}

	@Override
	public void initGui()
	{
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.shadeModel(7425);
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();

		if (!loadedSplashText && guiConfig.splashText != null)
		{
			if (guiConfig.splashText.synced)
			{
				this.splashText = CustomMainMenu.INSTANCE.config.getGUI("mainmenu").splashText;
			}
			else
			{
				loadSplashTexts();
			}

			loadedSplashText = true;
		}

		textLabels = new ArrayList<GuiCustomLabel>();
		buttonCounter = 0;
		this.viewportTexture = new DynamicTexture(256, 256);
		this.field_110351_G = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		if (calendar.get(2) + 1 == 11 && calendar.get(5) == 9)
		{
			this.splashText = "Happy birthday, ez!";
		}
		else if (calendar.get(2) + 1 == 6 && calendar.get(5) == 1)
		{
			this.splashText = "Happy birthday, Notch!";
		}
		else if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24)
		{
			this.splashText = "Merry X-mas!";
		}
		else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1)
		{
			this.splashText = "Happy new year!";
		}
		else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31)
		{
			this.splashText = "OOoooOOOoooo! Spooky!";
		}

		int idCounter = 6000;

		// Add Custom Buttons
		for (Button b : guiConfig.customButtons)
		{
			if (b.wrappedButtonID != -1)
			{
				this.buttonList.add(alignButton(b, new GuiCustomWrappedButton(b.wrappedButtonID, b.wrappedButtonID, b)));
			}
			else
			{
				this.buttonList.add(alignButton(b, new GuiCustomButton(idCounter, b)));
				idCounter++;
			}
		}

		// Add Labels
		for (Label t : guiConfig.customLabels)
		{
			textLabels.add(new GuiCustomLabel(this, t, modX(t.posX, t.alignment), modY(t.posY, t.alignment)));
		}
	}

	private GuiCustomButton alignButton(Button configButton, GuiCustomButton guiButton)
	{
		if (configButton != null)
		{
			guiButton.x = modX(configButton.posX, configButton.alignment);
			guiButton.y = modY(configButton.posY, configButton.alignment);
		}

		return guiButton;
	}

	public void confirmClicked(boolean result, int id)
	{
		if (result)
		{
			String link = null;

			if (beingChecked instanceof Button)
			{
				Button button = (Button) beingChecked;
				if (button.action != null && button.action instanceof ActionOpenLink)
				{
					link = ((ActionOpenLink) button.action).getLink();
				}
			}
			else if (beingChecked instanceof Label)
			{
				Label text = (Label) beingChecked;
				if (text.action != null && text.action instanceof ActionOpenLink)
				{
					link = ((ActionOpenLink) text.action).getLink();
				}
			}

			if (link != null)
			{
				try
				{
					Class oclass = Class.forName("java.awt.Desktop");
					Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
					oclass.getMethod("browse", new Class[] { URI.class }).invoke(object, new Object[] { new URI(link) });
				}
				catch (Throwable throwable)
				{
					CustomMainMenu.INSTANCE.logger.error("Couldn\'t open link", throwable);
				}
			}
		}

		this.mc.displayGuiScreen(this);
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (mouseButton == 0)
		{
			for (GuiCustomLabel label : textLabels)
			{
				label.mouseClicked(mouseX, mouseY, mouseButton);
			}
		}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		Panorama panorama = guiConfig.panorama;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		if (panorama != null)
		{
			if (guiConfig.panorama != null)
			{
				if (guiConfig.panorama.synced)
				{
					GuiCustom mainMenu = CustomMainMenu.INSTANCE.config.getGUI("mainmenu");
					this.panoramaTimer = mainMenu.panoramaTimer;

					if (mainMenu.guiConfig.panorama.animate)
					{
						this.panoramaTimer += mainMenu.guiConfig.panorama.animationSpeed * partialTicks;
					}
					else
					{
						this.panoramaTimer = mainMenu.guiConfig.panorama.position;
					}

					mainMenu.panoramaTimer = this.panoramaTimer;
				}
				else
				{
					if (guiConfig.panorama.animate)
					{
						this.panoramaTimer += guiConfig.panorama.animationSpeed * partialTicks;
					}
					else
					{
						this.panoramaTimer = guiConfig.panorama.position;
					}
				}
			}
			
			
			titlePanoramaPaths = panorama.locations;
			GlStateManager.disableAlpha();
			this.renderSkybox(mouseX, mouseY, partialTicks);
			GlStateManager.enableAlpha();

			if (panorama.gradient)
			{
				this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
				this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
			}
		}
		else
		{
			glBegin(GL_QUADS);

			glColor3f(0, 0, 0);
			glVertex3f(0, 0, 0);

			glColor3f(0, 0, 0);
			glVertex3f(0, this.height, 0);

			glColor3f(0, 0, 0);
			glVertex3f(this.width, this.height, 0);

			glColor3f(0, 0, 0);
			glVertex3f(this.width, 0, 0);

			glEnd();
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		Background background = guiConfig.background;
		GlStateManager.enableTexture2D();

		GlStateManager.disableBlend();

		if (background != null)
		{
			if (background == Background.OPTIONS_BACKGROUND)
			{
				this.drawDefaultBackground();
			}
			else if (!background.ichBinEineSlideshow)
			{
				background.image.bind();
				drawBackground(background.mode);
			}
			else
			{
				background.slideShow.getCurrentResource1().bind();
				drawBackground(background.mode);

				if (background.slideShow.fading())
				{
					GlStateManager.enableBlend();
					background.slideShow.getCurrentResource2().bind();

					GlStateManager.color(1.0F, 1.0F, 1.0F, background.slideShow.getAlphaFade(partialTicks));
					drawBackground(background.mode);
					GlStateManager.disableBlend();

					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				}
			}
		}

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// Images
		for (Image i : guiConfig.customImages)
		{
			int posX = modX(i.posX, i.alignment);
			int posY = modY(i.posY, i.alignment);

			if (i.ichBinEineSlideshow)
			{
				i.slideShow.getCurrentResource1().bind();
				RenderUtil.drawCompleteImage(posX, posY, i.width, i.height);

				if (i.slideShow.fading())
				{
					GlStateManager.enableBlend();
					i.slideShow.getCurrentResource2().bind();

					GlStateManager.color(1.0F, 1.0F, 1.0F, i.slideShow.getAlphaFade(partialTicks));
					RenderUtil.drawCompleteImage(posX, posY, i.width, i.height);
					GlStateManager.disableBlend();

					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				}
			}
			else
			{
				if (i.hoverImage != null && mouseX >= posX && mouseX <= posX + i.width && mouseY >= posY && mouseY <= posY + i.height)
				{
					i.hoverImage.bind();
				}
				else
				{
					i.image.bind();
				}

				RenderUtil.drawCompleteImage(posX, posY, i.width, i.height);
			}
		}
		GlStateManager.disableBlend();

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		SplashText splashText = guiConfig.splashText;
		if (splashText != null)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(modX(splashText.posX, splashText.alignment), modY(splashText.posY, splashText.alignment), 0.0F);
			GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
			float f1 = 1.8F - MathHelper.abs(MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L) / 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
			f1 = f1 * 100.0F / (float) (this.fontRenderer.getStringWidth(this.splashText) + 32);
			GlStateManager.scale(f1, f1, f1);
			this.drawCenteredString(this.fontRenderer, this.splashText, 0, -8, splashText.color);
			GlStateManager.popMatrix();
		}

		// Text
		// net.minecraftforge.client.ForgeHooksClient.renderMainMenu(this,
		// this.fontRendererObj, this.width, this.height); TODO

		for (GuiCustomLabel label : textLabels)
		{
			label.drawLabel(mc, mouseX, mouseY);
		}

		int w;

		for (w = 0; w < this.buttonList.size(); ++w)
		{
			((GuiButton) this.buttonList.get(w)).drawButton(this.mc, mouseX, mouseY, partialTicks);
		}

		for (Object o : this.buttonList)
		{
			if (o instanceof GuiCustomButton)
			{
				((GuiCustomButton) o).drawTooltip(this.mc, mouseX, mouseY);
			}
		}

		for (w = 0; w < this.labelList.size(); ++w)
		{
			((GuiLabel) this.labelList.get(w)).drawLabel(this.mc, mouseX, mouseY);
		}
	}

	private void drawImageWithMode(MODE mode, int x, int y, int width, int height)
	{
		int imageWidth = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
		int imageHeight = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);

		int drawWidth = 0;
		int drawHeight = 0;

		float factorWidth = width / (float) imageWidth;
		float factorHeight = height / (float) imageHeight;

		switch (mode)
		{
			case FILL:
				if (factorWidth > factorHeight)
				{
					drawWidth = (int) (imageWidth * factorWidth);
					drawHeight = (int) (imageHeight * factorWidth);
				}
				else
				{
					drawWidth = (int) (imageWidth * factorHeight);
					drawHeight = (int) (imageHeight * factorHeight);
				}

				RenderUtil.drawPartialImage(x, y, 0, 0, drawWidth, drawHeight, imageWidth, imageHeight);
				break;
			case STRETCH:
				RenderUtil.drawCompleteImage(x, y, width, height);
				break;
			case CENTER:
				RenderUtil.drawCompleteImage(x + (int) (width / 2F - imageWidth / 2F), y + (int) (height / 2F - imageHeight / 2F), imageWidth, imageHeight);
				break;
			case TILE:
				int countX = (int) Math.ceil(width / (float) imageWidth);
				int countY = (int) Math.ceil(height / (float) imageHeight);

				for (int cX = 0; cX < countX; cX++)
				{
					for (int cY = 0; cY < countY; cY++)
					{
						RenderUtil.drawCompleteImage(x + cX * imageWidth, y + cY * imageHeight, imageWidth, imageHeight);
					}
				}
				break;
		}
	}

	private void drawBackground(MODE mode)
	{
		drawImageWithMode(mode, 0, 0, width, height);
	}

	protected void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_)
	{
		this.mc.getFramebuffer().unbindFramebuffer();
		GlStateManager.viewport(0, 0, 256, 256);
		this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
		this.rotateAndBlurSkybox(p_73971_3_);
		this.rotateAndBlurSkybox(p_73971_3_);
		this.rotateAndBlurSkybox(p_73971_3_);
		this.rotateAndBlurSkybox(p_73971_3_);
		this.rotateAndBlurSkybox(p_73971_3_);
		this.rotateAndBlurSkybox(p_73971_3_);
		this.rotateAndBlurSkybox(p_73971_3_);
		this.mc.getFramebuffer().bindFramebuffer(true);
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		float f = this.width > this.height ? 120.0F / (float) this.width : 120.0F / (float) this.height;
		float f1 = (float) this.height * f / 256.0F;
		float f2 = (float) this.width * f / 256.0F;
		int i = this.width;
		int j = this.height;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		vertexBuffer.pos(0.0D, (double) j, (double) this.zLevel).tex((double) (0.5F - f1), (double) (0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuffer.pos((double) i, (double) j, (double) this.zLevel).tex((double) (0.5F - f1), (double) (0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuffer.pos((double) i, 0.0D, (double) this.zLevel).tex((double) (0.5F + f1), (double) (0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		vertexBuffer.pos(0.0D, 0.0D, (double) this.zLevel).tex((double) (0.5F + f1), (double) (0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		tessellator.draw();
	}

	private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		int i = 8;

		for (int j = 0; j < 64; ++j)
		{
			GlStateManager.pushMatrix();
			float f = ((float) (j % i) / (float) i - 0.5F) / 64.0F;
			float f1 = ((float) (j / i) / (float) i - 0.5F) / 64.0F;
			float f2 = 0.0F;
			GlStateManager.translate(f, f1, 0.0F);
			GlStateManager.rotate(MathHelper.sin(this.panoramaTimer / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-this.panoramaTimer * 0.1F, 0.0F, 1.0F, 0.0F);

			for (int k = 0; k < 6; ++k)
			{
				GlStateManager.pushMatrix();

				if (k == 1)
				{
					GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (k == 2)
				{
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (k == 3)
				{
					GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (k == 4)
				{
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (k == 5)
				{
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				titlePanoramaPaths[k].bind();
				vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				int l = 255 / (j + 1);
				float f3 = 0.0F;
				vertexBuffer.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
				vertexBuffer.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
				vertexBuffer.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
				vertexBuffer.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
				tessellator.draw();
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
			GlStateManager.colorMask(true, true, true, false);
		}

		vertexBuffer.setTranslation(0.0D, 0.0D, 0.0D);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.matrixMode(5889);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableDepth();
	}

	public void updateScreen()
	{
		for (Image i : guiConfig.customImages)
		{
			if (i.ichBinEineSlideshow)
			{
				i.slideShow.update();
			}
		}

		if (guiConfig.background != null && guiConfig.background.ichBinEineSlideshow)
		{
			guiConfig.background.slideShow.update();
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_R))
		{
			CustomMainMenu.INSTANCE.reload();

			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				mc.refreshResources();
			}
			mc.displayGuiScreen(new GuiMainMenu());
		}
	}

	private void rotateAndBlurSkybox(float p_73968_1_)
	{
		this.mc.getTextureManager().bindTexture(this.field_110351_G);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.colorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		GlStateManager.disableAlpha();
		int i = 3;

		if (guiConfig.panorama.blur)
		{
			for (int j = 0; j < i; ++j)
			{
				float f = 1.0F / (float) (j + 1);
				int k = this.width;
				int l = this.height;
				float f1 = (float) (j - i / 2) / 256.0F;
				vertexBuffer.pos((double) k, (double) l, (double) this.zLevel).tex((double) (0.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
				vertexBuffer.pos((double) k, 0.0D, (double) this.zLevel).tex((double) (1.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
				vertexBuffer.pos(0.0D, 0.0D, (double) this.zLevel).tex((double) (1.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
				vertexBuffer.pos(0.0D, (double) l, (double) this.zLevel).tex((double) (0.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
			}
		}

		tessellator.draw();
		GlStateManager.enableAlpha();
		GlStateManager.colorMask(true, true, true, true);
	}

	private int modX(int posX, Alignment alignment)
	{
		return (int) (posX + (width * alignment.factorX));
	}

	private int modY(int posY, Alignment alignment)
	{
		return (int) (posY + (height * alignment.factorY));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button instanceof GuiCustomWrappedButton && this.guiConfig.name.equals("mainmenu"))
		{
			GuiCustomWrappedButton wrapped = (GuiCustomWrappedButton) button;
			if (wrapped.wrappedButton != null)
			{
				ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(new GuiFakeMain(), wrapped.wrappedButton, new ArrayList<GuiButton>());
				if (MinecraftForge.EVENT_BUS.post(event))
				{
					return;
				}
				event.getButton().playPressSound(this.mc.getSoundHandler());
				if (this.equals(this.mc.currentScreen))
				{
					MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(new GuiFakeMain(), wrapped.wrappedButton, new ArrayList<GuiButton>()));
				}
			}
		}
		else if (button.id >= 6000 && button instanceof GuiCustomButton)
		{
			GuiCustomButton custom = (GuiCustomButton) button;
			// It's one of mine :o
			if (custom.b.action != null)
			{
				custom.b.action.perform(custom.b, this);
			}
		}
	}

	public void tick()
	{
		this.guiConfig.tick();
	}
}
