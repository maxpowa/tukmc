package maxpowa.tukmc;

import java.awt.Color;
import java.io.File;


import maxpowa.codebase.common.EnumMaxpowaMods;
import maxpowa.codebase.common.IOUtils;
import maxpowa.codebase.common.MoarReference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;

import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "tukmc_Vz", name = "TukMC", version = "Version [1.8] for 1.4.7")
public class mod_TukMC {

	public static File cacheFile;

	public static boolean spellcheckerEnabled = true;
	public static boolean closeOnFinish = false;
	public static boolean displayNotification = true;

	public static boolean shouldReopenChat = false;

	@Init
	public void onInit(FMLInitializationEvent event) {
		MoarReference.loadedMpMods.add(EnumMaxpowaMods.TUKMC.getAcronym());
		KeyBindingRegistry.registerKeyBinding(new KeyRegister());
		TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);
		cacheFile = IOUtils.getCacheFile(EnumMaxpowaMods.TUKMC);
		MinecraftForge.EVENT_BUS.register(new ChatListener());
		RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, new RenderPlayerTuk());

		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		spellcheckerEnabled = cmp.hasKey("spellcheckerEnabled") ? cmp.getBoolean("spellcheckerEnabled") : true;
		displayNotification = cmp.hasKey("displayNotification") ? cmp.getBoolean("displayNotification") : true;
		closeOnFinish = cmp.hasKey("closeOnFinish") ? cmp.getBoolean("closeOnFinish") : false;
		loadColorSettings();
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
		cmp.setInteger("redRaw", TukMCReference.RED);
		cmp.setInteger("greenRaw", TukMCReference.GREEN);
		cmp.setInteger("blueRaw", TukMCReference.BLUE);
	    Color c = new Color(TukMCReference.RED, TukMCReference.GREEN, TukMCReference.BLUE);
	    TukMCReference.BOX_INNER_COLOR = c.getRGB();
	    cmp.setInteger("hexFill", TukMCReference.BOX_INNER_COLOR);
		IOUtils.injectNBTToFile(cmp, cacheFile);
	}

	public static void loadColorSettings() {
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		TukMCReference.RED = cmp.hasKey("redRaw") ? cmp.getInteger("redRaw") : 0;
		TukMCReference.GREEN = cmp.hasKey("greenRaw") ? cmp.getInteger("greenRaw") : 0;
		TukMCReference.BLUE = cmp.hasKey("blueRaw") ? cmp.getInteger("blueRaw") : 0;
	    TukMCReference.BOX_INNER_COLOR = cmp.hasKey("hexFill") ? cmp.getInteger("hexFill") : 0x1A1A1A;
	}
	
	public static void defaultColorSettings() {
	    TukMCReference.BOX_INNER_COLOR = 0x1A1A1A;
	    TukMCReference.RED = 26;
	    TukMCReference.GREEN = 26;
	    TukMCReference.BLUE = 26;
	}
	
	public static void registerOpenWebsite(String s) {
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		NBTTagCompound subCmp = !cmp.hasKey("websites") ? new NBTTagCompound() : cmp.getCompoundTag("websites");
		int visits = subCmp.hasKey(s) ? subCmp.getInteger(s) : 0;
		subCmp.setInteger(s, visits + 1);
		cmp.setCompoundTag("websites", subCmp);
		IOUtils.injectNBTToFile(cmp, cacheFile);
	}

	public static int getWebsiteViews(String s) {
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		if (!cmp.hasKey("websites")) return 0;
		NBTTagCompound subCmp = cmp.getCompoundTag("websites");
		return !subCmp.hasKey(s) ? 0 : subCmp.getInteger(s);
	}
}
