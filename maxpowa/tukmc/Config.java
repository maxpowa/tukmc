package maxpowa.tukmc;

import java.util.TreeMap;


import maxpowa.codebase.common.IOUtils;
import net.minecraft.nbt.NBTTagCompound;

public class Config {

	public static TreeMap<String, Node> nodes = new TreeMap();

	public static final String NODE_RIGHT_BAR = "rightBar";
	public static final String NODE_LEFT_BAR = "leftBar";
	public static final String NODE_BOTTOM_ADORNMENTS = "bottomAddornments";
	public static final String NODE_COLORBLIND_MODE = "colorblindMode";
	public static final String NODE_NUMERICAL_DAMAGE_DISPLAY = "numDmg";
	public static final String NODE_STATUS_DISPLAY = "statusDisplay";
	public static final String NODE_BUFFS = "buffs";
	public static final String NODE_MUSIC = "music";
	public static final String NODE_BOSS_BAR = "bossBar";
	public static final String NODE_TOP_BAR = "topBar";
	public static final String NODE_DANGER_DISPLAY = "dangerDisplay";
	public static final String NODE_SHOW_CHAT = "showChat";
	public static final String NODE_SHOW_ARROWS = "showArrows";
	public static final String NODE_ITEMS_BACKGROUND = "itemsBackground";
	public static final String NODE_MCMMO = "mcmmo";
	public static final String NODE_FOOD_PREDICT = "foodPredict";
	public static final String NODE_DEFAULT_NAMEPLATE = "defaultNameplates";
	public static final String NODE_CHEAT_COMPASSCLOCK = "showCheatCompassClock";
	public static final String NODE_SMOOTH_TRANSITION = "smoothTransition";
	public static final String NODE_CUSTOM_BARS = "customBars";

	static {
		new Node(NODE_RIGHT_BAR, "Right Bar - (FPS + Ping)", true);
		new Node(NODE_LEFT_BAR, "Left Bar - (Player Count + Status)", true);
		new Node(NODE_BOTTOM_ADORNMENTS, "Bottom Decorations", false);
		new Node(NODE_COLORBLIND_MODE, "Colorblind Mode", false);
		new Node(NODE_NUMERICAL_DAMAGE_DISPLAY, "Numerical Item Damage Display", true);
		new Node(NODE_STATUS_DISPLAY, "Armor Status Display", true);
		new Node(NODE_BUFFS, "Buffs - (Potion effects)", true);
		new Node(NODE_MUSIC, "Record Display - (Jukebox Song)", true);
		new Node(NODE_BOSS_BAR, "Boss Bar", true);
		new Node(NODE_TOP_BAR, "Top Bar", true);
		new Node(NODE_DANGER_DISPLAY, "Danger Zone Display", true);
		new Node(NODE_SHOW_CHAT, "Display Chat", true);
		new Node(NODE_SHOW_ARROWS, "Show Arrows", true);
		new Node(NODE_ITEMS_BACKGROUND, "Item Bar Background", true);
		new Node(NODE_MCMMO, "McMMO Integration", true);
		new Node(NODE_FOOD_PREDICT, "Show Food Values - (Display wasted food)", true);
		new Node(NODE_DEFAULT_NAMEPLATE, "Use Default Nameplate", false);
		new Node(NODE_CHEAT_COMPASSCLOCK, "Display Compass & Clock", true);
		new Node(NODE_SMOOTH_TRANSITION, "Smooth Bar Transitions", true);
		new Node(NODE_CUSTOM_BARS, "Custom HP/XP/Food Bars", true);
		loadAllNodes();
	}

	public static boolean get(String value) {
		return !nodes.containsKey(value) ? false : nodes.get(value).isEnabled();
	}

	public static void loadNode(String nodeLabel) {
		if (!nodes.containsKey(nodeLabel)) return;
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(mod_TukMC.cacheFile);
		if (!cmp.hasKey("config")) return;
		NBTTagCompound configCmp = cmp.getCompoundTag("config");
		if (!configCmp.hasKey(nodeLabel)) return;
		Node node = nodes.get(nodeLabel);
		node.set(configCmp.getBoolean(nodeLabel));
	}

	public static void loadAllNodes() {
		for (String node : nodes.keySet())
			loadNode(node);
	}

	public static void saveNode(Node node) {
		NBTTagCompound cmp = IOUtils.getTagCompoundInFile(mod_TukMC.cacheFile);
		NBTTagCompound configCmp = cmp.hasKey("config") ? cmp.getCompoundTag("config") : new NBTTagCompound();
		configCmp.setBoolean(node.getLabel(), node.isEnabled());
		cmp.setCompoundTag("config", configCmp);
		IOUtils.injectNBTToFile(cmp, mod_TukMC.cacheFile);
		loadNode(node.getLabel());
	}

	public static class Node {

		public Node(String label, String displayName, boolean defaultEnable) {
			this.label = label;
			this.displayName = displayName;
			enabled = defaultEnable;
			nodes.put(label, this);
		}

		private boolean enabled;
		private String label;
		private String displayName;

		public void set(boolean b) {
			enabled = b;
		}

		public String getLabel() {
			return label;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public String getDisplayName() {
			return displayName;
		}
	}
}
