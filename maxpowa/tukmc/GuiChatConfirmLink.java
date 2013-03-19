package maxpowa.tukmc;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;


import maxpowa.codebase.common.ColorCode;
import net.minecraft.client.gui.ChatClickData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StringTranslate;

import cpw.mods.fml.relauncher.ReflectionHelper;

//Pulic class, the default one is default visibility
public class GuiChatConfirmLink extends net.minecraft.client.gui.GuiConfirmOpenLink {

	final ChatClickData theChatClickData;

	final GuiChat chatGui;
	final int times;
	private String copyLinkButtonText;

	public GuiChatConfirmLink(GuiChat par1GuiChat, GuiScreen par2GuiScreen, String par3Str, int par4, ChatClickData par5ChatClickData) {
		super(par2GuiScreen, (ColorCode.BRIGHT_GREEN + par3Str), par4);
        StringTranslate var4 = StringTranslate.getInstance();
		chatGui = par1GuiChat;
		theChatClickData = par5ChatClickData;
		times = mod_TukMC.getWebsiteViews(theChatClickData.getClickedUrl());
        this.copyLinkButtonText = var4.translateKey("chat.copy");
	}

	@Override
	public void initGui() {
		if (theChatClickData.getClickedUrl().contains("tinyurl.com/")) this.buttonList.add(new GuiButton(3, width / 2 - 50, 209, 100, 20, "Preview Link"));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155 + 0, 136, 100, 20, this.buttonText1));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 155 + 105, 136, 100, 20, this.copyLinkButtonText));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 155 + 210, 136, 100, 20, this.buttonText2));
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		drawCenteredString(fontRenderer, "Extra Info:", width / 2, 175, 0xFFFFFF);
		drawCenteredString(fontRenderer, times == 0 ? ColorCode.RED + "You have never been to this website." : String.format("%sYou have been to this website %s times.", ColorCode.BRIGHT_GREEN, times), width / 2, 190, 0xFFFFFF);
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
		if (par1GuiButton.id == 3) try {
			ReflectionHelper.setPrivateValue(net.minecraft.client.gui.GuiChat.class, chatGui, getURI(theChatClickData.getClickedUrl().replaceAll("tinyurl.com/", "preview.tinyurl.com/")), 6);
			chatGui.confirmClicked(true, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.actionPerformed(par1GuiButton);
	}

	public URI getURI(String s) {
		if (s == null) return null;
		else {
			Matcher var2 = GuiChat.pattern.matcher(s);

			if (var2.matches()) try {
				String var3 = var2.group(0);

				if (var2.group(1) == null) var3 = "http://" + var3;

				return new URI(var3);
			} catch (URISyntaxException var4) {
			}

			return null;
		}
	}
}
