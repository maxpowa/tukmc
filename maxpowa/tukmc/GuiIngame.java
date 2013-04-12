package maxpowa.tukmc;

import static maxpowa.tukmc.TukMCReference.BOX_EFFECT_OUTLINE_COLOR;
import static maxpowa.tukmc.TukMCReference.BOX_HIGHLIGHT_COLOR;
import static maxpowa.tukmc.TukMCReference.BOX_INNER_COLOR;
import static maxpowa.tukmc.TukMCReference.BOX_OUTLINE_COLOR;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
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
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import net.minecraft.client.renderer.Tessellator;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;

public class GuiIngame extends net.minecraft.client.gui.GuiIngame {

	private int tooltipOpenFor;
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
    private final Random rand = new Random();
	private int lastItem = 0;

	public GuiIngame() {
		super(CommonUtils.getMc());
		mc = CommonUtils.getMc();
		presistentChatGui = new GuiNewChat(mc);
	}

	@Override
	public void renderGameOverlay(float par1, boolean par2, int par3, int par4) {
		if (Config.get(Config.NODE_CUSTOM_BARS)) {
			++rendersElapsed;
			ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			int height = res.getScaledHeight();
			int width = res.getScaledWidth();
			FontRenderer fr = mc.fontRenderer;
			RenderItem ir = new RenderItem();
			mc.entityRenderer.setupOverlayRendering();
			
			if (rendersElapsed == 10 && mod_TukMC.updateChecker && mod_TukMC.updateText != null && mod_TukMC.updateVersion != null && mod_TukMC.updateVersion != mod_TukMC.TK_VERSION) {
				mc.displayGuiScreen(new GuiUpdate(mc));
			}
			
			drawGenericStuff(fr, width, height, par1);

			drawStatusBars(fr, width, height);

			// Render pointer onto the screen
			GL11.glPushMatrix();
			mc.renderEngine.bindTexture("/gui/icons.png");
			GL11.glEnable(GL_BLEND);
			GL11.glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
			drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
			GL11.glDisable(GL_BLEND);
			GL11.glPopMatrix();

			drawLeftBar(fr, width, height);

			drawRightBar(fr, width, height);

			drawRecordDisplay(fr, width, height, par1);

			if (Config.get(Config.NODE_DANGER_DISPLAY) || Config.get(Config.NODE_TOP_BAR) || mc.gameSettings.showDebugInfo) {
				int posX = MathHelper.floor_double(mc.thePlayer.posX);
				int posY = MathHelper.floor_double(mc.thePlayer.posY);
				int posZ = MathHelper.floor_double(mc.thePlayer.posZ);
				Chunk chunk = mc.theWorld.getChunkFromBlockCoords(posX, posZ);
				String biomeName = chunk.getBiomeGenForWorldCoords(posX & 15, posZ & 15, mc.theWorld.getWorldChunkManager()).biomeName;
				int blockLight = chunk.getSavedLightValue(EnumSkyBlock.Block, posX & 15, posY, posZ & 15);
				int direction = MathHelper.floor_double(mc.thePlayer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
	
				drawDebugInfo(fr, width, height, posX, posY, posZ, chunk, biomeName, blockLight, direction);
				drawTopBar(fr, ir, width, height, biomeName);
				drawDangerZone(fr, width, height, blockLight, ir);
			}

			drawMCMMO(fr, width, height);

			if (Config.get(Config.NODE_SHOW_CHAT)) presistentChatGui.drawChat(getUpdateCounter());
			
			drawBlockAtPointer(fr, ir, width, height);

			drawArrowCount(fr, width, height);

			drawBossBar(fr, width, height);

			drawPlayerList(fr, width, height);

			drawBuffs(fr, width, height);

			tooltip: {
				if (KeyRegister.showTooltipKB.pressed || (Config.get(Config.NODE_TOOLTIPS) && (tooltipOpenFor > 0))) {
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

					
					int yOffset = 0;
					if (Config.get(Config.NODE_TOOLTIPS)) {
						y = y-20;
						x = width/2-((lenght+4)/2);
					}
					
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

		} else {
			defaultHUD(par1, par2, par3, par4);
		}
	}

	private void drawBuffs(FontRenderer fr, int width, int height) {
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
			mc.renderEngine.bindTexture("/gui/inventory.png");
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
	}

	private void drawPlayerList(FontRenderer fr, int width, int height) {

		ScoreObjective scoreobjective = this.mc.theWorld.getScoreboard().func_96539_a(1);

		if (scoreobjective != null)
		{
			this.drawScoreboardSidebar(scoreobjective, height, width, fr);
		}

		scoreobjective = this.mc.theWorld.getScoreboard().func_96539_a(0);
		
		if (mc.gameSettings.keyBindPlayerList.pressed && (!mc.isIntegratedServerRunning() || mc.thePlayer.sendQueue.playerInfoList.size() > 1)) {
			mc.renderEngine.bindTexture("/font/default.png");
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
			drawDoubleOutlinedBox(var17-2, var44 - 2+15, var16 * var38 + 3, 9 * var40 + 3, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			int var19;
			int var20;
			int var47;

			for (var19 = 0; var19 < var13; ++var19) {
				var20 = var17 + var19 % var38 * var16;
				var47 = var44 + var19 / var38 * 9;
				drawOutlinedBox(var20, var47+15, var16 - 1, 8, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(GL11.GL_ALPHA_TEST);

				if (var19 < var39.size()) {
					GuiPlayerInfo var46 = (GuiPlayerInfo) var39.get(var19);
					
                    GuiPlayerInfo guiplayerinfo = (GuiPlayerInfo)var39.get(var19);
                    ScorePlayerTeam scoreplayerteam = this.mc.theWorld.getScoreboard().func_96509_i(guiplayerinfo.name);
                    String name = ScorePlayerTeam.func_96667_a(scoreplayerteam, guiplayerinfo.name);
                    
                    Integer dist = null;
					try {
						dist = Math.round(p.getDistanceToEntity(world.getPlayerEntityByName(var46.name)));
					} catch (Exception e) {
						//nuthin
					}
					if (dist != null && dist != 0) {
						name = (name + " - " + dist + "m");
					}

                    if (scoreobjective != null)
                    {
                        int j3 = var20 + fr.getStringWidth(name) + 5;
                        int l3 = var20 + var16 - 12 - 5;

                        if (l3 - j3 > 5)
                        {
                            Score score = scoreobjective.func_96682_a().func_96529_a(guiplayerinfo.name, scoreobjective);
                            String s4 = EnumChatFormatting.YELLOW + "" + score.func_96652_c();
                            fr.drawStringWithShadow(s4, l3 - fr.getStringWidth(s4) - 10, var47+15, 16777215);
                        }
                    }
					fr.drawStringWithShadow(name, var20, var47+15, 16777215);
					mc.renderEngine.bindTexture("/gui/icons.png");
					byte var50 = 0;
					byte var49;

					if (var46.responseTime < 0) var49 = 5;
					else if (var46.responseTime < 150) var49 = 0;
					else if (var46.responseTime < 300) var49 = 1;
					else if (var46.responseTime < 600) var49 = 2;
					else if (var46.responseTime < 1000) var49 = 3;
					else var49 = 4;

					zLevel += 100.0F;
					drawTexturedModalRect(var20 + var16 - 12, var47+15, 0 + var50 * 10, 176 + var49 * 8, 10, 8);
					GL11.glPushMatrix();
					GL11.glScalef(0.5F, 0.5F, 0.5F);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					String ms = var46.responseTime + " ms.";
					fr.drawStringWithShadow(ms, (var20 + var16 - 9 - fr.getStringWidth(ms) / 2 ) * 2, (var47 +15) * 2, 16777215);
					glEnable(GL11.GL_DEPTH_TEST);
					glPopMatrix();
					zLevel -= 100.0F;
				}
			}
		}
	}

	private void drawBossBar(FontRenderer fr, int width, int height) {
		if (BossStatus.bossName != null && BossStatus.statusBarLength > 0 && Config.get(Config.NODE_BOSS_BAR)) {
			mc.renderEngine.bindTexture("/font/default.png");
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
			mc.renderEngine.bindTexture("/gui/icons.png");
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
	}

	private void drawArrowCount(FontRenderer fr, int width, int height) {
		if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow && Config.get(Config.NODE_SHOW_ARROWS) && !mc.playerController.isInCreativeMode()) {
			glPushMatrix();
			glScalef(0.5F, 0.5F, 0.5F);
			int allArrows = 0;
			for (ItemStack stack : mc.thePlayer.inventory.mainInventory)
				if (stack != null && stack.itemID == Item.arrow.itemID) allArrows += stack.stackSize;
			String arrowStr = (allArrows <= 8 && !Config.get(Config.NODE_COLORBLIND_MODE) ? ColorCode.RED : "") + "Arrows: " + allArrows;
			int arrowStrWidth = fr.getStringWidth(arrowStr);
			mc.renderEngine.bindTexture("/font/default.png");
			fr.drawStringWithShadow(arrowStr, width - arrowStrWidth / 2, height - 21, 0xFFFFFF);
			glPopMatrix();
		}
	}

    private static List<String> itemDisplayNameMultiline(ItemStack itemstack, GuiContainer gui, boolean includeHandlers)
    {
        List<String> namelist = null;
        try
        {
            namelist = itemstack.getTooltip(Minecraft.getMinecraft().thePlayer, includeHandlers && Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        }
        catch(Exception exception) {}

        if(namelist == null)
            namelist = new ArrayList<String>();

        if(namelist.size() == 0)
            namelist.add("Unnamed");

        if(namelist.get(0) == null || namelist.get(0).equals(""))
            namelist.set(0, "Unnamed");

        namelist.set(0, "\247"+Integer.toHexString(itemstack.getRarity().rarityColor)+namelist.get(0));
        for(int i = 1; i < namelist.size(); i++)
            namelist.set(i, "\u00a77"+namelist.get(i));

        return namelist;
    }
	
    private static String itemDisplayNameShort(ItemStack itemstack)
    {
        List<String> list = itemDisplayNameMultiline(itemstack, null, false);
        return list.get(0);
    }
    
    private static ArrayList<ItemStack> getIdentifierItems(World world, EntityPlayer player, MovingObjectPosition hit)
    {
        int x = hit.blockX;
        int y = hit.blockY;
        int z = hit.blockZ;
        Block blockUnderMouse = Block.blocksList[world.getBlockId(x, y, z)];
        
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        
        if(items.size() > 0)
            return items;
        
        ItemStack pick = blockUnderMouse.getPickBlock(hit, world, x, y, z);
        if(pick != null)
            items.add(pick);
        
        try
        {
            items.addAll(blockUnderMouse.getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0));
        }
        catch(Exception e){}
        if(blockUnderMouse instanceof IShearable)
        {
            IShearable shearable = (IShearable)blockUnderMouse;
            if(shearable.isShearable(new ItemStack(Item.shears), world, x, y, z))
            {
                items.addAll(shearable.onSheared(new ItemStack(Item.shears), world, x, y, z, 0));
            }
        }
        
        if(items.size() == 0)
            items.add(0, new ItemStack(blockUnderMouse, 1, world.getBlockMetadata(x, y, z)));
        
        return items;
    }

	private void drawBlockAtPointer(FontRenderer fr, RenderItem ir, int width, int height) {
		if (Config.get(Config.NODE_BLOCK_DISPLAY) && (mc.renderViewEntity.rayTrace(5, 1.0F) != null) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE) {
	        
			World world = mc.theWorld;
            ArrayList<ItemStack> items = getIdentifierItems(world, mc.thePlayer, mc.objectMouseOver);
            
            String itemname = null;
            ItemStack stack = null;
            for(int i = 0; i < items.size(); i++)
            {
                try
                {
                    String s = itemDisplayNameShort(items.get(i));
                    if(s != null && !s.endsWith("Unnamed"))
                    {
                        itemname = s;
                        stack = items.get(i);
                        break;
                    }
                }
                catch(Exception e){}
            }
            if(itemname == null)
                return;

            itemname = itemname + " (" + stack.itemID + ")";
            
			drawDoubleOutlinedBox(width-50-fr.getStringWidth(itemname), 25-1+10, 22+fr.getStringWidth(itemname), 18, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			fr.drawString(itemname, width-30-fr.getStringWidth(itemname), 29+10, 0xFFFFFF);
			
	        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	        RenderHelper.enableGUIStandardItemLighting();
	        ir.renderItemAndEffectIntoGUI(fr, this.mc.renderEngine, stack, width-49-fr.getStringWidth(itemname), 25+10);
	        RenderHelper.disableStandardItemLighting();
	        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	        		        
		}
	}

	private void drawDangerZone(FontRenderer fr, int width, int height,
			int blockLight, RenderItem ir) {

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
			mc.renderEngine.bindTexture("/font/default.png");
			String light = (Config.get(Config.NODE_COLORBLIND_MODE) ? "" : ColorCode.RED) + "Danger Zone!";
			int lightLenght = fr.getStringWidth(light);
			drawDoubleOutlinedBox(39, 25+10, lightLenght + 20, 16, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			RenderHelper.enableGUIStandardItemLighting();
			ir.renderItemIntoGUI(fr, mc.renderEngine, new ItemStack(Item.skull, 1, 4), 40, 25+10);
			RenderHelper.disableStandardItemLighting();
			fr.drawStringWithShadow(light, 56, 29+10, 0xFFFFFF);
		}
	}

	private void drawTopBar(FontRenderer fr, RenderItem ir, int width, int height, String biomeName) {
		String time = new SimpleDateFormat("h:mm a").format(new Date()).toString();
		int invSlots = 0;
		for (ItemStack element : mc.thePlayer.inventory.mainInventory)
			if (element == null) ++invSlots;

		if (Config.get(Config.NODE_TOP_BAR)) {
			mc.renderEngine.bindTexture("/font/default.png");
			if (Config.get(Config.NODE_STATUS_DISPLAY)) IC2Integration.renderTopBar(mc, width, height);

			String topData = biomeName + " | " + time + " | Inv: " + invSlots;
			int size = fr.getStringWidth(topData);

			if (Config.get(Config.NODE_CHEAT_COMPASSCLOCK)) {
				drawDoubleOutlinedBox(width / 2 - size / 2 - 24, -1, 18, 18, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				if ((size % 2)==1) {
					drawDoubleOutlinedBox(width / 2 + size / 2 + 5, -1, 18, 18, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
					RenderHelper.enableGUIStandardItemLighting();
					ir.renderItemIntoGUI(fr, mc.renderEngine, new ItemStack(Item.pocketSundial), width / 2 + size / 2 + 6, 0);
					RenderHelper.disableStandardItemLighting();
				} else {
					drawDoubleOutlinedBox(width / 2 + size / 2 + 4, -1, 18, 18, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
					RenderHelper.enableGUIStandardItemLighting();
					ir.renderItemIntoGUI(fr, mc.renderEngine, new ItemStack(Item.pocketSundial), width / 2 + size / 2 + 5, 0);
					RenderHelper.disableStandardItemLighting();
				}

				RenderHelper.enableGUIStandardItemLighting();
				ir.renderItemIntoGUI(fr, mc.renderEngine, new ItemStack(Item.compass), width / 2 - size / 2 - 23, 0);
				RenderHelper.disableStandardItemLighting();
			}

			drawDoubleOutlinedBox(width / 2 - size / 2 - 4, -1, size + 6, 15, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			this.drawCenteredString(fr, topData, width / 2, 3, 0xFFFFFF);
		}
	}

	private void drawMCMMO(FontRenderer fr, int width, int height) {
		if (Config.get(Config.NODE_MCMMO)) {
			LevelUpData lvlData = McMMOIntegration.getActiveLevelUpData();
			if (lvlData != null) {
				mc.renderEngine.bindTexture("/font/default.png");
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
	}

	private void drawDebugInfo(FontRenderer fr, int width, int height,
			int posX, int posY, int posZ, Chunk chunk, String biomeName,
			int blockLight, int direction) {
		if (mc.gameSettings.showDebugInfo) {
			mc.renderEngine.bindTexture("/font/default.png");
			glPushMatrix();
			fr.drawStringWithShadow("Minecraft " + mod_TukMC.MC_VERSION + " (" + mc.debug + ")", 2, 2, 0xFFFFFF);
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
	}

	private void drawRecordDisplay(FontRenderer fr, int width, int height, float par1) {
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
				mc.renderEngine.bindTexture("/particles.png");
				glDisable(GL_DEPTH_TEST);
				glColor3f(colorInstance.getRed() / 255F, colorInstance.getGreen() / 255F, colorInstance.getBlue() / 255F);
				drawTexturedModalRect(width / 2 - length / 2 - 18, height - 68, 0, 64, 16, 16);
				glColor3f(colorInstance.getRed() / 255F, colorInstance.getGreen() / 255F, colorInstance.getBlue() / 255F);
				drawTexturedModalRect(width / 2 + length / 2, height - 68, 0, 64, 16, 16);
				glEnable(GL_DEPTH_TEST);
				mc.renderEngine.bindTexture("/font/default.png");
				fr.drawStringWithShadow(recordPlaying, width / 2 - length / 2, height - 65, colorRgb);
			}

			if (recordPlayingUpFor <= 0) recordIsPlaying = false;
			mc.renderEngine.bindTexture("/font/default.png");
		}
	}

	private void drawRightBar(FontRenderer fr, int width, int height) {
		if (Config.get(Config.NODE_RIGHT_BAR)) {
			mc.renderEngine.bindTexture("/font/default.png");
			int xoffset = 0;
			if (width - 183 <= (width / 2 + 90)) xoffset = (width-183)-(width/2+90);
			drawDoubleOutlinedBox(width - 180-xoffset, height - 20, 140, 16, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			fr.drawStringWithShadow("FPS: " + ClientUtils.getFPS(), width - 176-xoffset, height - 16, 0xFFFFFF);
			String ping = mc.isSingleplayer() ? "N/A (SP)" : ClientUtils.getPing() + " ms." + (mc.isIntegratedServerRunning() ? " (LAN)" : " (MP)");
			fr.drawStringWithShadow(ping, width - 44 - fr.getStringWidth(ping)-xoffset, height - 16, 0xFFFFFF);
		}
	}

	private void drawLeftBar(FontRenderer fr, int width, int height) {
		if (Config.get(Config.NODE_LEFT_BAR)) {
			GL11.glPushMatrix();
			mc.renderEngine.bindTexture("/font/default.png");
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
			GL11.glPopMatrix();
		}
	}

	private void drawGenericStuff(FontRenderer fr, int width, int height, float par1) {
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
			
			if (inv.currentItem != lastItem) {
				tooltipOpenFor = 30;
				lastItem = inv.currentItem;
			}

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
	}

	private void drawStatusBars(FontRenderer fr, int width, int height) {			
		boolean shouldDrawHUD = mc.playerController.shouldDrawHUD();

		if (shouldDrawHUD) {
			if (Config.get(Config.NODE_PLAIN_STATUS)) {
	            mc.renderEngine.bindTexture("/gui/icons.png");
		        boolean flag1;
		        int i1;
		        int j1;
		        int k1;
		        int l1;
		        int i2;
		        int j2;
		        int k2;
		        int l2;
		        int i3;
		        byte b0;
		        int j3;
		        int k3;
		        int l3;
	            int i4;
	            boolean flag2 = false;
		        
	            flag1 = this.mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

	            if (this.mc.thePlayer.hurtResistantTime < 10)
	            {
	                flag1 = false;
	            }

	            i1 = this.mc.thePlayer.getHealth();
	            j1 = this.mc.thePlayer.prevHealth;
	            this.rand.setSeed((long)(update * 312871));
	            FoodStats foodstats = this.mc.thePlayer.getFoodStats();
	            l1 = foodstats.getFoodLevel();
	            k1 = foodstats.getPrevFoodLevel();
	            
		        int k = width;
		        int l = height;
		        
				i2 = k / 2 - 91;
                i4 = k / 2 + 91;
                this.mc.mcProfiler.startSection("expBar");
                j2 = this.mc.thePlayer.xpBarCap();

                if (j2 > 0)
                {
                    short short1 = 182;
                    l2 = (int)(this.mc.thePlayer.experience * (float)(short1 + 1));
                    k2 = l - 32 + 3;
                    this.drawTexturedModalRect(i2, k2, 0, 64, short1, 5);

                    if (l2 > 0)
                    {
                        this.drawTexturedModalRect(i2, k2, 0, 69, l2, 5);
                    }
                }

                k3 = l - 39;
                l2 = k3 - 10;
                k2 = ForgeHooks.getTotalArmorValue(mc.thePlayer);
                i3 = -1;

                if (this.mc.thePlayer.isPotionActive(Potion.regeneration))
                {
                    i3 = update % 25;
                }

                this.mc.mcProfiler.endStartSection("healthArmor");
                int j4;
                int k4;
                int l4;

                for (j4 = 0; j4 < 10; ++j4)
                {
                    if (k2 > 0)
                    {
                        j3 = i2 + j4 * 8;

                        if (j4 * 2 + 1 < k2)
                        {
                            this.drawTexturedModalRect(j3, l2, 34, 9, 9, 9);
                        }

                        if (j4 * 2 + 1 == k2)
                        {
                            this.drawTexturedModalRect(j3, l2, 25, 9, 9, 9);
                        }

                        if (j4 * 2 + 1 > k2)
                        {
                            this.drawTexturedModalRect(j3, l2, 16, 9, 9, 9);
                        }
                    }

                    j3 = 16;

                    if (this.mc.thePlayer.isPotionActive(Potion.poison))
                    {
                        j3 += 36;
                    }
                    else if (this.mc.thePlayer.isPotionActive(Potion.wither))
                    {
                        j3 += 72;
                    }

                    b0 = 0;

                    if (flag1)
                    {
                        b0 = 1;
                    }

                    l4 = i2 + j4 * 8;
                    k4 = k3;

                    if (i1 <= 4)
                    {
                        k4 = k3 + rand.nextInt(2);
                    }

                    if (j4 == i3)
                    {
                        k4 -= 2;
                    }

                    byte b1 = 0;

                    if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
                    {
                        b1 = 5;
                    }

                    this.drawTexturedModalRect(l4, k4, 16 + b0 * 9, 9 * b1, 9, 9);

                    if (flag1)
                    {
                        if (j4 * 2 + 1 < j1)
                        {
                            this.drawTexturedModalRect(l4, k4, j3 + 54, 9 * b1, 9, 9);
                        }

                        if (j4 * 2 + 1 == j1)
                        {
                            this.drawTexturedModalRect(l4, k4, j3 + 63, 9 * b1, 9, 9);
                        }
                    }

                    if (j4 * 2 + 1 < i1)
                    {
                        this.drawTexturedModalRect(l4, k4, j3 + 36, 9 * b1, 9, 9);
                    }

                    if (j4 * 2 + 1 == i1)
                    {
                        this.drawTexturedModalRect(l4, k4, j3 + 45, 9 * b1, 9, 9);
                    }
                }

                this.mc.mcProfiler.endStartSection("food");

                for (j4 = 0; j4 < 10; ++j4)
                {
                    j3 = k3;
                    l3 = 16;
                    byte b2 = 0;

                    if (this.mc.thePlayer.isPotionActive(Potion.hunger))
                    {
                        l3 += 36;
                        b2 = 13;
                    }

                    if (this.mc.thePlayer.getFoodStats().getSaturationLevel() <= 0.0F && update % (l1 * 3 + 1) == 0)
                    {
                        j3 = k3 + (this.rand.nextInt(3) - 1);
                    }

                    if (flag2)
                    {
                        b2 = 1;
                    }

                    k4 = i4 - j4 * 8 - 9;
                    this.drawTexturedModalRect(k4, j3, 16 + b2 * 9, 27, 9, 9);

                    if (flag2)
                    {
                        if (j4 * 2 + 1 < k1)
                        {
                            this.drawTexturedModalRect(k4, j3, l3 + 54, 27, 9, 9);
                        }

                        if (j4 * 2 + 1 == k1)
                        {
                            this.drawTexturedModalRect(k4, j3, l3 + 63, 27, 9, 9);
                        }
                    }

                    if (j4 * 2 + 1 < l1)
                    {
                        this.drawTexturedModalRect(k4, j3, l3 + 36, 27, 9, 9);
                    }

                    if (j4 * 2 + 1 == l1)
                    {
                        this.drawTexturedModalRect(k4, j3, l3 + 45, 27, 9, 9);
                    }
                }

                this.mc.mcProfiler.endStartSection("air");

                if (this.mc.thePlayer.isInsideOfMaterial(Material.water))
                {
                    j4 = this.mc.thePlayer.getAir();
                    j3 = MathHelper.ceiling_double_int((double)(j4 - 2) * 10.0D / 300.0D);
                    l3 = MathHelper.ceiling_double_int((double)j4 * 10.0D / 300.0D) - j3;

                    for (l4 = 0; l4 < j3 + l3; ++l4)
                    {
                        if (l4 < j3)
                        {
                            this.drawTexturedModalRect(i4 - l4 * 8 - 9, l2, 16, 18, 9, 9);
                        }
                        else
                        {
                            this.drawTexturedModalRect(i4 - l4 * 8 - 9, l2, 25, 18, 9, 9);
                        }
                    }
                }
                
				int lvl = mc.thePlayer.experienceLevel;
				if (lvl > 0)
				fr.drawStringWithShadow(lvl+"", width / 2 - (fr.getStringWidth(lvl+"") / 2), height - 39, 0xFFFFFF);
				
                this.mc.mcProfiler.endSection();
            } else {
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
		}
	}

	private void drawScoreboardSidebar(ScoreObjective par1ScoreObjective, int par2, int par3, FontRenderer par4FontRenderer)
	{
		Scoreboard scoreboard = par1ScoreObjective.func_96682_a();
		Collection collection = scoreboard.func_96534_i(par1ScoreObjective);

		if (collection.size() <= 15)
		{
			int k = par4FontRenderer.getStringWidth(par1ScoreObjective.func_96678_d());
			String s;

			for (Iterator iterator = collection.iterator(); iterator.hasNext(); k = Math.max(k, par4FontRenderer.getStringWidth(s)))
			{
				Score score = (Score)iterator.next();
				ScorePlayerTeam scoreplayerteam = scoreboard.func_96509_i(score.func_96653_e());
				s = ScorePlayerTeam.func_96667_a(scoreplayerteam, score.func_96653_e()) + ": " + EnumChatFormatting.RED + score.func_96652_c();
			}

			int l = collection.size() * par4FontRenderer.FONT_HEIGHT;
			int i1 = par2 / 2 + l / 3;
			byte b0 = 3;
			int j1 = par3 - k - b0;
			int k1 = 0;
			Iterator iterator1 = collection.iterator();

			while (iterator1.hasNext())
			{
				Score score1 = (Score)iterator1.next();
				++k1;
				ScorePlayerTeam scoreplayerteam1 = scoreboard.func_96509_i(score1.func_96653_e());
				String s1 = ScorePlayerTeam.func_96667_a(scoreplayerteam1, score1.func_96653_e());
				String s2 = EnumChatFormatting.RED + "" + score1.func_96652_c();
				int l1 = i1 - k1 * par4FontRenderer.FONT_HEIGHT;
				int i2 = par3 - b0 + 2;
				//                this.drawDoubleOutlinedBox(j1 - 2, l1, i2, l1 + par4FontRenderer.FONT_HEIGHT, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
				par4FontRenderer.drawString(s1, j1, l1, 0xFFFFFF);
				par4FontRenderer.drawString(s2, i2 - par4FontRenderer.getStringWidth(s2), l1, 3648127);

				if (k1 == collection.size())
				{
					String s3 = par1ScoreObjective.func_96678_d();
					this.drawDoubleOutlinedBox(j1 - 2, l1 - par4FontRenderer.FONT_HEIGHT - 1, i2, l1 - 1 - 93 + ((k1-1)*(par4FontRenderer.FONT_HEIGHT+6)), BOX_INNER_COLOR, BOX_OUTLINE_COLOR);

					par4FontRenderer.drawString(s3, j1 + k / 2 - par4FontRenderer.getStringWidth(s3) / 2, l1 - par4FontRenderer.FONT_HEIGHT, 3648127);
				}
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
			if (ForgeHooksClient.renderInventoryItem(new RenderBlocks(), render, stack, itemRenderer.renderWithColor, zLevel, (float)x, (float)y)) return;

			int dmg = stack.getItemDamageForDisplay();
			int color = (int) Math.round(255.0D - dmg * 255.0D / stack.getMaxDamage());
			int shiftedColor = Config.get(Config.NODE_COLORBLIND_MODE) ? 0xFFFFFF : 255 - color << 16 | color << 8;
			Color shiftedColor1 = new Color(shiftedColor);

			if (stack != null && stack.hasEffect()) {
				glDepthFunc(GL_GREATER);
				glDisable(GL_LIGHTING);
				glDepthMask(false);
				render.bindTexture("/misc/glint.png");
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

			int offset = -10;

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
        ItemStack itemstack = this.mc.thePlayer.inventory.mainInventory[par1];
		RenderItem itemRenderer = new RenderItem();

        if (itemstack != null)
        {
            float f1 = (float)itemstack.animationsToGo - par4;

            if (f1 > 0.0F)
            {
                GL11.glPushMatrix();
                float f2 = 1.0F + f1 / 5.0F;
                GL11.glTranslatef((float)(par2 + 8), (float)(par3 + 12), 0.0F);
                GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
                GL11.glTranslatef((float)(-(par2 + 8)), (float)(-(par3 + 12)), 0.0F);
            }

            itemRenderer.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, itemstack, par2, par3);

            if (f1 > 0.0F)
            {
                GL11.glPopMatrix();
            }
        }
    }
	private void defaultHUD(float par1, boolean par2, int par3, int par4) {
		super.renderGameOverlay(par1, par2, par3, par4);
		if (Config.get(Config.NODE_SHOW_CHAT)) presistentChatGui.drawChat(getUpdateCounter());
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
		if (tooltipOpenFor > 0) --tooltipOpenFor;
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

	/**
	 * Renders the portal overlay. Args: portalStrength, width, height
	 */
	private void renderPortalOverlay(float par1, int par2, int par3)
	{
		if (par1 < 1.0F)
		{
			par1 *= par1;
			par1 *= par1;
			par1 = par1 * 0.8F + 0.2F;
		}

		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, par1);
		this.mc.renderEngine.bindTexture("/terrain.png");
		Icon icon = Block.portal.getBlockTextureFromSide(1);
		float f1 = icon.getMinU();
		float f2 = icon.getMinV();
		float f3 = icon.getMaxU();
		float f4 = icon.getMaxV();
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(0.0D, (double)par3, -90.0D, (double)f1, (double)f4);
		tessellator.addVertexWithUV((double)par2, (double)par3, -90.0D, (double)f3, (double)f4);
		tessellator.addVertexWithUV((double)par2, 0.0D, -90.0D, (double)f3, (double)f2);
		tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, (double)f1, (double)f2);
		tessellator.draw();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public GuiNewChat getChatGUI() {
		return presistentChatGui;
	}
}
