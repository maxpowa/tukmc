package maxpowa.tukmc;

import maxpowa.codebase.common.ColorCode;
import maxpowa.codebase.common.CommonUtils;
import maxpowa.codebase.common.FormattingCode;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class ChatNames {

    static Minecraft mc;

    public static String check(ClientChatReceivedEvent event) {
        mc = CommonUtils.getMc();
        if (event.message.contains("maxpowa"))
            return event.message.replace("maxpowa", ColorCode.GOLD + "maxpowa"
                    + FormattingCode.RESET);
        return event.message;
    }
}
