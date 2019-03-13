package space_repair;

public class RepairPowerup extends Powerup {
    public RepairPowerup(GameInstance inGame, Coordinate inCoord) {
        super(inGame, inCoord);
        clr = new RGBColor(0f,0f,255f);
        effectTime = 10000;
    }
    public void doEffect() {
        Player p = game.getPlayer();
        p.setRawRepairRate(p.getRawRepairRate()*2);
    }
    public void undoEffect() {
        Player p = game.getPlayer();
        p.setRawRepairRate(p.getRawRepairRate()/2);
    }
    public int getType() { return 3; }
}
