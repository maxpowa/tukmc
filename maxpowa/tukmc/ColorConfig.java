package maxpowa.tukmc;

import maxpowa.codebase.common.FormattingCode;
import maxpowa.tukmc.Config.Node;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.EnumOptions;
import net.minecraft.client.settings.GameSettings;

public class ColorConfig extends GuiScreen {
	
    private GuiScreen parentScreen;

	public ColorConfig(GuiScreen par1GuiScreen)
    {
        this.parentScreen = par1GuiScreen;
    }
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		int height = res.getScaledHeight();
		int width = res.getScaledWidth();
	}
	
	@Override
	public void initGui() {
		super.initGui();
		ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		int height = res.getScaledHeight();
		int width = res.getScaledWidth();
		this.controlList.add(new GuiMenuSlider(0, (width/2)-75, height/2 - 22, "RED : " + TukMCReference.RED, 255));
		this.controlList.add(new GuiMenuSlider(1, (width/2)-75, height/2, "GREEN : " + TukMCReference.GREEN, 255));
		this.controlList.add(new GuiMenuSlider(2, (width/2)-75, height/2 + 22, "BLUE : " + TukMCReference.BLUE, 255));
		this.controlList.add(new GuiButton(10, width/2-102, height/2 - 44, 100, 20, "Save"));
		this.controlList.add(new GuiButton(11, width/2+2, height/2 - 44, 100, 20, "Default"));
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == 10) {
			mod_TukMC.saveColorSettings();
            this.mc.displayGuiScreen(this.parentScreen);
		} else if (par1GuiButton.id == 11) {
			mod_TukMC.defaultColorSettings();
            this.mc.displayGuiScreen(this.parentScreen);
		}
		super.actionPerformed(par1GuiButton);
	}
	
}
