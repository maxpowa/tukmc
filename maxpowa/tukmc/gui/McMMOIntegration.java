package maxpowa.tukmc.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maxpowa.codebase.common.ColorCode;
import maxpowa.codebase.common.CommonUtils;
import maxpowa.tukmc.gui.McMMOIntegration.SkillData.UsageType;
import net.minecraft.client.gui.GuiMainMenu;

public final class McMMOIntegration {

    public static Map<String, String> toolSkillsMap = new HashMap<String,String>();
    public static Map<String, String> skillToolsMap = new HashMap<String, String>();

    public static List<SkillData> skillData = new ArrayList<SkillData>();

    private static LevelUpData activeLevelUpData;
    private static int time;

    static {
        registerSkill("AXE", "Tree Feller");
        registerSkill("AXE", "Skull Splitter");
        registerSkill("PICKAXE", "Super Breaker");
        registerSkill("SHOVEL", "Giga Drill Breaker");
        registerSkill("HOE", "Green Terra");
        registerSkill("FISTS", "Berserk");
        registerSkill("SWORD", "Serrated Strikes");
    }

    public static void registerSkill(String tool, String skillName) {
        toolSkillsMap.put(tool, skillName);
        skillToolsMap.put(skillName, tool);
    }

    public static void setLevelUpData(LevelUpData data) {
        time = 50;
        activeLevelUpData = data;
    }

    public static void addSkillData(SkillData data) {
        skillData.add(data);
    }

    public static LevelUpData getActiveLevelUpData() {
        return time <= 0 ? null : activeLevelUpData;
    }

    public static boolean passMessage(String s) {
        if (s.startsWith(ColorCode.GREY + "**YOU LOWER YOUR")) {
            String tool = s.substring(
                    (ColorCode.BRIGHT_GREEN + "**YOU LOWER YOUR ").length(),
                    s.length() - 2);
            for (SkillData data : skillData)
                if (data.type == UsageType.READY
                        && data.tool.equalsIgnoreCase(tool)) {
                    data.remove = true;
                }
            return true;
        }
        if (s.startsWith(ColorCode.RED + "**") && s.endsWith("has worn off**")) {
            String ability = s.substring((ColorCode.RED + "**").length(),
                    s.length() - "has worn off**".length() - 1);
            for (SkillData data : skillData)
                if (data.type == UsageType.ACTIVE
                        && data.name.equalsIgnoreCase(ability)) {
                    data.type = UsageType.COOLDOWN;
                }
            return true;
        }
        if (s.startsWith(ColorCode.BRIGHT_GREEN + "Your " + ColorCode.YELLOW)
                && s.endsWith("ability is refreshed!")) {
            String skill = s
                    .substring(
                            (ColorCode.BRIGHT_GREEN + "Your " + ColorCode.YELLOW)
                                    .length(),
                            s.length()
                                    - (ColorCode.BRIGHT_GREEN + "ability is refreshed!")
                                            .length());
            System.out.println("parsed: *" + skill + "*");
            for (SkillData data : skillData)
                if (data.type == UsageType.COOLDOWN
                        && data.getName().equalsIgnoreCase(skill)) {
                    data.remove = true;
                }
            return true;
        }

        return false;
    }

    public static void tick() {
        if (CommonUtils.getMc().currentScreen != null
                && CommonUtils.getMc().currentScreen instanceof GuiMainMenu) {
            skillData.clear();
            time = 0;
        }
        if (time > 0) {
            --time;
        }
        List<SkillData> removeThis = new ArrayList<SkillData>();
        for (SkillData skill : skillData)
            if (skill.remove) {
                removeThis.add(skill);
            }
        skillData.removeAll(removeThis);
    }

    public static class SkillData {

        private String tool;
        private String name;
        public UsageType type;
        private boolean remove = false;

        public SkillData(String tool, String name, UsageType type) {
            this.tool = tool;
            this.name = name;
            this.type = type;
        }

        public String getTool() {
            return tool;
        }

        public String getName() {
            return name;
        }

        public static SkillData fromString(String s) {
            if (s.startsWith(ColorCode.BRIGHT_GREEN + "**YOU READY YOUR")) {
                String tool = s
                        .substring(
                                (ColorCode.BRIGHT_GREEN + "**YOU READY YOUR ")
                                        .length(), s.length() - 2);
                return new SkillData(tool, toolSkillsMap.get(tool),
                        UsageType.READY);
            }
            if (s.startsWith(ColorCode.BRIGHT_GREEN + "**")
                    && s.endsWith(" ACTIVATED**")) {
                String skill = s.substring(
                        (ColorCode.BRIGHT_GREEN + "**").length(), s.length()
                                - "ACTIVATED**".length() - 1);
                for (String skill1 : skillToolsMap.keySet())
                    if (skill1.equalsIgnoreCase(skill))
                        return new SkillData(skillToolsMap.get(skill1), skill1,
                                UsageType.ACTIVE);
            }

            return null;
        }

        public static enum UsageType {

            READY("Ready"), ACTIVE("Active"), COOLDOWN("On Cooldown");

            private UsageType(String name) {
                this.name = name;
            }

            private String name;

            public String getName() {
                return name;
            }
        }
    }

    public static class LevelUpData {

        private String skill;
        private short level;

        public LevelUpData(String skill, short level) {
            this.skill = skill;
            this.level = level;
        }

        public String getSkill() {
            return skill;
        }

        public short getLevel() {
            return level;
        }

        public static LevelUpData fromString(String s) {
            if (s.startsWith("" + ColorCode.YELLOW)
                    && s.contains("skill increased by 1. Total (")
                    && s.endsWith(")")) {
                String[] splitted = s.substring(2).split(" ");
                String skill = splitted[0];
                short level = Short.parseShort(splitted[splitted.length - 1]
                        .substring(1,
                                splitted[splitted.length - 1].length() - 1)
                        .replaceAll(",", ""));

                return new LevelUpData(skill, level);
            } else
                return null;
        }
    }
}
