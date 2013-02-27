package maxpowa.tukmc;

import static maxpowa.tukmc.TukMCReference.BOX_EFFECT_OUTLINE_COLOR;
import static maxpowa.tukmc.TukMCReference.BOX_HIGHLIGHT_COLOR;
import static maxpowa.tukmc.TukMCReference.BOX_INNER_COLOR;
import static maxpowa.tukmc.TukMCReference.BOX_OUTLINE_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import ic2.api.IElectricItem;
import maxpowa.codebase.common.ColorCode;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;

public class IC2Integration {
	
	static RenderItem ir = new RenderItem();

	public static void renderTopBar(Minecraft mc, int width, int height) {
		renderNormalTopBar(mc, width, height);
	}

	private static void renderNormalTopBar(Minecraft mc, int width, int height) {
		FontRenderer fr = mc.fontRenderer;
		ItemStack boots = ((EntityPlayer)mc.thePlayer).inventory.armorInventory[0];
		ItemStack pants = ((EntityPlayer)mc.thePlayer).inventory.armorInventory[1];
		ItemStack chest = ((EntityPlayer)mc.thePlayer).inventory.armorInventory[2];
		ItemStack head = ((EntityPlayer)mc.thePlayer).inventory.armorInventory[3];
		drawDoubleOutlinedBox(width / 2 - 12, -1, 18, 31, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
		drawDoubleOutlinedBox(width / 2 + 12, -1, 18, 31, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
		drawDoubleOutlinedBox(width / 2 - 36, -1, 18, 31, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
		drawDoubleOutlinedBox(width / 2 + 36, -1, 18, 31, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
		if(boots != null) {
			RenderHelper.enableGUIStandardItemLighting();
			ir.renderItemIntoGUI(fr, mc.renderEngine, boots, width / 2 + 37, 14);
			RenderHelper.disableStandardItemLighting();
			int dmg = boots.getItemDamageForDisplay();
			int color = (int) Math.round(255.0D - dmg * 255.0D / boots.getMaxDamage());
			int shiftedColor = Config.get(Config.NODE_COLORBLIND_MODE) ? 0xFFFFFF : 255 - color << 16 | color << 8;
			renderSlots(boots, fr, 0, dmg, width /2 + 37, 14, shiftedColor);
		} 
		if (pants != null) {
			RenderHelper.enableGUIStandardItemLighting();
			ir.renderItemIntoGUI(fr, mc.renderEngine, pants, width / 2 + 13, 14);
			RenderHelper.disableStandardItemLighting();
			int dmg = pants.getItemDamageForDisplay();
			int color = (int) Math.round(255.0D - dmg * 255.0D / pants.getMaxDamage());
			int shiftedColor = Config.get(Config.NODE_COLORBLIND_MODE) ? 0xFFFFFF : 255 - color << 16 | color << 8;
			renderSlots(pants, fr, 0, dmg, width /2 + 13, 14, shiftedColor);
		} 
		if (chest != null) {
			RenderHelper.enableGUIStandardItemLighting();
			ir.renderItemIntoGUI(fr, mc.renderEngine, chest, width / 2 - 11, 14);
			RenderHelper.disableStandardItemLighting();
			int dmg = chest.getItemDamageForDisplay();
			int color = (int) Math.round(255.0D - dmg * 255.0D / chest.getMaxDamage());
			int shiftedColor = Config.get(Config.NODE_COLORBLIND_MODE) ? 0xFFFFFF : 255 - color << 16 | color << 8;
			renderSlots(chest, fr, 0, dmg, width /2 - 11, 14, shiftedColor);
		} 
		if (head != null) {
			RenderHelper.enableGUIStandardItemLighting();
			ir.renderItemIntoGUI(fr, mc.renderEngine, head, width / 2 - 35, 14);
			RenderHelper.disableStandardItemLighting();
			int dmg = head.getItemDamageForDisplay();
			int color = (int) Math.round(255.0D - dmg * 255.0D / head.getMaxDamage());
			int shiftedColor = Config.get(Config.NODE_COLORBLIND_MODE) ? 0xFFFFFF : 255 - color << 16 | color << 8;
			renderSlots(head, fr, 0, dmg, width /2 - 35, 14, shiftedColor);
		} 
	}

	public static void renderSlots(ItemStack stack, FontRenderer font, int offset, int dmg, int x, int y, int shiftedColor) {
		if (ModLoader.isModLoaded("IC2")) {
			renderIC2Slots(stack, font, offset, dmg, x, y, shiftedColor);
		} else {
			renderNormalSlots(stack, font, offset, dmg, x, y, shiftedColor);
		}
	}
	
	private static void renderNormalSlots(ItemStack stack, FontRenderer font, int offset, int dmg, int x, int y, int shiftedColor) {
		if (stack.isItemStackDamageable()) {
			String dmgStr = "" + (Config.get(Config.NODE_NUMERICAL_DAMAGE_DISPLAY) ? stack.getMaxDamage() - dmg + 1 : (stack.getItemDamage() == 0 ? 100 : Math.max(1, (stack.getMaxDamage() - stack.getItemDamage()) * 100 / stack.getMaxDamage())) + "%");
			offset = 6;
			int unbreakLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
			glPushMatrix();
			glScalef(0.5F, 0.5F, 0.5F);
			font.drawStringWithShadow(dmgStr, (x + 16 - font.getStringWidth(dmgStr) / 2) * 2, (y + 11) * 2, dmg == 0 ? 0xFFFFFF : shiftedColor);
			if (unbreakLvl > 0) font.drawStringWithShadow(ColorCode.PINK + "" + unbreakLvl, (x + 1) * 2, (y + 1) * 2, 0xFFFFFF);
			glScalef(1F, 1F, 1F);
			glPopMatrix();
		}
	}
	
	private static void renderIC2Slots(ItemStack stack, FontRenderer font, int offset, int dmg, int x, int y, int shiftedColor) {
		if (stack.isItemStackDamageable() && !(stack.getItem() instanceof IElectricItem)) {
			String dmgStr = "" + (Config.get(Config.NODE_NUMERICAL_DAMAGE_DISPLAY) ? stack.getMaxDamage() - dmg + 1 : (stack.getItemDamage() == 0 ? 100 : Math.max(1, (stack.getMaxDamage() - stack.getItemDamage()) * 100 / stack.getMaxDamage())) + "%");
			offset = 6;
			int unbreakLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
			glPushMatrix();
			glScalef(0.5F, 0.5F, 0.5F);
			font.drawStringWithShadow(dmgStr, (x + 16 - font.getStringWidth(dmgStr) / 2) * 2, (y + 11) * 2, dmg == 0 ? 0xFFFFFF : shiftedColor);
			if (unbreakLvl > 0) font.drawStringWithShadow(ColorCode.PINK + "" + unbreakLvl, (x + 1) * 2, (y + 1) * 2, 0xFFFFFF);
			glScalef(1F, 1F, 1F);
			glPopMatrix();
		} else if (stack.getItem() instanceof IElectricItem) {
	        int maxcharge = ((IElectricItem)stack.getItem()).getMaxCharge();
			int charge = stack.stackTagCompound.getInteger("charge");
			int cur = Math.round(((float)charge / maxcharge)*100);
			String dmgStr = "UNKNOWN";
			dmgStr = ("" + cur + "%");
			offset = 6;
			glPushMatrix();
			glScalef(0.5F, 0.5F, 0.5F);
			font.drawStringWithShadow(dmgStr, (x + 16 - font.getStringWidth(dmgStr) / 2) * 2, (y + 11) * 2, dmg == 0 ? 0xFFFFFF : shiftedColor);
			glScalef(1F, 1F, 1F);
			glPopMatrix();
		}
	}
	
	
	public static void drawDoubleOutlinedBox(int x, int y, int width, int height, int color, int outlineColor) {
		drawDoubleOutlinedBox(x, y, width, height, color, outlineColor, color);
	}

	public static void drawDoubleOutlinedBox(int x, int y, int width, int height, int color, int outlineColor, int outline2Color) {
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2, (y + height) * 2 + 2, color);
		drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1, (y + height) * 2 + 1, outlineColor);
		drawSolidRect(x * 2, y * 2, (x + width) * 2, (y + height) * 2, outline2Color);
		glPopMatrix();
	}

	public static void drawSolidRect(int vertex1, int vertex2, int vertex3, int vertex4, int color) {
		float zLevel = -90.0F;
		glPushMatrix();
		Color color1 = new Color(color);
		Tessellator tess = Tessellator.instance;
		glDisable(GL_TEXTURE_2D);
		tess.startDrawingQuads();
		tess.setColorOpaque(color1.getRed(), color1.getGreen(), color1.getBlue());
		tess.addVertex(vertex1, vertex4, zLevel);
		tess.addVertex(vertex3, vertex4, zLevel);
		tess.addVertex(vertex3, vertex2, zLevel);
		tess.addVertex(vertex1, vertex2, zLevel);
		tess.draw();
		glEnable(GL_TEXTURE_2D);
		glPopMatrix();
	}
	
}
