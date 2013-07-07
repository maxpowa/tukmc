package maxpowa.codebase.common;

import maxpowa.codebase.client.ColorRGB;
import maxpowa.tukmc.util.TukMCReference;

public enum ColorCode {

    BLACK('0', new ColorRGB(0, 0, 0)), DARK_BLUE('1', new ColorRGB(0, 0, 170)), DARK_GREEN(
            '2', new ColorRGB(0, 170, 0)), DARK_AQUA('3', new ColorRGB(0, 170,
            170)), DARK_RED('4', new ColorRGB(170, 0, 0)), PURPLE('5',
            new ColorRGB(170, 0, 170)), GOLD('6', new ColorRGB(170, 170, 0)), GREY(
            '7', new ColorRGB(170, 170, 170)), DARK_GREY('8', new ColorRGB(65,
            65, 65)), INDIGO('9', new ColorRGB(65, 65, 255)), BRIGHT_GREEN('a',
            new ColorRGB(65, 255, 65)), AQUA('b', new ColorRGB(65, 255, 255)), RED(
            'c', new ColorRGB(255, 65, 65)), PINK('d', new ColorRGB(255, 65,
            255)), YELLOW('e', new ColorRGB(255, 255, 65)), WHITE('f',
            new ColorRGB(255, 255, 255));

    private ColorCode(char color, ColorRGB rgb) {
        this.color = color;
        this.rgb = rgb;
    }

    char color;
    ColorRGB rgb;

    public ColorRGB asRGB() {
        return rgb;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(TukMCReference.FORMATTING_CODE_CHAR)
                .append(color).toString();
    }
}
