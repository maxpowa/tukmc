package maxpowa.tukmc.gui;

import static maxpowa.tukmc.util.TukMCReference.BOX_INNER_COLOR;
import static maxpowa.tukmc.util.TukMCReference.BOX_OUTLINE_COLOR;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maxpowa.codebase.common.ColorCode;
import maxpowa.codebase.common.IOUtils;
import maxpowa.tukmc.mod_TukMC;
import maxpowa.tukmc.handlers.KeyRegister;
import maxpowa.tukmc.util.TimedChatLine;
import maxpowa.tukmc.util.TukMCReference;
import maxpowa.tukmc.util.UrlShortener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatClickData;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.network.packet.Packet19EntityAction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class GuiChat extends net.minecraft.client.gui.GuiChat {

    public static final Pattern pattern = Pattern
            .compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,3})(/\\S*)?$");
    String username;
    String tooltip = "";
    public static final String CHARS = "GSLNWOC";
    boolean isBed;
    static int cooldown = 0;
    static int Yoffset = 75;
    static int lightInner = Color.getColor(null, BOX_INNER_COLOR).brighter()
            .brighter().getRGB();
    boolean command = false;
    
    public void setCommand() {
        this.command = true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        if (mc.thePlayer != null) {
            username = ColorCode.BRIGHT_GREEN + "<" + mc.thePlayer.username
                    + "> ";
        } else {
            username = ColorCode.RED + "<UNKNOWN> ";
        }
        inputField = new GuiTextField(fontRenderer,
                fontRenderer.getStringWidth(username) + 2, height - 170, 360, 4);
        inputField.setMaxStringLength(100);
        inputField.setEnableBackgroundDrawing(false);
        inputField.setFocused(true);
        inputField.setCanLoseFocus(false);
        if (command)
            inputField.setText("/");
        for (int i = 0; i < CHARS.length(); i++) {
            String s = ""+CHARS.charAt(i);
            if (s.equalsIgnoreCase("S"))
                s = ColorCode.BRIGHT_GREEN+"S";
            else if (s.equalsIgnoreCase("L"))
                s = ColorCode.DARK_AQUA+"L";
            else if (s.equalsIgnoreCase("G"))
                s = ColorCode.AQUA+"G";
            else if (s.equalsIgnoreCase("N"))
                s = ColorCode.GOLD+"N";
            else if (s.equalsIgnoreCase("W"))
                s = ColorCode.RED+"W";
            else if (s.equalsIgnoreCase("C"))
                s = ColorCode.INDIGO+"C";
            buttonList.add(new GuiTukButton(i, 15 + i * 12, height - 112
                    - Yoffset, 9, 10, s));
        }
    }

    public GuiTextField getInputField() {
        return inputField;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        for (int k = 0; k < buttonList.size(); ++k) {
            GuiButton guibutton = (GuiButton) buttonList.get(k);
            guibutton.drawButton(mc, par1, par2);
        }
        mc.getTextureManager().bindTexture(
                new ResourceLocation("textures/font/ascii.png"));
        drawDoubleOutlinedBox(2, height - 112 - Yoffset, 10, 10,
                BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
        fontRenderer.drawStringWithShadow(">", 5, height - 111 - Yoffset,
                0x55FF55);
        int textWidth = fontRenderer.getStringWidth(username) + 9
                + fontRenderer.getStringWidth(inputField.getText());
        if (textWidth >= 428) {
            textWidth = 428;
        }
        drawDoubleOutlinedBox(1, height - 170 - 2, textWidth, 11,
                BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
        fontRenderer.drawString(username, 3, height - 170, 0xFFFFFF);
        inputField.drawTextBox();
        for (int k = 0; k < buttonList.size(); ++k) {
            GuiTukButton guibutton = (GuiTukButton) buttonList.get(k);
            if (guibutton.isMouseOver()) {
                tooltip = "";
                switch (guibutton.id) {
                    case 0:
                        tooltip = "Converts the text in the chat field into a;Let me "+TukMCReference.Google+" That For You link.";
                        break;
                    case 1:
                        tooltip = "Shortens a link using "+ColorCode.BRIGHT_GREEN +"goo.gl"+". ;"
                                + ColorCode.RED + "(May take a couple seconds)";
                        break;
                    case 2:
                        tooltip = (mod_TukMC.closeOnFinish ? "Unlocks"
                                : "Locks")
                                + " the Chat GUI ("
                                + (mod_TukMC.closeOnFinish ? "doesn't exit"
                                        : "exits") + ";after saying something)";
                        break;
                    case 3:
                        tooltip = (mod_TukMC.displayNotification ? "Disables"
                                : "Enables")
                                + " notifications;for new messages.";
                        break;
                    case 4:
                        tooltip = "Wipes the Chat.";
                        break;
                    case 5:
                        tooltip = "Prints all out all the chat to a text;file. "
                                + ColorCode.RED + "(Must be pressing SHIFT)";
                        break;
                    case 6:
                        tooltip = "Character Picker";
                        break;
                }
                if (tooltip != "") {
                    String[] tokens = tooltip.split(";");
                    int length = 12;
                    for (String s : tokens) {
                        length = Math.max(length,
                                fontRenderer.getStringWidth(s));
                    }
                    if (guibutton.id + 1 <= CHARS.length()) {
                        drawDoubleOutlinedBox(14, height - 114 - tokens.length
                                * 12 - Yoffset, length + 6, tokens.length * 12,
                                lightInner, BOX_OUTLINE_COLOR);
                        drawOutlinedBox(15 + guibutton.id * 12, height - 114
                                - Yoffset, 9, 1, lightInner, BOX_OUTLINE_COLOR);
                        drawSolidRect(14 + guibutton.id * 12, height - 115
                                - Yoffset, 26 + guibutton.id * 12, height - 114
                                - Yoffset, lightInner);
                        drawSolidRect(15 + guibutton.id * 12, height - 115
                                - Yoffset, 24 + guibutton.id * 12, height - 112
                                - Yoffset, lightInner);
                    }
                    int i = 0;
                    for (String s : tokens) {
                        fontRenderer.drawStringWithShadow(s, 18, height - 112
                                - tokens.length * 12 + i * 12 - Yoffset,
                                0xFFFFFF);
                        ++i;
                    }
                }
            }
        }
        if (isBed) {
            drawDoubleOutlinedBox(260, height - 80, 185, 16, BOX_INNER_COLOR,
                    BOX_OUTLINE_COLOR);
            fontRenderer.drawStringWithShadow("Press ESC to leave your bed.",
                    265, height - 76, 0xFFFFFF);
        }
        if (Keyboard.isKeyDown(KeyRegister.showTooltipKB.keyCode))
            if (mc.ingameGUI.getChatGUI() != null) {
                ChatClickData clickData = mc.ingameGUI.getChatGUI()
                        .func_73766_a(Mouse.getX(), Mouse.getY() + 34);
                if (clickData != null) {
                    ChatLine line = ReflectionHelper.getPrivateValue(
                            ChatClickData.class, clickData, 2);
                    if (line != null && line instanceof TimedChatLine) {
                        String time = "Recieved: "
                                + ((TimedChatLine) line).getTime()
                                + ColorCode.GREY
                                + ((TimedChatLine) line).getElapsedTime();
                        int timeWidth = fontRenderer.getStringWidth(time);
                        drawDoubleOutlinedBox(Mouse.getX() / 2 + 2, height
                                - Mouse.getY() / 2 - 16, timeWidth + 4, 12,
                                BOX_INNER_COLOR, BOX_OUTLINE_COLOR);
                        fontRenderer.drawStringWithShadow(time,
                                Mouse.getX() / 2 + 4, height - Mouse.getY() / 2
                                        - 14, 0xFFFFFF);
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
            mc.displayGuiScreen((GuiScreen) null);
        }

        if (par2 == 28 && mod_TukMC.closeOnFinish) {
            mod_TukMC.shouldReopenChat = true;
        }

        super.keyTyped(par1, par2);
    }

    @Override
    public void handleMouseInput() {
        int var1 = Mouse.getEventDWheel();
        int relativeWidth = Mouse.getEventX() * width / mc.displayWidth;
        int relativeHeight = height - Mouse.getEventY() * height
                / mc.displayHeight - 1;

        if (Mouse.getEventButtonState()) {
            int button = Mouse.getEventButton();
            Minecraft.getSystemTime();
            mouseClicked(relativeWidth, relativeHeight, button);
        } else if (Mouse.getEventButton() != -1) {
            mouseMovedOrUp(relativeWidth, relativeHeight,
                    Mouse.getEventButton());
        }

        if (var1 != 0) {
            if (var1 > 1) {
                var1 = 1;
            }

            if (var1 < -1) {
                var1 = -1;
            }

            if (isShiftKeyDown()) {
                var1 *= 7;
            }

            mc.ingameGUI.getChatGUI().scroll(var1);
        }
    }

    protected void actionPerformed(GuiButton par1GuiButton) {
        switch (par1GuiButton.id) {
            case 0: {
                URI uri = getURI();
                if (uri == null) {
                    String text = inputField.getText();
                    if (MathHelper.stringNullOrLengthZero(text)) {
                        break;
                    }

                    String s = "http://lmgtfy.com/?q="
                            + text.replaceAll(" ", "+");
                    inputField.setText(s);
                }
                break;
            }
            case 1: {
                URI uri = getURI();
                if (uri != null) {
                    String text = inputField.getText();
                    if (text.contains("goo.gl/") || text.contains("tiny.cc/")) {
                        break;
                    }
                    try {
                        inputField.setText(UrlShortener.shorten(text));
                    } catch (Exception e) {
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
                mod_TukMC
                        .setDisplayNotification(!mod_TukMC.displayNotification);
                break;
            }
            case 4: {
                mc.ingameGUI.getChatGUI().clearChatMessages();
                break;
            }
            case 5: {
                if (isShiftKeyDown()) {
                    File cacheFolder = IOUtils.getCacheFile("tukmc")
                            .getParentFile();
                    File subFolder = new File(cacheFolder, "TukMC ChatLogs");
                    if (!subFolder.exists()) {
                        subFolder.mkdir();
                    }
                    File log = new File(subFolder, new SimpleDateFormat(
                            "yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
                    try {
                        log.createNewFile();
                        BufferedWriter writer = new BufferedWriter(
                                new FileWriter(log));
                        List<TimedChatLine> chatLines = GuiNewChat
                                .getChatLines();
                        ListIterator<TimedChatLine> it = chatLines
                                .listIterator();
                        while (it.hasNext()) {
                            it.next();
                        }
                        while (it.hasPrevious()) {
                            TimedChatLine line = it.previous();
                            String lineString = "[" + line.getTime() + " ("
                                    + line.getMillisOfCreation() + ")" + "] "
                                    + line.getChatLineString() + "\r";
                            writer.write(StringUtils
                                    .stripControlCodes(lineString));
                        }
                        writer.close();
                        mc.thePlayer.addChatMessage("Chat saved to "
                                + log.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case 6:
                mc.displayGuiScreen(new GuiSpecialChars(this, inputField.getText()));
                break;
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        if (par3 == 0 && mc.gameSettings.chatLinks) {
            ChatClickData var4 = mc.ingameGUI.getChatGUI().func_73766_a(
                    Mouse.getX(),
                    Mouse.getY() + (mc.fontRenderer.FONT_HEIGHT - 1) * 4);

            if (var4 != null) {
                URI var5 = var4.getURI();

                if (var5 != null) {
                    if (mc.gameSettings.chatLinksPrompt) {
                        ReflectionHelper.setPrivateValue(
                                net.minecraft.client.gui.GuiChat.class, this,
                                var5, 6);
                        mc.displayGuiScreen(new GuiChatConfirmLink(this, this,
                                var4.getClickedUrl(), 0, var4));
                    }

                    return;
                }
            }
        }

        for (int l = 0; l < buttonList.size(); ++l) {
            GuiTukButton guibutton = (GuiTukButton) buttonList.get(l);

            if (guibutton.mousePressed(mc, par1, par2)) {
                mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                this.actionPerformed(guibutton);
            }
        }

        inputField.mouseClicked(par1, par2, par3);
    }

    public URI getURI() {
        String var1 = inputField.getText();

        if (var1 == null)
            return null;
        else {
            Matcher var2 = pattern.matcher(var1);

            if (var2.matches()) {
                try {
                    String var3 = var2.group(0);

                    if (var2.group(1) == null) {
                        var3 = "http://" + var3;
                    }

                    return new URI(var3);
                } catch (URISyntaxException var4) {
                }
            }

            return null;
        }
    }

    public void drawOutlinedBox(int x, int y, int width, int height, int color,
            int outlineColor) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2,
                (y + height) * 2 + 2, outlineColor);
        drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1,
                (y + height) * 2 + 1, color);
        GL11.glPopMatrix();
    }

    public void drawDoubleOutlinedBox(int x, int y, int width, int height,
            int color, int outlineColor) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        drawSolidRect(x * 2 - 2, y * 2 - 2, (x + width) * 2 + 2,
                (y + height) * 2 + 2, color);
        drawSolidRect(x * 2 - 1, y * 2 - 1, (x + width) * 2 + 1,
                (y + height) * 2 + 1, outlineColor);
        drawSolidRect(x * 2, y * 2, (x + width) * 2, (y + height) * 2, color);
        GL11.glPopMatrix();
    }

    public void drawSolidRect(int vertex1, int vertex2, int vertex3,
            int vertex4, int color) {
        GL11.glPushMatrix();
        Color color1 = new Color(color);
        Tessellator tess = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tess.startDrawingQuads();
        tess.setColorOpaque(color1.getRed(), color1.getGreen(),
                color1.getBlue());
        tess.addVertex(vertex1, vertex4, zLevel);
        tess.addVertex(vertex3, vertex4, zLevel);
        tess.addVertex(vertex3, vertex2, zLevel);
        tess.addVertex(vertex1, vertex2, zLevel);
        tess.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

}
