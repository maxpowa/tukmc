package maxpowa.tukmc;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import maxpowa.codebase.common.CommonUtils;
import net.minecraft.client.Minecraft;

import net.minecraft.client.settings.KeyBinding;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeyRegister extends KeyHandler {

	public static KeyBinding showTooltipKB = new KeyBinding("TukMC Show Tooltip", Keyboard.KEY_DELETE);
	public static KeyBinding openConfigKB = new KeyBinding("TukMC Open Config", Keyboard.KEY_K);

	public KeyRegister() {
		super(new KeyBinding[] { showTooltipKB, openConfigKB }, new boolean[] { true, false });
	}

	@Override
	public String getLabel() {
		return "TukMC";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		Minecraft mc = CommonUtils.getMc();
		if (kb.keyCode == openConfigKB.keyCode && mc.currentScreen == null) mc.displayGuiScreen(new GuiConfig());
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.allOf(TickType.class);
	}

}
