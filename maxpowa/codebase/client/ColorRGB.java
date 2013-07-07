package maxpowa.codebase.client;

import net.minecraft.client.renderer.Tessellator;

public final class ColorRGB {

    private int red;
    private int green;
    private int blue;

    public ColorRGB(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public String toString() {
        return toHex(getRed()) + toHex(getGreen()) + toHex(getBlue());
    }

    public int getRed() {
        return clamp(red);
    }

    public int getGreen() {
        return clamp(green);
    }

    public int getBlue() {
        return clamp(blue);
    }

    public float getRedF() {
        return getRed() / 255F;
    }

    public float getGreenF() {
        return getGreen() / 255F;
    }

    public float getBlueF() {
        return getBlue() / 255F;
    }

    public static int clamp(int i) {
        return i > 255 ? 255 : i < 0 ? 0 : i;
    }

    public void colorizeTessellator(Tessellator tessellator) {
        colorizeTessellator(tessellator, 255);
    }

    public void colorizeTessellator(Tessellator tessellator, int alpha) {
        tessellator.setColorRGBA(getRed(), getGreen(), getBlue(), alpha);
    }

    public static String toHex(int i) {
        String s = Integer.toHexString(i & 0xff);
        while (s.length() < 2) {
            s = s.concat("0");
        }

        return s.toUpperCase();
    }

}
