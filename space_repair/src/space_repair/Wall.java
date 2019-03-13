package space_repair;

import static space_repair.GameInstance.TEX_RECT;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

public class Wall implements Selectable {
    public static final int cornerConstant = 20;
    
    protected Square square;
    protected GameInstance game;
    
    //represents the shade which the wall will be highlighted
    //0 = none, 1 = blue, 2 = flash animation(repair module only)
    protected int highlight;
    
    public Square getSquare() { return square; }
    public int getHighlight() { return highlight; }
    public void highlight(int i) { highlight = i; }
    
    public Wall(GameInstance inGame, Square inSquare) {
        square = inSquare;
        game = inGame;
    }
    public boolean withinBounds(int u, int i) {
        if( (u >= square.topLeftCoord.x && u <= square.bottomRightCoord.x) && 
            (i >= square.topLeftCoord.y && i <= square.bottomRightCoord.y) )
            return true;
        else 
            return false;
    }
    public boolean isNear(int x, int y, int buffer) {
        Square modSquare = new Square();
        //System.out.println("modSquare==square: " + (modSquare==square));
        modSquare.topLeftCoord.x = square.topLeftCoord.x - buffer;
        modSquare.topLeftCoord.y = square.topLeftCoord.y - buffer;
        modSquare.bottomRightCoord.x = square.bottomRightCoord.x + buffer;
        modSquare.bottomRightCoord.y = square.bottomRightCoord.y + buffer;
        return modSquare.withinBounds(x, y);
    }
    public void draw() {
        if(highlight == 1) {
            glColor4f(0, 0, 180f, 0.8f);
        } else {
            glColor4f(1,1,1,1);
        }
        
        Square renderSquare = game.convertSquare(square);
        Texture t = game.getTexture(TEX_RECT);
        int texSide = t.getImageWidth();
        int renderSquareW = renderSquare.getWidth();
        int renderSquareH = renderSquare.getHeight();
        
        float x;
        if(renderSquareW > renderSquareH) {
            texSide = renderSquareH;
            x = (float) renderSquareW/ (float) texSide;
            glBegin(GL_QUADS);
                glTexCoord2f(0,0);
                glVertex2i(renderSquare.topLeftCoord.x, renderSquare.topLeftCoord.y);
                glTexCoord2f(x, 0);
                glVertex2i(renderSquare.bottomRightCoord.x, renderSquare.topLeftCoord.y);
                glTexCoord2f(x, 1f);
                glVertex2i(renderSquare.bottomRightCoord.x, renderSquare.bottomRightCoord.y);
                glTexCoord2f(0, 1f);
                glVertex2i(renderSquare.topLeftCoord.x, renderSquare.bottomRightCoord.y);
            glEnd();
        } else if(renderSquareH >= renderSquareW) {
            texSide = renderSquareW;
            x = (float)renderSquareH/(float)texSide;
            glBegin(GL_QUADS);
                glTexCoord2f(0,0);
                glVertex2i(renderSquare.topLeftCoord.x, renderSquare.topLeftCoord.y);
                glTexCoord2f(1, 0);
                glVertex2i(renderSquare.bottomRightCoord.x, renderSquare.topLeftCoord.y);
                glTexCoord2f(1, x);
                glVertex2i(renderSquare.bottomRightCoord.x, renderSquare.bottomRightCoord.y);
                glTexCoord2f(0, x);
                glVertex2i(renderSquare.topLeftCoord.x, renderSquare.bottomRightCoord.y);
            glEnd();
        }
        
        glColor4f(1,1,1,1);
        highlight = 0;
    }
    
    public void move(int ix, int iy) {
        square.topLeftCoord.x += ix;
        square.bottomRightCoord.x += ix;
        square.topLeftCoord.y += iy;
        square.bottomRightCoord.y += iy;
    }
    public void update(int ix, int iy) {
        square.bottomRightCoord.x = ix;
        square.bottomRightCoord.y = iy;
    }
    public void checkVerts() {
        if(square.topLeftCoord.x > square.bottomRightCoord.x && 
           square.topLeftCoord.y > square.bottomRightCoord.y) {
            Coordinate tempCoord = square.bottomRightCoord;
            square.bottomRightCoord = square.topLeftCoord;
            square.topLeftCoord = tempCoord;
        }
    }
    
    public Square checkCorners(int a, int b) {
        Square tempSquare;
        
        tempSquare = new Square(square.topLeftCoord, 
                                new Coordinate(square.topLeftCoord.x+cornerConstant, 
                                               square.topLeftCoord.y+cornerConstant));
        if(boundsCheck(a,b,tempSquare))
            return tempSquare;
        
        tempSquare = new Square(new Coordinate(square.bottomRightCoord.x-cornerConstant, square.topLeftCoord.y),
                                new Coordinate(square.bottomRightCoord.x, square.topLeftCoord.y+cornerConstant));
        if(boundsCheck(a,b,tempSquare))
            return tempSquare;
        
        tempSquare = new Square(new Coordinate(square.topLeftCoord.x, square.bottomRightCoord.y-cornerConstant),
                                new Coordinate(square.topLeftCoord.x+cornerConstant, square.bottomRightCoord.y));
        if(boundsCheck(a,b,tempSquare))
            return tempSquare;
        
        tempSquare = new Square(new Coordinate(square.bottomRightCoord.x-cornerConstant, 
                                               square.bottomRightCoord.y-cornerConstant),
                                square.bottomRightCoord);
        if(boundsCheck(a,b,tempSquare))
            return tempSquare;
        
        return null;
    }
    public int getCornerNumber(int a, int b) {
        Square tempSquare;
        tempSquare = new Square(square.topLeftCoord, 
                                new Coordinate(square.topLeftCoord.x+cornerConstant, 
                                               square.topLeftCoord.y+cornerConstant));
        if(boundsCheck(a,b,tempSquare))
            return 1;
        
        tempSquare = new Square(new Coordinate(square.bottomRightCoord.x-cornerConstant, square.topLeftCoord.y),
                                new Coordinate(square.bottomRightCoord.x, square.topLeftCoord.y+cornerConstant));
        if(boundsCheck(a,b,tempSquare))
            return 2;
        
        tempSquare = new Square(new Coordinate(square.topLeftCoord.x, square.bottomRightCoord.y-cornerConstant),
                                new Coordinate(square.topLeftCoord.x+cornerConstant, square.bottomRightCoord.y));
        if(boundsCheck(a,b,tempSquare))
            return 3;
        
        tempSquare = new Square(new Coordinate(square.bottomRightCoord.x-cornerConstant, 
                                               square.bottomRightCoord.y-cornerConstant),
                                square.bottomRightCoord);
        if(boundsCheck(a,b,tempSquare))
            return 4;
        return 0;
    }
    public void changeCorner(int corner, int a, int b) {
        if(corner == 1) {
            square.topLeftCoord.x = a;
            square.topLeftCoord.y = b;
        } else if(corner == 2) {
            square.bottomRightCoord.x = a;
            square.topLeftCoord.y = b;
        } else if(corner == 3) {
            square.topLeftCoord.x = a;
            square.bottomRightCoord.y = b;
        } else if(corner == 4) {
            square.bottomRightCoord.x = a;
            square.bottomRightCoord.y = b;
        }
    }
    
    public Square checkSides(int a, int b) {
        Square tempSquare;
        tempSquare = new Square(square.topLeftCoord, 
                                new Coordinate(square.bottomRightCoord.x, square.topLeftCoord.y+cornerConstant));
        if(boundsCheck(a,b,tempSquare))
            return tempSquare;
        
        tempSquare = new Square(new Coordinate(square.bottomRightCoord.x-cornerConstant, square.topLeftCoord.y),
                                new Coordinate(square.bottomRightCoord.x, square.bottomRightCoord.y));
        if(boundsCheck(a,b,tempSquare))
            return tempSquare;
        
        tempSquare = new Square(new Coordinate(square.topLeftCoord.x, square.bottomRightCoord.y-cornerConstant),
                                new Coordinate(square.bottomRightCoord.x, square.bottomRightCoord.y));
        if(boundsCheck(a,b,tempSquare))
            return tempSquare;
        
        tempSquare = new Square(square.topLeftCoord, 
                                new Coordinate(square.topLeftCoord.x+cornerConstant, square.bottomRightCoord.y));
        if(boundsCheck(a,b,tempSquare))
            return tempSquare;
        
        return null;
    }
    public int getSideNumber(int a, int b) {
        Square tempSquare;
        tempSquare = new Square(square.topLeftCoord, 
                                new Coordinate(square.bottomRightCoord.x, square.topLeftCoord.y+cornerConstant));
        if(boundsCheck(a,b,tempSquare))
            return 1;
        
        tempSquare = new Square(new Coordinate(square.bottomRightCoord.x-cornerConstant, square.topLeftCoord.y),
                                new Coordinate(square.bottomRightCoord.x, square.bottomRightCoord.y));
        if(boundsCheck(a,b,tempSquare))
            return 2;
        
        tempSquare = new Square(new Coordinate(square.topLeftCoord.x, square.bottomRightCoord.y-cornerConstant),
                                new Coordinate(square.bottomRightCoord.x, square.bottomRightCoord.y));
        if(boundsCheck(a,b,tempSquare))
            return 3;
        
        tempSquare = new Square(square.topLeftCoord, 
                                new Coordinate(square.topLeftCoord.x+cornerConstant, square.bottomRightCoord.y));
        if(boundsCheck(a,b,tempSquare))
            return 4;
        
        return 0;
    }
    public void changeSide(int side, int a, int b) {
        if(side == 1) {
            square.topLeftCoord.y = b;
        } else if(side == 2) {
            square.bottomRightCoord.x = a;
        } else if(side == 3) {
            square.bottomRightCoord.y = b;
        } else if(side == 4) {
            square.topLeftCoord.x = a;
        }
    }
    
    public boolean boundsCheck(int a, int b, Square square) {
        if( (a >= square.topLeftCoord.x && a <= square.bottomRightCoord.x) && (b >= square.topLeftCoord.y && b <= square.bottomRightCoord.y) )
            return true;
        else 
            return false;
    }
    public boolean boundsCheck(int a, int b, int a1, int b1, int a2, int b2) {
        if( (a >= a1 && a <= a2) && (b >= b1 && b <= b2) )
            return true;
        else 
            return false;
    }
    
}
