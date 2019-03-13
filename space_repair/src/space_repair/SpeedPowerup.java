package space_repair;

public class SpeedPowerup extends Powerup {
    public SpeedPowerup(GameInstance inGame, Coordinate inCoord) {
        super(inGame, inCoord);
        clr = new RGBColor(100f, 0, 0f);
        effectTime = 10000;
    }
    public void doEffect() {
        Player p = game.getPlayer();
        p.setMoveValue(p.getMoveValue()*2);
    }
    public void undoEffect() {
        Player p = game.getPlayer();
        p.setMoveValue(p.getMoveValue()/2);
    }
    public int getType() { return 2; }
}
