package maxpowa.tukmc.gui;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import maxpowa.codebase.common.FormattingCode;
import maxpowa.tukmc.util.Config;
import maxpowa.tukmc.util.TukMCReference;
import maxpowa.tukmc.util.Config.Node;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;

@Deprecated
public class GuiConfig extends GuiScreen {

    static List<String> names = Arrays.asList(Config.nodes.keySet().toArray(
            new String[Config.nodes.size()]));

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        int i = 0;
        for (String s : names) {
            Node node = Config.nodes.get(s);
            node.isEnabled();
            buttonList.add(new GuiSmallButton(i, width / 2 - 125, height / 2
                    - 150 + i * 15, 250, 15, node.getDisplayName()));
            ++i;
        }
        buttonList.add(new GuiButton(1337, 5, 5, 100, 20, "Colors..."));
        buttonList.add(new GuiButton(1338, 5, 30, 100, 20, "New Config"));
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);
        FontRenderer fr = mc.fontRenderer;
        ScaledResolution res = new ScaledResolution(mc.gameSettings,
                mc.displayWidth, mc.displayHeight);
        int height = res.getScaledHeight();
        int width = res.getScaledWidth();
        String configStr = FormattingCode.ITALICS + "TukMC Config";
        drawCenteredString(fr, configStr, width / 2, height / 2 - 160, 0xFFFFFF);
        int i = 0;
        for (String s : names) {
            Node node = Config.nodes.get(s);
            boolean enabled = node.isEnabled();
            drawOutlinedBox(width / 2 + 113, height / 2 - 85 + i * 15 - 60, 5,
                    5, enabled ? 0xFF00 : 0xFF0000,
                    TukMCReference.BOX_OUTLINE_COLOR);
            i++;
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id >= 0 && par1GuiButton.id <= names.size()) {
            String name = names.get(par1GuiButton.id);
            Node node = Config.nodes.get(name);
            node.set(!node.isEnabled());
            Config.saveNode(node);
        } else if (par1GuiButton.id == 1337) {
            mc.displayGuiScreen(new GuiColorConfig(this));
        } else if (par1GuiButton.id == 1338) {
            mc.displayGuiScreen(new GuiNewConfig());
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
