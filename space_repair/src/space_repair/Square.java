package space_repair;

public class Square {
    public Coordinate topLeftCoord, bottomRightCoord;
    public Square(Coordinate inTL, Coordinate inBR) {
        topLeftCoord = inTL;
        bottomRightCoord = inBR;
    }
    public Square(int x, int y, int x1, int y1) {
        topLeftCoord = new Coordinate(x, y);
        bottomRightCoord = new Coordinate(x1, y1);
    }
    public Square(){
        topLeftCoord = new Coordinate();
        bottomRightCoord = new Coordinate();
    }
    public Square(Square s) {
        this.topLeftCoord = s.topLeftCoord;
        this.bottomRightCoord = s.bottomRightCoord;
    }
    public int getWidth() {
        return bottomRightCoord.x - topLeftCoord.x;
    }
    public int getHeight() {
        return bottomRightCoord.y - topLeftCoord.y;
    }
    public Coordinate getMiddle() {
        int x = topLeftCoord.x + (int)((double)this.getWidth()/2.0);
        int y = topLeftCoord.y + (int)((double)this.getHeight()/2.0);
        return new Coordinate(x, y);
    }
    public boolean withinBounds(int u, int i) {
        if( (u >= topLeftCoord.x && u <= bottomRightCoord.x) && 
            (i >= topLeftCoord.y && i <= bottomRightCoord.y) )
            return true;
        else 
            return false;
    }
}
