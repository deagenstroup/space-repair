package space_repair;

import java.awt.Font;
import java.awt.HeadlessException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import mainmenu.Options;
import mainmenu.Window;
import static space_repair.Player.DOWN;
import static space_repair.Player.LEFT;
import static space_repair.Player.RIGHT;
import static space_repair.Player.UP;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertex2i;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class GameInstance {
    //GENERAL VARIABLES (used in both modes)
    public static final int TEX_RECT = 0,
                            TEX_PLAYER = 1,
                            TEX_PLAYER1 = 2,
                            TEX_PLAYER2 = 3,
                            TEX_PLAYER3 = 4,
                            TEX_GREENLIGHT = 5,
                            TEX_REDLIGHT = 6,
                            TEX_BACKGROUND = 7,
                            TEX_REPAIR = 8,
                            
                            SOUND_DAMAGE = 0,
                            SOUND_DESTROYED = 1,
            
                            FPS = 60;
    
    private int levelWidth, levelHeight, screenWidth, screenHeight;
    
    //the plain text file name of the level
    private String levelname, theme;
    
    private Window window;
    private ArrayList<Wall> walls;
    private ArrayList<RepairModule> repairs;
    private Player player;
    private ArrayList<Powerup> powers;
    private Texture[] textures;
    
    //GAME VARIABLES
    //how long the player has to prepare at the begining of the level(milliseconds)
    public static final int COUNTDOWN = 0000,
                            
                            //how often the game jumps in difficulty
                            LEVEL_PERIOD = 30000;
    
                //the time at which the game began
    private int startTime,
            
                level;
    
    //ther percent chance at any given second for a repair module to break
    private double breakProb;
    
    //true if the player has won the level by reaching the winning boundries
    private boolean hasWon,
            
                    //true if the game has not yet begun and red light is still
                    //shown
                    countdown,
                    
                    loop;
    
    //used to calculate how big the light is
    private int lightRad = 100;
    
    private Coordinate light;
    private Audio[] sounds;
    private TrueTypeFont bigFont, smallFont;
    
    //EDITOR MODE VARIABLES
    //the different modes the user can be in
    public static final int BOX = 1,
                            WIN_BOX = 2,
                            SPAWN = 3,
                            LIGHT_PU = 4,
                            SPEED_PU = 5,
                            REPAIR_PU = 6,
                            COMREPAIR_PU = 7;
    
    
    //distance in pixels required for a wall to snap to another wall
    public static final int snapDistance = 7;
    
    //remainders of x and y for use when moving selected item in editor mode
    private double remX, remY;
    
    private int mode,
            
                //if the selcted item is a wall, this signifies where in the
                //walls arraylist it is
                selectedItemIndex,
            
                //signifes which corner is selected, if any at all, 0 being none selected
                cornerSelect, 
            
                //signifies which side is selected, if any at all, 0 being none selected
                sideSelect;
    
    private boolean spawnPlaced,
                    winPlaced,
                    
                    //true if something is selected
                    selected, 
                    
                    //true if the user is in the middle of creating a wall or winning area
                    creatingBox,
            
                    mouseClick,
                    
                    //true if the name of the level needs to be added to the
                    //level list
                    addName,
            
                    //if true, the snapping feature is turned on
                    snapping;
    
    private Selectable selectedItem;
    
    public int getLightRadius() { return lightRad; }
    public int getWidth() { return levelWidth; }
    public int getHeight() { return levelHeight; }
    public TrueTypeFont getSmallFont() { return smallFont; }
    public Player getPlayer() { return player; }
    public ArrayList<RepairModule> getRepairs() { return repairs; }
    public int getTime() { return window.getTime() - startTime; }
    public Texture getTexture(int i) { return textures[i]; }
    public Audio getSound(int i) { return sounds[i]; }
    public ArrayList<Wall> getWalls() {
        ArrayList<Wall> retList = new ArrayList<Wall>();
        retList.addAll(walls);
        retList.addAll(repairs);
        return retList; 
    }
    
    public void changeLevelName(String inName) { levelname = inName; }
    public void changeLightRadius(int inRad) { lightRad = inRad; }
    public void setLoop(boolean inBool) { loop = inBool; }
    
    public GameInstance(Window inWin, Options inOpt) {
        window = inWin;
        theme = "space";
        screenWidth = inOpt.resWidth;
        screenHeight = inOpt.resHeight;
        textures = new Texture[9];
        sounds = new Audio[2];
        
        walls = new ArrayList<Wall>();
        repairs = new ArrayList<RepairModule>();
        player = new Player( 14, 14, this );
        powers = new ArrayList<Powerup>();
        hasWon = false;
        countdown = true;
        light = new Coordinate(player.getX(), player.getY());       
        level = 1;
        breakProb = 2.5*6.66666666666666;
    }
    
    public void playGame() {
        this.pickLevel(false);
        
        if(screenWidth == 0 && screenHeight == 0) {
            screenWidth = levelWidth;
            screenHeight = levelHeight;
        }
        
        startTime = window.getTime();
        
        try {
            Display.setDisplayMode(new DisplayMode(screenWidth, screenHeight));
            Display.setTitle("Maze");
            Display.create();
        } catch(LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
        
        //the font scales with the diagnol screen size(NEEDS TO BE AFTER DISPLAY CODE)
        double c = Math.sqrt(Math.pow(screenHeight,2) + Math.pow(screenWidth,2));
        bigFont = new TrueTypeFont(new Font("Times New Roman", Font.BOLD, 
                                  (int)(c*(50.0/2037.7399245242264))), false);
        smallFont = new TrueTypeFont(new Font("Courier New", Font.BOLD, 
                                  (int)(c*(30.0/2037.7399245242264))), false);
        
        //Powerup.radius = (int)(c*(5.0/565.685424949238));
        
        //glMatrixMode(GL_PROJECTION);
        //glLoadIdentity();
        glMatrixMode(GL_PROJECTION);
        glOrtho(0, screenWidth, screenHeight, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_TEXTURE_2D);
        
        this.initTexs();
        this.initSounds();
        
        while(!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT);
            
            //debugging input
            while(Keyboard.next()) {
                int x = Keyboard.getEventKey();
                if(Keyboard.getEventKeyState()) {
                    if(x == Keyboard.KEY_1) {
                        player.setMoveValue(player.getMoveValue()+1);
                        System.out.println("moveValue: " + player.getMoveValue());
                    } else if(x == Keyboard.KEY_2) {
                        player.setMoveValue(player.getMoveValue()-1);
                        System.out.println("moveValue: " + player.getMoveValue());
                    //} else if(x == Keyboard.KEY_SPACE) {
                    //    System.out.println("x: " + player.getX() + "  y: " + player.getY());
                    } else if(x == Keyboard.KEY_Y) {
                        System.out.println("this.getTime(): " + this.getTime());
                    } else if(x == Keyboard.KEY_3) {
                        System.out.println("isNear: " + repairs.get(0).isNear(player.getX(), player.getY(), 15));
                    } else if(x == Keyboard.KEY_4) {
                        repairs.get(0).initiateFlash();
                    } else if(x == Keyboard.KEY_5) {
                        repairs.get(1).initiateFlash();
                    } else if(x == Keyboard.KEY_6) {
                        repairs.get(2).initiateFlash();
                    } else if(x == Keyboard.KEY_7) {
                        this.randomAsteriod();
                    } else if(x == Keyboard.KEY_8) {
                        System.out.println("repairRate: " + this.getPlayer().getRepairRate());
                    }
                }
            }
            
            RepairModule rp = null;
            
            if(repairs.size() == 0 && !hasWon) {
                int sec = (this.getTime()-COUNTDOWN) / 1000;
                int msec = (this.getTime()-COUNTDOWN) % 1000;
                String s = "Game Over\nTime Lasted: " + sec + "." + msec + " seconds";
                JOptionPane.showMessageDialog(window, s);
                hasWon = true;
            }
            if(!hasWon) {
                //main controls
                if(!countdown) {
                    if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
                        player.gameMove(RIGHT);
                    }
                    if(Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
                        player.gameMove(LEFT);
                    }
                    if(Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)) {
                        player.gameMove(UP);
                    }
                    if(Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
                        player.gameMove(DOWN);
                    }
                }

                //repair controls and functionality
                rp = player.isNearRepair();
                if(rp != null) {
                    rp.highlight(1);
                    if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                        rp.addHealth(player.getRawRepairRate());
                    } else if(Keyboard.isKeyDown(Keyboard.KEY_Z)) {
                        rp.drainHealth(player.getRawRepairRate());
                    }
                }

                player.checkEffect();

                if(this.getTime() > (startTime + LEVEL_PERIOD*level)) {
                    level++;
                    breakProb *= 1.666;
                    System.out.println("breakProb: " + breakProb);
                    //System.out.println("time: " + this.getTime() + "   level: " + level);
                }

                double frameChance = breakProb / (double)FPS;
                if(randomChance(frameChance)) {
                    this.randomAsteriod();
                }
            }
            //drawing the background
            glColor4f(1,1,1,1);
            drawSquare(new Square(0, 0, screenWidth, screenHeight), textures[TEX_BACKGROUND]);
            
            //dectects colliosions between players and powerups and takes action
            //upon those collisions or draws powerups
            for(int i = 0; i < powers.size(); i++) {
                Powerup p = powers.get(i);
                if(!p.getDepleted()) {
                    if(player.withinRadius(p.getCoord())) {
                        p.absorb();
                    } else {
                        p.draw();
                    }
                } else {
                    p.checkRespawn();
                }
                
            }
            
            //drawing the walls
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            textures[TEX_RECT].bind();
            for(Wall w : walls) {
                w.draw();
            }
            player.draw();
            
            //drawing the repair modules
            for(RepairModule rep : repairs)
                rep.draw();
            
            if(!countdown) {
                light.x = Mouse.getX();
                light.y = screenHeight - Mouse.getY();
            } else {
                light = this.convertCoord(player.getCoord());
            }
            
            //draw the light
            glDisable(GL_TEXTURE_2D);
            drawLight(lightRad);
            glEnable(GL_TEXTURE_2D);
            
            //drawing the repair modules
            for(RepairModule rep : repairs) {
                if(rep.getHighlight() == 2)
                    rep.draw();
            }
            
            //drawing the instructions to repair
            if(rp != null && rp.getHealth() < RepairModule.MAX_HEALTH) {
                float yPos = 0;
                if(player.getY() < levelHeight/2) {
                    yPos = (float)((double)screenHeight*(14.0/15.0));
                } else {
                    yPos = (float)((double)screenHeight*(1.0/15.0));
                }
                newDrawString(bigFont, (int)((double)screenWidth/2.0), (int)yPos,
                        "Press space to repair module",
                        Color.white);
            }
            
            //drawing the countdown light(if needed)
            if(countdown) {
                if(this.getTime() > COUNTDOWN) {
                    countdown = false;
                } else {
                    textures[TEX_REDLIGHT].bind();
                    this.drawLightStop(new Coordinate(screenWidth/2, screenHeight/2), 40);
                }
            } else if(this.getTime() <= COUNTDOWN+1000) {
                textures[TEX_GREENLIGHT].bind();
                this.drawLightStop(new Coordinate(screenWidth/2, screenHeight/2), 40);
            }
            
            Display.update();
            Display.sync(FPS);
        }
        for(Texture t : textures) {
            t.release();
        }
        //for(Audio a : sounds) {
        //    a.release();
        //}
        Display.destroy();
    }
    
    public void launchEditor() {
        this.pickLevel(true);
        
        if(levelname.equals("*NewLevel*")) {
            addName = true;
            String input;
            try {
                input = JOptionPane.showInputDialog("Enter levelWidth: ");
                levelWidth = Integer.parseInt(input);
                input = JOptionPane.showInputDialog("Enter levelHeight: ");
                levelHeight = Integer.parseInt(input);
            } catch( NumberFormatException e ) {
                System.out.println("Error: input provided was not an integer");
                return;
            }
            if(levelWidth < 0 || levelHeight < 0) {
                System.out.println("Error: input was negative");
                return;
            }
            
            try { 
                levelname = JOptionPane.showInputDialog(window, "Enter name of level: ");
            } catch(HeadlessException e) {
                System.out.println("Error getting filename input");
                levelname = "level";
            }
            //adding walls around the edge of the window so the player cannot leave
            //the window
            walls.add(new Wall(this, new Square(new Coordinate(0,-100),new Coordinate(levelWidth,0))));
            walls.add(new Wall(this, new Square(new Coordinate(levelWidth,0),new Coordinate(levelWidth+100,levelHeight))));
            walls.add(new Wall(this, new Square(new Coordinate(0,levelHeight),new Coordinate(levelWidth,levelHeight+100))));
            walls.add(new Wall(this, new Square(new Coordinate(-100,0),new Coordinate(0,levelHeight))));
            
            player = null;
        } else {
            addName = false;
        }
        
        
        snapping = true;
        
        try {
            Display.setDisplayMode(new DisplayMode(screenWidth, screenHeight));
            Display.setTitle("Level Editor");
            Display.create();
        } catch(LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
        
        //the font scales with the diagnol screen size(NEEDS TO BE AFTER DISPLAY CODE)
        double C = Math.sqrt(Math.pow(screenHeight,2) + Math.pow(screenWidth,2));
        smallFont = new TrueTypeFont(new Font("Courier New", Font.BOLD, 
                                  (int)(C*(30.0/2037.7399245242264))), false);
        this.initTexs();
        
        glMatrixMode(GL_PROJECTION);
        //glLoadIdentity();
        glOrtho(0, screenWidth, screenHeight, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        
        while(!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT);
            
            while(Keyboard.next()) {
                int x = Keyboard.getEventKey();
                if(Keyboard.getEventKeyState()) {
                    if(x == Keyboard.KEY_D && selected) {
                        if(selectedItemIndex >= 0) {
                            if(selectedItem instanceof Powerup) {
                                powers.remove(selectedItemIndex);
                            } else if(selectedItem instanceof RepairModule) {
                                repairs.remove(selectedItemIndex);
                            } else if(selectedItem instanceof Wall) {
                                walls.remove(selectedItemIndex);
                            }
                            selectedItemIndex = -1;
                        }
                        else if(selectedItemIndex == -2) {
                            player = null;
                            spawnPlaced = false;
                        }
                        selected = false;
                        selectedItem = null;
                    } else if(x == Keyboard.KEY_1) {
                        mode = BOX;
                    } else if(x == Keyboard.KEY_2) {
                        mode = WIN_BOX;
                    } else if(x == Keyboard.KEY_3) {
                        mode = SPAWN;
                    } else if(x == Keyboard.KEY_4) {
                        mode = LIGHT_PU;
                    } else if(x == Keyboard.KEY_5) {
                        mode = SPEED_PU;
                    } else if(x == Keyboard.KEY_6) {
                        mode = REPAIR_PU;
                    } else if(x == Keyboard.KEY_7) {
                        mode = COMREPAIR_PU;
                    } else if(x == Keyboard.KEY_S) {
                        this.writeToFile();
                        addName = false;
                    } else if(x == Keyboard.KEY_TAB && Keyboard.getEventKeyState()) {
                        snapping = !snapping;
                        System.out.println("snapping: " + snapping);
                    }
                    System.out.println("Mode: " + mode);
                }
            }
            
            int x = Mouse.getX();
            int y = screenHeight-Mouse.getY();
            Coordinate c = this.convertCoordToGame(new Coordinate(x,y));
            x = c.x;
            y = c.y;
            
            if(selected) {
                if(cornerSelect != 0) {
                    ((Wall)selectedItem).changeCorner(cornerSelect, x, y);
                } else if(sideSelect != 0) {
                    ((Wall)selectedItem).changeSide(sideSelect, x, y);
                } 
                else {
                    Coordinate dc = this.convertCoordToGameSpecial(new Coordinate(Mouse.getDX(), -Mouse.getDY()));
                    int dx = dc.x;
                    int dy = dc.y;
                    selectedItem.move(dx, dy);

                    //snapping system
                    if(snapping) {
                        if(selectedItem instanceof Wall) {
                            Wall selectedWall = (Wall) selectedItem;
                            for(Wall w : walls) {
                                if(selectedWall != w) {
                                    int temp;
                                    temp = selectedWall.square.topLeftCoord.x - w.square.bottomRightCoord.x;
                                    if(Math.abs(temp) < snapDistance && (selectedWall.square.topLeftCoord.y<w.square.bottomRightCoord.y && selectedWall.square.bottomRightCoord.y>w.square.topLeftCoord.y))
                                        selectedWall.move(-temp, 0);

                                    temp = selectedWall.square.topLeftCoord.y - w.square.bottomRightCoord.y;
                                    if(Math.abs(temp) < snapDistance && (selectedWall.square.topLeftCoord.x<w.square.bottomRightCoord.x && selectedWall.square.bottomRightCoord.x>w.square.topLeftCoord.x))
                                        selectedWall.move(0, -temp);

                                    temp = w.square.topLeftCoord.x - selectedWall.square.bottomRightCoord.x;
                                    if(Math.abs(temp) < snapDistance && (selectedWall.square.topLeftCoord.y<w.square.bottomRightCoord.y && selectedWall.square.bottomRightCoord.y>w.square.topLeftCoord.y))
                                        selectedWall.move(temp, 0);

                                    temp = w.square.topLeftCoord.y - selectedWall.square.bottomRightCoord.y;
                                    if(Math.abs(temp) < snapDistance && (selectedWall.square.topLeftCoord.x<w.square.bottomRightCoord.x && selectedWall.square.bottomRightCoord.x>w.square.topLeftCoord.x))
                                        selectedWall.move(0, temp);

                                }
                            }
                        }
                    }
                }
                    
                if(!Mouse.isButtonDown(0)) {
                    if(cornerSelect != 0 || sideSelect != 0) {
                        ((Wall)selectedItem).checkVerts();
                    }
                    selectedItem = null;
                    selected = false;
                    selectedItemIndex = -1;
                    cornerSelect = 0;
                    sideSelect = 0;
                }
            } 
            
            else {
                if(Mouse.isButtonDown(0)) {
                    remX = 0.0;
                    remY = 0.0;
                    if( player != null && player.getSquare().withinBounds(x, y) ) {
                        selectedItem = player;
                        selectedItemIndex = -2;
                        selected = true;
                    } else {
                        boolean skip = false;
                        for(int i = 0; i < walls.size(); i++) {
                            Wall b = walls.get(i);
                            if(b.getSquare().withinBounds(x, y)) {
                                selectedItem = b;
                                selected = true;
                                selectedItemIndex = i;
                                cornerSelect = b.getCornerNumber(x, y);
                                sideSelect = b.getSideNumber(x, y);
                                skip = true;
                                break;
                            }
                        }
                        if(!skip) {
                            for(int i = 0; i < repairs.size(); i++) {
                                RepairModule w = repairs.get(i);
                                if(w.getSquare().withinBounds(x, y)) {
                                    selectedItem = w;
                                    selected = true;
                                    selectedItemIndex = i;
                                    cornerSelect = w.getCornerNumber(x, y);
                                    sideSelect = w.getSideNumber(x, y);
                                    skip = true;
                                    break;
                                }
                            }
                        }
                        if(!skip) {
                            for(int i = 0; i < powers.size(); i++) {
                                Powerup p = powers.get(i);
                                if(p.withinBounds(new Coordinate(x, y))) {
                                    //System.out.println("test");
                                    selectedItem = p;
                                    selected = true;
                                    selectedItemIndex = i;
                                }
                            }
                        }
                    }
                }
                
                if(mode == BOX || mode == WIN_BOX) {
                    if(creatingBox) {
                        if(Mouse.isButtonDown(1)) {
                            ((Wall)selectedItem).update(x, y);
                        } else {
                            ((Wall)selectedItem).checkVerts();
                            creatingBox = false;
                        }
                    } else {
                        if(Mouse.isButtonDown(1)) {
                            if(mode == BOX) {
                                walls.add(new Wall(this, new Square(new Coordinate(x,y), new Coordinate(x,y))));
                                selectedItem = walls.get(walls.size()-1);
                            } else if(mode == WIN_BOX) {
                                repairs.add(new RepairModule(this, new Square(x, y, x, y)));
                                selectedItem = repairs.get(repairs.size()-1);
                            }
                            creatingBox = true;
                        }
                    }
                }
                
                else if(Mouse.isButtonDown(1)) {
                    mouseClick = true;
                }
                else if(mouseClick){
                    Coordinate coords = new Coordinate(x, y);
                    if(mode == SPAWN && !spawnPlaced) {
                        player = new Player(coords.x, coords.y, this);
                        spawnPlaced = true;
                    } else if(mode == LIGHT_PU) {
                        powers.add(new LightPowerup(this, coords));
                    } else if(mode == SPEED_PU) {
                        powers.add(new SpeedPowerup(this, coords));
                    } else if(mode == REPAIR_PU) {
                        powers.add(new RepairPowerup(this, coords));
                    } else if(mode == COMREPAIR_PU) {
                        powers.add(new CompleteRepairPowerup(this, coords));
                    }
                    mouseClick = false;
                }
            }
            
            
            
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            textures[TEX_RECT].bind();
            for(Wall b : walls) {
                b.draw();
            }
            glDisable(GL_BLEND);
            if(player != null)
                player.draw();
            for(RepairModule w : repairs) {
                w.draw();
            }
            for(Powerup p : powers) {
                p.draw();
            }
            
            if(!selected) {
                for(Wall w : walls) {
                    Square square = w.checkCorners(x, y);
                    if(square == null) {
                        square = w.checkSides(x, y);
                    }
                    if(w.getSquare().withinBounds(x, y) && square != null ) {
                        this.drawSquare(this.convertSquare(square), new RGBColor(255f, 0, 0));
                    }
                }
                for(RepairModule w : repairs) {
                    Square square = w.checkCorners(x, y);
                    if(square == null) {
                        square = w.checkSides(x, y);
                    }
                    if(w.getSquare().withinBounds(x, y) && square != null ) {
                        this.drawSquare(this.convertSquare(square), new RGBColor(255f, 0, 0));
                    }
                }
            }
            
            //display coordinate info
            ArrayList<Wall> allWalls = this.getWalls();
            for(Wall w : allWalls) {
                if(w.getSquare().withinBounds(x,y)) {
                    Coordinate tl = w.getSquare().topLeftCoord;
                    Coordinate br = w.getSquare().bottomRightCoord;
                    String str = "(" + tl.x + "," + tl.y + ")";
                    String str1 = "(" + br.x + "," + br.y + ")";
                    Coordinate text = convertCoord(tl);
                    Coordinate text1 = convertCoord(br);
                    text1.y -= (smallFont.getHeight());
                    text1.x -= (smallFont.getWidth(str1));
                    oldDrawString(smallFont, text.x,text.y,str, Color.white);
                    oldDrawString(smallFont, text1.x, text1.y, str1, Color.white);
                }
            }
            for(Powerup p : powers) {
                if(p.withinBounds(new Coordinate(x,y))) {
                    Coordinate coord = p.getCoord();
                    String str = "(" + coord.x + "," + coord.y + ")";
                    coord = convertCoord(coord);
                    newDrawString(smallFont, coord.x, coord.y-smallFont.getHeight()/2, str, Color.white);
                }
            }
            if(player != null && player.getSquare().withinBounds(x,y)) {
                Coordinate coord = player.getCoord();
                String str = "(" + coord.x + "," + coord.y + ")";
                coord = convertCoord(coord);
                newDrawString(smallFont, coord.x, coord.y-smallFont.getHeight()/2, str, Color.white);
            }
            
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
        //System.exit(0);
    }
    
    public void launchReflector() {
        this.pickLevel(false);
        levelWidth *= 2;
        levelHeight *= 2;
        
        for(int i = 0; i <= 3; i++) {
            walls.remove(3-i);
        }
        ArrayList<Wall> newWalls = new ArrayList<Wall>();
        for(Wall w : walls) {
            //quadrant 1
            Square oldSq = w.getSquare();
            Square Sq = new Square(oldSq.topLeftCoord.x, oldSq.topLeftCoord.y, oldSq.bottomRightCoord.x, oldSq.bottomRightCoord.y);
            int width = Sq.getWidth();
            int height = Sq.getHeight();
            Sq.topLeftCoord.x = (levelWidth/2 + ((levelWidth/2)-Sq.topLeftCoord.x)) - width;
            Sq.bottomRightCoord.x = (levelWidth/2 + ((levelWidth/2)-Sq.bottomRightCoord.x)) + width;
            newWalls.add(new Wall(this, Sq));
            
            //quadrant 4
            Square Sq1 = new Square(Sq.topLeftCoord.x, Sq.topLeftCoord.y, Sq.bottomRightCoord.x, Sq.bottomRightCoord.y);
            Sq1.bottomRightCoord.y = (levelHeight/2 + ((levelHeight/2)-Sq.topLeftCoord.y));
            Sq1.topLeftCoord.y = (int)(levelHeight/2.0 + ((levelHeight/2)-Sq.bottomRightCoord.y));
            newWalls.add(new Wall(this, Sq1));
            
            //quadrant 3
            Square Sq2 = new Square(oldSq.topLeftCoord.x, oldSq.topLeftCoord.y, oldSq.bottomRightCoord.x, oldSq.bottomRightCoord.y);
            Sq2.bottomRightCoord.y = (levelHeight/2 + ((levelHeight/2)-oldSq.topLeftCoord.y));
            Sq2.topLeftCoord.y = (int)(levelHeight/2.0 + ((levelHeight/2)-oldSq.bottomRightCoord.y));
            newWalls.add(new Wall(this, Sq2));
        }
        walls.addAll(newWalls);
        ArrayList<RepairModule> newRepairs = new ArrayList<RepairModule>();
        for(RepairModule w : repairs) {
            //quadrant 1
            Square oldSq = w.getSquare();
            Square Sq = new Square(oldSq.topLeftCoord.x, oldSq.topLeftCoord.y, oldSq.bottomRightCoord.x, oldSq.bottomRightCoord.y);
            int width = Sq.getWidth();
            int height = Sq.getHeight();
            Sq.topLeftCoord.x = (levelWidth/2 + ((levelWidth/2)-Sq.topLeftCoord.x)) - width;
            Sq.bottomRightCoord.x = (levelWidth/2 + ((levelWidth/2)-Sq.bottomRightCoord.x)) + width;
            newRepairs.add(new RepairModule(this, Sq));
            
            //quadrant 4
            Square Sq1 = new Square(Sq.topLeftCoord.x, Sq.topLeftCoord.y, Sq.bottomRightCoord.x, Sq.bottomRightCoord.y);
            Sq1.bottomRightCoord.y = (levelHeight/2 + ((levelHeight/2)-Sq.topLeftCoord.y));
            Sq1.topLeftCoord.y = (int)(levelHeight/2.0 + ((levelHeight/2)-Sq.bottomRightCoord.y));
            newRepairs.add(new RepairModule(this, Sq1));
            
            //quadrant 3
            Square Sq2 = new Square(oldSq.topLeftCoord.x, oldSq.topLeftCoord.y, oldSq.bottomRightCoord.x, oldSq.bottomRightCoord.y);
            Sq2.bottomRightCoord.y = (levelHeight/2 + ((levelHeight/2)-oldSq.topLeftCoord.y));
            Sq2.topLeftCoord.y = (int)(levelHeight/2.0 + ((levelHeight/2)-oldSq.bottomRightCoord.y));
            newRepairs.add(new RepairModule(this, Sq2));
        }
        repairs.addAll(newRepairs);
        
        ArrayList<Powerup> newPowers = new ArrayList<Powerup>();
        for(Powerup p : powers) {
            Coordinate coord = p.getCoord();
            int px = coord.x;
            int py = coord.y;
            
            this.specialAdd(newPowers, p, levelHeight/2 + ((levelHeight/2)-px), py);
            this.specialAdd(newPowers, p, levelHeight/2 + ((levelHeight/2)-px), levelHeight/2 + ((levelHeight/2)-py));
            this.specialAdd(newPowers, p, px, levelHeight/2 + ((levelHeight/2)-py));
        }
        powers.addAll(newPowers);
        
        //adding walls around the edge of the window so the player cannot leave
        //the window
        walls.add(0, new Wall(this, new Square(new Coordinate(0,-100),new Coordinate(levelWidth,0))));
        walls.add(0, new Wall(this, new Square(new Coordinate(levelWidth,0),new Coordinate(levelWidth+100,levelHeight))));
        walls.add(0, new Wall(this, new Square(new Coordinate(0,levelHeight),new Coordinate(levelWidth,levelHeight+100))));
        walls.add(0, new Wall(this, new Square(new Coordinate(-100,0),new Coordinate(0,levelHeight))));
        
        
        levelname = levelname + "_reflected";
        addName = true;
        this.writeToFile();
    }
    public void specialAdd(ArrayList<Powerup> powers, Powerup p, int x, int y) {
        if(p instanceof LightPowerup)
            powers.add(new LightPowerup(this, new Coordinate(x,y)));
        else if(p instanceof SpeedPowerup)
            powers.add(new SpeedPowerup(this, new Coordinate(x,y)));
        else if(p instanceof RepairPowerup)
            powers.add(new RepairPowerup(this, new Coordinate(x,y)));
        else if(p instanceof CompleteRepairPowerup)
            powers.add(new CompleteRepairPowerup(this, new Coordinate(x,y)));
    }
    
    public void randomAsteriod() {
        int ranRepair = (int)(Math.random() * repairs.size());
        //System.out.println("ranRepair: " + ranRepair);
        int ranDamage = (int)(Math.random() * 30) + 30;
        //System.out.println("ranDamage: " + ranDamage);
        double health = repairs.get(ranRepair).damage(ranDamage);
        if(health == 0.0) {
            repairs.remove(ranRepair);
            sounds[SOUND_DESTROYED].playAsSoundEffect(1.0f, 1.0f, false);
        } else {
            sounds[SOUND_DAMAGE].playAsSoundEffect(1.0f, 1.0f, false);
        }
    }
    
    public boolean withinBounds(int x, int y) {
        if( (x >= 0 && x <= levelWidth) && (y >= 0 && y <= levelHeight) )
            return true;
        else
            return false;
    }
    
    public static boolean randomChance(double chance) {
        int n = (int)(Math.random() * (100.0/chance)) + 1;
        if(n == 1)
            return true;
        else
            return false;
    }
    
    public static void oldDrawString(TrueTypeFont font, int x, int y, String s, Color clr) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        font.drawString(x, y, s, clr);
        glDisable(GL_BLEND);
    }
    
    public static void newDrawString(TrueTypeFont font, int x, int y, String s, Color clr) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        int width = font.getWidth(s);
        int height = font.getHeight(s);
        x = (int)((double)x - (double)width/2.0);
        y = (int)((double)y - (double)height/2.0);
        font.drawString(x, y, s, clr);
        glDisable(GL_BLEND);
    }
    
    //converts coordinates from the game dimensions to the screen dimensions
    //so they can be rendered properly
    public Coordinate convertCoord(Coordinate coord) {
        int x = (int)(((double)screenWidth/(double)levelWidth)*(double)coord.x);
        int y = (int)(((double)screenHeight/(double)levelHeight)*(double)coord.y);
        return new Coordinate(x, y);
    }
    
    //same but from screen to game
    public Coordinate convertCoordToGame(Coordinate coord) {
        int x = (int)(((double)levelWidth/(double)screenWidth)*(double)coord.x);
        int y = (int)(((double)levelHeight/(double)screenHeight)*(double)coord.y);
        return new Coordinate(x, y);
    }
    
    //used with Mouse.getDX() and Mouse.getDY(), keeps track of pixels which
    //are rounded off when converting and adds them back when they reach
    //a full number
    public Coordinate convertCoordToGameSpecial(Coordinate coord) {
        double x = (((double)levelWidth/(double)screenWidth)*(double)coord.x);
        remX += x % 1.0;
        double y = (((double)levelHeight/(double)screenHeight)*(double)coord.y);
        remY += y % 1.0;
        
        int addX = (int)remX;
        int addY = (int)remY;
        remX = remX % 1.0;
        remY = remY % 1.0;
        return new Coordinate((int)x+addX, (int)y+addY);
    }
    
    public static int roundUp(double input) {
        int ret = (int)input;
        double r = input % 1.0;
        if(r != 0.0) {
            ret++;
        }
        return ret;
    }
    
    public Square convertSquare(Square inSq) {
        Square retSq = new Square();
        retSq.topLeftCoord = this.convertCoord(inSq.topLeftCoord);
        retSq.bottomRightCoord = this.convertCoord(inSq.bottomRightCoord);
        return retSq;
    }
    
    public void drawSquare(Square s, RGBColor color) {
        color.setColorMode();
        glBegin(GL_QUADS);
            glVertex2i(s.topLeftCoord.x, s.topLeftCoord.y);
            glVertex2i(s.bottomRightCoord.x, s.topLeftCoord.y);
            glVertex2i(s.bottomRightCoord.x, s.bottomRightCoord.y);
            glVertex2i(s.topLeftCoord.x, s.bottomRightCoord.y);
        glEnd();
    }
    
    public static void drawSquare(Square s, Texture tex) {
        tex.bind();
        glBegin(GL_QUADS);
            glTexCoord2f(0,0);
            glVertex2i(s.topLeftCoord.x, s.topLeftCoord.y);
            glTexCoord2f(1,0);
            glVertex2i(s.bottomRightCoord.x, s.topLeftCoord.y);
            glTexCoord2f(1,1);
            glVertex2i(s.bottomRightCoord.x, s.bottomRightCoord.y);
            glTexCoord2f(0,1);
            glVertex2i(s.topLeftCoord.x, s.bottomRightCoord.y);
        glEnd();
    }
    
    public static void drawLightStop(Coordinate pos, int width) {
        int height = (int)((1.0/0.44)*(double)width);
        Square s = new Square( pos.x - (int)((double)width/2.0), pos.y - (int)((double)height/2.0),
                               pos.x + (int)((double)width/2.0), pos.y + (int)((double)height/2.0) );
        glColor4f(1,1,1,1);
        glBegin(GL_QUADS);
            glTexCoord2f(0,0);
            glVertex2i(s.topLeftCoord.x, s.topLeftCoord.y);
            glTexCoord2f(1,0);
            glVertex2i(s.bottomRightCoord.x, s.topLeftCoord.y);
            glTexCoord2f(1,1);
            glVertex2i(s.bottomRightCoord.x, s.bottomRightCoord.y);
            glTexCoord2f(0,1);
            glVertex2i(s.topLeftCoord.x, s.bottomRightCoord.y);
        glEnd();
    }
    
    public void drawLight(int radius) {
        RGBColor color = new RGBColor(0,0,0);
        int widthRad = (int)(((double)screenWidth/(double)levelWidth)*(double)radius);
        int heightRad = (int)(((double)screenHeight/(double)levelHeight)*(double)radius);
        
        Square squ = new Square(light.x-widthRad, 0, light.x+widthRad, light.y-heightRad);
        drawSquare(squ, color);
        
        squ = new Square(light.x-widthRad, light.y+heightRad, light.x+widthRad, screenHeight);
        drawSquare(squ, color);
        
        squ = new Square(0,0, light.x-widthRad, screenHeight);
        drawSquare(squ, color);
        
        squ = new Square(light.x+widthRad, 0, screenWidth, screenHeight);
        drawSquare(squ, color);
    }
    
    //NOT MY FUNCTION, COPIED FROM FORUM
    public static void drawFilledCircle(double x, double y, double radius){
	int i;
	int triangleAmount = 20; //# of triangles used to draw circle
	
	//GLdouble radius = 0.8f; //radius
        double twicePi = 2.0f * PI;
	
	glBegin(GL_TRIANGLE_FAN);
		glVertex2f((float)x, (float)y); // center of circle
		for(i = 0; i <= triangleAmount;i++) { 
			glVertex2f(
		            (float)x + ((float)radius * (float)cos(i *  twicePi / triangleAmount)), 
			    (float)y + ((float)radius * (float)sin(i * twicePi / triangleAmount))
			);
		}
	glEnd();
    }
    
    public void initTexs() {
        textures[TEX_GREENLIGHT] = texLoader("greenlight");
        textures[TEX_REDLIGHT] = texLoader("redlight");
        if(theme.equals("space")) {
            textures[TEX_RECT] = texLoader("spaceship");
            textures[TEX_PLAYER] = texLoader("player");
            textures[TEX_PLAYER1] = texLoader("player1");
            textures[TEX_PLAYER2] = texLoader("player2");
            textures[TEX_PLAYER3] = texLoader("player3");
            textures[TEX_BACKGROUND] = texLoader("space1");
            textures[TEX_REPAIR] = texLoader("chamber");
        } else if(theme.equals("heli")) {
            textures[TEX_RECT] = texLoader("brick5");
            textures[TEX_PLAYER] = texLoader("apache2");
            textures[TEX_PLAYER1] = texLoader("apache3");
            textures[TEX_PLAYER2] = texLoader("apache");
            textures[TEX_PLAYER3] = texLoader("apache1");
            textures[TEX_BACKGROUND] = texLoader("sky");
            textures[TEX_REPAIR] = texLoader("helipad");
        }
    }
    
    public void initSounds() {
        sounds[SOUND_DAMAGE] = soundLoader("shock");
        sounds[SOUND_DESTROYED] = soundLoader("shutdown");
    }
    
    public Texture texLoader(String name) {
        Texture t;
        try {
            t = TextureLoader.getTexture("PNG", new FileInputStream(new File("../textures/" + name + ".png")));
            return t;
        } catch(IOException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
        return null;
    }
    
    public Audio soundLoader(String name) {
        Audio s;
        try {
            s = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("../sounds/"+name+".wav"));
            return s;
        } catch(IOException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
        return null;
    }
    
    public void pickLevel(boolean editorMode) {
        new LevelSelector(this, editorMode);
        
        loop = true;
        
        //wait for the user to press a button on the levelpicker
        while( loop ) {
            try {
                Thread.sleep(100); 
            } catch(InterruptedException e) {
                
            }
        }
        
        if(!levelname.equals("*NewLevel*") && !this.readInLevel(levelname)) {
            System.out.println("Error: reading in level file");
            System.exit(1);
        }
    }
    
    public boolean readInLevel(String levelname) {
        try {
            FileReader fr = new FileReader("../levels/"+levelname);
            BufferedReader br = new BufferedReader(fr);
            String temp;
            
            temp = br.readLine();
            levelWidth = Integer.parseInt(temp.substring(0, temp.indexOf(" ")));
            levelHeight = Integer.parseInt(temp.substring(temp.indexOf(" ")+1));
            
            temp = br.readLine();
            player.setX(Integer.parseInt(temp.substring(0, temp.indexOf(" "))));
            player.setY(Integer.parseInt(temp.substring(temp.indexOf(" ")+1)));
            
            temp = br.readLine();
            while(!temp.equals(";")) {
                repairs.add(new RepairModule(this, extractCoords(temp)));
                temp = br.readLine();
            }
            
            temp = br.readLine();
            while(!temp.equals(";")) {
                this.addPowerup(temp);
                temp = br.readLine();
            }
            
            while( (temp = br.readLine()) != null ) {
                walls.add(new Wall(this, extractCoords(temp)));
            }
        } catch(IOException e) {
            System.out.println("ERROR: reading from file");
            return false;
        }
        return true;
    }
    //inputs a string and gets the four coordinates of a rectangle from it
    private Square extractCoords(String line) {
        int[] retCoords = new int[4];
        int u = 0;
        String c, temp = "";
        for(int i = 0; i < line.length(); i++) {
            c = line.substring(i, i+1);
            if(c.equals(" ")) {
                retCoords[u] = Integer.parseInt(temp);
                u++;
                temp = "";
            }
            else {
                temp += c;
            }
        }
        retCoords[u] = Integer.parseInt(temp);
        return new Square(new Coordinate(retCoords[0], retCoords[1]), 
                          new Coordinate(retCoords[2], retCoords[3]));
    } 
    private void addPowerup(String line) {
        int x;
        int y;
        x = Integer.parseInt(line.substring(0, line.indexOf(" ")));
        line = line.substring(line.indexOf(" ")+1);
        y = Integer.parseInt(line.substring(0, line.indexOf(" ")));
        int type = Integer.parseInt(line.substring(line.indexOf(" ")+1));
        if(type == 1) {
            powers.add(new LightPowerup(this, new Coordinate(x,y)));
        } else if(type == 2) {
            powers.add(new SpeedPowerup(this, new Coordinate(x,y)));
        } else if(type == 3) {
            powers.add(new RepairPowerup(this, new Coordinate(x,y)));
        } else if(type == 4) {
            powers.add(new CompleteRepairPowerup(this, new Coordinate(x,y)));
        }
    }
    
    public void writeToFile() {
        String filename = levelname;
        try {
            FileWriter fr = new FileWriter("../levels/"+filename);
            PrintWriter pw = new PrintWriter(fr);
            
            pw.println(levelWidth + " " + levelHeight);
            pw.println(player.getX() + " " + player.getY());
            for(RepairModule r : repairs) {
                printWall(pw, r);
            }
            pw.println(";");
            for(Powerup p : powers) {
                pw.println(p.getCoord().x + " " + p.getCoord().y + " " + p.getType());
            }
            pw.println(";");
            for(Wall b : walls) {
                printWall(pw, b);
            }
            
            pw.close();
        } catch(IOException e) {
            System.out.println("ERROR: reading controls file");
        }
        if(addName)
            this.addLevelName(filename);
    }
    private void printWall(PrintWriter pw, Wall w) {
        Square square = w.getSquare();
        pw.println(square.topLeftCoord.x+ " "+ square.topLeftCoord.y+ " "+ square.bottomRightCoord.x+ " "+ square.bottomRightCoord.y);
    }
    
    private void addLevelName(String inName) {
        ArrayList<String> levelNames = new ArrayList<String>();
        try {
            FileReader fr = new FileReader("../levels/level_list.txt");
            BufferedReader br = new BufferedReader(fr);
            
            String temp;
            
            while( (temp = br.readLine()) != null ) {
                levelNames.add(temp);
            }
            
            br.close();
        } catch(IOException e) {
            System.out.println("Error: reading level list");
        }
        
        levelNames.add(inName);
        
        try {
            FileWriter fw = new FileWriter("../levels/level_list.txt");
            PrintWriter pw = new PrintWriter(fw);
            for(String s : levelNames) {
                pw.println(s);
            }
            pw.close();
        } catch(IOException e) {
            System.out.println("Error: writing out to level list");
        }
    }
}
