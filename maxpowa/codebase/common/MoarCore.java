package maxpowa.codebase.common;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;


import maxpowa.codebase.client.handlers.ClientTickHandler;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;

public class MoarCore {

	public static File cacheFile;
	public static Set<String> loadedMpMods = new TreeSet();

	@PreInit
	public void onPreInit(FMLPreInitializationEvent event) {
		IOUtils.initFiles();
	}

	@Init
	public void onInit(FMLInitializationEvent event) {
		if (CommonUtils.getSide().isClient()) clientInit();
		else serverInit();
	}

	public void clientInit() {
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
		cacheFile = IOUtils.getCacheFile(EnumMaxpowaMods.MOARCORE);
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(cacheFile);
		if (!cmp.hasKey("hasConnectedToServer")) cmp.setBoolean("hasConnectedToServer", false);
		IOUtils.injectNBTToFile(cmp, cacheFile);
	}

	public void serverInit() {

	}
}
