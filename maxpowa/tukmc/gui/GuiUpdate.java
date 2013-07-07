package maxpowa.tukmc.gui;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;

import java.awt.Color;
import java.net.URI;
import java.util.List;

import maxpowa.codebase.common.FormattingCode;
import maxpowa.tukmc.mod_TukMC;
import maxpowa.tukmc.util.TukMCReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;

public class GuiUpdate extends GuiScreen {

    private FontRenderer fr;
    private boolean refresh = false;

    public GuiUpdate(Minecraft mc, boolean update) {
        fr = mc.fontRenderer;
        refresh = update;
        mod_TukMC.updatePopup = false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        if (refresh) {
            mod_TukMC.getVersion();
        }
        buttonList.add(new GuiTukButton(100, width / 2 + 85, height / 2 - 73,
                15, 15, "X"));
        buttonList.add(new GuiTukButton(101, width / 2 - 101, height / 2 + 55,
                139, 20, "Take me there, kindly sir!"));
        //buttonList.add(new GuiTukButton(103, width / 2 - 69, height / 2 + 55, 107, 20, "Download Now!"));
        buttonList.add(new GuiTukButton(102, width / 2 + 42, height / 2 + 55,
                58, 20, "No thanks"));
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);
        drawDoubleOutlinedBox(width / 2 - 101, height / 2 - 55, 201, 107,
                TukMCReference.BOX_INNER_COLOR,
                TukMCReference.BOX_OUTLINE_COLOR);
        drawDoubleOutlinedBox(width / 2 - 101, height / 2 - 73, 183, 15,
                TukMCReference.BOX_INNER_COLOR,
                TukMCReference.BOX_OUTLINE_COLOR);
        String title = FormattingCode.ITALICS
                + "There is a new version of TukMC!";
        fr.drawString(title, width / 2 - fr.getStringWidth(title) / 2 - 9,
                height / 2 - 69, 0xFFFFFF);
        if (mod_TukMC.updateText != null && mod_TukMC.updateVersion != null
                && mod_TukMC.updateMCVersion != null) {
            String vercompare = "Current: " + mod_TukMC.updateVersion
                    + " for Minecraft " + mod_TukMC.updateMCVersion;
            fr.drawString(vercompare, width / 2 - fr.getStringWidth(vercompare)
                    / 2, height / 2 - 51, 0xFFFFFF);
            String relnotetitle = FormattingCode.BOLD + "Release Notes:";
            fr.drawString(relnotetitle,
                    width / 2 - fr.getStringWidth(relnotetitle) / 2,
                    height / 2 - 37, 0xFFFFFF);
            @SuppressWarnings("rawtypes")
            List updateText = mc.fontRenderer.listFormattedStringToWidth(
                    mod_TukMC.updateText, 200);
            int i = 0;
            for (Object o : updateText) {
                String s = (String) o;
                fr.drawString(s, width / 2 - fr.getStringWidth(s) / 2, height
                        / 2 - 26 + 10 * i, 0xFFFFFF);
                i++;
            }
        } else {
            String retrievingUpdates = "Retrieving updates...";
            if (mod_TukMC.updateCheckerErrorStatus) {
                retrievingUpdates = "Update check failed.";
                String retrievingUpdates2 = "Please check log for more info.";
                fr.drawString(retrievingUpdates2,
                        width / 2 - fr.getStringWidth(retrievingUpdates2) / 2,
                        height / 2 - 39, 0xFFFFFF);
            }
            fr.drawString(retrievingUpdates,
                    width / 2 - fr.getStringWidth(retrievingUpdates) / 2,
                    height / 2 - 51, 0xFFFFFF);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 100 || par1GuiButton.id == 102) {
            mc.displayGuiScreen(null);
        } else if (par1GuiButton.id == 101) {
            try {
                URI tukmcurl = new URI("http://goo.gl/XRqI5");
                Class oclass = Class.forName("java.awt.Desktop");
                Object object = oclass.getMethod("getDesktop", new Class[0])
                        .invoke((Object) null, new Object[0]);
                oclass.getMethod("browse", new Class[] { URI.class }).invoke(
                        object, new Object[] { tukmcurl });
            } catch (Throwable throwable) {
                System.out
                        .println("Failed opening browser, please manually open http://goo.gl/XRqI5 in your web browser.");
            }
        }
        super.actionPerformed(par1GuiButton);
    }

    public void drawOutlinedBox(int x, int y, int width, int height, int color,
            int outlineColor) {
        glPushMatrix();
        glScalef(0.5F, 0.5F, 0.5F);
        drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2,
                (y + height) * 2 + 2, outlineColor);
        drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1,
                (y + height) * 2 + 1, color);
        glPopMatrix();
    }

    public void drawDoubleOutlinedBox(int x, int y, int width, int height,
            int color, int outlineColor) {
        drawDoubleOutlinedBox(x, y, width, height, color, outlineColor, color);
    }

    public void drawDoubleOutlinedBox(int x, int y, int width, int height,
            int color, int outlineColor, int outline2Color) {
        glPushMatrix();
        glScalef(0.5F, 0.5F, 0.5F);
        drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2,
                (y + height) * 2 + 2, color);
        drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1,
                (y + height) * 2 + 1, outlineColor);
        drawSolidRect(x * 2, y * 2, (x + width) * 2, (y + height) * 2,
                outline2Color);
        glPopMatrix();
    }

    public void drawSolidRect(int vertex1, int vertex2, int vertex3,
            int vertex4, int color) {
        glPushMatrix();
        Color color1 = new Color(color);
        Tessellator tess = Tessellator.instance;
        glDisable(GL_TEXTURE_2D);
        tess.startDrawingQuads();
        tess.setColorOpaque(color1.getRed(), color1.getGreen(),
                color1.getBlue());
        tess.addVertex(vertex1, vertex4, zLevel);
        tess.addVertex(vertex3, vertex4, zLevel);
        tess.addVertex(vertex3, vertex2, zLevel);
        tess.addVertex(vertex1, vertex2, zLevel);
        tess.draw();
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }
}
