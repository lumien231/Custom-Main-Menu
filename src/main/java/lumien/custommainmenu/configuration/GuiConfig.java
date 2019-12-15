package lumien.custommainmenu.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.configuration.elements.Background;
import lumien.custommainmenu.configuration.elements.Button;
import lumien.custommainmenu.configuration.elements.Image;
import lumien.custommainmenu.configuration.elements.Panorama;
import lumien.custommainmenu.configuration.elements.Slideshow;
import lumien.custommainmenu.configuration.elements.SplashText;
import lumien.custommainmenu.configuration.elements.Label;
import lumien.custommainmenu.gui.GuiCustom;
import lumien.custommainmenu.lib.ANCHOR;
import lumien.custommainmenu.lib.actions.ActionConnectToServer;
import lumien.custommainmenu.lib.actions.ActionLoadWorld;
import lumien.custommainmenu.lib.actions.ActionOpenFolder;
import lumien.custommainmenu.lib.actions.ActionOpenGUI;
import lumien.custommainmenu.lib.actions.ActionOpenLink;
import lumien.custommainmenu.lib.actions.ActionOpenModConfig;
import lumien.custommainmenu.lib.actions.ActionQuit;
import lumien.custommainmenu.lib.actions.ActionRefresh;
import lumien.custommainmenu.lib.actions.IAction;
import lumien.custommainmenu.lib.texts.IText;
import lumien.custommainmenu.lib.texts.TextResourceLocation;
import lumien.custommainmenu.lib.texts.TextString;
import lumien.custommainmenu.lib.texts.TextURL;
import lumien.custommainmenu.lib.textures.ITexture;
import lumien.custommainmenu.lib.textures.TextureApng;
import lumien.custommainmenu.lib.textures.TextureResourceLocation;
import lumien.custommainmenu.lib.textures.TextureURL;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GuiConfig
{
	public String name;
	public int guiScale;

	public ArrayList<Button> customButtons;

	// Labels
	public ArrayList<Label> customLabels;

	// Images
	public ArrayList<Image> customImages;

	// Alignments
	public HashMap<String, Alignment> alignments;

	// Other
	public SplashText splashText;
	public Panorama panorama;
	public Background background;

	// Web URLs
	public List<TextURL> textUrls = new ArrayList<TextURL>();

	public void load(String name, JsonObject jsonObject)
	{
		if (name.endsWith("_small"))
		{
			guiScale = 1;
			name = name.replace("_small", "");
		}
		else if (name.endsWith("_normal"))
		{
			guiScale = 2;
			name = name.replace("_normal", "");
		}
		else if (name.endsWith("_large"))
		{
			guiScale = 3;
			name = name.replace("_large", "");
		}
		else if (name.endsWith("_auto"))
		{
			guiScale = 0;
			name = name.replace("_auto", "");
		}
		else
		{
			guiScale = -1;
		}

		this.name = name;
		// Pre Load because badly coded
		loadAlignments(jsonObject);

		customLabels = new ArrayList<Label>();
		customImages = new ArrayList<Image>();
		customButtons = new ArrayList<Button>();
		splashText = null;
		panorama = null;
		background = null;

		if (jsonObject.has("buttons"))
			loadButtons(jsonObject);

		if (jsonObject.has("labels"))
			loadLabels(jsonObject);

		if (jsonObject.has("images"))
			loadImages(jsonObject);

		if (jsonObject.has("other"))
			loadOthers(jsonObject);
	}

	private void loadAlignments(JsonObject jsonObject)
	{
		alignments = new HashMap<String, Alignment>();

		// Default Alignments
		alignments.put("bottom_left", new Alignment(0, 1));
		alignments.put("top_left", new Alignment(0, 0));
		alignments.put("top_right", new Alignment(1, 0));
		alignments.put("bottom_right", new Alignment(1, 1));
		alignments.put("center", new Alignment(0.5F, 0.5F));
		alignments.put("button", new Alignment(0.5F, 0.25F));
		alignments.put("top_center", new Alignment(0.5F, 0F));
		alignments.put("left_center", new Alignment(0F, 0.5F));
		alignments.put("bottom_center", new Alignment(0.5F, 1F));
		alignments.put("right_center", new Alignment(1F, 0.5F));

		// Custom Alignments
		if (jsonObject.has("alignments"))
		{
			JsonObject alignmentObject = (JsonObject) jsonObject.get("alignments");
			Set<Entry<String, JsonElement>> buttons = alignmentObject.entrySet();

			for (Entry<String, JsonElement> entry : buttons)
			{
				String name = entry.getKey();
				JsonObject object = (JsonObject) entry.getValue();
				Alignment a = new Alignment(object.get("factorWidth").getAsFloat(), object.get("factorHeight").getAsFloat());
				alignments.put(name, a);
			}
		}
	}

	private void loadOthers(JsonObject jsonObject)
	{
		JsonObject other = (JsonObject) jsonObject.get("other");

		JsonObject splashTextObject = (JsonObject) other.get("splash-text");
		if (splashTextObject != null)
		{
			if (splashTextObject.has("color") && splashTextObject.has("alignment"))
			{
				splashText = new SplashText(this, splashTextObject.get("posX").getAsInt(), splashTextObject.get("posY").getAsInt(), splashTextObject.get("color").getAsInt(), splashTextObject.get("alignment").getAsString());
			}
			else if (splashTextObject.has("color"))
			{
				splashText = new SplashText(this, splashTextObject.get("posX").getAsInt(), splashTextObject.get("posY").getAsInt(), splashTextObject.get("color").getAsInt(), "top_center");
			}
			else if (splashTextObject.has("alignment"))
			{
				splashText = new SplashText(this, splashTextObject.get("posX").getAsInt(), splashTextObject.get("posY").getAsInt(), splashTextObject.get("alignment").getAsString());
			}
			else
			{
				splashText = new SplashText(this, splashTextObject.get("posX").getAsInt(), splashTextObject.get("posY").getAsInt(), "top_center");
			}

			if (splashTextObject.has("synced"))
			{
				splashText.synced = splashTextObject.get("synced").getAsBoolean();
			}

			if (splashTextObject.has("texts"))
			{
				splashText.setSplashTexts(getWantedText(splashTextObject.get("texts")));
			}

			// Backwards Compability for older menus
			if (splashTextObject.has("file"))
			{
				splashText.setSplashTexts(new TextResourceLocation(getStringPlease(splashTextObject.get("file"))));
			}
		}

		JsonObject panoramaObject = (JsonObject) other.get("panorama");
		if (panoramaObject != null)
		{
			panorama = new Panorama(this, getStringPlease(panoramaObject.get("images")), panoramaObject.get("blur").getAsBoolean(), panoramaObject.get("gradient").getAsBoolean());

			if (panoramaObject.has("animate"))
			{
				panorama.setAnimate(panoramaObject.get("animate").getAsBoolean());
			}

			if (panoramaObject.has("position"))
			{
				panorama.setPosition(panoramaObject.get("position").getAsInt());
			}

			if (panoramaObject.has("animationSpeed"))
			{
				panorama.setAnimationSpeed(panoramaObject.get("animationSpeed").getAsInt());
			}

			if (panoramaObject.has("synced"))
			{
				panorama.synced = panoramaObject.get("synced").getAsBoolean();
			}
		}

		JsonElement backgroundElement = (JsonElement) other.get("background");
		if (backgroundElement != null)
		{
			if (backgroundElement.isJsonPrimitive() && backgroundElement.getAsJsonPrimitive().isString() && backgroundElement.getAsString().equals("options"))
			{
				background = Background.OPTIONS_BACKGROUND;
			}
			else
			{
				JsonObject backgroundObject = (JsonObject) backgroundElement;
				background = new Background(this, getWantedTexture(getStringPlease(backgroundObject.get("image"))));

				if (backgroundObject.has("mode"))
				{
					background.setMode(backgroundObject.get("mode").getAsString());
				}

				if (backgroundObject.has("slideshow"))
				{
					JsonObject slideShowObject = backgroundObject.get("slideshow").getAsJsonObject();
					background.ichBinEineSlideshow = true;

					if (slideShowObject.has("synced") && slideShowObject.get("synced").getAsBoolean())
					{
						GuiCustom mainMenu = CustomMainMenu.INSTANCE.config.getGUI("mainmenu");
						background.slideShow = mainMenu.guiConfig.background.slideShow;
					}
					else
					{
						JsonArray imageArray = slideShowObject.get("images").getAsJsonArray();
						String[] images = new String[imageArray.size()];

						for (int i = 0; i < images.length; i++)
						{
							images[i] = imageArray.get(i).getAsString();
						}

						Slideshow slideShow = new Slideshow(this, images);

						if (slideShowObject.has("displayDuration"))
						{
							slideShow.displayDuration = slideShowObject.get("displayDuration").getAsInt();
						}

						if (slideShowObject.has("fadeDuration"))
						{
							slideShow.fadeDuration = slideShowObject.get("fadeDuration").getAsInt();
						}

						if (slideShowObject.has("shuffle"))
						{
							if (slideShowObject.get("shuffle").getAsBoolean())
							{
								slideShow.shuffle();
							}
						}

						background.slideShow = slideShow;
					}
				}
			}
		}
	}

	private void loadImages(JsonObject jsonObject)
	{
		JsonObject textElements = jsonObject.get("images").getAsJsonObject();
		Set<Entry<String, JsonElement>> images = textElements.entrySet();

		for (Entry<String, JsonElement> entry : images)
		{
			String name = entry.getKey();
			JsonElement element = entry.getValue();

			customImages.add(getImage((JsonObject) element));
		}
	}

	private void loadLabels(JsonObject jsonObject)
	{
		Set<Entry<String, JsonElement>> labels = new HashSet<Entry<String, JsonElement>>();

		JsonElement textElement = jsonObject.get("texts");
		if (textElement != null)
		{
			CustomMainMenu.INSTANCE.logger.log(Level.ERROR, "The texts category in CMM has been renamed to labels.");
		}

		JsonElement labelElement = jsonObject.get("labels");
		if (labelElement != null)
		{
			JsonObject labelElements = jsonObject.get("labels").getAsJsonObject();
			labels.addAll(labelElements.entrySet());
		}

		for (Entry<String, JsonElement> entry : labels)
		{
			String name = entry.getKey();
			JsonElement element = entry.getValue();

			customLabels.add(getLabel(name, (JsonObject) element));
		}

	}

	private void loadButtons(JsonObject jsonObject)
	{
		JsonObject buttonElements = jsonObject.get("buttons").getAsJsonObject();
		Set<Entry<String, JsonElement>> buttons = buttonElements.entrySet();

		for (Entry<String, JsonElement> entry : buttons)
		{
			String name = entry.getKey();
			JsonObject object = (JsonObject) entry.getValue();
			Button b = getButton(object);
			b.name = entry.getKey();
			customButtons.add(b);
		}
	}

	private Button getButton(JsonObject jsonObject)
	{
		Button b = new Button(this, getWantedText(jsonObject.get("text")), jsonObject.get("posX").getAsInt(), jsonObject.get("posY").getAsInt(), jsonObject.get("width").getAsInt(), jsonObject.get("height").getAsInt());

		if (jsonObject.has("alignment"))
		{
			b.setStringAlignment(jsonObject.get("alignment").getAsString());
		}

		if (jsonObject.has("texture"))
		{
			b.setTexture(getWantedTexture(getStringPlease(jsonObject.get("texture"))));
		}

		if (jsonObject.has("normalTextColor"))
		{
			b.normalTextColor = jsonObject.get("normalTextColor").getAsInt();
		}

		if (jsonObject.has("hoverTextColor"))
		{
			b.hoverTextColor = jsonObject.get("hoverTextColor").getAsInt();
		}

		if (jsonObject.has("shadow"))
		{
			b.shadow = jsonObject.get("shadow").getAsBoolean();
		}

		if (jsonObject.has("imageWidth"))
		{
			b.imageWidth = jsonObject.get("imageWidth").getAsInt();
		}

		if (jsonObject.has("imageHeight"))
		{
			b.imageHeight = jsonObject.get("imageHeight").getAsInt();
		}

		if (jsonObject.has("wrappedButton"))
		{
			b.setWrappedButton(jsonObject.get("wrappedButton").getAsInt());
		}

		if (jsonObject.has("action"))
		{
			JsonObject actionObject = (JsonObject) jsonObject.get("action");

			b.action = getWantedAction(actionObject);
		}

		if (jsonObject.has("tooltip"))
		{
			b.tooltip = getWantedText(jsonObject.get("tooltip"));
		}

		if (jsonObject.has("hoverText"))
		{
			b.hoverText = getWantedText(jsonObject.get("hoverText"));
		}

		if (jsonObject.has("pressSound"))
		{
			b.pressSound = getStringPlease(jsonObject.get("pressSound"));
		}

		if (jsonObject.has("hoverSound"))
		{
			b.hoverSound = getStringPlease(jsonObject.get("hoverSound"));
		}

		if (jsonObject.has("textOffsetX"))
		{
			b.textOffsetX = jsonObject.get("textOffsetX").getAsInt();
		}

		if (jsonObject.has("textOffsetY"))
		{
			b.textOffsetY = jsonObject.get("textOffsetY").getAsInt();
		}

		return b;
	}

	private Image getImage(JsonObject jsonObject)
	{
		Image image = new Image(this, jsonObject.get("posX").getAsInt(), jsonObject.get("posY").getAsInt(), jsonObject.get("width").getAsInt(), jsonObject.get("height").getAsInt(), getAlignment("top_left"));
		if (jsonObject.has("alignment"))
		{
			image.alignment = getAlignment(jsonObject.get("alignment").getAsString());
		}

		if (jsonObject.has("hoverImage"))
		{
			image.hoverImage = getWantedTexture(getStringPlease(jsonObject.get("hoverImage")));
		}

		if (jsonObject.has("image"))
		{
			image.image = getWantedTexture(getStringPlease(jsonObject.get("image")));
		}
		else if (jsonObject.has("slideshow"))
		{
			JsonObject slideShowObject = jsonObject.get("slideshow").getAsJsonObject();
			image.ichBinEineSlideshow = true;

			JsonArray imageArray = slideShowObject.get("images").getAsJsonArray();
			String[] images = new String[imageArray.size()];

			for (int i = 0; i < images.length; i++)
			{
				images[i] = imageArray.get(i).getAsString();
			}

			Slideshow slideShow = new Slideshow(this, images);

			if (slideShowObject.has("displayDuration"))
			{
				slideShow.displayDuration = slideShowObject.get("displayDuration").getAsInt();
			}

			if (slideShowObject.has("fadeDuration"))
			{
				slideShow.fadeDuration = slideShowObject.get("fadeDuration").getAsInt();
			}

			if (slideShowObject.has("shuffle"))
			{
				if (slideShowObject.get("shuffle").getAsBoolean())
				{
					slideShow.shuffle();
				}
			}

			image.slideShow = slideShow;
		}
		else
		{
			throw new RuntimeException("Images either need an image or slideshow property");
		}

		return image;
	}

	private Label getLabel(String name, JsonObject jsonObject)
	{
		Label label = new Label(this, name, getWantedText(jsonObject.get("text")), jsonObject.get("posX").getAsInt(), jsonObject.get("posY").getAsInt());

		if (jsonObject.has("alignment"))
		{
			label.setAlignment(jsonObject.get("alignment").getAsString());
		}

		if (jsonObject.has("color"))
		{
			label.setColor(jsonObject.get("color").getAsInt());
		}

		if (jsonObject.has("hoverColor"))
		{
			label.setHoverColor(jsonObject.get("hoverColor").getAsInt());
		}

		if (jsonObject.has("action"))
		{
			JsonObject actionObject = (JsonObject) jsonObject.get("action");

			label.action = getWantedAction(actionObject);
		}

		if (jsonObject.has("hoverText"))
		{
			label.hoverText = getWantedText(jsonObject.get("hoverText"));
		}

		if (jsonObject.has("fontSize"))
		{
			label.fontSize = jsonObject.get("fontSize").getAsFloat();
		}

		if (jsonObject.has("pressSound"))
		{
			label.pressSound = getStringPlease(jsonObject.get("pressSound"));
		}

		if (jsonObject.has("hoverSound"))
		{
			label.hoverSound = getStringPlease(jsonObject.get("hoverSound"));
		}

		if (jsonObject.has("anchor"))
		{
			String stringAnchor = jsonObject.get("anchor").getAsString();

			if (stringAnchor.equals("start"))
			{
				label.anchor = ANCHOR.START;
			}
			else if (stringAnchor.equals("middle"))
			{
				label.anchor = ANCHOR.MIDDLE;
			}
			else if (stringAnchor.equals("end"))
			{
				label.anchor = ANCHOR.END;
			}
		}

		return label;
	}

	private String getStringPlease(JsonElement jsonElement)
	{
		Random rng = new Random();
		if (jsonElement.isJsonPrimitive())
		{
			return jsonElement.getAsString();
		}
		else if (jsonElement.isJsonArray())
		{
			JsonArray array = jsonElement.getAsJsonArray();
			return array.get(rng.nextInt(array.size())).getAsString();
		}
		else
		{
			CustomMainMenu.INSTANCE.logger.log(Level.ERROR, "Error getting random value out of " + jsonElement.toString());
			return "ERROR";
		}
	}

	public static ITexture getWantedTexture(String textureString)
	{
		if (textureString.startsWith("web:"))
		{
			String url = textureString.substring(4, textureString.length());

			return new TextureURL(url);
		}
		else if (textureString.endsWith("apng"))
		{
			return new TextureApng(textureString);
		}
		else
		{
			return new TextureResourceLocation(textureString);
		}
	}

	private IAction getWantedAction(JsonObject actionObject)
	{
		String actionType = actionObject.get("type").getAsString();

		if (actionType.equals("openLink"))
		{
			return new ActionOpenLink(actionObject.get("link").getAsString());
		}
		else if (actionType.equalsIgnoreCase("loadWorld"))
		{
			return new ActionLoadWorld(actionObject.get("dirName").getAsString(), actionObject.get("saveName").getAsString());
		}
		else if (actionType.equalsIgnoreCase("connectToServer"))
		{
			return new ActionConnectToServer(actionObject.get("ip").getAsString(), actionObject.get("serverName") != null ? actionObject.get("serverName").getAsString() : "Minecraft Server");
		}
		else if (actionType.equalsIgnoreCase("openGui"))
		{
			return new ActionOpenGUI(actionObject.get("gui").getAsString());
		}
		else if (actionType.equalsIgnoreCase("quit"))
		{
			return new ActionQuit();
		}
		else if (actionType.equalsIgnoreCase("refresh"))
		{
			return new ActionRefresh();
		}
		else if (actionType.equalsIgnoreCase("openFolder"))
		{
			return new ActionOpenFolder(actionObject.get("folderName").getAsString());
		}
		else if (actionType.equalsIgnoreCase("openModConfig"))
		{
			return new ActionOpenModConfig(actionObject.get("modid").getAsString());
		}

		return null;
	}

	public Alignment getAlignment(String name)
	{
		if (alignments.containsKey(name))
		{
			return alignments.get(name);
		}
		else
		{
			return alignments.get("top_left");
		}
	}

	public IText getWantedText(JsonElement element)
	{
		if (element.isJsonPrimitive() || element.isJsonArray())
		{
			String textString = getStringPlease(element);
			if (textString.startsWith("web:"))
			{
				String url = textString.substring(4, textString.length());

				TextURL tURL = new TextURL(url, -1);
				
				textUrls.add(tURL);
				
				return tURL;
			}
			else if (textString.startsWith("file:"))
			{
				String resource = textString.substring(5, textString.length());

				return new TextResourceLocation(resource);
			}

			return new TextString(textString);
		}
		else if (element.isJsonObject())
		{
			JsonObject object = element.getAsJsonObject();

			String textType = object.get("type").getAsString();

			if (textType.equals("web"))
			{
				String url = object.get("url").getAsString();

				int refreshInterval = -1;

				if (object.has("refreshInterval"))
				{
					refreshInterval = object.get("refreshInterval").getAsInt();
				}

				TextURL tURL = new TextURL(url, refreshInterval);
				
				textUrls.add(tURL);
				
				return tURL;
			}
			else if (textType.equals("file"))
			{
				String rl = object.get("location").getAsString();
				
				return new TextResourceLocation(rl);
			}
		}

		
		return new TextString("INVALID TEXT");
	}

	public void tick()
	{
		textUrls.forEach((url) -> url.tick());
	}
}
