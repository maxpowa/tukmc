package maxpowa.tukmc.gui;

import static maxpowa.tukmc.util.TukMCReference.BOX_INNER_COLOR;
import static maxpowa.tukmc.util.TukMCReference.BOX_OUTLINE_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;

import java.awt.Color;

import maxpowa.codebase.common.FormattingCode;
import maxpowa.tukmc.util.TukMCReference;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;

public class GuiSpecialChars extends GuiScreen {

    private static int pageNumber = 1;
    private static String allchars = "";
    private int pageCount;
    private String addedChars = "";
    public GuiChat parentScreen;
    private String inputFieldText = "";

    public GuiSpecialChars(GuiChat guiChat, String string) {
        addedChars = "";
        inputFieldText = string;
        parentScreen = guiChat;
        allchars = TukMCReference.otherEmotes;
//      char esc;
//      for (int i = 1; i <= 400; i++) {
//          if (i >= 161 && i <= 400) {
//              esc = (char) i;
//              allchars += "" + esc;
//          }
//      }
        pageCount = (int) (Math.ceil(allchars.length() / 135) + 1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        buttonList.clear();
        super.initGui();

        int pn = pageNumber;
        int n = pn * 135 - 135;
        // Begin loop for columns
        char str;
        for (int i = 1; i < 15; i++) {
            // Begin loop for rows
            for (int j = 1; j < 9; j++) {
                if (n < allchars.length()) {
                    str = allchars.charAt(n);
                    buttonList.add(new GuiTukButton(n,
                            width / 2 - 135 + 17 * i, height / 2 - 75 + 17 * j,
                            15, 15, str + ""));
                    n++;
                }
            }
        }
        buttonList.add(new GuiTukButton(14096, width / 2 - 120, height / 2 - 84,
                15, 16, "<"));
        buttonList.add(new GuiTukButton(14097, width / 2 + 105, height / 2 - 84,
                15, 16, ">"));
        buttonList.add(new GuiTukButton(14098, width / 2 + 107,
                height / 2 - 100, 11, 11, "x"));
        buttonList.add(new GuiTukButton(14099, width / 2 + 127, height / 2 + 15,
                75, 20, "Backspace"));
        buttonList.add(new GuiTukButton(14100, width / 2 + 127, height / 2 - 15,
                75, 20, "Insert in Chat"));
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);
        this.initGui();
        FontRenderer fr = mc.fontRenderer;
        ScaledResolution res = new ScaledResolution(mc.gameSettings,
                mc.displayWidth, mc.displayHeight);
        int height = res.getScaledHeight();
        int width = res.getScaledWidth();
        String charStr = "Characters to be added: "
                + (addedChars.equalsIgnoreCase("") ? "None" : addedChars);
        drawDoubleOutlinedBox(width / 2 - 100, height / 2 - 86, 200, 20,
                BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
        drawDoubleOutlinedBox(width / 2 - 120, height / 2 + 79, 240, 12,
                BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
        this.drawCenteredString(fr, FormattingCode.ITALICS
                + "TukMC Character List / Page " + pageNumber + " of "
                + pageCount, width / 2, height / 2 - 80, 0xFFFFFF);
        this.drawCenteredString(fr, charStr, width / 2, height / 2 + 81,
                0xFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id >= 0 && par1GuiButton.id < allchars.length()) {
            if (addedChars.length() <= 11) {
                addedChars += allchars.charAt(par1GuiButton.id) + "";
            }
        } else if (par1GuiButton.id == 14096) {
            if (pageNumber > 1) {
                pageNumber--;
            } else {
                pageNumber = pageCount;
            }
        } else if (par1GuiButton.id == 14097) {
            if (pageNumber < pageCount) {
                pageNumber++;
            } else {
                pageNumber = 1;
            }
        } else if (par1GuiButton.id == 14098) {
            mc.displayGuiScreen(parentScreen);
        } else if (par1GuiButton.id == 14099) {
            if (addedChars.length() > 0) {
                addedChars = addedChars.substring(0, addedChars.length() - 1);
            }
        } else if (par1GuiButton.id == 14100) {
            mc.displayGuiScreen(parentScreen);
            if (addedChars.length() > 0) {
                parentScreen.getInputField().writeText(inputFieldText + addedChars);
            }
        } else {
            // aintdoingnuthin
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
