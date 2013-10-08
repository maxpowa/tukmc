package maxpowa.tukmc.gui;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

public class GuiIngameVanilla extends GuiIngameForge
{
    private static final int WHITE = 0xFFFFFF;

    private ScaledResolution res = null;
    private FontRenderer fontrenderer = null;
    private RenderGameOverlayEvent eventParent;
    
    public GuiIngameVanilla(Minecraft mc)
    {
        super(mc);
    }

    @Override
    public void renderGameOverlay(float partialTicks, boolean hasScreen, int mouseX, int mouseY)
    {
        res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        eventParent = new RenderGameOverlayEvent(partialTicks, res, mouseX, mouseY);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        renderHealthMount = mc.thePlayer.ridingEntity instanceof EntityLivingBase;
        renderFood = (mc.thePlayer.ridingEntity == null || !(mc.thePlayer.ridingEntity instanceof EntityLivingBase));
        renderJumpBar = mc.thePlayer.isRidingHorse();
        
        if (!mc.playerController.enableEverythingIsScrewedUpMode())
        {            
            if (this.mc.playerController.shouldDrawHUD())
            {
                if (renderHealth) renderHealth(width, height);
                if (renderArmor)  renderArmor(width, height);
                if (renderFood)   renderFood(width, height);
                if (renderHealthMount) renderHealthMount(width, height);
                if (renderAir)    renderAir(width, height);
            }
        }

        if (renderJumpBar)
        {
            renderJumpBar(width, height);
        }
        else if (renderExperiance) 
        {
            renderExperience(width, height);
        }
    }

    public ScaledResolution getResolution()
    {
        return res;
    }

    @Override
    public void renderArmor(int width, int height)
    {
        if (pre(ARMOR)) return;
        mc.mcProfiler.startSection("armor");

        int left = width / 2 - 91;
        int top = height - left_height;

        int level = ForgeHooks.getTotalArmorValue(mc.thePlayer);
        for (int i = 1; level > 0 && i < 20; i += 2)
        {
            if (i < level)
            {
                drawTexturedModalRect(left, top, 34, 9, 9, 9);
            }
            else if (i == level)
            {
                drawTexturedModalRect(left, top, 25, 9, 9, 9);
            }
            else if (i > level)
            {
                drawTexturedModalRect(left, top, 16, 9, 9, 9);
            }
            left += 8;
        }
        left_height += 10;

        mc.mcProfiler.endSection();
        post(ARMOR);
    }
    
    @Override
    public void renderAir(int width, int height)
    {
        if (pre(AIR)) return;
        mc.mcProfiler.startSection("air");
        int left = width / 2 + 91;
        int top = height - right_height;

        if (mc.thePlayer.isInsideOfMaterial(Material.water))
        {
            int air = mc.thePlayer.getAir();
            int full = MathHelper.ceiling_double_int((double)(air - 2) * 10.0D / 300.0D);
            int partial = MathHelper.ceiling_double_int((double)air * 10.0D / 300.0D) - full;

            for (int i = 0; i < full + partial; ++i)
            {
                drawTexturedModalRect(left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
            }
            right_height += 10;
        }

        mc.mcProfiler.endSection();
        post(AIR);
    }

    public void renderHealth(int width, int height)
    {
        bind(this.icons);
        if (pre(HEALTH)) return;
        mc.mcProfiler.startSection("health");

        boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

        if (mc.thePlayer.hurtResistantTime < 10)
        {
            highlight = false;
        }

        AttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        int health = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
        int healthLast = MathHelper.ceiling_float_int(mc.thePlayer.prevHealth);
        float healthMax = (float)attrMaxHealth.getAttributeValue();
        float absorb = this.mc.thePlayer.getAbsorptionAmount();

        int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        
        this.rand.setSeed((long)(updateCounter * 312871));
        
        int left = width / 2 - 91;
        int top = height - left_height;
        left_height = (healthRows * rowHeight)+20;
        if (rowHeight != 10) left_height += 10 - rowHeight;

        int regen = -1;
        if (mc.thePlayer.isPotionActive(Potion.regeneration))
        {
            regen = updateCounter % 25;
        }
        
        final int TOP =  9 * (mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;
        if (mc.thePlayer.isPotionActive(Potion.poison))      MARGIN += 36;
        else if (mc.thePlayer.isPotionActive(Potion.wither)) MARGIN += 72;
        float absorbRemaining = absorb;
        
        for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i)
        {
            
            int row = MathHelper.ceiling_float_int((float)(i + 1) / 10.0F) - 1;
            int x = left + i % 10 * 8;
            int y = top - row * rowHeight;

            if (health <= 4) y += rand.nextInt(2);
            if (i == regen) y -= 2;     

            drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

            if (highlight)
            {
                if (i * 2 + 1 < healthLast)
                    drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); //6
                else if (i * 2 + 1 == healthLast)
                    drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); //7
            }

            if (absorbRemaining > 0.0F)
            {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                    drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); //17
                else
                    drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); //16
                absorbRemaining -= 2.0F;
            }
            else
            {
                if (i * 2 + 1 < health)
                    drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); //4
                else if (i * 2 + 1 == health)
                    drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); //5
            }
        }
        
        mc.mcProfiler.endSection();
        post(HEALTH);
    }

    public void renderFood(int width, int height)
    {
        if (pre(FOOD)) return;
        mc.mcProfiler.startSection("food");

        int left = width / 2 + 91;
        int top = height - right_height;
        boolean unused = false;// Unused flag in vanilla, seems to be part of a 'fade out' mechanic

        FoodStats stats = mc.thePlayer.getFoodStats();
        int level = stats.getFoodLevel();
        int levelLast = stats.getPrevFoodLevel();

        for (int i = 0; i < 10; ++i)
        {
            right_height = 10+30;
            
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;
            int icon = 16;
            byte backgound = 0;

            if (mc.thePlayer.isPotionActive(Potion.hunger))
            {
                icon += 36;
                backgound = 13;
            }
            if (unused) backgound = 1; //Probably should be a += 1 but vanilla never uses this

            if (mc.thePlayer.getFoodStats().getSaturationLevel() <= 0.0F && updateCounter % (level * 3 + 1) == 0)
            {
                y = top + (rand.nextInt(3) - 1);
            }

            drawTexturedModalRect(x, y, 16 + backgound * 9, 27, 9, 9);

            if (unused)
            {
                if (idx < levelLast)
                    drawTexturedModalRect(x, y, icon + 54, 27, 9, 9);
                else if (idx == levelLast)
                    drawTexturedModalRect(x, y, icon + 63, 27, 9, 9);
            }

            if (idx < level)
                drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
            else if (idx == level)
                drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
        }
        mc.mcProfiler.endSection();
        post(FOOD);
    }

    @Override
    public void renderExperience(int width, int height)
    {
        bind(this.widgetsTexPath);
        if (pre(EXPERIENCE)) return;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        if (mc.playerController.func_78763_f())
        {
            mc.mcProfiler.startSection("expBar");
            int cap = this.mc.thePlayer.xpBarCap();
            int left = width / 2 - 91;
    
            if (cap > 0)
            {
                short barWidth = 182;
                int filled = (int)(mc.thePlayer.experience * (float)(barWidth + 1));
                int top = height - 32 + 3;
                drawTexturedModalRect(left, top, 0, 64, barWidth, 5);
    
                if (filled > 0)
                {
                    drawTexturedModalRect(left, top, 0, 69, filled, 5);
                }
            }

            this.mc.mcProfiler.endSection();
            
        
            if (mc.playerController.func_78763_f() && mc.thePlayer.experienceLevel > 0)
            {
                mc.mcProfiler.startSection("expLevel");
                boolean flag1 = false;
                int color = flag1 ? 16777215 : 8453920;
                String text = "" + mc.thePlayer.experienceLevel;
                int x = (width - mc.fontRenderer.getStringWidth(text)) / 2;
                int y = height - 31 - 4;
                mc.fontRenderer.drawString(text, x + 1, y, 0);
                mc.fontRenderer.drawString(text, x - 1, y, 0);
                mc.fontRenderer.drawString(text, x, y + 1, 0);
                mc.fontRenderer.drawString(text, x, y - 1, 0);
                mc.fontRenderer.drawString(text, x, y, color);
                mc.mcProfiler.endSection();
            }
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        post(EXPERIENCE);
    }

    @Override
    public void renderJumpBar(int width, int height)
    {
        bind(this.widgetsTexPath);
        if (pre(JUMPBAR)) return;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        mc.mcProfiler.startSection("jumpBar");
        float charge = mc.thePlayer.getHorseJumpPower();
        final int barWidth = 182;
        int x = (width / 2) - (barWidth / 2);
        int filled = (int)(charge * (float)(barWidth + 1));
        int top = height - 32 + 3;

        drawTexturedModalRect(x, top, 0, 84, barWidth, 5);

        if (filled > 0)
        {
            this.drawTexturedModalRect(x, top, 0, 89, filled, 5);
        }

        mc.mcProfiler.endSection();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        post(JUMPBAR);
    }

    @Override
    public void renderToolHightlight(int width, int height)
    {
        if (this.mc.gameSettings.heldItemTooltips)
        {
            mc.mcProfiler.startSection("toolHighlight");

            if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null)
            {
                String name = this.highlightingItemStack.getDisplayName();

                int opacity = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);
                if (opacity > 255) opacity = 255;

                if (opacity > 0)
                {
                    int y = height - 59;
                    if (!mc.playerController.shouldDrawHUD()) y += 14;

                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    FontRenderer font = highlightingItemStack.getItem().getFontRenderer(highlightingItemStack);
                    if (font != null)
                    {
                        int x = (width - font.getStringWidth(name)) / 2;
                        font.drawStringWithShadow(name, x, y, WHITE | (opacity << 24));
                    }
                    else
                    {
                        int x = (width - fontrenderer.getStringWidth(name)) / 2;
                        fontrenderer.drawStringWithShadow(name, x, y, WHITE | (opacity << 24));
                    }
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glPopMatrix();
                }
            }

            mc.mcProfiler.endSection();
        }
    }

    @Override
    public void renderHealthMount(int width, int height)
    {
        Entity tmp = mc.thePlayer.ridingEntity;
        if (!(tmp instanceof EntityLivingBase)) return;

        bind(this.widgetsTexPath);
        
        if (pre(HEALTHMOUNT)) return;
        
        boolean unused = false;
        int left_align = width / 2 + 91;
        
        mc.mcProfiler.endStartSection("mountHealth");
        EntityLivingBase mount = (EntityLivingBase)tmp;
        int health = (int)Math.ceil((double)mount.getHealth());
        float healthMax = mount.getMaxHealth();
        int hearts = (int)(healthMax + 0.5F) / 2;

        if (hearts > 30) hearts = 30;
        
        final int MARGIN = 52;
        final int BACKGROUND = MARGIN + (unused ? 1 : 0);
        final int HALF = MARGIN + 45;
        final int FULL = MARGIN + 36;

        for (int heart = 0; hearts > 0; heart += 20)
        {
            int top = height - right_height;
            
            int rowCount = Math.min(hearts, 10);
            hearts -= rowCount;

            for (int i = 0; i < rowCount; ++i)
            {
                int x = left_align - i * 8 - 9;
                drawTexturedModalRect(x, top, BACKGROUND, 9, 9, 9);

                if (i * 2 + 1 + heart < health)
                    drawTexturedModalRect(x, top, FULL, 9, 9, 9);
                else if (i * 2 + 1 + heart == health)
                    drawTexturedModalRect(x, top, HALF, 9, 9, 9);

                right_height = 38+(i);
            }
        }
        post(HEALTHMOUNT);
    }

    //Helper macros
    private boolean pre(ElementType type)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, type));
    }
    private void post(ElementType type)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, type));
    }
    private void bind(ResourceLocation res)
    {
        mc.getTextureManager().bindTexture(res);
    }
}
