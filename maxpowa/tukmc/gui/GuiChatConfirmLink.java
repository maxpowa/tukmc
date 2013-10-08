package maxpowa.tukmc.gui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;

import maxpowa.codebase.common.ColorCode;
import maxpowa.tukmc.mod_TukMC;
import maxpowa.tukmc.util.UrlShortener;
import net.minecraft.client.gui.ChatClickData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringTranslate;
import cpw.mods.fml.relauncher.ReflectionHelper;

//Pulic class, the default one is default visibility
public class GuiChatConfirmLink extends
        net.minecraft.client.gui.GuiConfirmOpenLink {

    final ChatClickData theChatClickData;

    final GuiChat chatGui;
    final int times;
    private String copyLinkButtonText;

    private String resolvedURL;

    public GuiChatConfirmLink(GuiChat par1GuiChat, GuiScreen par2GuiScreen,
            String par3Str, int par4, ChatClickData par5ChatClickData) {
        super(par2GuiScreen, ColorCode.BRIGHT_GREEN + par3Str, par4, false);
        chatGui = par1GuiChat;
        theChatClickData = par5ChatClickData;
        times = mod_TukMC.getWebsiteViews(theChatClickData.getClickedUrl());
        copyLinkButtonText = I18n.getString("chat.copy");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        if (theChatClickData.getClickedUrl().contains("tinyurl.com/")) {
            buttonList.add(new GuiTukButton(3, width / 2 - 50, 209, 100, 20,
                    "Preview Link"));
        }
        buttonList.add(new GuiTukButton(0, width / 2 - 155 + 0, 136, 100, 20,
                buttonText1));
        buttonList.add(new GuiTukButton(2, width / 2 - 155 + 105, 136, 100, 20,
                copyLinkButtonText));
        buttonList.add(new GuiTukButton(1, width / 2 - 155 + 210, 136, 100, 20,
                buttonText2));
        resolvedURL = UrlShortener.expand(theChatClickData.getClickedUrl());
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        boolean showURL = !resolvedURL.equalsIgnoreCase("blank");
        super.drawScreen(par1, par2, par3);
        drawCenteredString(fontRenderer, "Extra Info:", width / 2, 175,
                0xFFFFFF);
        if (showURL)
            drawCenteredString(fontRenderer, ColorCode.BRIGHT_GREEN +
                    "Resolved URL: " + resolvedURL, 
                width / 2, 190, 0xFFFFFF);
        drawCenteredString(
                fontRenderer,
                times == 0 ? ColorCode.RED
                        + "You have never been to this website." : String
                        .format("%sYou have been to this website %s times.",
                                ColorCode.BRIGHT_GREEN, times), width / 2, showURL?205:190,
                                0xFFFFFF);
    }

    @Override
    public void copyLinkToClipboard() {
        setClipboardString(theChatClickData.getClickedUrl());
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 0) {
            String url = theChatClickData.getClickedUrl();
            mod_TukMC.registerOpenWebsite(url);
        }
        if (par1GuiButton.id == 3) {
            try {
                ReflectionHelper.setPrivateValue(
                        net.minecraft.client.gui.GuiChat.class,
                        chatGui,
                        getURI(theChatClickData.getClickedUrl().replaceAll(
                                "tinyurl.com/", "preview.tinyurl.com/")), 6);
                chatGui.confirmClicked(true, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.actionPerformed(par1GuiButton);
    }

    public URI getURI(String s) {
        if (s == null)
            return null;
        else {
            Matcher var2 = GuiChat.pattern.matcher(s);

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
}
