package maxpowa.tukmc.gui;

import java.awt.Color;
import java.util.List;

import maxpowa.codebase.common.FormattingCode;
import maxpowa.tukmc.mod_TukMC;
import maxpowa.tukmc.util.TukMCReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

public class GuiConfirmation extends GuiScreen {

    private FontRenderer fr;
    private String bodyText;
    private GuiScreen parent;
    private String action;

    public GuiConfirmation(Minecraft mc, String bodyText, String action,
            GuiScreen parent) {
        fr = mc.fontRenderer;
        this.bodyText = bodyText;
        this.parent = parent;
        this.action = action;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        buttonList.add(new GuiTukButton(100, width / 2 + 85, height / 2 - 73,
                15, 15, "X"));
        buttonList.add(new GuiTukButton(101, width / 2 - 101, height / 2 + 55,
                100, 20, "I understand."));
        buttonList.add(new GuiTukButton(102, width / 2 + 2, height / 2 + 55,
                98, 20, "Get me outta here!"));
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
        String title = FormattingCode.ITALICS + "Please confirm!";
        fr.drawString(title, width / 2 - fr.getStringWidth(title) / 2 - 9,
                height / 2 - 69, 0xFFFFFF);
        @SuppressWarnings("unchecked")
        List<String> bodyList = mc.fontRenderer.listFormattedStringToWidth(bodyText,
                175);
        int i = 0;
        for (Object o : bodyList) {
            String s = (String) o;
            fr.drawString(s, width / 2 - fr.getStringWidth(s) / 2, height / 2
                    - 51 + 10 * i, 0xFFFFFF);
            i++;
        }
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 100 || par1GuiButton.id == 102) {
            mc.displayGuiScreen(parent);
        } else if (par1GuiButton.id == 101) {
            if (action.equalsIgnoreCase("defaultchat")) {
                mod_TukMC.setDefaultChat(!mod_TukMC.defaultChat);
                mc.displayGuiScreen(parent);
            }
        }
        super.actionPerformed(par1GuiButton);
    }

    public void drawOutlinedBox(int x, int y, int width, int height, int color,
            int outlineColor) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2,
                (y + height) * 2 + 2, outlineColor);
        drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1,
                (y + height) * 2 + 1, color);
        GL11.glPopMatrix();
    }

    public void drawDoubleOutlinedBox(int x, int y, int width, int height,
            int color, int outlineColor) {
        drawDoubleOutlinedBox(x, y, width, height, color, outlineColor, color);
    }

    public void drawDoubleOutlinedBox(int x, int y, int width, int height,
            int color, int outlineColor, int outline2Color) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2,
                (y + height) * 2 + 2, color);
        drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1,
                (y + height) * 2 + 1, outlineColor);
        drawSolidRect(x * 2, y * 2, (x + width) * 2, (y + height) * 2,
                outline2Color);
        GL11.glPopMatrix();
    }

    public void drawSolidRect(int vertex1, int vertex2, int vertex3,
            int vertex4, int color) {
        GL11.glPushMatrix();
        Color color1 = new Color(color);
        Tessellator tess = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tess.startDrawingQuads();
        tess.setColorOpaque(color1.getRed(), color1.getGreen(),
                color1.getBlue());
        tess.addVertex(vertex1, vertex4, zLevel);
        tess.addVertex(vertex3, vertex4, zLevel);
        tess.addVertex(vertex3, vertex2, zLevel);
        tess.addVertex(vertex1, vertex2, zLevel);
        tess.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }
}
