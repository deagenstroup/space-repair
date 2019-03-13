
package mainmenu;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import space_repair.GameInstance;


public class Window extends JFrame implements ActionListener {
    public static void main(String args[]) {
        new Window();
    }
    
    private Options opt;
    
    private int milliseconds;
    private Timer timer;
    private TimerTask timerTask;
    
    WindowMenu winMenu;
    private boolean launchGame = false,
                    launchMaker = false,
                    reflector = false,
                    options = false;
    
    public Options getOptions() { return opt; }
    public void setOptions(Options inopt) { opt = inopt; }
    public int getTime() { return milliseconds; }
    
    public Window() {
        super("Maze Game");
        this.setVisible(true);
        this.setSize(300, 400);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());
        this.setLocationRelativeTo(null);
        
        opt = new Options();
        opt.readInOptions();
        
        milliseconds = 0;
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                milliseconds++;
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1);
        
        this.menu();
    }
    
    public void menu() {
        this.addMenu();
        while(true) {
            System.out.println();
            if(launchGame) {
                //System.out.println("test");
                GameInstance mz = new GameInstance(this, opt);
                this.setVisible(false);
                mz.playGame();
                this.setVisible(true);
                launchGame = false;
            } else if(launchMaker) {
                GameInstance game = new GameInstance(this, opt);
                game.launchEditor();
                launchMaker = false;
            } else if(reflector) {
                GameInstance game = new GameInstance(this, opt);
                game.launchReflector();
                reflector = false;
            } else if(options) {
                this.getContentPane().removeAll();
                this.add(new OptionsMenu(this, 225));
                options = false;
                this.revalidate();
                this.repaint();
            }
        }
    }
    
    public void addMenu() {
        this.getContentPane().removeAll();
        winMenu = new WindowMenu(this, 275);
        this.add(winMenu);
        this.revalidate();
        this.repaint();
    }
    
    public void actionPerformed(ActionEvent e) {
        String bt = e.getActionCommand();
        if(bt.equals("Play Game")) {
            launchGame = true;
        } else if(bt.equals("Launch Editor")) {
            launchMaker = true;
        } else if(bt.equals("Reflector Tool")) {
            reflector = true;
        } else if(bt.equals("Options")) {
            options = true;
        }
    }
}
