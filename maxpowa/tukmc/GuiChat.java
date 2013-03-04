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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javaQuery.j2ee.tinyURL;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import maxpowa.codebase.common.ColorCode;
import maxpowa.codebase.common.EnumMaxpowaMods;
import maxpowa.codebase.common.FormattingCode;
import maxpowa.codebase.common.IOUtils;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.ChatClickData;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.MathHelper;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.network.packet.Packet19EntityAction;
import net.minecraft.util.StringUtils;
import net.minecraft.client.renderer.Tessellator;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class GuiChat extends net.minecraft.client.gui.GuiChat {

	public static final Pattern pattern = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,3})(/\\S*)?$");
	String username;
	String tooltip = "";
	public static final String CHARS = "GTLNWO";
	boolean isBed;
    static int cooldown = 0;
    static int Yoffset = 75;

	@Override
	public void initGui() {
		super.initGui();
		username = ColorCode.BRIGHT_GREEN + "<" + mc.thePlayer.username + "> ";
		inputField = new GuiTextField(fontRenderer, fontRenderer.getStringWidth(username) + 2, (height - 170), 360, 4);
		inputField.setMaxStringLength(100);
		inputField.setEnableBackgroundDrawing(false);
		inputField.setFocused(true);
		inputField.setCanLoseFocus(false);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		int x = Mouse.getX();
		int y = Mouse.getY();
		drawDoubleOutlinedBox(2, height - 112 - Yoffset, 10, 10, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);		
		fontRenderer.drawStringWithShadow(">", 5, height - 111 - Yoffset, 0x55FF55);
		
		int textWidth = fontRenderer.getStringWidth(username) + 9 + fontRenderer.getStringWidth(inputField.getText());
		if (textWidth >= 428) {
			textWidth = 428;
		}
		drawDoubleOutlinedBox(1, (height - 170) - 2, textWidth, 11, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
		fontRenderer.drawString(username, 3, (height - 170), 0xFFFFFF);
		inputField.drawTextBox();
		int max = CHARS.length() * 26 + 10;
		int min = 28;
  		int box = (x - 9) / ((max - min) / CHARS.length()) - 1;
		boolean is = y >= (202 - Yoffset) && y <= (223 - Yoffset) && x >= min && x <= max;
		for (int i = 0; i < CHARS.length(); i++) {
			drawDoubleOutlinedBox(15 + i * 12, height - (is && i == box ? 114 : 112) - Yoffset, 9, (is && box == i ? 12 : 10), BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			fontRenderer.drawStringWithShadow("" + CHARS.charAt(i), 17 + i * 12, height - 111 - Yoffset, 0xFFFFFF);
		}
		if (tooltip != "") {
			String[] tokens = tooltip.split(";");
			int length = 12;
			for (String s : tokens)
				length = Math.max(length, fontRenderer.getStringWidth(s));
					if (box <= CHARS.length() - 1) {
						drawDoubleOutlinedBox(14, height - 114 - tokens.length * 12 - Yoffset, length + 6, tokens.length * 12, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
						drawOutlinedBox(15 + box * 12, height - 114 - Yoffset, 9, 1, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
						drawSolidRect(14 + box * 12, height - 115 - Yoffset, 26 + box * 12, height - 114 - Yoffset, BOX_INNER_COLOR);
						drawSolidRect(15 + box * 12, height - 115 - Yoffset, 24 + box * 12, height - 112 - Yoffset, BOX_INNER_COLOR);
					}
					int i = 0;
					for (String s : tokens) {
						fontRenderer.drawStringWithShadow(s, 18, height - 112 - tokens.length * 12 + i * 12 - Yoffset, 0xFFFFFF);
						++i;
					}
		}
		if (isBed) {
			drawDoubleOutlinedBox(260, height - 80, 185, 16, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
			fontRenderer.drawStringWithShadow("Press ESC to leave your bed.", 265, height - 76, 0xFFFFFF);
		}
		if (Keyboard.isKeyDown(KeyRegister.showTooltipKB.keyCode)) if (mc.ingameGUI.getChatGUI() != null) {
			ChatClickData clickData = mc.ingameGUI.getChatGUI().func_73766_a(Mouse.getX(), Mouse.getY()+34);
			if (clickData != null) {
				ChatLine line = ReflectionHelper.getPrivateValue(ChatClickData.class, clickData, 2);
				if (line != null && line instanceof TimedChatLine) {
					String time = "Recieved: " + ((TimedChatLine) line).getTime() + ColorCode.GREY + ((TimedChatLine) line).getElapsedTime();
					int timeWidth = fontRenderer.getStringWidth(time);
					drawDoubleOutlinedBox(Mouse.getX() / 2 + 2, height - Mouse.getY() / 2 - 16, timeWidth + 4, 12, BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
					fontRenderer.drawStringWithShadow(time, Mouse.getX() / 2 + 4, height - Mouse.getY() / 2 - 14, 0xFFFFFF);
				}
			}
		}		
	}

	public void setBed() {
		isBed = true;
	}

	private void wakeEntity() {
		NetClientHandler var1 = mc.thePlayer.sendQueue;
		var1.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 3));
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		if (par2 == 1 && mc.thePlayer.isPlayerSleeping()) {
			wakeEntity();
	        mc.displayGuiScreen((GuiScreen)null);
		}

		if (par2 == 28 && mod_TukMC.closeOnFinish) mod_TukMC.shouldReopenChat = true;
		super.keyTyped(par1, par2);
	}

	@Override
	public void handleMouseInput() {
		int var1 = Mouse.getEventDWheel();
		int max = CHARS.length() * 26 + 10;
		int min = 28;
		int x = Mouse.getX();
		int y = Mouse.getY()-Yoffset-77;
		int box = (x - 9) / ((max - min) / CHARS.length()) - 1;
		if (y >= 202 && y <= 223 && x >= min && x <= max && box >= 0) switch (box) {
			case 0:
				tooltip = "Converts the text in the chat field into a;Let me Google That For You link.";
				break;
			case 1:
				tooltip = "Shortens a link using tinyurl. " + ColorCode.RED + "(May take;" + ColorCode.RED + "a while)";
				break;
			case 2:
				tooltip = (mod_TukMC.closeOnFinish ? "Unlocks" : "Locks") + " the Chat GUI (" + (mod_TukMC.closeOnFinish ? "doesn't exit" : "exits") + " after;saying something)";
				break;
			case 3:
				tooltip = (mod_TukMC.displayNotification ? "Disables" : "Enables") + " notifications for new messages.";
				break;
			case 4:
				tooltip = "Wipes the Chat.";
				break;
			case 5:
				tooltip = "Prints all out all the chat to a text;file. " + ColorCode.RED + "(Must be pressing SHIFT)";
				break;
		}
		else tooltip = "";

		int relativeWidth = Mouse.getEventX() * width / mc.displayWidth;
		int relativeHeight = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		if (Mouse.getEventButtonState()) {
			int button = Mouse.getEventButton();
			Minecraft.getSystemTime();
			mouseClicked(relativeWidth, relativeHeight, button);
		} else if (Mouse.getEventButton() != -1) mouseMovedOrUp(relativeWidth, relativeHeight, Mouse.getEventButton());

		if (var1 != 0) {
			if (var1 > 1) var1 = 1;

			if (var1 < -1) var1 = -1;

			if (isShiftKeyDown()) var1 *= 7;

			mc.ingameGUI.getChatGUI().scroll(var1);
		}
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		int x = Mouse.getX();
		int y = Mouse.getY()-Yoffset-77;
		int max = CHARS.length() * 26 + 10;
		int min = 28;
		int box = (x - 9) / ((max - min) / CHARS.length()) - 1;
		if (y >= 202 && y <= 223 && x >= min && x <= max) {
			mc.sndManager.playSoundFX("random.click", 1F, 1F);
			switch (box) {
				case 0: {
					URI uri = getURI();
					if (uri == null) {
						String text = inputField.getText();
						if (MathHelper.stringNullOrLengthZero(text)) break;

						String s = "http://lmgtfy.com/?q=" + text.replaceAll(" ", "+");
						inputField.setText(s);
					}
					break;
				}
				case 1: {
					URI uri = getURI();
					if (uri != null) {
						String text = inputField.getText();
						if (text.contains("tinyurl.com/")) break;
						try {
							tinyURL url = new tinyURL();
							inputField.setText(url.getTinyURL(text).replaceAll("http://preview.", ""));
						} catch (Exception e){
							break;
						}
					}
					break;
				}
				case 2: {
					mod_TukMC.setCloseOnFinish(!mod_TukMC.closeOnFinish);
					break;
				}
				case 3: {
					mod_TukMC.setDisplayNotification(!mod_TukMC.displayNotification);
					break;
				}
				case 4: {
					mc.ingameGUI.getChatGUI().func_73761_a();
					break;
				}
				case 5: {
					if (isShiftKeyDown()) {
						File cacheFolder = IOUtils.getCacheFile(EnumMaxpowaMods.TUKMC).getParentFile();
						File subFolder = new File(cacheFolder, "TukMC ChatLogs");
						if (!subFolder.exists()) subFolder.mkdir();
						File log = new File(subFolder, new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
						try {
							log.createNewFile();
							BufferedWriter writer = new BufferedWriter(new FileWriter(log));
							List<TimedChatLine> chatLines = ((maxpowa.tukmc.GuiNewChat) mc.ingameGUI.getChatGUI()).getChatLines();
							ListIterator<TimedChatLine> it = chatLines.listIterator();
							while (it.hasNext())
								it.next();
							while (it.hasPrevious()) {
								TimedChatLine line = it.previous();
								String lineString = "[" + line.getTime() + " (" + line.getMillisOfCreation() + ")" + "] " + line.getChatLineString() + "\r";
								writer.write(StringUtils.stripControlCodes(lineString));
							}
							writer.close();
							mc.thePlayer.addChatMessage("Chat saved to " + log.getAbsolutePath());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;						
				}
			}
		}
		if (par3 == 0 && mc.gameSettings.chatLinks) {
			ChatClickData var4 = mc.ingameGUI.getChatGUI().func_73766_a(Mouse.getX(), Mouse.getY()+((mc.fontRenderer.FONT_HEIGHT-1)*4));

			if (var4 != null) {
				URI var5 = var4.getURI();

				if (var5 != null) {
					if (mc.gameSettings.chatLinksPrompt) {
						ReflectionHelper.setPrivateValue(net.minecraft.client.gui.GuiChat.class, this, var5, 6);
						mc.displayGuiScreen(new GuiChatConfirmLink(this, this, var4.getClickedUrl(), 0, var4));
					}

					return;
				}
			}
		}

		inputField.mouseClicked(par1 * 2, par2 * 2, par3);
	}

	public URI getURI() {
		String var1 = inputField.getText();

		if (var1 == null) return null;
		else {
			Matcher var2 = pattern.matcher(var1);

			if (var2.matches()) try {
				String var3 = var2.group(0);

				if (var2.group(1) == null) var3 = "http://" + var3;

				return new URI(var3);
			} catch (URISyntaxException var4) {
			}

			return null;
		}
	}

	public void drawOutlinedBox(int x, int y, int width, int height, int color, int outlineColor) {
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2, (y + height) * 2 + 2, outlineColor);
		drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1, (y + height) * 2 + 1, color);
		glPopMatrix();
	}

	public void drawDoubleOutlinedBox(int x, int y, int width, int height, int color, int outlineColor) {
		glPushMatrix();
		glScalef(0.5F, 0.5F, 0.5F);
		drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2, (y + height) * 2 + 2, color);
		drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1, (y + height) * 2 + 1, outlineColor);
		drawSolidRect(x * 2, y * 2, (x + width) * 2, (y + height) * 2, color);
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
