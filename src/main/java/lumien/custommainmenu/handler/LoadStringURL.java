package lumien.custommainmenu.handler;

import lumien.custommainmenu.lib.texts.TextURL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LoadStringURL extends Thread {
    volatile TextURL text;

    public LoadStringURL(TextURL text) {
        this.text = text;

        this.setDaemon(true);
    }

    @Override
    public void run() {
        try (
                InputStream is = text.getURL().openStream();
                InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                BufferedReader in = new BufferedReader(isr)
        ) {
            StringBuilder builder = new StringBuilder();

            String inputLine = null;
            do {
                if (inputLine != null) {
                    builder.append(inputLine);
                }

                String newInput = null;
                try {
                    newInput = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (inputLine != null) {
                    builder.append("\n");
                }

                inputLine = newInput;
            }
            while (inputLine != null);

            synchronized (text.string) {
                text.string = builder.toString();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
