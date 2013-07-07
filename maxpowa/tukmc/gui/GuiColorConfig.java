package maxpowa.tukmc.gui;

import maxpowa.tukmc.mod_TukMC;
import maxpowa.tukmc.util.TukMCReference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiColorConfig extends GuiScreen {

    private GuiScreen parentScreen;
    public boolean autoApply = true;

    public GuiColorConfig(GuiScreen par1GuiScreen) {
        parentScreen = par1GuiScreen;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);
        ScaledResolution res = new ScaledResolution(mc.gameSettings,
                mc.displayWidth, mc.displayHeight);
        int height = res.getScaledHeight();
        int width = res.getScaledWidth();
        String applystr = "Autoapply " + (autoApply ? "enabled." : "disabled.");
        mc.fontRenderer.drawStringWithShadow(applystr, width / 2 - 172
                + mc.fontRenderer.getStringWidth(applystr) / 2,
                height / 2 + 50, 14737632);
        if (autoApply) {
            mod_TukMC.saveColorSettings();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution res = new ScaledResolution(mc.gameSettings,
                mc.displayWidth, mc.displayHeight);
        int height = res.getScaledHeight();
        int width = res.getScaledWidth();
        buttonList.add(new GuiMenuSlider(0, width / 2 - 151, height / 2 - 22,
                "RED : " + TukMCReference.RED_INNER, 255));
        buttonList.add(new GuiMenuSlider(1, width / 2 - 151, height / 2,
                "GREEN : " + TukMCReference.GREEN_INNER, 255));
        buttonList.add(new GuiMenuSlider(2, width / 2 - 151, height / 2 + 22,
                "BLUE : " + TukMCReference.BLUE_INNER, 255));
        buttonList.add(new GuiMenuSlider(3, width / 2 + 1, height / 2 - 22,
                "RED : " + TukMCReference.RED_OUTER, 255));
        buttonList.add(new GuiMenuSlider(4, width / 2 + 1, height / 2,
                "GREEN : " + TukMCReference.GREEN_OUTER, 255));
        buttonList.add(new GuiMenuSlider(5, width / 2 + 1, height / 2 + 22,
                "BLUE : " + TukMCReference.BLUE_OUTER, 255));
        buttonList.add(new GuiTukButton(10, width / 2 - 151, height / 2 - 44,
                74, 20, "Save"));
        buttonList.add(new GuiTukButton(12, width / 2 - 75, height / 2 - 44,
                74, 20, "Apply"));
        buttonList.add(new GuiTukButton(11, width / 2 + 1, height / 2 - 44,
                150, 20, "Default Values"));
        buttonList.add(new GuiTukButton(13, width / 2 - 151, height / 2 + 44,
                150, 20, ""));
        buttonList.add(new GuiTukButton(14, width / 2 + 1, height / 2 + 44,
                150, 20, "Exit"));
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 10) {
            mod_TukMC.saveColorSettings();
            mc.displayGuiScreen(parentScreen);
        } else if (par1GuiButton.id == 11) {
            mod_TukMC.defaultColorSettings();
            mc.displayGuiScreen(parentScreen);
        } else if (par1GuiButton.id == 12) {
            mod_TukMC.saveColorSettings();
        } else if (par1GuiButton.id == 13) {
            autoApply = !autoApply;
        } else if (par1GuiButton.id == 14) {
            mod_TukMC.loadColorSettings();
            mc.displayGuiScreen(parentScreen);
        }
        super.actionPerformed(par1GuiButton);
    }

}
