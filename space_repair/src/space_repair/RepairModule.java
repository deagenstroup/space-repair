package space_repair;

import static org.lwjgl.opengl.GL11.glColor4f;
import org.newdawn.slick.Color;
import static space_repair.GameInstance.TEX_REPAIR;

public class RepairModule extends Wall {
    public static final int MAX_HEALTH = 100,
                            FLASH_TIME = 2000,
                            FLASH_PERIOD = 400;
    
    private double health;
    private int flashStart, flashEnd;
    
    public double getHealth() { return health; }
    public void setHealth(int inH) { health = inH; }
    
    public RepairModule(GameInstance inGame, Square inSquare) {
        super(inGame, inSquare);
        health = MAX_HEALTH;
    }
    
    public void drainHealth(double inAmount) {
        if(health > 0)
            health -= inAmount;
        if(health < 0)
            health = 0;
    }
    public void addHealth(double inAmount) {
        if(health < MAX_HEALTH)
            health += inAmount; 
        if(health > MAX_HEALTH)
            health = MAX_HEALTH;
    }
    
    public double damage(double inDamage) {
        this.initiateFlash();
        this.drainHealth(inDamage);
        return health;
    }
    
    //preforms a flash animation alternating between the repairmodule being
    //highlighted red and not being highlighted for a period of time
    //used to indicate that it has been struck by a asteriod
    public void initiateFlash() {
        flashStart = game.getTime();
        highlight = 2;
        flashEnd = game.getTime() + FLASH_TIME;
    }
    
    public void draw() {
        if(highlight == 1) {
            glColor4f(0, 0, 180f, 0.8f);
        } else if(highlight == 2) {
            if(game.getTime() < flashEnd) {
                if(((game.getTime()-flashStart)/FLASH_PERIOD)%2 == 1) {
                    glColor4f(1,1,1,1);
                }
                else {
                    glColor4f(255f,0,0,0.8f);
                }
            } else {
                highlight = 0;
            }
        } else {
            glColor4f(1,1,1,1);
        }
        game.drawSquare(game.convertSquare(square), game.getTexture(TEX_REPAIR));
        if(highlight == 1) {
            Coordinate middle = game.convertSquare(square).getMiddle();
            Color c = Color.red;
            if(health > 66.6)
                c = Color.green;
            else if(health > 33.3)
                c = Color.yellow;
            else
                c = Color.red;
            GameInstance.newDrawString(game.getSmallFont(), middle.x, middle.y, (int)health/1+"%", c);
        }
        glColor4f(1,1,1,1);
        if(highlight == 1)
            highlight = 0;
    }
    
    public void print() {
        System.out.println("gametime: " + game.getTime());
        System.out.println("flash: " + flashStart + " - " + flashEnd);
    }
}
