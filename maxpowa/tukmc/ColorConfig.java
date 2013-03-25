package maxpowa.tukmc;

import java.util.ArrayList;

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
    public boolean autoApply = true;

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
		String applystr = "Autoapply " + ((autoApply) ? "enabled." : "disabled.");
		mc.fontRenderer.drawStringWithShadow(applystr, width/2-172+(mc.fontRenderer.getStringWidth(applystr)/2), height/2+50, 14737632);
		if (autoApply) {
			mod_TukMC.saveColorSettings();
		}
	}
	
	@Override
	public void initGui() {
		super.initGui();
		ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		int height = res.getScaledHeight();
		int width = res.getScaledWidth();
		this.buttonList.add(new GuiMenuSlider(0, (width/2)-151, height/2 - 22, "RED : " + TukMCReference.RED_INNER, 255));
		this.buttonList.add(new GuiMenuSlider(1, (width/2)-151, height/2, "GREEN : " + TukMCReference.GREEN_INNER, 255));
		this.buttonList.add(new GuiMenuSlider(2, (width/2)-151, height/2 + 22, "BLUE : " + TukMCReference.BLUE_INNER, 255));
		this.buttonList.add(new GuiMenuSlider(3, (width/2)+1, height/2 - 22, "RED : " + TukMCReference.RED_OUTER, 255));
		this.buttonList.add(new GuiMenuSlider(4, (width/2)+1, height/2, "GREEN : " + TukMCReference.GREEN_OUTER, 255));
		this.buttonList.add(new GuiMenuSlider(5, (width/2)+1, height/2 + 22, "BLUE : " + TukMCReference.BLUE_OUTER, 255));
		this.buttonList.add(new GuiTukButton(10, width/2-151, height/2 - 44, 74, 20, "Save"));
		this.buttonList.add(new GuiTukButton(12, width/2-75, height/2 - 44, 74, 20, "Apply"));
		this.buttonList.add(new GuiTukButton(11, width/2+1, height/2 - 44, 150, 20, "Default Values"));
		this.buttonList.add(new GuiTukButton(13, width/2-151, height/2 + 44, 150, 20, ""));
		this.buttonList.add(new GuiTukButton(14, width/2+1, height/2 + 44, 150, 20, "Exit"));
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == 10) {
			mod_TukMC.saveColorSettings();
            this.mc.displayGuiScreen(this.parentScreen);
		} else if (par1GuiButton.id == 11) {
			mod_TukMC.defaultColorSettings();
            this.mc.displayGuiScreen(this.parentScreen);
		} else if (par1GuiButton.id == 12) {
			mod_TukMC.saveColorSettings();
		} else if (par1GuiButton.id == 13) {
			autoApply = !autoApply;
		} else if (par1GuiButton.id == 14) {
			mod_TukMC.loadColorSettings();
			this.mc.displayGuiScreen(this.parentScreen);
		}
		super.actionPerformed(par1GuiButton);
	}
	
}
