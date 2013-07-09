package maxpowa.tukmc.handlers;

import java.util.EnumSet;

import org.lwjgl.Sys;

import maxpowa.codebase.common.CommonUtils;
import maxpowa.tukmc.mod_TukMC;
import maxpowa.tukmc.gui.McMMOIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler {

    public static boolean ticked = false;
    public static boolean tracked = false;

    public static boolean deathadded = false;

    private static int lastRemoval = 0;
    private static int msgs = 0;

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        if (!ticked && CommonUtils.getMc().ingameGUI != null) {
            CommonUtils.getMc().ingameGUI = new maxpowa.tukmc.gui.GuiIngame();
            ticked = true;
        }
        if (!tracked && CommonUtils.getMc().thePlayer != null) {
            Minecraft mc = CommonUtils.getMc();
            mod_TukMC.tracker.trackEvent("@VERSION@", mc.thePlayer.username.toString(), "OS:"+System.getProperty("os.name", "Unknown")+" "+(Sys.is64Bit()?"64bit":"32bit"));
            mod_TukMC.tracker.trackEvent("@VERSION@", mc.thePlayer.username.toString(), "JAVAVERSION:"+System.getProperty("java.version", "Unknown"));
            mod_TukMC.tracker.trackEvent("@VERSION@", mc.thePlayer.username.toString(), "FMLVERSION:"+ForgeVersion.getVersion());
            mod_TukMC.tracker.trackEvent("@VERSION@", mc.thePlayer.username.toString(), "LWJGL:"+Sys.getVersion());
            mod_TukMC.tracker.trackEvent("@VERSION@", mc.thePlayer.username.toString(), "RESOLUTION:"+mc.displayWidth+"x"+mc.displayHeight);
            mod_TukMC.tracker.trackEvent("@VERSION@", mc.thePlayer.username.toString(), "MEMORY:"+(Runtime.getRuntime().maxMemory()/1024L/1024L)+"MB alloc.");
            StringBuilder builder = new StringBuilder();
            builder.append("MODS: ");
            for (ModContainer mod : Loader.instance().getModList()) {
                builder.append(mod.getName());
                builder.append(" \"");
                builder.append(mod.getModId());
                builder.append("\" ");
                builder.append(mod.getVersion());
                builder.append(" | ");
                System.out.println(builder.toString());
            }
            mod_TukMC.tracker.trackEvent("@VERSION@", mc.thePlayer.username.toString(), builder.toString());
            mod_TukMC.setTracked(true);
        }

        GuiScreen gui = CommonUtils.getMc().currentScreen;

        if (gui instanceof net.minecraft.client.gui.GuiGameOver && !deathadded) {
            mod_TukMC.deaths++;
            deathadded = true;
        } else if (!(gui instanceof net.minecraft.client.gui.GuiGameOver)
                && deathadded) {
            deathadded = false;
        }

        if (!mod_TukMC.defaultChat) {
            if (gui != null && gui instanceof GuiChat
                    && !(gui instanceof maxpowa.tukmc.gui.GuiChat)
                    || mod_TukMC.shouldReopenChat
                    && (gui == null || !(gui instanceof GuiChat))) {
                CommonUtils.getMc().displayGuiScreen(
                        new maxpowa.tukmc.gui.GuiChat());
            }
            mod_TukMC.shouldReopenChat = false;

            if (gui instanceof GuiSleepMP) {
                ((maxpowa.tukmc.gui.GuiChat) CommonUtils.getMc().currentScreen)
                        .setBed();
            }
        }

        if (type.equals(EnumSet.of(TickType.CLIENT))) {
            McMMOIntegration.tick();
            if (lastRemoval > 0) {
                --lastRemoval;
            }
            if (lastRemoval <= 0 && msgs > 0) {
                --msgs;
                lastRemoval = 10;
            }
        }
    }

    public static void addMsg() {
        msgs++;
        lastRemoval = 40;
    }

    public static int getMsgs() {
        return msgs;
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT, TickType.RENDER);
    }

    @Override
    public String getLabel() {
        return "TukMC";
    }

}
