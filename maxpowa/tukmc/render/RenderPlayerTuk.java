package maxpowa.tukmc.render;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;

import java.awt.Color;
import java.util.List;

import maxpowa.codebase.common.CommonUtils;
import maxpowa.tukmc.util.Config;
import maxpowa.tukmc.util.TukMCReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RenderPlayerTuk extends RenderPlayer {

    @SuppressWarnings("unchecked")
    @Override
    protected void renderLivingLabel(EntityLivingBase par1EntityLivingBase, String par2Str, double par3, double par5, double par7, int par9) {
        // protected void renderLivingLabel(EntityPlayer par1EntityPlayer,
        // double par2, double par4, double par6) {
        if (Config.get(Config.NODE_DEFAULT_NAMEPLATE)) {
            super.renderLivingLabel(par1EntityLivingBase, par2Str, par3, par5,
                    par7, par9);
            return;
        }

        if (Minecraft.isGuiEnabled()
                && par1EntityLivingBase != renderManager.livingPlayer
                && par1EntityLivingBase instanceof EntityPlayer) {
            Minecraft mc = CommonUtils.getMc();
            boolean name = false;
            float var8 = 1.6F;
            float var9 = 0.016666668F * var8;
            par1EntityLivingBase.getDistanceSqToEntity(renderManager.livingPlayer);
            String var13 = par2Str;
            if (par2Str.equalsIgnoreCase(par1EntityLivingBase.getEntityName())) {
                var13 = par2Str
                        + " - "
                        + Math.round(par1EntityLivingBase
                                .getDistanceToEntity(mc.thePlayer)) + "m";
                name = true;
            } else {
                var13 = par2Str;
                name = false;
            }

            NetClientHandler var37 = mc.thePlayer.sendQueue;
            List<GuiPlayerInfo> var39 = var37.playerInfoList;
            GuiPlayerInfo var46 = null;
            for (GuiPlayerInfo info : var39)
                if (info.name.equals(par2Str)) {
                    var46 = info;
                }

            byte var49 = 4;
            if (var46 != null && name) {
                if (var46.responseTime < 0) {
                    var49 = 5;
                } else if (var46.responseTime < 150) {
                    var49 = 0;
                } else if (var46.responseTime < 300) {
                    var49 = 1;
                } else if (var46.responseTime < 600) {
                    var49 = 2;
                } else if (var46.responseTime < 1000) {
                    var49 = 3;
                } else {
                    var49 = 4;
                }
            }

            FontRenderer var14 = getFontRendererFromRenderManager();
            GL11.glPushMatrix();
            GL11.glTranslatef((float) par3 + 0.0F, (float) par5 + 2.5F,
                    (float) par7);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(-var9, -var9, var9);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glTranslatef(0.0F, 0.25F / var9, 0.0F);
            GL11.glDepthMask(false);
            if (!par1EntityLivingBase.isSneaking()) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }
            int var16 = var14.getStringWidth(var13) / 2;
            if (!par1EntityLivingBase.isSneaking()) {
                GL11.glDepthMask(true);
            }
            drawDoubleOutlinedBox(-var16 - 7,
                    par1EntityLivingBase.isPlayerSleeping() ? 50 : -1,
                    var16 * 2 + 18, 10, TukMCReference.BOX_INNER_COLOR,
                    TukMCReference.BOX_OUTLINE_COLOR);
            var14.drawStringWithShadow(var13,
                    -var14.getStringWidth(var13) / 2 - 6,
                    par1EntityLivingBase.isPlayerSleeping() ? 51 : 0, 0xFFFFFF);
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/gui/icons.png"));
            if (name) {
                drawTexturedModalRect(var16 - 1,
                        par1EntityLivingBase.isPlayerSleeping() ? 51 : -0, 0,
                        176 + var49 * 8, 10, 8);
            }
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            if (!par1EntityLivingBase.isSneaking()) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();

        } else {
            
        }
    }

    protected void sleepCheck(EntityLiving par1EntityLiving, double par2,
            double par4, double par6, String par8Str, float par9, double par10) {
        if (par1EntityLiving.isPlayerSleeping()) {
            this.renderLivingLabel(par1EntityLiving, par8Str, par2,
                    par4 - 1.5D, par6, 64);
        } else {
            this.renderLivingLabel(par1EntityLiving, par8Str, par2, par4, par6,
                    64);
        }
    }

    public void drawTexturedModalRect(int par1, int par2, int par3, int par4,
            int par5, int par6) {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV(par1 + 0, par2 + par6, 0, (par3 + 0) * var7,
                (par4 + par6) * var8);
        var9.addVertexWithUV(par1 + par5, par2 + par6, 0, (par3 + par5) * var7,
                (par4 + par6) * var8);
        var9.addVertexWithUV(par1 + par5, par2 + 0, 0, (par3 + par5) * var7,
                (par4 + 0) * var8);
        var9.addVertexWithUV(par1 + 0, par2 + 0, 0, (par3 + 0) * var7,
                (par4 + 0) * var8);
        var9.draw();
    }

    public void drawDoubleOutlinedBox(int x, int y, int width, int height,
            int color, int outlineColor) {
        glPushMatrix();
        glScalef(0.5F, 0.5F, 0.5F);
        drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2,
                (y + height) * 2 + 2, color);
        drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1,
                (y + height) * 2 + 1, outlineColor);
        drawSolidRect(x * 2, y * 2, (x + width) * 2, (y + height) * 2, color);
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
        tess.addVertex(vertex1, vertex4, 0F);
        tess.addVertex(vertex3, vertex4, 0F);
        tess.addVertex(vertex3, vertex2, 0F);
        tess.addVertex(vertex1, vertex2, 0F);
        tess.draw();
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

}
