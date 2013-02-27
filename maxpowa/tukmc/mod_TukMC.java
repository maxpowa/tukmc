package maxpowa.tukmc;

import java.io.File;


import maxpowa.codebase.common.EnumMaxpowaMods;
import maxpowa.codebase.common.IOUtils;
import maxpowa.codebase.common.MoarCore;
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

@Mod(modid = "tukmc_Vz", name = "TukMC", version = "Version [1.7] for 1.4.7")
public class mod_TukMC {

	public static File cacheFile;

	public static boolean spellcheckerEnabled = true;
	public static boolean closeOnFinish = false;
	public static boolean displayNotification = true;

	public static boolean shouldReopenChat = false;

	@Init
	public void onInit(FMLInitializationEvent event) {
		MoarCore.loadedMpMods.add(EnumMaxpowaMods.TUKMC.getAcronym());
		KeyBindingRegistry.registerKeyBinding(new KeyRegister());
		TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);
		cacheFile = IOUtils.getCacheFile(EnumMaxpowaMods.TUKMC);
		MinecraftForge.EVENT_BUS.register(new ChatListener());
		RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, new RenderPlayerTuk());

		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		spellcheckerEnabled = cmp.hasKey("spellcheckerEnabled") ? cmp.getBoolean("spellcheckerEnabled") : true;
		displayNotification = cmp.hasKey("displayNotification") ? cmp.getBoolean("displayNotification") : true;
		closeOnFinish = cmp.hasKey("closeOnFinish") ? cmp.getBoolean("closeOnFinish") : false;
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
