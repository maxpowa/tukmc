package maxpowa.tukmc;

import java.awt.Color;
import java.io.File;

import maxpowa.codebase.common.IOUtils;
import maxpowa.codebase.tracking.AnalyticsConfigData;
import maxpowa.codebase.tracking.AnalyticsTracker;
import maxpowa.codebase.tracking.AnalyticsTracker.GoogleAnalyticsVersion;
import maxpowa.tukmc.handlers.ChatListener;
import maxpowa.tukmc.handlers.KeyRegister;
import maxpowa.tukmc.handlers.TickHandler;
import maxpowa.tukmc.render.MobHealthBars;
import maxpowa.tukmc.render.RenderPlayerTuk;
import maxpowa.tukmc.update.RunnableUpdateCheck;
import maxpowa.tukmc.util.TukMCReference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "tukmc_Vz", name = "TukMC", version = "Version [@VERSION@] for @MCVERSION@")
public class mod_TukMC {

    public static String MC_VERSION = "@MCVERSION@";
    public static String TK_VERSION = "@VERSION@";
    
    public static AnalyticsTracker tracker = null;

    public static File cacheFile;

    public static boolean spellcheckerEnabled = true;
    public static boolean closeOnFinish = false;
    public static boolean displayNotification = true;
    public static boolean updateChecker = true;
    public static boolean defaultChat = false;

    public static String updateVersion = null;
    public static String updateText = null;
    public static String updateMCVersion = null;
    public static boolean updatePopup = true;

    public static boolean shouldReopenChat = false;
    public static int deaths = 0;
    public static int negativeMobKills = 0;
    public static int negativePKills = 0;

    protected static Thread updateCheckerThread = null;
    public static boolean updateCheckerErrorStatus = false;

    public static void getVersion() {
        updateCheckerThread = new Thread(new RunnableUpdateCheck());
        System.out.println("[TukMC] Starting Update Checker thread.");
        updateCheckerThread.start();
    }

    @EventHandler
    public void onInit(FMLInitializationEvent event) {
        KeyBindingRegistry.registerKeyBinding(new KeyRegister());
        TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);
        cacheFile = IOUtils.getCacheFile("tukmc");
        MinecraftForge.EVENT_BUS.register(new ChatListener());
        MinecraftForge.EVENT_BUS.register(new MobHealthBars());
        RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class,
                new RenderPlayerTuk());

        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
        spellcheckerEnabled = cmp.hasKey("spellcheckerEnabled") ? cmp
                .getBoolean("spellcheckerEnabled") : true;
        displayNotification = cmp.hasKey("displayNotification") ? cmp
                .getBoolean("displayNotification") : true;
        closeOnFinish = cmp.hasKey("closeOnFinish") ? cmp
                .getBoolean("closeOnFinish") : false;
        updateChecker = cmp.hasKey("checkupdate") ? cmp
                .getBoolean("checkupdate") : true;
        defaultChat = cmp.hasKey("defaultChat") ? cmp.getBoolean("defaultChat")
                : false;
        TickHandler.tracked = cmp.hasKey("tracked@VERSION@") ? cmp.getBoolean("tracked@VERSION@") : false;
        loadColorSettings();

        tracker = new AnalyticsTracker(new AnalyticsConfigData("UA-42183585-2"), GoogleAnalyticsVersion.V_4_7_2);
        
        if (updateChecker) {
            getVersion();
        }
    }

    public static void setUpdateChecker(boolean b) {
        updateChecker = b;
        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
        cmp.setBoolean("checkupdate", b);
        IOUtils.injectNBTToFile(cmp, cacheFile);
    }

    public static void setDefaultChat(boolean b) {
        defaultChat = b;
        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
        cmp.setBoolean("defaultChat", b);
        IOUtils.injectNBTToFile(cmp, cacheFile);
    }

    public static void setTracked(boolean b) {
        TickHandler.tracked = b;
        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
        cmp.setBoolean("tracked@VERSION@", b);
        IOUtils.injectNBTToFile(cmp, cacheFile);
    }

    public static void setSpellcheckerEnabled(boolean b) {
        spellcheckerEnabled = b;
        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
        cmp.setBoolean("spellcheckerEnabled", b);
        IOUtils.injectNBTToFile(cmp, cacheFile);
    }

    public static void setCloseOnFinish(boolean b) {
        closeOnFinish = b;
        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
        cmp.setBoolean("closeOnFinish", b);
        IOUtils.injectNBTToFile(cmp, cacheFile);
    }

    public static void setDisplayNotification(boolean b) {
        displayNotification = b;
        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
        cmp.setBoolean("displayNotification", b);
        IOUtils.injectNBTToFile(cmp, cacheFile);
    }

    public static void saveColorSettings() {
        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
        cmp.setInteger("red_inner", TukMCReference.RED_INNER);
        cmp.setInteger("green_inner", TukMCReference.GREEN_INNER);
        cmp.setInteger("blue_inner", TukMCReference.BLUE_INNER);
        cmp.setInteger("red_outer", TukMCReference.RED_OUTER);
        cmp.setInteger("green_outer", TukMCReference.GREEN_OUTER);
        cmp.setInteger("blue_outer", TukMCReference.BLUE_OUTER);
        Color c_inner = new Color(TukMCReference.RED_INNER,
                TukMCReference.GREEN_INNER, TukMCReference.BLUE_INNER);
        TukMCReference.BOX_INNER_COLOR = c_inner.getRGB();
        cmp.setInteger("hex_inner", TukMCReference.BOX_INNER_COLOR);
        Color c_outer = new Color(TukMCReference.RED_OUTER,
                TukMCReference.GREEN_OUTER, TukMCReference.BLUE_OUTER);
        TukMCReference.BOX_OUTLINE_COLOR = c_outer.getRGB();
        cmp.setInteger("hex_outline", TukMCReference.BOX_OUTLINE_COLOR);
        IOUtils.injectNBTToFile(cmp, cacheFile);
        TukMCReference.BOX_HIGHLIGHT_COLOR = Color
                .getColor(null, TukMCReference.BOX_INNER_COLOR).brighter()
                .brighter().brighter().getRGB();
    }

    public static void loadColorSettings() {
        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
        TukMCReference.RED_INNER = cmp.hasKey("red_inner") ? cmp
                .getInteger("red_inner") : 26;
        TukMCReference.GREEN_INNER = cmp.hasKey("green_inner") ? cmp
                .getInteger("green_inner") : 26;
        TukMCReference.BLUE_INNER = cmp.hasKey("blue_inner") ? cmp
                .getInteger("blue_inner") : 26;
        TukMCReference.RED_OUTER = cmp.hasKey("red_outer") ? cmp
                .getInteger("red_outer") : 150;
        TukMCReference.GREEN_OUTER = cmp.hasKey("green_outer") ? cmp
                .getInteger("green_outer") : 154;
        TukMCReference.BLUE_OUTER = cmp.hasKey("blue_outer") ? cmp
                .getInteger("blue_outer") : 165;
        TukMCReference.BOX_INNER_COLOR = cmp.hasKey("hex_inner") ? cmp
                .getInteger("hex_inner") : 0x1A1A1A;
        TukMCReference.BOX_OUTLINE_COLOR = cmp.hasKey("hex_outline") ? cmp
                .getInteger("hex_outline") : 0x969AA5;
    }

    public static void defaultColorSettings() {
        TukMCReference.BOX_INNER_COLOR = 0x1A1A1A;
        TukMCReference.BOX_HIGHLIGHT_COLOR = 0x4C4C4C;
        TukMCReference.BOX_OUTLINE_COLOR = 0x969AA5;
        TukMCReference.RED_INNER = 26;
        TukMCReference.GREEN_INNER = 26;
        TukMCReference.BLUE_INNER = 26;
        TukMCReference.RED_OUTER = 150;
        TukMCReference.GREEN_OUTER = 154;
        TukMCReference.BLUE_OUTER = 165;
        saveColorSettings();
    }

    public static void registerOpenWebsite(String s) {
        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
        NBTTagCompound subCmp = !cmp.hasKey("websites") ? new NBTTagCompound()
                : cmp.getCompoundTag("websites");
        int visits = subCmp.hasKey(s) ? subCmp.getInteger(s) : 0;
        subCmp.setInteger(s, visits + 1);
        cmp.setCompoundTag("websites", subCmp);
        IOUtils.injectNBTToFile(cmp, cacheFile);
    }

    public static int getWebsiteViews(String s) {
        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
        if (!cmp.hasKey("websites"))
            return 0;
        NBTTagCompound subCmp = cmp.getCompoundTag("websites");
        return !subCmp.hasKey(s) ? 0 : subCmp.getInteger(s);
    }
}
