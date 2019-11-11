package lumien.custommainmenu.lib.texts;

import java.net.MalformedURLException;
import java.net.URL;

import lumien.custommainmenu.handler.LoadStringURL;
import lumien.custommainmenu.lib.StringReplacer;

public class TextURL implements IText
{
	URL url;
	public String string;

	int refreshInterval;
	int refreshCounter;

	LoadStringURL loadThread;

	public TextURL(String url, int refreshInterval)
	{
		try
		{
			this.url = new URL(StringReplacer.replacePlaceholders(url));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

		this.string = "";

		(loadThread = new LoadStringURL(this)).start();

		this.refreshInterval = refreshInterval;
		this.refreshCounter = 0;
	}

	public void tick()
	{
		if (this.refreshInterval != -1 && this.refreshInterval >= 60)
		{
			this.refreshCounter++;
			if (this.refreshCounter >= this.refreshInterval && !this.loadThread.isAlive())
			{
				(loadThread = new LoadStringURL(this)).start();
				this.refreshCounter = 0;
			}
		}
	}

	@Override
	public String get()
	{
		synchronized (string)
		{
			return string;
		}
	}

	public URL getURL()
	{
		return url;
	}
}
