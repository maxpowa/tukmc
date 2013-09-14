package maxpowa.tukmc.util;

import java.util.ArrayList;
import java.util.TreeMap;

import maxpowa.codebase.common.IOUtils;
import maxpowa.tukmc.mod_TukMC;
import net.minecraft.nbt.NBTTagCompound;

public class Config {

    public static TreeMap<String, Node> nodes = new TreeMap<String, Node>();
    public static ArrayList<String> nodekeys = new ArrayList<String>();

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
    public static final String NODE_SHOW_ARROWS = "showArrows";
    public static final String NODE_ITEMS_BACKGROUND = "itemsBackground";
    public static final String NODE_MCMMO = "mcmmo";
    public static final String NODE_FOOD_PREDICT = "foodPredict";
    public static final String NODE_DEFAULT_NAMEPLATE = "defaultNameplates";
    public static final String NODE_CHEAT_COMPASSCLOCK = "showCheatCompassClock";
    public static final String NODE_SMOOTH_TRANSITION = "smoothTransition";
    public static final String NODE_CUSTOM_BARS = "customBars";
    public static final String NODE_BLOCK_DISPLAY = "blockDisplay";
    public static final String NODE_BLOCK_DISPLAY_ID = "blockDisplayID";
    public static final String NODE_PLAIN_STATUS = "plainStatus";
    public static final String NODE_TOOLTIPS = "tooltips";
    public static final String NODE_TOOLTIP_ALWAYS_ON = "tooltipson";
    public static final String NODE_STATBAR = "statsbar";
    public static final String NODE_24HR_CLOCK = "24hrclock";
    public static final String NODE_INV_SLOT = "invslots";
    public static final String NODE_DIRECTION = "direction";
    public static final String NODE_DEGREES = "directiondegrees";
    public static final String NODE_ALT_STATUS = "altstatus";
    public static final String NODE_HEALTH_BARS = "healthBars";
    public static final String NODE_REPLACE_PING_WITH_TIME = "replacePing";
    public static final String NODE_HEALTHBAR_NO_TEXT = "healthbarText";

    static {
        new Node(NODE_RIGHT_BAR, "Right Bar - (FPS + Ping)", true);
        new Node(NODE_LEFT_BAR, "Left Bar - (Player Count + Status)", true);
        new Node(NODE_BOTTOM_ADORNMENTS, "Bottom Decorations", false);
        new Node(NODE_COLORBLIND_MODE, "Colorblind Mode", false);
        new Node(NODE_NUMERICAL_DAMAGE_DISPLAY,
                "Numerical Item Damage Display", false);
        new Node(NODE_STATUS_DISPLAY, "Armor Status Display", true);
        new Node(NODE_BUFFS, "Buffs - (Potion effects)", true);
        new Node(NODE_MUSIC, "Record Display - (Jukebox Song)", true);
        new Node(NODE_BOSS_BAR, "Boss Bar", true);
        new Node(NODE_TOP_BAR, "Top Bar", true);
        new Node(NODE_DANGER_DISPLAY, "Danger Zone Display", true);
        new Node(NODE_SHOW_ARROWS, "Show Arrows", true);
        new Node(NODE_ITEMS_BACKGROUND, "Item Bar Background", true);
        new Node(NODE_MCMMO, "McMMO Integration", true);
        new Node(NODE_FOOD_PREDICT, "Show Food Values - (Display wasted food)",
                true);
        new Node(NODE_DEFAULT_NAMEPLATE, "Use Default Nameplate", false);
        new Node(NODE_CHEAT_COMPASSCLOCK, "Display Compass & Clock", true);
        new Node(NODE_CUSTOM_BARS, "Custom Health/Food/XP Bars", true);
        new Node(NODE_BLOCK_DISPLAY, "Display Block at pointer", true);
        new Node(NODE_BLOCK_DISPLAY_ID, "Display Block ID with Block Name",
                true);
        new Node(NODE_PLAIN_STATUS, "Plain Status Bars", false);
        new Node(NODE_TOOLTIPS, "Tooltip Auto-Popup", false);
        new Node(NODE_TOOLTIP_ALWAYS_ON, "Tooltips Stay On FOREVER", false);
        new Node(NODE_STATBAR, "Side Stats Bar", false);
        new Node(NODE_SMOOTH_TRANSITION, "Smooth Bar Transitions", true);
        new Node(NODE_24HR_CLOCK, "24 Hour Clock", false);
        new Node(NODE_INV_SLOT, "Display Free Inv. Slots (Top)", true);
        new Node(NODE_DIRECTION, "Display Player Direction (Top)", true);
        new Node(NODE_DEGREES, "Display Player Direction + Degrees (Top)",
                false);
        new Node(NODE_ALT_STATUS, "Alternate Health/Food/XP Look", false);
        new Node(NODE_HEALTH_BARS, "Health Bars", true);
        new Node(NODE_REPLACE_PING_WITH_TIME, "Replace Ping with Time", false);
        new Node(NODE_HEALTHBAR_NO_TEXT, "Disable text above Health Bars", false);
        loadAllNodes();
    }

    public static boolean get(String value) {
        return !nodes.containsKey(value) ? false : nodes.get(value).isEnabled();
    }

    public static void loadNode(String nodeLabel) {
        if (!nodes.containsKey(nodeLabel))
            return;
        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(mod_TukMC.cacheFile);
        if (!cmp.hasKey("config"))
            return;
        NBTTagCompound configCmp = cmp.getCompoundTag("config");
        if (!configCmp.hasKey(nodeLabel))
            return;
        Node node = nodes.get(nodeLabel);
        node.set(configCmp.getBoolean(nodeLabel));
    }

    public static void loadAllNodes() {
        for (String node : nodes.keySet()) {
            loadNode(node);
            nodekeys.add(node);
        }
    }

    public static void saveNode(Node node) {
        NBTTagCompound cmp = IOUtils.getTagCompoundInFile(mod_TukMC.cacheFile);
        NBTTagCompound configCmp = cmp.hasKey("config") ? cmp
                .getCompoundTag("config") : new NBTTagCompound();
        configCmp.setBoolean(node.getLabel(), node.isEnabled());
        cmp.setCompoundTag("config", configCmp);
        IOUtils.injectNBTToFile(cmp, mod_TukMC.cacheFile);
        loadNode(node.getLabel());
    }

    public static int getSize() {
        return nodes.size();
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

    public String get(int selected) {
        return nodekeys.get(selected);
    }

    public String returnText(int selected) {
        return nodes.get(nodekeys.get(selected)).getDisplayName();
    }
}
