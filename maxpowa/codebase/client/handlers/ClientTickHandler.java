package maxpowa.codebase.client.handlers;

import java.util.EnumSet;

import org.lwjgl.opengl.GL11;

import maxpowa.codebase.client.CornerText;
import maxpowa.codebase.common.ColorCode;
import maxpowa.codebase.common.CommonUtils;
import maxpowa.codebase.common.IOUtils;
import maxpowa.codebase.common.MoarReference;
import maxpowa.codebase.common.MoarCore;
import net.minecraft.client.Minecraft;

import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.ServerData;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ClientTickHandler implements ITickHandler {

	public static int clientTicksElapsed = 0;
	public static int renderTicksElapsed = 0;

	static int serverWarnTicksElapsed = 0;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.RENDER))) onRenderTick(tickData);
		else if (type.equals(EnumSet.of(TickType.CLIENT))) onClientTick(tickData);
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER, TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "VazCore_Client";
	}

	public void onClientTick(Object... tickData) {
		++clientTicksElapsed;
	}

	public void onRenderTick(Object... tickData) {
		++renderTicksElapsed;

		CornerText.onTick((Float) tickData[0]);

		Minecraft mc = CommonUtils.getMc();
		GuiScreen screen = mc.currentScreen;

		if (screen != null && screen instanceof GuiConnecting) {
			GuiConnecting conn = (GuiConnecting) screen;
			ServerData data = mc.getServerData();
		}

		if (mc.theWorld != null && mc.getServerData() != null) {
			NBTTagCompound cmp = IOUtils.getTagCompoundInFile(MoarCore.cacheFile);
			cmp.setBoolean("hasConnectedToServer", true);
			IOUtils.injectNBTToFile(cmp, MoarCore.cacheFile);
		}

	}

}
