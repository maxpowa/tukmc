package maxpowa.tukmc.handlers;

import java.util.EnumSet;
import maxpowa.codebase.common.CommonUtils;
import maxpowa.tukmc.gui.GuiChat;
import maxpowa.tukmc.gui.GuiNewConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeyRegister extends KeyHandler {

    public static KeyBinding showTooltipKB = new KeyBinding(
            "TukMC Show Tooltip", Keyboard.KEY_DELETE);
    public static KeyBinding openConfigKB = new KeyBinding("TukMC Open Config",
            Keyboard.KEY_K);
    public static KeyBinding commandKB = new KeyBinding("TukMC Chat Command", 
    		Keyboard.KEY_SLASH);
//    public static KeyBinding debugOut = new KeyBinding("TukMC Debug Utility", 
//    		Keyboard.KEY_NUMPADENTER);
    
    public static int count = 0;

    public KeyRegister() {
        super(new KeyBinding[] { showTooltipKB, openConfigKB, commandKB}, new boolean[] {
                true, false, false});
    }

    @Override
    public String getLabel() {
        return "TukMC";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb,
            boolean tickEnd, boolean isRepeat) {
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
        Minecraft mc = CommonUtils.getMc();
        if (kb.keyCode == openConfigKB.keyCode && mc.currentScreen == null) {
            mc.displayGuiScreen(new GuiNewConfig());
        } else if (kb.keyCode == commandKB.keyCode && mc.currentScreen == null) {
        	GuiChat chatGui = new GuiChat();
        	chatGui.setCommand();
        	mc.displayGuiScreen(chatGui);
        }
//        } else if (kb.keyCode == debugOut.keyCode && mc.thePlayer.inventory.mainInventory != null) {
//            System.out.println("Click!");
//            for (ItemStack is : mc.thePlayer.inventory.mainInventory) {
//               if (is != null && is.hasTagCompound())
//        			System.out.println(is.getDisplayName() + " -> " + is.getTagCompound().toString());
//        	  }
//        }
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.allOf(TickType.class);
    }

}
