package maxpowa.tukmc;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.ChatClickData;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StringTranslate;
import net.minecraft.util.StringUtils;

public class GuiNewChat extends net.minecraft.client.gui.GuiNewChat {
	private final Minecraft mc;
	private final List sentMessages = new ArrayList();
	private final static LinkedList chatLines = new LinkedList();
	private int scrollDist = 0;
	private int height;
	private int width;
	private boolean adorn = false;

	public GuiNewChat(Minecraft par1Minecraft) {
		super(par1Minecraft);
		mc = par1Minecraft;
	}

	public static LinkedList<TimedChatLine> getChatLines() {
		return chatLines;
	}

	@Override
	public void drawChat(int updatePass) {
		adorn = Config.get(Config.NODE_BOTTOM_ADORNMENTS);
		ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		height = res.getScaledHeight();
		width = res.getScaledWidth();
		if (this.mc.gameSettings.chatVisibility != 2) {            
			byte maxView = 10;
			boolean chatOpen = false;
            float chatBoxColor = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
            float chatWidth = mc.gameSettings.chatWidth;
    		if (this.chatLines.size() > 0) {
                if (this.getChatOpen())
                {
                    maxView = 15;
                    chatOpen = true;
                }
                int updateInt;
                int finColor;
                for (int renderLine = 0; renderLine + this.scrollDist < this.chatLines.size() && renderLine < maxView; ++renderLine) {
            		mc.renderEngine.bindTexture("/font/default.png");
                	ChatLine chatLine = (ChatLine)this.chatLines.get(renderLine + this.scrollDist);
                    this.mc.fontRenderer.drawStringWithShadow(chatWidth +"", 10, 10, 0xFFFFFF);
                    
                    if (chatLine != null) {
                        updateInt = updatePass - chatLine.getUpdatedCounter();
                        
                        if (updateInt < 200 || chatOpen) {
                        	if (!chatOpen) {
	                        	double fade = (double)updateInt / 200.0D;
	                            fade = 1.0D - fade;
	                            fade *= 10.0D;
	
	                            if (fade < 0.0D) {
	                                fade = 0.0D;
	                            } else if (fade > 1.0D) {
	                                fade = 1.0D;
	                            }
	                            
	                            fade *= fade;
	                            finColor = (int)(255.0D * fade);
                        	} else {
                                finColor = 255;
                        	}
                            
                            finColor = (int)((float)finColor * chatBoxColor);
                        	
                            if (finColor > 3) {
                            	int renderPosX = (adorn ? 12 : 0);
                                int renderPosY = (renderLine * (-9)) + height - 32;
                                
                                drawRect(3+renderPosX, renderPosY - 1, Math.round(320*chatWidth)+7+renderPosX, renderPosY + 8, finColor / 2 << 24);
                                GL11.glEnable(GL11.GL_BLEND);
                                String chatMessage = chatLine.getChatLineString();
                                if (!this.mc.gameSettings.chatColours)
                                {
                                    chatMessage = StringUtils.stripControlCodes(chatMessage);
                                }
                                this.mc.fontRenderer.drawStringWithShadow(chatMessage, 3+renderPosX, renderPosY, 16777215 + (finColor << 24));
                            }
                        }
                    }
                    
                }
    		}
    	}
	}

	@Override
	public void clearChatMessages() {
		chatLines.clear();
		sentMessages.clear();
	}

	@Override
	public void printChatMessage(String par1Str) {
		printChatMessageWithOptionalDeletion(par1Str, 0);
	}

	@Override
	public void printChatMessageWithOptionalDeletion(String par1Str, int par2) {
		boolean var3 = getChatOpen();
		boolean var4 = true;

		if (par2 != 0) deleteChatLine(par2);

		Iterator var5 = mc.fontRenderer.listFormattedStringToWidth(par1Str, 320).iterator();

		while (var5.hasNext()) {
			String var6 = (String) var5.next();

			if (var3 && scrollDist > 0) scroll(1);

			if (!var4) var6 = " " + var6;

			var4 = false;
			chatLines.add(0, new TimedChatLine(mc.ingameGUI.getUpdateCounter(), var6, par2));
		}

		while (chatLines.size() > 500)
			chatLines.remove(chatLines.size() - 1);
	}

	@Override
	public List getSentMessages() {
		return sentMessages;
	}

	@Override
	public void addToSentMessages(String par1Str) {
		if (sentMessages.isEmpty() || !((String) sentMessages.get(sentMessages.size() - 1)).equals(par1Str)) sentMessages.add(par1Str);
	}

	@Override
	public void resetScroll() {
		scrollDist = 0;
	}

	@Override
	public void scroll(int par1) {
		scrollDist += par1;
		int var2 = chatLines.size();

		if (scrollDist > var2 - 14) scrollDist = var2 - 14;

		if (scrollDist <= 0) scrollDist = 0;
	}

	@Override
	public ChatClickData func_73766_a(int par1, int par2) {
		if (!getChatOpen()) return null;
		else {
			ScaledResolution var3 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			int var4 = var3.getScaleFactor();
			int var5 = par1 / var4 - 3;
			int var6 = par2 / var4 - 40;

			if (var5 >= 0 && var6 >= 0) {
				int var7 = Math.min(20, chatLines.size());

				if (var5 <= 320 && var6 < mc.fontRenderer.FONT_HEIGHT * var7 + var7) {
					int var8 = var6 / (mc.fontRenderer.FONT_HEIGHT + 1) + scrollDist;
					return new ChatClickData(mc.fontRenderer, (ChatLine) chatLines.get(var8), var5, var6 - (var8 - scrollDist) * mc.fontRenderer.FONT_HEIGHT + var8);
				} else return null;
			} else return null;
		}
	}
	
	private void drawNewRect(int x, int y, int x2, int y2, int col){
		drawRect(x, y, x2, y2, col);
	}

	@Override
	public void addTranslatedMessage(String par1Str, Object... par2ArrayOfObj) {
		printChatMessage(StringTranslate.getInstance().translateKeyFormat(par1Str, par2ArrayOfObj));
	}

	@Override
	public boolean getChatOpen() {
		return mc.currentScreen instanceof GuiChat;
	}

	@Override
	public void deleteChatLine(int par1) {
		Iterator var2 = chatLines.iterator();
		ChatLine var3;

		do {
			if (!var2.hasNext()) return;

			var3 = (ChatLine) var2.next();
		} while (var3.getChatLineID() != par1);

		var2.remove();
	}
}
