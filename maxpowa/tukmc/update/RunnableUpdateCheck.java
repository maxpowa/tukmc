package maxpowa.tukmc.update;

import java.io.InputStream;
import java.net.URL;

import maxpowa.tukmc.mod_TukMC;

public class RunnableUpdateCheck implements Runnable {

    boolean updchk = true;

    private final static String version = "https://www.dropbox.com/s/iai5sk56nn00jm7/latestVersion.html?dl=1";

    @Override
    public void run() {
        if (updchk) {
            try {
                mod_TukMC.updateCheckerErrorStatus = false;
                Long time = System.currentTimeMillis();
                String data = getData(version);
                mod_TukMC.updateVersion = data.substring(
                        data.indexOf("[version]") + 9,
                        data.indexOf("[/version]"));
                mod_TukMC.updateText = data.substring(
                        data.indexOf("[changes]") + 9,
                        data.indexOf("[/changes]"));
                mod_TukMC.updateMCVersion = data.substring(
                        data.indexOf("[mcversion]") + 11,
                        data.indexOf("[/mcversion]"));
                System.out.println("[TukMC] Found v" + mod_TukMC.updateVersion
                        + ". Running v" + mod_TukMC.TK_VERSION
                        + ". Check completed in "
                        + (System.currentTimeMillis() - time) + "ms.");
                updchk = false;
                return;
            } catch (Exception e) {
                System.err
                        .println("[TukMC] Failed to check for updates, please check your internet connection. If the problem persists for more than 24 hours, please report on the forum thread.");
                updchk = false;
                mod_TukMC.updateCheckerErrorStatus = true;
                return;
            }
        }
    }

    public static String getData(String address) throws Exception {
        URL url = new URL(address);

        InputStream html = null;
        html = url.openStream();
        int c = 0;
        StringBuffer buffer = new StringBuffer("");
        while (c != -1) {
            c = html.read();
            buffer.append((char) c);
        }
        return buffer.toString();
    }

}
