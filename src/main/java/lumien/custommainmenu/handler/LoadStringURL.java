package lumien.custommainmenu.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lumien.custommainmenu.lib.texts.TextURL;

public class LoadStringURL extends Thread
{
	TextURL text;

	public LoadStringURL(TextURL text)
	{
		this.text = text;

		this.setDaemon(true);
	}

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(text.getURL().openStream()))) {
            synchronized (text.string) {
                text.string = br.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
