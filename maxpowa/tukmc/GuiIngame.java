package maxpowa.tukmc;

import static maxpowa.tukmc.TukMCReference.BOX_EFFECT_OUTLINE_COLOR;
import static maxpowa.tukmc.TukMCReference.BOX_HIGHLIGHT_COLOR;
import static maxpowa.tukmc.TukMCReference.BOX_INNER_COLOR;
import static maxpowa.tukmc.TukMCReference.BOX_OUTLINE_COLOR;
import static maxpowa.tukmc.TukMCReference.MC_VERSION;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import maxpowa.codebase.client.ClientUtils;
import maxpowa.codebase.common.ColorCode;
import maxpowa.codebase.common.CommonUtils;
import maxpowa.codebase.common.FormattingCode;
import maxpowa.tukmc.McMMOIntegration.LevelUpData;
import maxpowa.tukmc.McMMOIntegration.SkillData;
import maxpowa.tukmc.McMMOIntegration.SkillData.UsageType;
import net.minecraft.client.Minecraft;

import net.minecraft.block.Block;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.util.Direction;
import net.minecraft.util.FoodStats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StatCollector;
import net.minecraft.client.renderer.Tessellator;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeHooks;

public class GuiIngame extends net.minecraft.client.gui.GuiIngame {

	private long rendersElapsed = 0;
	private Minecraft mc;
	private String recordPlaying = "";
	private int recordPlayingUpFor = 0;
	private boolean recordIsPlaying = false;
	private GuiNewChat presistentChatGui;
	private EntityPlayer p;
	private World world;
	private int smoothHP = 0;
    private RenderBlocks itemRenderBlocks = new RenderBlocks();
    private static int update = 0;

	public GuiIngame() {
		super(CommonUtils.getMc());
		mc = CommonUtils.getMc();
		presistentChatGui = new GuiNewChat(mc);
	}

	@Override
	public void renderGameOverlay(float par1, boolean par2, int par3, int par4) {
		++rendersElapsed;
		ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		int height = res.getScaledHeight();
		int width = res.getScaledWidth();
		FontRenderer fr = mc.fontRenderer;
		RenderItem ir = new RenderItem();
		mc.entityRenderer.setupOverlayRendering();
		glEnable(GL_BLEND);
		if (Minecraft.isFancyGraphicsEnabled()) renderVignette(mc.thePlayer.getBrightness(par1), width, height);
		else glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		ItemStack head = mc.thePlayer.inventory.armorItemInSlot(3);
		if (mc.gameSettings.thirdPersonView == 0 && head != null && head.itemID == Block.pumpkin.blockID) renderPumpkinBlur(width, height);

		if (!hasPotion(Potion.confusion)) {
			float portalTime = mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * par1;

			if (portalTime > 0.0F) renderPortalOverlay(portalTime, width, height);
		}

		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		zLevel = -90.0F;
		if (Config.get(Config.NODE_BOTTOM_ADORNMENTS)) {
			drawDoubleOutlinedBox(6, height - 98, 5, 5, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			drawDoubleOutlinedBox(width - 10, height - 98, 5, 5, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			drawOutlinedBox(50, height - 13, width - 100, 1, BOX_OUTLINE_COLOR, BOX_INNER_COLOR);
			drawOutlinedBox(8, height - 13, 40, 1, BOX_OUTLINE_COLOR, BOX_INNER_COLOR);
			drawOutlinedBox(8, height - 92, 1, 80, BOX_OUTLINE_COLOR, BOX_INNER_COLOR);
			drawOutlinedBox(width - 48, height - 13, 40, 1, BOX_OUTLINE_COLOR, BOX_INNER_COLOR);
			drawOutlinedBox(width - 8, height - 92, 1, 80, BOX_OUTLINE_COLOR, BOX_INNER_COLOR);
			glPushMatrix();
			glScalef(0.5F, 0.5F, 0.5F);
			drawSolidRect(15, height * 2 - 27, 23, height * 2 - 23, BOX_OUTLINE_COLOR);
			drawSolidRect(width * 2 - 18, height * 2 - 27, width * 2 - 13, height * 2 - 23, BOX_OUTLINE_COLOR);
			drawSolidRect(14, height * 2 - 186, 20, height * 2 - 185, BOX_OUTLINE_COLOR);
			drawSolidRect(width * 2 - 18, height * 2 - 186, width * 2 - 10, height * 2 - 185, BOX_OUTLINE_COLOR);
			glPopMatrix();
		}

		if (Config.get(Config.NODE_ITEMS_BACKGROUND)) drawDoubleOutlinedBox(width / 2 - 90, height - 22, 180, 20, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);

		InventoryPlayer inv = mc.thePlayer.inventory;
		for (int i = 0; i < 9; ++i) {
			int i1 = width / 2 - 88 + i * 20;
			int i2 = height - 20;
			boolean isHighlight = inv.currentItem == i;
			boolean isSlot = inv.mainInventory[i] != null;

			if (isSlot) drawDoubleOutlinedBox(i1, i2, 16, 16, isHighlight ? BOX_HIGHLIGHT_COLOR : BOX_INNER_COLOR, inv.mainInventory[i].hasEffect() && !isHighlight ? BOX_EFFECT_OUTLINE_COLOR : BOX_OUTLINE_COLOR);
			else if (isHighlight) if (!Config.get(Config.NODE_ITEMS_BACKGROUND)) drawDoubleOutlinedBox(i1 + 2, i2 + 2, 12, 12, BOX_INNER_COLOR, BOX_OUTLINE_COLOR, BOX_HIGHLIGHT_COLOR);
			else drawDoubleOutlinedBox(i1 + 1, i2 + 1, 14, 14, BOX_HIGHLIGHT_COLOR, BOX_HIGHLIGHT_COLOR);
		}
		glEnable(GL_RESCALE_NORMAL);
		RenderHelper.enableGUIStandardItemLighting();
		glDisable(GL_BLEND);
		for (int i = 0; i < 9; ++i) {
			int i1 = width / 2 - 88 + i * 20;
			int i2 = height - 20;

			renderSlot(i, i1, i2, par1, fr);
		}
		RenderHelper.disableStandardItemLighting();
		glDisable(GL_RESCALE_NORMAL);
		boolean shouldDrawHUD = mc.playerController.shouldDrawHUD();

		if (Config.get(Config.NODE_CUSTOM_BARS)) {
			if (shouldDrawHUD) {
				drawDoubleOutlinedBox(width / 2 - 90, height - 42, 180, 10, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				drawDoubleOutlinedBox(width / 2 - 90, height - 29, 80, 4, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				int healthBottom = hasPotion(Potion.regeneration) ? 0xd82424 : 0;
				int healthTop = hasPotion(Potion.wither) ? BOX_INNER_COLOR : 0x901414;
				if (hasPotion(Potion.poison)) healthBottom = 0x375d12;
				int hp = mc.thePlayer.getHealth();
				int food = mc.thePlayer.getFoodStats().getFoodLevel();
				int hitp = (int) Math.round(((double)hp / mc.thePlayer.getMaxHealth())*180);
				drawSolidGradientRect(width / 2 - 90, height - 42, hitp, 10, healthBottom, healthTop);
				int foodHeal = 0;
				boolean overkill = false;
				if (food != 20) {
					int barWidth = 0;
					ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
					if (stack != null) {
						Item item = stack.getItem();
						if (item != null && item instanceof ItemFood) {
							foodHeal = ((ItemFood) item).getHealAmount();
							barWidth = Math.min(20, food + foodHeal);
							if (food + foodHeal > 20) overkill = true;
						}
					}
					if (barWidth > 0 && Config.get(Config.NODE_FOOD_PREDICT)) drawSolidGradientRect(width / 2 - 90, height - 29, barWidth * 4, 4, overkill ? 0 : 0xd82424, 0x901414);
				}
				drawSolidGradientRect(width / 2 - 90, height - 29, food * 4, 4, hasPotion(Potion.hunger) ? 0x0c1702 : 0x6a410b, hasPotion(Potion.hunger) ? 0x1d3208 : 0x8e5409);
				glPushMatrix();
				glScalef(0.5F, 0.5F, 0.5F);
				if (foodHeal > 0 && Config.get(Config.NODE_FOOD_PREDICT)) fr.drawString("Will Heal: " + foodHeal + (overkill ? " (Waste " + (food + foodHeal - 20) + ")" : ""), width - 178, height * 2 - 57, 0xFFFFFF);
				if (!hasPotion(Potion.wither)) fr.drawStringWithShadow((hp < 5 ? ColorCode.RED : "") + "" + hp, width + 168, height * 2 - 84, 0xFFFFFF);
				fr.drawStringWithShadow((food < 5 ? ColorCode.RED : "") + "" + food, width - 33, height * 2 - 58, 0xFFFFFF);
				glPopMatrix();
				int lvl = mc.thePlayer.experienceLevel;
				String lvlStr = ColorCode.BRIGHT_GREEN + "" + lvl;
				if (lvl > 0) {
					drawDoubleOutlinedBox(width / 2 - fr.getStringWidth(lvlStr) / 2 - 1, height - 32, fr.getStringWidth(lvlStr) + 2, 10, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
					fr.drawStringWithShadow(lvlStr, width / 2 - (fr.getStringWidth(lvlStr) / 2), height - 31, 0xFFFFFF);
				}
				drawDoubleOutlinedBox(width / 2 + 10, height - 29, 80, 4, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				drawSolidGradientRect(width / 2 + 10, height - 29, (int) (mc.thePlayer.experience * 80), 4, 0x05d714, 0x8fea96);
				glPushMatrix();
				glScalef(0.5F, 0.5F, 0.5F);
				int relativeXP = (int) Math.floor(mc.thePlayer.experience * mc.thePlayer.xpBarCap());
				String lvlXP = ColorCode.BRIGHT_GREEN + "" + relativeXP;
				fr.drawStringWithShadow(lvlXP + "/" + mc.thePlayer.xpBarCap(), (int) (width + 120 - (fr.getStringWidth(lvlXP + "/" + mc.thePlayer.xpBarCap()))), height * 2 - 58, 0xFFFFFF);
				glPopMatrix();
	
				if (mc.thePlayer.isInsideOfMaterial(Material.water)) {
					int record = recordIsPlaying ? 20 : 0;
					int air = mc.thePlayer.getAir() + 20;
					drawDoubleOutlinedBox(width / 2 - 80, height - 60 - record, 160, 5, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
					drawSolidGradientRect(width / 2 - 80, height - 60 - record, air / 2, 5, air < 60 ? 0xff1818 : 0x18cbff, air < 60 ? 0xff8c8c : 0x8ce5ff);
					String airStr = "Air:";
					int offset = (int) (air >= 60 ? 0 : Math.sin(rendersElapsed) * 10);
					fr.drawStringWithShadow(airStr, width / 2 - fr.getStringWidth(airStr) / 2 + offset, height - 72 - record, 0xFFFFFF);
				}
			}
		} else {
			defaultHUD();
		}

		glBindTexture(GL_TEXTURE_2D, mc.renderEngine.getTexture("/gui/icons.png"));
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
		drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
		glDisable(GL_BLEND);

		if (Config.get(Config.NODE_LEFT_BAR)) {
			int xoffset = 0;
			if (183 >= (width / 2 - 90)) {
				xoffset = 183-(width/2-90);
				drawDoubleOutlinedBox(40-xoffset, height - 20, 140, 16, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			} else {
				drawDoubleOutlinedBox(40, height - 20, 140, 16, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			}
			fr.drawStringWithShadow("Online: " + mc.thePlayer.sendQueue.playerInfoList.size(), 44-xoffset, height - 16, 0xFFFFFF);
			String status = "";
			int fallDmg = MathHelper.ceiling_float_int(mc.thePlayer.fallDistance - 3.0F);
			if (mc.thePlayer.isSneaking()) status = "Sneaking";
			if (mc.thePlayer.isSprinting()) status = "Sprinting";
			else if (mc.thePlayer.getFoodStats().getFoodLevel() <= 6) status = ColorCode.RED + "Can't Sprint";
			if (mc.thePlayer.capabilities.isFlying) status = "Flying";
			else if (fallDmg > 0 && !mc.thePlayer.capabilities.isCreativeMode) status = "Falling: " + ColorCode.RED + fallDmg;
			String stat = (status.equals("") ? mc.thePlayer.username : status);
			fr.drawStringWithShadow(stat, 176 - fr.getStringWidth(stat)-xoffset, height - 16, 0xFFFFFF);
		}

		if (Config.get(Config.NODE_RIGHT_BAR)) {
			int xoffset = 0;
			if (width - 183 <= (width / 2 + 90)) xoffset = (width-183)-(width/2+90);
			drawDoubleOutlinedBox(width - 180-xoffset, height - 20, 140, 16, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			fr.drawStringWithShadow("FPS: " + ClientUtils.getFPS(), width - 176-xoffset, height - 16, 0xFFFFFF);
			String ping = mc.isSingleplayer() ? "N/A (SP)" : ClientUtils.getPing() + " ms." + (mc.isIntegratedServerRunning() ? " (LAN)" : " (MP)");
			fr.drawStringWithShadow(ping, width - 44 - fr.getStringWidth(ping)-xoffset, height - 16, 0xFFFFFF);
		}

		if (recordIsPlaying && Config.get(Config.NODE_MUSIC)) {
			float color = recordPlayingUpFor - par1;
			int colorValue = (int) (color * 256.0F / 20.0F);
			int colorRgb = 0xFFFFFF;
			if (colorValue > 255) colorValue = 255;
			if (colorValue > 0) {
				colorRgb = Color.HSBtoRGB(color / 50.0F, 0.7F, 0.6F) & 16777215;
				Color colorInstance = new Color(colorRgb);
				int length = fr.getStringWidth(recordPlaying);

				drawDoubleOutlinedBox(width / 2 - length / 2 - 20, height - 70, length + 40, 20, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				glBindTexture(GL_TEXTURE_2D, mc.renderEngine.getTexture("/particles.png"));
				glDisable(GL_DEPTH_TEST);
				glColor3f(colorInstance.getRed() / 255F, colorInstance.getGreen() / 255F, colorInstance.getBlue() / 255F);
				drawTexturedModalRect(width / 2 - length / 2 - 18, height - 68, 0, 64, 16, 16);
				glColor3f(colorInstance.getRed() / 255F, colorInstance.getGreen() / 255F, colorInstance.getBlue() / 255F);
				drawTexturedModalRect(width / 2 + length / 2, height - 68, 0, 64, 16, 16);
				glEnable(GL_DEPTH_TEST);
				fr.drawStringWithShadow(recordPlaying, width / 2 - length / 2, height - 65, colorRgb);
			}

			if (recordPlayingUpFor <= 0) recordIsPlaying = false;
		}

		int posX = MathHelper.floor_double(mc.thePlayer.posX);
		int posY = MathHelper.floor_double(mc.thePlayer.posY);
		int posZ = MathHelper.floor_double(mc.thePlayer.posZ);
		Chunk chunk = mc.theWorld.getChunkFromBlockCoords(posX, posZ);
		String biomeName = chunk.getBiomeGenForWorldCoords(posX & 15, posZ & 15, mc.theWorld.getWorldChunkManager()).biomeName;
		int blockLight = chunk.getSavedLightValue(EnumSkyBlock.Block, posX & 15, posY, posZ & 15);
		int direction = MathHelper.floor_double(mc.thePlayer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (mc.gameSettings.showDebugInfo) {
			glPushMatrix();
			fr.drawStringWithShadow("Minecraft " + MC_VERSION + " (" + mc.debug + ")", 2, 2, 0xFFFFFF);
			fr.drawStringWithShadow(mc.debugInfoRenders(), 2, 12, 0xFFFFFF);
			fr.drawStringWithShadow(mc.getEntityDebug(), 2, 22, 0xFFFFFF);
			fr.drawStringWithShadow(mc.debugInfoEntities(), 2, 32, 0xFFFFFF);
			fr.drawStringWithShadow(mc.getWorldProviderName(), 2, 42, 0xFFFFFF);
			long maxMemory = Runtime.getRuntime().maxMemory();
			long totalMemory = Runtime.getRuntime().totalMemory();
			long freeMemory = Runtime.getRuntime().freeMemory();
			long usedMemory = totalMemory - freeMemory;
			String string = "Used memory: " + usedMemory * 100L / maxMemory + "% (" + usedMemory / 1024L / 1024L + "MB) of " + maxMemory / 1024L / 1024L + "MB";
			drawString(fr, string, width - fr.getStringWidth(string) - 2, 2, 14737632);
			string = "Allocated memory: " + totalMemory * 100L / maxMemory + "% (" + totalMemory / 1024L / 1024L + "MB)";
			drawString(fr, string, width - fr.getStringWidth(string) - 2, 12, 14737632);
			drawString(fr, String.format("x: %.5f (%d) // c: %d (%d)", Double.valueOf(mc.thePlayer.posX), Integer.valueOf(posX), Integer.valueOf(posX >> 4), Integer.valueOf(posX & 15)), 2, 64, 14737632);
			drawString(fr, String.format("y: %.3f (feet pos, %.3f eyes pos)", Double.valueOf(mc.thePlayer.boundingBox.minY), Double.valueOf(mc.thePlayer.posY)), 2, 72, 14737632);
			drawString(fr, String.format("z: %.5f (%d) // c: %d (%d)", Double.valueOf(mc.thePlayer.posZ), Integer.valueOf(posZ), Integer.valueOf(posZ >> 4), Integer.valueOf(posZ & 15)), 2, 80, 14737632);
			drawString(fr, "f: " + direction + " (" + Direction.directions[direction] + ") / " + MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw), 2, 88, 14737632);

			if (mc.theWorld != null && mc.theWorld.blockExists(posX, posY, posZ)) drawString(fr, "lc: " + (chunk.getTopFilledSegment() + 15) + " b: " + biomeName + " bl: " + blockLight + " sl: " + chunk.getSavedLightValue(EnumSkyBlock.Sky, posX & 15, posY, posZ & 15) + " rl: " + chunk.getBlockLightValue(posX & 15, posY, posZ & 15, 0), 2, 96, 14737632);
			drawString(fr, String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", Float.valueOf(mc.thePlayer.capabilities.getWalkSpeed()), Float.valueOf(mc.thePlayer.capabilities.getFlySpeed()), Boolean.valueOf(mc.thePlayer.onGround), Integer.valueOf(mc.theWorld.getHeightValue(posX, posZ))), 2, 104, 14737632);
			glPopMatrix();
		}

		if (Config.get(Config.NODE_MCMMO)) {
			LevelUpData lvlData = McMMOIntegration.getActiveLevelUpData();
			if (lvlData != null) {
				String levelUp = ColorCode.RED + "Level Up!";
				String skillLeveledUp = ColorCode.YELLOW + lvlData.getSkill() + ": [" + lvlData.getLevel() + "]";
				glPushMatrix();
				glScalef(2F, 2F, 2F);
				fr.drawStringWithShadow(levelUp, width / 4 - fr.getStringWidth(levelUp) / 2, 15, 0xFFFFFF);
				glPopMatrix();
				fr.drawStringWithShadow(skillLeveledUp, width / 2 - fr.getStringWidth(skillLeveledUp) / 2, 52, 0xFFFFFF);
			}

			int maxSize = 20;
			for (SkillData skillData : McMMOIntegration.skillData)
				maxSize = Math.max(fr.getStringWidth((skillData.type == UsageType.READY ? skillData.getTool().charAt(0) + skillData.getTool().substring(1).toLowerCase() : skillData.getName()) + ": " + skillData.type.getName()), maxSize);

			if (McMMOIntegration.skillData.size() > 0) drawDoubleOutlinedBox(10, 58, maxSize + 4, McMMOIntegration.skillData.size() * 11 + 6, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			int i = 0;
			for (SkillData skillData : McMMOIntegration.skillData) {
				fr.drawStringWithShadow((skillData.type == UsageType.READY ? skillData.getTool().charAt(0) + skillData.getTool().substring(1).toLowerCase() : skillData.getName()) + ": " + skillData.type.getName(), 12, 60 + i * 12, 0xFFFFFF);
				++i;
			}
		}

		if (Config.get(Config.NODE_SHOW_CHAT)) presistentChatGui.drawChat(getUpdateCounter());

		String time = new SimpleDateFormat("h:mm a").format(new Date()).toString();
		int invSlots = 0;
		for (ItemStack element : mc.thePlayer.inventory.mainInventory)
			if (element == null) ++invSlots;

		if (Config.get(Config.NODE_TOP_BAR)) {
			if (Config.get(Config.NODE_STATUS_DISPLAY)) IC2Integration.renderTopBar(mc, width, height);

			String topData = biomeName + " | " + time + " | Inv: " + invSlots;
			int size = fr.getStringWidth(topData);

			if (Config.get(Config.NODE_CHEAT_COMPASSCLOCK)) {
			drawDoubleOutlinedBox(width / 2 - size / 2 - 14, -1, 18, 18, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			if ((size % 2)==1) {
				drawDoubleOutlinedBox(width / 2 + size / 2 + 15, -1, 18, 18, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			} else {
				drawDoubleOutlinedBox(width / 2 + size / 2 + 14, -1, 18, 18, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			}

			RenderHelper.enableGUIStandardItemLighting();
			ir.renderItemIntoGUI(fr, mc.renderEngine, new ItemStack(Item.compass), width / 2 - size / 2 - 13, 0);
			ir.renderItemIntoGUI(fr, mc.renderEngine, new ItemStack(Item.pocketSundial), width / 2 + size / 2 + 16, 0);
			RenderHelper.disableStandardItemLighting();
			}

			drawDoubleOutlinedBox(width / 2 - size / 2+6, -1, size + 6, 15, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			fr.drawStringWithShadow(topData, width / 2 - size / 2+10, 3, 0xFFFFFF);
		}

		if (mc.isSingleplayer())
		{
			p = mc.getIntegratedServer().getConfigurationManager().getPlayerForUsername(mc.thePlayer.username);
			if (p != null) {
				world = mc.getIntegratedServer().worldServerForDimension(p.dimension);
			}
		} else {
			p = mc.thePlayer;
			world = mc.theWorld;
		}

		if (blockLight < 7 && Config.get(Config.NODE_DANGER_DISPLAY) && !world.isDaytime()) {
			String light = (Config.get(Config.NODE_COLORBLIND_MODE) ? "" : ColorCode.RED) + "Danger Zone!";
			int lightLenght = fr.getStringWidth(light);
			drawDoubleOutlinedBox(39, 25, lightLenght + 20, 16, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			RenderHelper.enableGUIStandardItemLighting();
			ir.renderItemIntoGUI(fr, mc.renderEngine, new ItemStack(Item.skull, 1, 4), 40, 25);
			RenderHelper.disableStandardItemLighting();
			fr.drawStringWithShadow(light, 56, 29, 0xFFFFFF);
		}

		if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow && Config.get(Config.NODE_SHOW_ARROWS) && !mc.playerController.isInCreativeMode()) {
			glPushMatrix();
			glScalef(0.5F, 0.5F, 0.5F);
			int allArrows = 0;
			for (ItemStack stack : mc.thePlayer.inventory.mainInventory)
				if (stack != null && stack.itemID == Item.arrow.itemID) allArrows += stack.stackSize;
			String arrowStr = (allArrows <= 8 && !Config.get(Config.NODE_COLORBLIND_MODE) ? ColorCode.RED : "") + "Arrows: " + allArrows;
			int arrowStrWidth = fr.getStringWidth(arrowStr);
			fr.drawStringWithShadow(arrowStr, width - arrowStrWidth / 2, height - 21, 0xFFFFFF);
			glPopMatrix();
		}

		if (BossStatus.bossName != null && BossStatus.statusBarLength > 0 && Config.get(Config.NODE_BOSS_BAR)) {
			int yoffset = 15;
			int xoffset = 6;
			if (Config.get(Config.NODE_BOTTOM_ADORNMENTS)) {
				drawDoubleOutlinedBox(width / 2 - 126 + xoffset, 34+yoffset, 5, 5, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				drawDoubleOutlinedBox(width / 2 + 121 + xoffset, 34+yoffset, 5, 5, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				drawOutlinedBox(width / 2 - 119 + xoffset, 36+yoffset, 238, 1, BOX_OUTLINE_COLOR, BOX_INNER_COLOR);
				glPushMatrix();
				glDisable(GL_DEPTH_TEST);
				glScalef(0.5F, 0.5F, 0.5F);
				drawSolidRect(width - 243 + xoffset, 71+yoffset, width + 240, 75, BOX_OUTLINE_COLOR);
				glEnable(GL_DEPTH_TEST);
				glPopMatrix();
			}
			BossStatus.statusBarLength--;
			drawOutlinedBox(width / 2 - fr.getStringWidth(BossStatus.bossName) / 2 - 3 + xoffset, 20+yoffset, fr.getStringWidth(BossStatus.bossName) + 6 + xoffset, 14, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			drawDoubleOutlinedBox(width / 2 - 91 + xoffset, 31+yoffset, 182 + xoffset, 10, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			int renderHealth = (int) (BossStatus.healthScale * 182);
			drawSolidGradientRect(width / 2 - 91 + xoffset, 31+yoffset, renderHealth + xoffset, 10, 0, 0x25092e);
			fr.drawStringWithShadow(ColorCode.PURPLE + BossStatus.bossName, width / 2 - fr.getStringWidth(BossStatus.bossName) / 2 + xoffset+3, 21+yoffset, 0xFFFFFF);
			String hp = (BossStatus.healthScale < 0.1 ? ColorCode.RED : "") + "" + Math.round(BossStatus.healthScale * 100) + "%";
			if (!(BossStatus.healthScale < 0)) fr.drawStringWithShadow(hp, width / 2 - fr.getStringWidth(hp) / 2 + xoffset+2, 32+yoffset, 0xFFFFFF);
		} else if (BossStatus.bossName != null && BossStatus.statusBarLength > 0 && !Config.get(Config.NODE_BOSS_BAR)) {
			int xoffset = 7;
			--BossStatus.statusBarLength;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/icons.png"));
			FontRenderer var1 = this.mc.fontRenderer;
			ScaledResolution var2 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
			int var3 = var2.getScaledWidth();
			short var4 = 182;
			int var5 = var3 / 2 - var4 / 2;
			int var6 = (int)(BossStatus.healthScale * (float)(var4 + 1));
			byte var7 = 30;
			this.drawTexturedModalRect(var5+xoffset, var7, 0, 74, var4, 5);
			this.drawTexturedModalRect(var5+xoffset, var7, 0, 74, var4, 5);

			if (var6 > 0)
			{
				this.drawTexturedModalRect(var5+xoffset, var7, 0, 79, var6, 5);
			}

			String var8 = BossStatus.bossName;
			var1.drawStringWithShadow(var8, var3 / 2 - var1.getStringWidth(var8) / 2 + xoffset + 2, var7 - 1, 16777215);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (mc.gameSettings.keyBindPlayerList.pressed && (!mc.isIntegratedServerRunning() || mc.thePlayer.sendQueue.playerInfoList.size() > 1)) {
			NetClientHandler var37 = mc.thePlayer.sendQueue;
			List var39 = var37.playerInfoList;
			int var13 = var37.currentServerMaxPlayers;
			int var40 = var13;
			int var38;

			for (var38 = 1; var40 > 20; var40 = (var13 + var38 - 1) / var38)
				++var38;

			int var16 = 300 / var38;

			if (var16 > 150) var16 = 150;

			int var17 = (width - var38 * var16) / 2;
			byte var44 = 22;
			drawDoubleOutlinedBox(var17 + 6, var44 - 2+15, var16 * var38 + 3, 9 * var40 + 3, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			int var19;
			int var20;
			int var47;

			for (var19 = 0; var19 < var13; ++var19) {
				var20 = var17 + var19 % var38 * var16;
				var47 = var44 + var19 / var38 * 9;
				drawOutlinedBox(var20+8, var47+15, var16 - 1, 8, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(GL11.GL_ALPHA_TEST);

				if (var19 < var39.size()) {
					GuiPlayerInfo var46 = (GuiPlayerInfo) var39.get(var19);
					String name = var46.name;
					try {
						Integer dist = Math.round(p.getDistanceToEntity(world.getPlayerEntityByName(var46.name)));
						if (dist != null && dist != 0) {
							if (name.equalsIgnoreCase("maxpowa")) {
								name = ColorCode.GOLD+name + " - " + dist + "m"+FormattingCode.RESET;
								fr.drawStringWithShadow(name, var20+8, var47+15, 16777215);
							} else {
								fr.drawStringWithShadow(name + " - " + dist + "m", var20+8, var47+15, 16777215);
							}
						} else {
							if (name.equalsIgnoreCase("maxpowa")) {
								name = ColorCode.GOLD+name+FormattingCode.RESET;
								fr.drawStringWithShadow(name, var20+8, var47+15, 16777215);
							} else {
								fr.drawStringWithShadow(name, var20+8, var47+15, 16777215);
							}
						}
					} catch (Exception ex) {
						if (name.equalsIgnoreCase("maxpowa")) {
							name = ColorCode.GOLD+name+FormattingCode.RESET;
							fr.drawStringWithShadow(name, var20+8, var47+15, 16777215);
						} else {
							fr.drawStringWithShadow(name, var20+8, var47+15, 16777215);
						}
					}
					mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/icons.png"));
					byte var50 = 0;
					byte var49;

					if (var46.responseTime < 0) var49 = 5;
					else if (var46.responseTime < 150) var49 = 0;
					else if (var46.responseTime < 300) var49 = 1;
					else if (var46.responseTime < 600) var49 = 2;
					else if (var46.responseTime < 1000) var49 = 3;
					else var49 = 4;

					zLevel += 100.0F;
					drawTexturedModalRect(var20 + var16 - 12+8, var47+15, 0 + var50 * 10, 176 + var49 * 8, 10, 8);
					GL11.glPushMatrix();
					GL11.glScalef(0.5F, 0.5F, 0.5F);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					String ms = var46.responseTime + " ms.";
					fr.drawStringWithShadow(ms, (var20 + var16 - 9 - fr.getStringWidth(ms) / 2 +8) * 2, (var47 +15) * 2, 16777215);
					glEnable(GL11.GL_DEPTH_TEST);
					glPopMatrix();
					zLevel -= 100.0F;
				}
			}
		}

		Collection<PotionEffect> potions = mc.thePlayer.getActivePotionEffects();
		int xPotOffset = 0;
		int yPotOffset = 0;
		int itr = 0;
		if (Config.get(Config.NODE_BUFFS)) for (PotionEffect effect : potions) {
			Potion pot = Potion.potionTypes[effect.getPotionID()];
			if (itr % 8 == 0) {
				xPotOffset = 0;
				yPotOffset += 1;
			}
			String effectStr = Potion.getDurationString(effect);
			drawDoubleOutlinedBox(width - 30 - xPotOffset * 21, height - 9 - yPotOffset * 28, fr.getStringWidth(effectStr) / 2 + 2, 8, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			drawDoubleOutlinedBox(width - 30 - xPotOffset * 21, height - 26 - yPotOffset * 28, 18, 18, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			glDisable(GL_DEPTH_TEST);
			int index = pot.getStatusIconIndex();
			mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/inventory.png"));
			if (pot.hasStatusIcon()) drawTexturedModalRect(width - 30 - xPotOffset * 21, height - 26 - yPotOffset * 28, 0 + index % 8 * 18, 198 + index / 8 * 18, 18, 18);
			glEnable(GL_DEPTH_TEST);

			String level = StatCollector.translateToLocal("enchantment.level." + (effect.getAmplifier() + 1));

			if (level.length() < 5 && !level.equals(StatCollector.translateToLocal("enchantment.level.1"))) fr.drawStringWithShadow(level, width - 29 - xPotOffset * 21, height - 25 - yPotOffset * 28, 0xFFFFFF);

			glPushMatrix();
			glScalef(0.5F, 0.5F, 0.5F);
			fr.drawStringWithShadow((effect.getIsAmbient() ? ColorCode.RED : "") + effectStr, (width - 29 - xPotOffset * 21) * 2, (height - 6 - yPotOffset * 28) * 2, 0xFFFFFF);
			glPopMatrix();
			++itr;
			++xPotOffset;
		}

		tooltip: {
			if (KeyRegister.showTooltipKB.pressed) {
				ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
				if (stack == null) break tooltip;
				int loc = mc.thePlayer.inventory.currentItem;

				int x = width / 2 - 88 + loc * 20;
				int y = height - 20;

				List<String> tokensList = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
				if (tokensList.isEmpty()) break tooltip;

				glPushMatrix();
				glDisable(GL_DEPTH_TEST);

				int lenght = 12;
				for (String s : tokensList)
					lenght = Math.max(lenght, fr.getStringWidth(s));
				drawDoubleOutlinedBox(x, y - tokensList.size() * 12 - 5, lenght + 4, tokensList.size() * 12, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				int i = 1;
				for (String s : tokensList) {
					if (i == 1) s = "\u00a7" + Integer.toHexString(stack.getRarity().rarityColor) + s;
					else s = "\u00a77" + s;
					if (i == 1) fr.drawStringWithShadow(s, x + 2, y - (tokensList.size() + 1) * 12 + i * 12 - 3, 0xFFFFFF);
					else fr.drawString(s, x + 2, y - (tokensList.size() + 1) * 12 + i * 12 - 3, 0xFFFFFF);
					++i;
				}
				glEnable(GL_DEPTH_TEST);
				glPopMatrix();
			}
		}
	}

	public void drawDoubleOutlinedBox(int x, int y, int width, int height, int color, int outlineColor) {
		drawDoubleOutlinedBox(x, y, width, height, color, outlineColor, color);
	}

	public void drawDoubleOutlinedBox(int x, int y, int width, int height, int color, int outlineColor, int outline2Color) {
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2, (y + height) * 2 + 2, color);
		drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1, (y + height) * 2 + 1, outlineColor);
		drawSolidRect(x * 2, y * 2, (x + width) * 2, (y + height) * 2, outline2Color);
		glPopMatrix();
	}

	public void drawOutlinedBox(int x, int y, int width, int height, int color, int outlineColor) {
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2, (y + height) * 2 + 2, outlineColor);
		drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1, (y + height) * 2 + 1, color);
		glPopMatrix();
	}

	public void drawSolidRect(int vertex1, int vertex2, int vertex3, int vertex4, int color) {
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

	public void drawSolidGradientRect(int x, int y, int width, int height, int color1, int color2) {
		drawSolidGradientRect0(x * 2, y * 2, (x + width) * 2, (y + height) * 2, color1, color2);
	}

	public void drawSolidGradientRect0(int vertex1, int vertex2, int vertex3, int vertex4, int color1, int color2) {
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		Color color1Color = new Color(color1);
		Color color2Color = new Color(color2);
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_ALPHA_TEST);
		glShadeModel(GL_SMOOTH);
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.setColorOpaque(color1Color.getRed(), color1Color.getGreen(), color1Color.getBlue());
		tess.addVertex(vertex1, vertex4, zLevel);
		tess.addVertex(vertex3, vertex4, zLevel);
		tess.setColorOpaque(color2Color.getRed(), color2Color.getGreen(), color2Color.getBlue());
		tess.addVertex(vertex3, vertex2, zLevel);
		tess.addVertex(vertex1, vertex2, zLevel);
		tess.draw();
		glShadeModel(GL_FLAT);
		glEnable(GL_ALPHA_TEST);
		glEnable(GL_TEXTURE_2D);
		glPopMatrix();
	}

	private void renderSlot(int slot, int x, int y, float ticks, FontRenderer font) {
    	renderInventorySlot(slot, x, y, ticks);
		RenderEngine render = mc.renderEngine;
		RenderItem itemRenderer = new RenderItem();
		ItemStack stack = mc.thePlayer.inventory.mainInventory[slot];

		if (stack != null) {
			if (ForgeHooksClient.renderInventoryItem(new RenderBlocks(), render, stack, itemRenderer.field_77024_a, zLevel, (float)x, (float)y)) return;

			int dmg = stack.getItemDamageForDisplay();
			int color = (int) Math.round(255.0D - dmg * 255.0D / stack.getMaxDamage());
			int shiftedColor = Config.get(Config.NODE_COLORBLIND_MODE) ? 0xFFFFFF : 255 - color << 16 | color << 8;
			Color shiftedColor1 = new Color(shiftedColor);

			if (stack != null && stack.hasEffect()) {
				glDepthFunc(GL_GREATER);
				glDisable(GL_LIGHTING);
				glDepthMask(false);
				render.bindTexture(render.getTexture("/misc/glint.png"));
				zLevel -= 50.0F;
				glEnable(GL_BLEND);
				if (mc.thePlayer.inventory.currentItem == slot) glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				else glBlendFunc(GL_DST_COLOR, GL_DST_COLOR);
				if (slot == mc.thePlayer.inventory.currentItem) {
					if (!stack.isItemDamaged() || Config.get(Config.NODE_COLORBLIND_MODE)) glColor4f(0.5F, 0.25F, 0.8F, 0.4F);
					else glColor4f(shiftedColor1.getRed() / 255F, shiftedColor1.getGreen() / 255F, shiftedColor1.getBlue() / 255F, 0.4F);
					renderGlint(x * 431278612 + y * 32178161, x, y, 16, 16);
				}
				glDisable(GL_BLEND);
				glDepthMask(true);
				zLevel += 50.0F;
				glEnable(GL_LIGHTING);
				glDepthFunc(GL_LEQUAL);
			}
            itemRenderer.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, stack, x, y);

			glDisable(GL_LIGHTING);
			glDisable(GL_DEPTH_TEST);

			int offset = 0;

			IC2Integration.renderSlots(stack, font, offset, dmg, x, y, shiftedColor);

			if (stack.stackSize > 1) {
				String size = ""+stack.stackSize;
				int sizeWidth = font.getStringWidth(size);
				glPushMatrix();
				glScalef(0.5F, 0.5F, 0.5F);
				font.drawStringWithShadow(size, (x + 16 - sizeWidth / 2) * 2, (y + 12 - offset) * 2, 0xFFFFFF);
				glScalef(1F, 1F, 1F);
				glPopMatrix();
			}

			glEnable(GL_LIGHTING);
			glEnable(GL_DEPTH_TEST);
		}
	}
	
    /**
     * Renders the specified item of the inventory slot at the specified location. Args: slot, x, y, partialTick
     */
    private void renderInventorySlot(int par1, int par2, int par3, float par4)
    {
		RenderItem ir = new RenderItem();
        ItemStack var5 = this.mc.thePlayer.inventory.mainInventory[par1];

        if (var5 != null)
        {
            float var6 = (float)var5.animationsToGo - par4;

            if (var6 > 0.0F)
            {
                GL11.glPushMatrix();
                float var7 = 1.0F + var6 / 5.0F;
                GL11.glTranslatef((float)(par2 + 8), (float)(par3 + 12), 0.0F);
                GL11.glScalef(1.0F / var7, (var7 + 1.0F) / 2.0F, 1.0F);
                GL11.glTranslatef((float)(-(par2 + 8)), (float)(-(par3 + 12)), 0.0F);
            }

            ir.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, var5, par2, par3);

            if (var6 > 0.0F)
            {
                GL11.glPopMatrix();
            }
        }
    }
    
    private void defaultHUD() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/icons.png"));
        ScaledResolution var5 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int var6 = var5.getScaledWidth();
        int var7 = var5.getScaledHeight();
        FontRenderer var8 = this.mc.fontRenderer;
        Random rand = new Random();
        boolean var11;
        int var12;
        int var13;
        int var17;
        int var16;
        int var19;
        int var20;
        int var23;
        int var22;
        int var24;
        int var47;
        var12 = this.mc.thePlayer.getHealth();
        var13 = this.mc.thePlayer.prevHealth;
        boolean var14 = false;
        FoodStats var15 = this.mc.thePlayer.getFoodStats();
        rand.setSeed((long)(update * 312871));
        var16 = var15.getFoodLevel();
        var17 = var15.getPrevFoodLevel();
        int var18;
        var11 = this.mc.thePlayer.hurtResistantTime / 3 % 2 == 1;
    	
        if (this.mc.playerController.shouldDrawHUD())
        {
            var18 = var6 / 2 - 91;
            var19 = var6 / 2 + 91;
            this.mc.mcProfiler.startSection("expBar");
            var20 = this.mc.thePlayer.xpBarCap();

            if (var20 > 0)
            {
                short var21 = 182;
                var22 = (int)(this.mc.thePlayer.experience * (float)(var21 + 1));
                var23 = var7 - 32 + 3;
                this.drawTexturedModalRect(var18, var23, 0, 64, var21, 5);
                
                if (var22 > 0)
                {
                    this.drawTexturedModalRect(var18, var23, 0, 69, var22, 5);
                }
            }

            var47 = var7 - 39;
            var22 = var47 - 10;
            var23 = ForgeHooks.getTotalArmorValue(mc.thePlayer);
            var24 = -1;

            if (this.mc.thePlayer.isPotionActive(Potion.regeneration))
            {
                var24 = update % 25;
            }

            this.mc.mcProfiler.endStartSection("healthArmor");
            int var25;
            int var26;
            int var29;
            int var28;

            for (var25 = 0; var25 < 10; ++var25)
            {
                if (var23 > 0)
                {
                    var26 = var18 + var25 * 8;

                    if (var25 * 2 + 1 < var23)
                    {
                        this.drawTexturedModalRect(var26, var22, 34, 9, 9, 9);
                    }

                    if (var25 * 2 + 1 == var23)
                    {
                        this.drawTexturedModalRect(var26, var22, 25, 9, 9, 9);
                    }

                    if (var25 * 2 + 1 > var23)
                    {
                        this.drawTexturedModalRect(var26, var22, 16, 9, 9, 9);
                    }
                }

                var26 = 16;

                if (this.mc.thePlayer.isPotionActive(Potion.poison))
                {
                    var26 += 36;
                }
                else if (this.mc.thePlayer.isPotionActive(Potion.wither))
                {
                    var26 += 72;
                }

                byte var27 = 0;

                if (var11)
                {
                    var27 = 1;
                }

                var28 = var18 + var25 * 8;
                var29 = var47;

                if (var12 <= 4)
                {
                    var29 = var47 + rand.nextInt(2);
                }

                if (var25 == var24)
                {
                    var29 -= 2;
                }

                byte var30 = 0;

                if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
                {
                    var30 = 5;
                }

                this.drawTexturedModalRect(var28, var29, 16 + var27 * 9, 9 * var30, 9, 9);

                if (var11)
                {
                    if (var25 * 2 + 1 < var13)
                    {
                        this.drawTexturedModalRect(var28, var29, var26 + 54, 9 * var30, 9, 9);
                    }

                    if (var25 * 2 + 1 == var13)
                    {
                        this.drawTexturedModalRect(var28, var29, var26 + 63, 9 * var30, 9, 9);
                    }
                }

                if (var25 * 2 + 1 < var12)
                {
                    this.drawTexturedModalRect(var28, var29, var26 + 36, 9 * var30, 9, 9);
                }

                if (var25 * 2 + 1 == var12)
                {
                    this.drawTexturedModalRect(var28, var29, var26 + 45, 9 * var30, 9, 9);
                }
            }

            this.mc.mcProfiler.endStartSection("food");
            int var51;

            for (var25 = 0; var25 < 10; ++var25)
            {
                var26 = var47;
                var51 = 16;
                byte var52 = 0;

                if (this.mc.thePlayer.isPotionActive(Potion.hunger))
                {
                    var51 += 36;
                    var52 = 13;
                }

                if (this.mc.thePlayer.getFoodStats().getSaturationLevel() <= 0.0F && update % (var16 * 3 + 1) == 0)
                {
                    var26 = var47 + (rand.nextInt(3) - 1);
                }

                if (var14)
                {
                    var52 = 1;
                }

                var29 = var19 - var25 * 8 - 9;
                this.drawTexturedModalRect(var29, var26, 16 + var52 * 9, 27, 9, 9);

                if (var14)
                {
                    if (var25 * 2 + 1 < var17)
                    {
                        this.drawTexturedModalRect(var29, var26, var51 + 54, 27, 9, 9);
                    }

                    if (var25 * 2 + 1 == var17)
                    {
                        this.drawTexturedModalRect(var29, var26, var51 + 63, 27, 9, 9);
                    }
                }

                if (var25 * 2 + 1 < var16)
                {
                    this.drawTexturedModalRect(var29, var26, var51 + 36, 27, 9, 9);
                }

                if (var25 * 2 + 1 == var16)
                {
                    this.drawTexturedModalRect(var29, var26, var51 + 45, 27, 9, 9);
                }
            }

            this.mc.mcProfiler.endStartSection("air");

            if (this.mc.thePlayer.isInsideOfMaterial(Material.water))
            {
                var25 = this.mc.thePlayer.getAir();
                var26 = MathHelper.ceiling_double_int((double)(var25 - 2) * 10.0D / 300.0D);
                var51 = MathHelper.ceiling_double_int((double)var25 * 10.0D / 300.0D) - var26;

                for (var28 = 0; var28 < var26 + var51; ++var28)
                {
                    if (var28 < var26)
                    {
                        this.drawTexturedModalRect(var19 - var28 * 8 - 9, var22, 16, 18, 9, 9);
                    }
                    else
                    {
                        this.drawTexturedModalRect(var19 - var28 * 8 - 9, var22, 25, 18, 9, 9);
                    }
                }
            }

            this.mc.mcProfiler.endSection();
            
            GL11.glPushMatrix();
			int lvl = mc.thePlayer.experienceLevel;
			String lvlStr = ColorCode.BRIGHT_GREEN + "" + lvl;
			switch (lvl) {
				case 0:
					break;
				default: 
					var8.drawStringWithShadow(lvlStr, var6 / 2 - (var8.getStringWidth(lvlStr) / 2), var7 - 39, 0xFFFFFF);
					break;
			}
			GL11.glPopMatrix();
        }
    }
    
	@Override
	public void setRecordPlayingMessage(String record) {
		recordPlaying = record;
		recordPlayingUpFor = 60;
		recordIsPlaying = true;
	}

	@Override
	public void updateTick() {
		if (recordPlayingUpFor > 0) --recordPlayingUpFor;
		super.updateTick();
		update++;
	}

	// The method in GuiIngame is private, full override was necessary.
	// I don't know what some of the params are, so I left them all as parX
	private void renderGlint(int par1, int par2, int par3, int par4, int par5) {
		for (int i = 0; i < 2; ++i) {
			float var7 = 0.00390625F;
			float var8 = 0.00390625F;
			float var9 = Minecraft.getSystemTime() % (3000 + i * 1873) / (3000.0F + i * 1873) * 256F;
			float var10 = 0F;
			float var12 = i == 1 ? -1F : 4F;
			Tessellator tess = Tessellator.instance;
			tess.startDrawingQuads();
			tess.addVertexWithUV(par2, par3 + par5, zLevel, (var9 + par5 * var12) * var7, (var10 + par5) * var8);
			tess.addVertexWithUV(par2 + par4, par3 + par5, zLevel, (var9 + par4 + par5 * var12) * var7, (var10 + par5) * var8);
			tess.addVertexWithUV(par2 + par4, par3 + 0, zLevel, (var9 + par4) * var7, var10 * var8);
			tess.addVertexWithUV(par2 + 0, par3 + 0, zLevel, var9 * var7, var10 * var8);
			tess.draw();
		}
	}

	// Hopefully to clean code, will be used a fair bit
	public boolean hasPotion(Potion pot) {
		return mc.thePlayer.isPotionActive(pot.id);
	}

	private void renderPumpkinBlur(int par1, int par2) {
		glDisable(GL_DEPTH_TEST);
		glDepthMask(false);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor4f(1F, 1F, 1F, 1F);
		glDisable(GL_ALPHA_TEST);
		glBindTexture(GL_TEXTURE_2D, mc.renderEngine.getTexture("%blur%/misc/pumpkinblur.png"));
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.addVertexWithUV(0D, par2, -90D, 0D, 1D);
		tess.addVertexWithUV(par1, par2, -90D, 1D, 1D);
		tess.addVertexWithUV(par1, 0D, -90D, 1D, 0D);
		tess.addVertexWithUV(0D, 0D, -90D, 0D, 0D);
		tess.draw();
		glDepthMask(true);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_ALPHA_TEST);
		glColor4f(1F, 1F, 1F, 1F);
	}

	private void renderVignette(float par1, int par2, int par3) {
		par1 = 1.0F - par1;
		if (par1 < 0.0F) par1 = 0.0F;
		if (par1 > 1.0F) par1 = 1.0F;

		prevVignetteBrightness = (float) (prevVignetteBrightness + (par1 - prevVignetteBrightness) * 0.01);
		glDisable(GL_DEPTH_TEST);
		glDepthMask(false);
		glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_COLOR);
		glColor4f(prevVignetteBrightness, prevVignetteBrightness, prevVignetteBrightness, 1F);
		glBindTexture(GL_TEXTURE_2D, mc.renderEngine.getTexture("%blur%/misc/vignette.png"));
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.addVertexWithUV(0D, par3, -90D, 0D, 1D);
		tess.addVertexWithUV(par2, par3, -90D, 1D, 1D);
		tess.addVertexWithUV(par2, 0D, -90D, 1D, 0D);
		tess.addVertexWithUV(0D, 0D, -90D, 0D, 0D);
		tess.draw();
		glDepthMask(true);
		glEnable(GL_DEPTH_TEST);
		glColor4f(1F, 1F, 1F, 1F);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	private void renderPortalOverlay(float par1, int par2, int par3) {
		if (par1 < 1.0F) {
			par1 *= par1;
			par1 *= par1;
			par1 = par1 * 0.8F + 0.2F;
		}

		glDisable(GL_ALPHA_TEST);
		glDisable(GL_DEPTH_TEST);
		glDepthMask(false);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor4f(1F, 1F, 1F, par1);
		glBindTexture(GL_TEXTURE_2D, mc.renderEngine.getTexture("/terrain.png"));
		float var4 = Block.portal.blockIndexInTexture % 16 / 16.0F;
		float var5 = Block.portal.blockIndexInTexture / 16 / 16.0F;
		float var6 = (Block.portal.blockIndexInTexture % 16 + 1) / 16.0F;
		float var7 = (Block.portal.blockIndexInTexture / 16 + 1) / 16.0F;
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.addVertexWithUV(0D, par3, -90D, var4, var7);
		tess.addVertexWithUV(par2, par3, -90D, var6, var7);
		tess.addVertexWithUV(par2, 0D, -90D, var6, var5);
		tess.addVertexWithUV(0D, 0D, -90D, var4, var5);
		tess.draw();
		glDepthMask(true);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_ALPHA_TEST);
		glColor4f(1F, 1F, 1F, 1F);
	}

	@Override
	public GuiNewChat getChatGUI() {
		return presistentChatGui;
	}
}
