package maxpowa.tukmc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.client.gui.ChatLine;

public class TimedChatLine extends ChatLine {

    private String time;
    private long timeMillis;

    public TimedChatLine(int par1, String par2Str, int par3) {
        super(par1, par2Str, par3);
        time = new SimpleDateFormat("kk:mm:ss").format(new Date());
        timeMillis = System.currentTimeMillis();
    }

    public long getMillisOfCreation() {
        return timeMillis;
    }

    public String getTime() {
        return time;
    }

    public String getElapsedTime() {
        long timeElapsed = System.currentTimeMillis() - timeMillis;
        Date date = new Date(timeElapsed);
        String dateFormat = timeElapsed >= 3600000 ? "kk-, mm_, ss."
                : timeElapsed >= 60000 ? "mm_, ss." : "ss.";
        String formattedDate = new SimpleDateFormat(dateFormat).format(date);
        return " (".concat(formattedDate.replace('-', 'h').replace('_', 'm')
                .replace('.', 's').concat(" ago)"));
    }

}
