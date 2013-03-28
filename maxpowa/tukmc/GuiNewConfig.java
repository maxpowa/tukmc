package maxpowa.tukmc;

import static maxpowa.tukmc.TukMCReference.BOX_INNER_COLOR;
import static maxpowa.tukmc.TukMCReference.BOX_OUTLINE_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import maxpowa.codebase.common.FormattingCode;
import maxpowa.tukmc.Config.Node;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;

public class GuiNewConfig extends GuiScreen {

	private GuiScreen parentscreen;
	private int pageMin;
	private int pageMax;
	private int pageNumber = 1;
	private int pageCount = (int) (Math.ceil(Config.getSize()/6)+1);

	public GuiNewConfig() {
		// init funcs maybe
	}
	
	@Override
	public void initGui() {
		this.buttonList.clear();
		super.initGui();
		drawDoubleOutlinedBox(width / 2 - 74, height / 2 - 86, 148, 20, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
		int i = 1;
		for (String s : Config.nodekeys) {
			if (i <= pageMax && i >= pageMin) {
				Node node = Config.nodes.get(s);
				boolean enabled = node.isEnabled();
				this.buttonList.add(new GuiTukButton(i, width / 2 - 125, height / 2 - 70 + (i-pageMin+1) * 21, 250, 19, node.getDisplayName()));
			}
			++i;
		}
		this.buttonList.add(new GuiTukButton(101, width / 2 - 125 - 76, height / 2 - 85, 70, 20, "Colors..."));
  		this.buttonList.add(new GuiTukButton(1337, width / 2 - 125, height / 2 - 85, 20, 20, "<"));
		this.buttonList.add(new GuiTukButton(1338, width / 2 + 105, height / 2 - 85, 20, 20, ">"));
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		this.initGui();
		pageMax = pageNumber * 6;
		pageMin = pageMax - 5;
		FontRenderer fr = mc.fontRenderer;
		ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		int height = res.getScaledHeight();
		int width = res.getScaledWidth();
		this.drawCenteredString(fr, FormattingCode.ITALICS + "TukMC Config / Page " + pageNumber + " of " + pageCount, width / 2, height / 2 - 80, 0xFFFFFF);
		int i = 1;
		for (String s : Config.nodekeys) {
			if (i <= pageMax && i >= pageMin) {
				Node node = Config.nodes.get(s);
				boolean enabled = node.isEnabled();
				drawOutlinedBox(width / 2 + 113, height / 2 - 5 + (i-pageMin+1) * 21 - 58, 5, 5, enabled ? 0xFF00 : 0xFF0000, TukMCReference.BOX_OUTLINE_COLOR);
			}
			i++;
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id >= 0 && par1GuiButton.id <= Config.getSize()) {
			String name = Config.nodekeys.get(par1GuiButton.id-1);
			Node node = Config.nodes.get(name);
			node.set(!node.isEnabled());
			Config.saveNode(node);
		} else if (par1GuiButton.id == 101) {
			mc.displayGuiScreen(new ColorConfig(this));
		} else if (par1GuiButton.id == 1337) {
			if (pageNumber > 1) {
				this.pageNumber--;
			}
		} else if (par1GuiButton.id == 1338) {
			if (pageNumber < (pageCount)){
				this.pageNumber++;
			}
		}
		super.actionPerformed(par1GuiButton);
	}
	
	public void drawOutlinedBox(int x, int y, int width, int height, int color, int outlineColor) {
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2, (y + height) * 2 + 2, outlineColor);
		drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1, (y + height) * 2 + 1, color);
		glPopMatrix();
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

}
