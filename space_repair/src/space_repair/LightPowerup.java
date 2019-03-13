package space_repair;

public class LightPowerup extends Powerup {
    public LightPowerup(GameInstance inGame, Coordinate inCoord) {
        super(inGame, inCoord);
        clr = new RGBColor(255f, 255f, 0);
        effectTime = 20000;
    }
    public void doEffect() {
        game.changeLightRadius(game.getLightRadius()*2);
    }
    public void undoEffect() {
        game.changeLightRadius(game.getLightRadius()/2);
    }
    public int getType() { return 1; }
}
