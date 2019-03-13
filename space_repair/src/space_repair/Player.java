package space_repair;

import java.util.ArrayList;
import static space_repair.GameInstance.TEX_PLAYER;
import static space_repair.GameInstance.TEX_PLAYER1;
import static space_repair.GameInstance.TEX_PLAYER2;
import static space_repair.GameInstance.TEX_PLAYER3;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import org.newdawn.slick.opengl.Texture;

public class Player implements Selectable {
    
    public static int RIGHT = 1,
                      DOWN = 2,
                      LEFT = 3,
                      UP = 4;
    
    private static final int r = 13;
    
    private GameInstance game;
    private int x, y, direction;
    
    //the amount of health points that are added per frame
    private double repairRate;
    
    //the amount of pixels that the player is moved in one frame
    private int moveValue;
    
    //a list of powerups under which the player is currently beeing affected by
    private ArrayList<Powerup> effects;
    
    public int getMoveValue() { return moveValue; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Coordinate getCoord() { return new Coordinate(x,y); }
    public double getRawRepairRate() { return repairRate; }
    public double getRepairRate() { return repairRate*GameInstance.FPS; }
    
    public void setMoveValue(int inMV) { moveValue = inMV; }
    public void setX(int ix) { x = ix; }
    public void setY(int iy) { y = iy; }
    public void setRawRepairRate(double inRate) { repairRate = inRate; }
    public void setRepairRate(double inRate) { 
        repairRate = inRate*(1.0/GameInstance.FPS);
    }
    public void addEffect(Powerup inP) { effects.add(inP); }
    public void removeEffect(Powerup inP) { effects.remove(inP); }
    
    public Player(int ix, int iy, GameInstance im) {
        game = im;
        moveValue = 6;
        x = ix;
        y = iy;
        this.setRepairRate(10);
        direction = LEFT;
        effects = new ArrayList<Powerup>();
    }
    
    //the player is treated as a circle when powerups are concerned
    public boolean withinRadius(Coordinate coord) {
        double distance = Math.sqrt(Math.pow((coord.x-x), 2) + Math.pow((coord.y-y), 2));
        if(distance < r){//(r+Powerup.FIXED_RADIUS)*(3/4)) {
            return true;
        }
        return false;
    }
    
    public void checkEffect() {
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for(int i = 0; i < effects.size(); i++) {
            Powerup p = effects.get(i);
            if(p.checkEffect())
                indexes.add(i);
        }
        
        int w = 0;
        for(Integer u : indexes) {
            effects.remove(u-w);
            w++;
        }
    }
    
    public RepairModule isNearRepair() {
        ArrayList<RepairModule> repairs = game.getRepairs();
        for(RepairModule r : repairs) {
            if(r.isNear(x, y, 17)) {
                return r;
            }
        }
        return null;
    }
    
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }
    
    public void gameMove(int inDirect) {
        if(inDirect == RIGHT)
            this.gameMove(moveValue, 0);
        else if(inDirect == DOWN)
            this.gameMove(0, moveValue);
        else if(inDirect == LEFT)
            this.gameMove(0-moveValue, 0);
        else if(inDirect == UP)
            this.gameMove(0, 0-moveValue);
        direction = inDirect;
    }
    public void gameMove(int dx, int dy) {
        int[] oldCoords = this.getCoords();
        int[] newCoords = this.getCoords();
        for(int i = 0; i < newCoords.length; i+=2) {
            newCoords[i] += dx;
            newCoords[i+1] += dy;
        }
        ArrayList<Wall> walls = game.getWalls();
        for( Wall w : walls ) {
            for(int i = 0; i < newCoords.length; i+=2) {
                int x = oldCoords[i];
                int y = oldCoords[i+1];
                Square wallSquare = w.getSquare();
                if(w.getSquare().withinBounds(newCoords[i], newCoords[i+1])) {
                    if(dx > 0) {
                        dx = wallSquare.topLeftCoord.x - x-1;
                    } else if(dx < 0) {
                        dx = wallSquare.bottomRightCoord.x - x+1;
                    } else if(dy > 0) {
                        dy = wallSquare.topLeftCoord.y - y-1;
                    } else if(dy < 0) {
                        dy = wallSquare.bottomRightCoord.y - y+1;
                    }
                    break;
                }
            }
        }
        x += dx;
        y += dy;
    }
    
    public void draw() {
        //glColor3f(0,0,255f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Texture t = game.getTexture(TEX_PLAYER);
        if(direction == UP)
            t = game.getTexture(TEX_PLAYER);
        else if(direction == RIGHT)
            t = game.getTexture(TEX_PLAYER1);
        else if(direction == DOWN)
            t = game.getTexture(TEX_PLAYER2);
        else if(direction == LEFT)
            t = game.getTexture(TEX_PLAYER3);
        Square renderSquare = game.convertSquare(new Square(x-r,y-r,x+r,y+r));
        game.drawSquare(renderSquare, t);
        glDisable(GL_BLEND);
    }
    
    public Square getSquare() {
        return new Square(x-r,y-r,x+r,y+r);
    }
    
    //returns coordinates of all four vertices
    private int[] getCoords() {
        int[] coords = new int[8];
        coords[0] = x-r;
        coords[1] = y-r;
        coords[2] = x+r;
        coords[3] = y-r;
        coords[4] = x+r;
        coords[5] = y+r;
        coords[6] = x-r;
        coords[7] = y+r;
        return coords;
    }
}
