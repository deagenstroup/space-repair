package space_repair;

import static org.lwjgl.opengl.GL11.glColor4f;

public class RGBColor {
    public float r, g, b;
    public RGBColor(float ir, float ig, float ib) {
        r = ir;
        g = ig;
        b = ib;
    }
    public void setColorMode() {
        glColor4f(r, g, b, 0);
    }
}
