package maxpowa.tukmc.gui;

import static maxpowa.tukmc.util.TukMCReference.BOX_INNER_COLOR;
import static maxpowa.tukmc.util.TukMCReference.BOX_OUTLINE_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import ic2.api.item.IElectricItem;

import java.awt.Color;

import maxpowa.codebase.common.ColorCode;
import maxpowa.tukmc.util.Config;
import net.machinemuse.api.electricity.MuseElectricItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Loader;

public class ModCompat {

    static RenderItem ir = new RenderItem();

    public static void renderTopBar(Minecraft mc, int width, int yoffset) {
        renderNormalTopBar(mc, width, yoffset);
    }

    private static void renderNormalTopBar(Minecraft mc, int width, int yoffset) {
        FontRenderer fr = mc.fontRenderer;
        ItemStack boots = ((EntityPlayer) mc.thePlayer).inventory.armorInventory[0];
        ItemStack pants = ((EntityPlayer) mc.thePlayer).inventory.armorInventory[1];
        ItemStack chest = ((EntityPlayer) mc.thePlayer).inventory.armorInventory[2];
        ItemStack head = ((EntityPlayer) mc.thePlayer).inventory.armorInventory[3];
        drawDoubleOutlinedBox(width / 2 - 22, -1, 18, 31 - yoffset,
                BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
        drawDoubleOutlinedBox(width / 2 + 2, -1, 18, 31 - yoffset,
                BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
        drawDoubleOutlinedBox(width / 2 - 46, -1, 18, 31 - yoffset,
                BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
        drawDoubleOutlinedBox(width / 2 + 26, -1, 18, 31 - yoffset,
                BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
        if (boots != null) {
            RenderHelper.enableGUIStandardItemLighting();
            ir.renderItemAndEffectIntoGUI(fr, mc.renderEngine, boots,
                    width / 2 + 27, 14 - yoffset);
            RenderHelper.disableStandardItemLighting();
            int dmg = boots.getItemDamageForDisplay();
            int color = (int) Math.round(255.0D - dmg * 255.0D
                    / boots.getMaxDamage());
            int shiftedColor = Config.get(Config.NODE_COLORBLIND_MODE) ? 0xFFFFFF
                    : 255 - color << 16 | color << 8;
            renderSlots(boots, fr, 0, dmg, width / 2 + 27, 14 - yoffset,
                    shiftedColor);
        }
        if (pants != null) {
            RenderHelper.enableGUIStandardItemLighting();
            ir.renderItemAndEffectIntoGUI(fr, mc.renderEngine, pants,
                    width / 2 + 3, 14 - yoffset);
            RenderHelper.disableStandardItemLighting();
            int dmg = pants.getItemDamageForDisplay();
            int color = (int) Math.round(255.0D - dmg * 255.0D
                    / pants.getMaxDamage());
            int shiftedColor = Config.get(Config.NODE_COLORBLIND_MODE) ? 0xFFFFFF
                    : 255 - color << 16 | color << 8;
            renderSlots(pants, fr, 0, dmg, width / 2 + 3, 14 - yoffset,
                    shiftedColor);
        }
        if (chest != null) {
            RenderHelper.enableGUIStandardItemLighting();
            ir.renderItemAndEffectIntoGUI(fr, mc.renderEngine, chest,
                    width / 2 - 21, 14 - yoffset);
            RenderHelper.disableStandardItemLighting();
            int dmg = chest.getItemDamageForDisplay();
            int color = (int) Math.round(255.0D - dmg * 255.0D
                    / chest.getMaxDamage());
            int shiftedColor = Config.get(Config.NODE_COLORBLIND_MODE) ? 0xFFFFFF
                    : 255 - color << 16 | color << 8;
            renderSlots(chest, fr, 0, dmg, width / 2 - 21, 14 - yoffset,
                    shiftedColor);
        }
        if (head != null) {
            RenderHelper.enableGUIStandardItemLighting();
            ir.renderItemAndEffectIntoGUI(fr, mc.renderEngine, head,
                    width / 2 - 45, 14 - yoffset);
            RenderHelper.disableStandardItemLighting();
            int dmg = head.getItemDamageForDisplay();
            int color = (int) Math.round(255.0D - dmg * 255.0D
                    / head.getMaxDamage());
            int shiftedColor = Config.get(Config.NODE_COLORBLIND_MODE) ? 0xFFFFFF
                    : 255 - color << 16 | color << 8;
            renderSlots(head, fr, 0, dmg, width / 2 - 45, 14 - yoffset,
                    shiftedColor);
        }
    }

    public static void renderSlots(ItemStack stack, FontRenderer font,
            int offset, int dmg, int x, int y, int shiftedColor) {
        boolean flag = Minecraft.getMinecraft().fontRenderer.getUnicodeFlag();
        Minecraft.getMinecraft().fontRenderer.setUnicodeFlag(false);
        if (Loader.isModLoaded("IC2")) {
            renderIC2Slots(stack, font, offset, dmg, x, y, shiftedColor);
        } else {
            renderNormalSlots(stack, font, offset, dmg, x, y, shiftedColor);
        }
        Minecraft.getMinecraft().fontRenderer.setUnicodeFlag(flag);
    }

    private static void renderNormalSlots(ItemStack stack, FontRenderer font,
            int offset, int dmg, int x, int y, int shiftedColor) {
        if (stack.isItemStackDamageable()) {
            String dmgStr = "";
            if (Config.get(Config.NODE_NUMERICAL_DAMAGE_DISPLAY)) {
            	dmgStr = (stack.getMaxDamage() - dmg + 1)+"";
            } else {
            	dmgStr = (stack.getItemDamage() == 0 ? 100 : Math.max(1, (stack.getMaxDamage() - stack.getItemDamage()) * 100 / stack.getMaxDamage())) + "%";
            }
            int unbreakLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("InfiTool")) {
            	NBTTagCompound toolnbt = stack.getTagCompound().getCompoundTag("InfiTool");
            	if (!toolnbt.getBoolean("Broken"))
            		dmgStr = Math.round(toolnbt.getInteger("Damage") / toolnbt.getInteger("TotalDurability"))+"";
            }
            glPushMatrix();
            glScalef(0.5F, 0.5F, 0.5F);
            font.drawStringWithShadow(dmgStr,
                    (x + 16 - font.getStringWidth(dmgStr) / 2) * 2,
                    (y + 11) * 2, shiftedColor);
            if (unbreakLvl > 0) {
                font.drawStringWithShadow(ColorCode.PINK + "" + unbreakLvl,
                        (x + 1) * 2, (y + 1) * 2, 0xFFFFFF);
            }
            glScalef(1F, 1F, 1F);
            glPopMatrix();
        } else if (stack.stackSize > 1 && stack != null) {
            String s1 = String.valueOf(stack.stackSize);
            glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            glScalef(0.5F, 0.5F, 0.5F);
            font.drawStringWithShadow(s1,
                    (x + 16 - font.getStringWidth(s1) / 2) * 2, (y + 11) * 2,
                    16777215);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            glPopMatrix();
        }
    }

    private static void renderIC2Slots(ItemStack stack, FontRenderer font,
            int offset, int dmg, int x, int y, int shiftedColor) {
        if (stack.isItemStackDamageable()
                && !(stack.getItem() instanceof IElectricItem)) {
            String dmgStr = ""
                    + (Config.get(Config.NODE_NUMERICAL_DAMAGE_DISPLAY) ? stack
                            .getMaxDamage() - dmg + 1
                            : (stack.getItemDamage() == 0 ? 100 : Math.max(
                                    1,
                                    (stack.getMaxDamage() - stack
                                            .getItemDamage())
                                            * 100
                                            / stack.getMaxDamage()))
                                    + "%");
            int unbreakLvl = EnchantmentHelper.getEnchantmentLevel(
                    Enchantment.unbreaking.effectId, stack);
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("InfiTool")) {
                NBTTagCompound toolnbt = stack.getTagCompound().getCompoundTag("InfiTool");
                if (!toolnbt.getBoolean("Broken"))
                    dmgStr = Math.round(toolnbt.getInteger("Damage") / toolnbt.getInteger("TotalDurability"))+"";
            }
            glPushMatrix();
            glScalef(0.5F, 0.5F, 0.5F);
            font.drawStringWithShadow(dmgStr,
                    (x + 16 - font.getStringWidth(dmgStr) / 2) * 2,
                    (y + 11) * 2, dmg == 0 ? 0xFFFFFF : shiftedColor);
            if (unbreakLvl > 0) {
                font.drawStringWithShadow(ColorCode.PINK + "" + unbreakLvl,
                        (x + 1) * 2, (y + 1) * 2, 0xFFFFFF);
            }
            glScalef(1F, 1F, 1F);
            glPopMatrix();
        } else if (stack.getItem() instanceof IElectricItem) {
            int maxcharge = ((IElectricItem) stack.getItem())
                    .getMaxCharge(stack);
            String dmgStr;
            try {
                int charge = stack.stackTagCompound.getInteger("charge");
                int cur = Math.round((float) charge / maxcharge * 100);
                dmgStr = "" + cur + "%";
            } catch (Exception ex) {
                dmgStr = "";
            }
            glPushMatrix();
            glScalef(0.5F, 0.5F, 0.5F);
            font.drawStringWithShadow(dmgStr,
                    (x + 16 - font.getStringWidth(dmgStr) / 2) * 2,
                    (y + 11) * 2, dmg == 0 ? 0xFFFFFF : shiftedColor);
            glScalef(1F, 1F, 1F);
            glPopMatrix();
        } else if (Loader.isModLoaded("mmPowersuits")) {
            if (stack.getItem() instanceof MuseElectricItem) {
                double maxcharge = ((MuseElectricItem) stack.getItem())
                        .getMaxEnergy(stack);
                String dmgStr;
                try {
                    double charge = ((MuseElectricItem) stack.getItem()).getCurrentEnergy(stack);
                    int cur = Math.round((float) (charge / maxcharge * 100));
                    dmgStr = "" + cur + "%";
                } catch (Exception ex) {
                    dmgStr = "";
                }
                glPushMatrix();
                glScalef(0.5F, 0.5F, 0.5F);
                font.drawStringWithShadow(dmgStr,
                        (x + 16 - font.getStringWidth(dmgStr) / 2) * 2,
                        (y + 11) * 2, dmg == 0 ? 0xFFFFFF : shiftedColor);
                glScalef(1F, 1F, 1F);
                glPopMatrix();
            }
        } else if (stack.stackSize > 1 && stack != null) {
            String s1 = stack.stackSize+"";
            glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            glScalef(0.5F, 0.5F, 0.5F);
            font.drawStringWithShadow(s1,
                    (x + 16 - font.getStringWidth(s1) / 2) * 2, (y + 11) * 2,
                    16777215);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            glPopMatrix();
        }
    }

    public static void drawDoubleOutlinedBox(int x, int y, int width,
            int height, int color, int outlineColor) {
        drawDoubleOutlinedBox(x, y, width, height, color, outlineColor, color);
    }

    public static void drawDoubleOutlinedBox(int x, int y, int width,
            int height, int color, int outlineColor, int outline2Color) {
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

    public static void drawSolidRect(int vertex1, int vertex2, int vertex3,
            int vertex4, int color) {
        float zLevel = -90.0F;
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
