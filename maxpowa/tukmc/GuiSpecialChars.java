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
import java.util.List;

import org.lwjgl.opengl.GL11;

import maxpowa.codebase.common.FormattingCode;
import maxpowa.tukmc.Config.Node;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;

public class GuiSpecialChars extends GuiScreen {

		private GuiScreen parentscreen;
		private int pageMin;
		private int pageMax;
		private int pageNumber = 1;
		private static final String allchars = ChatAllowedCharacters.allowedCharacters;
		private int pageCount = (int) (Math.ceil(allchars.length()/10)+1);
		private List addedChars;
		public GuiChat parentScreen;

		public GuiSpecialChars(GuiChat guiChat) {
			parentScreen = guiChat;
		}
		
		@Override
		public void initGui() {
			this.buttonList.clear();
			super.initGui();
			drawDoubleOutlinedBox(width / 2 - 100, height / 2 - 86, 200, 20, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			for (int i = 0; i < allchars.length(); i++) {
				for (int n = 0; i < 15; n++) {
					if (i <= pageMax && i >= pageMin) {
						char c = allchars.charAt(i);
						this.buttonList.add(new GuiTukButton(i, width / 2 - 125 + (n*14), height / 2 - 70 + (i-pageMin+1) * 14, 12, 12, c+""));
					}
				}
			}
	  		this.buttonList.add(new GuiTukButton(4096, width / 2 - 125, height / 2 - 86, 20, 20, "<"));
			this.buttonList.add(new GuiTukButton(4097, width / 2 + 105, height / 2 - 86, 20, 20, ">"));
			this.buttonList.add(new GuiTukButton(4098, width / 2 + 115, height / 2 - 100, 10, 10, "x"));
		}
		
		@Override
		public void drawScreen(int par1, int par2, float par3) {
			super.drawScreen(par1, par2, par3);
			this.initGui();
			pageMax = pageNumber * 10;
			pageMin = pageMax - 9;
			FontRenderer fr = mc.fontRenderer;
			ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			int height = res.getScaledHeight();
			int width = res.getScaledWidth();
			this.drawCenteredString(fr, FormattingCode.ITALICS + "TukMC Character List / Page " + pageNumber + " of " + pageCount, width / 2, height / 2 - 80, 0xFFFFFF);
		}
		
		@Override
		protected void actionPerformed(GuiButton par1GuiButton) {
			if (par1GuiButton.id >= 0 && par1GuiButton.id <= Config.getSize()) {
				addedChars.add(allchars.charAt(par1GuiButton.id));
			} else if (par1GuiButton.id == 4096) {
				if (pageNumber > 1) {
					this.pageNumber--;
				}
			} else if (par1GuiButton.id == 4097) {
				if (pageNumber < (pageCount)){
					this.pageNumber++;
				}
			} else if (par1GuiButton.id == 4098) {
				mc.displayGuiScreen(parentScreen);
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
