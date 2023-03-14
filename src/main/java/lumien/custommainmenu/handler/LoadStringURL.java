package lumien.custommainmenu.handler;

import lumien.custommainmenu.lib.texts.TextURL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

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
            text.string = in.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
