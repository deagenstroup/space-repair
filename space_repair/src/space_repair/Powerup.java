
package space_repair;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public abstract class Powerup implements Selectable {
    public static final int RESPAWN_TIME = 23000;
    public static int radius = 5;
    
    protected Coordinate pos;
    protected RGBColor clr;
    protected GameInstance game;
    protected boolean depleted;
    protected int effectTime;
    
    private int absorbTime;
    
    public Coordinate getCoord() { return pos; }
    public boolean getDepleted() { return depleted; }
    public void setDepeleted(boolean inD) { depleted = inD; }
    
    public Powerup(GameInstance inGame, Coordinate inCoord) {
        game = inGame;
        pos = inCoord;
        depleted = false;
        clr = new RGBColor(255f, 255f, 255f);
    }
    public void draw() {
        glDisable(GL_TEXTURE_2D);
        clr.setColorMode();
        Coordinate renderPos = game.convertCoord(pos);
        GameInstance.drawFilledCircle(renderPos.x, renderPos.y, radius);
        glEnable(GL_TEXTURE_2D);
    }
    
    public boolean withinBounds(Coordinate coord) {
        double distance = Math.sqrt(Math.pow((coord.x-pos.x), 2) + Math.pow((coord.y-pos.y), 2));
        if(distance < radius){
            return true;
        }
        return false;
    }
    public void move(int ix, int iy) {
        pos.x += ix;
        pos.y += iy;
    }
    public boolean checkEffect() {
        if(game.getTime() - absorbTime >= effectTime) {
            this.undoEffect();
            return true;
        } else
            return false;
    }
    public void checkRespawn() {
        if(game.getTime() - absorbTime >= RESPAWN_TIME) {
            depleted = false;
        }
    }
    public void absorb() {
        depleted = true;
        absorbTime = game.getTime();
        game.getPlayer().addEffect(this);
        this.doEffect();
    }
    
    public abstract int getType();
    public abstract void doEffect();
    public abstract void undoEffect();
}
