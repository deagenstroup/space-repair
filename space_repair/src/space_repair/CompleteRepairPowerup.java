package space_repair;

import java.util.ArrayList;

public class CompleteRepairPowerup extends Powerup {
    public CompleteRepairPowerup(GameInstance inGame, Coordinate inCoord) {
        super(inGame, inCoord);
        clr = new RGBColor(255f,0f,255f);
        effectTime = 0;
    }
    public void doEffect() {
        ArrayList<RepairModule> reps = new ArrayList<RepairModule>(game.getRepairs());
        for(int u = 0; u < reps.size(); u++) {
            if(reps.get(u).getHealth() == RepairModule.MAX_HEALTH) {
                reps.remove(u);
                u--;
            }
        }
        if(reps.size() > 0) {
            int i = (int)(Math.random() * (double)reps.size());
            reps.get(i).setHealth(RepairModule.MAX_HEALTH);
        }
    }
    //since it is a one time effect, there is nothing to undo
    public void undoEffect() {}
    public int getType() { return 4; }
}
