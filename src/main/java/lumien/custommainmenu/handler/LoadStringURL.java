package lumien.custommainmenu.handler;

import lumien.custommainmenu.lib.texts.TextURL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class LoadStringURL extends Thread {
    volatile TextURL text;

    public LoadStringURL(TextURL text) {
        this.text = text;

        this.setDaemon(true);
    }

	@Override
	public void run()
	{
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new InputStreamReader(text.getURL().openStream()));
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

		StringBuilder builder = new StringBuilder();

		String inputLine = null;
		do
		{
			if (inputLine != null)
			{
				builder.append(inputLine);
			}

			String newInput = null;
			try
			{
				newInput = in.readLine();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			if (inputLine != null)
			{
				builder.append("\n");
			}
			
			inputLine = newInput;
		}
		while (inputLine != null);

		try
		{
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		synchronized (text.string)
		{
			text.string = builder.toString();
		}
	}
}
