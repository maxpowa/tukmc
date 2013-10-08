package maxpowa.tukmc.gui;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;

import java.awt.Color;

import maxpowa.tukmc.util.TukMCReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiTukButton extends GuiButton {

    int BOX_OUTLINE_COLOR = TukMCReference.BOX_OUTLINE_COLOR;
    int BOX_INNER_COLOR = TukMCReference.BOX_INNER_COLOR;

    public GuiTukButton(int par1, int par2, int par3, int par4, int par5,
            String par6Str) {
        super(par1, par2, par3, par4, par5, par6Str);
    }

    public GuiTukButton(int par1, int par2, int par3, int par4, int par5,
            String par6Str, int outlineColor, int innerColor) {
        super(par1, par2, par3, par4, par5, par6Str);
        BOX_INNER_COLOR = innerColor;
        BOX_OUTLINE_COLOR = outlineColor;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
        if (drawButton) {
            FontRenderer fontrenderer = par1Minecraft.fontRenderer;
            par1Minecraft.getTextureManager().bindTexture(new ResourceLocation("textures/gui/widgets.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            field_82253_i = par2 >= xPosition && par3 >= yPosition
                    && par2 < xPosition + width && par3 < yPosition + height;
            int color = 0;
            this.getHoverState(field_82253_i);
            if (!enabled) {
                color = Color.getColor(null, BOX_INNER_COLOR).darker().darker()
                        .getRGB();
            } else if (!field_82253_i) {
                color = TukMCReference.BOX_INNER_COLOR;
            } else {
                color = Color.getColor(null, BOX_INNER_COLOR).brighter()
                        .brighter().getRGB();
            }
            this.drawDoubleOutlinedBox(xPosition, yPosition, width, height,
                    color, BOX_OUTLINE_COLOR);
            // this.drawSolidRect(this.xPosition + this.width / 2,
            // this.yPosition, this.width / 2, this.height, color);
            this.mouseDragged(par1Minecraft, par2, par3);
            int l = 0xFFFFFF;

            if (!enabled) {
                l = -6250336;
            } else if (field_82253_i) {
                l = 0xFFFFFF;
            }

            this.drawCenteredString(fontrenderer, displayString, xPosition
                    + width / 2, yPosition + (height - 8) / 2, l);
        }
    }

    public boolean isMouseOver() {
        if (this.getHoverState(field_82253_i) == 2)
            return true;
        return false;
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
