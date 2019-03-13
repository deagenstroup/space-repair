package space_repair;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;

public class LevelSelector extends JFrame implements ActionListener {
    
    private ArrayList<String> levelNames;
    private GameInstance game;
    
    public LevelSelector(GameInstance inGame, boolean editorMode) {
        game = inGame;
        levelNames = new ArrayList<String>();
        
        this.readLevelList();
        
        this.setVisible(true);
        this.setSize(300, 100*levelNames.size());
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout( levelNames.size(),1));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        
        this.addButtons(editorMode);
    }
    public void addButtons(boolean editorMode) {
        for(String s : levelNames) {
            JButton b = new JButton(s);
            b.addActionListener(this);
            this.add(b);
        }
        if(editorMode) {
            JButton b = new JButton("*NewLevel*");
            b.addActionListener(this);
            this.add(b);
        }
    }
    
    public void readLevelList() {
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
    }
    
    public void actionPerformed(ActionEvent e ) {
        String name = e.getActionCommand();
        game.changeLevelName(name);
        game.setLoop(false);
        this.dispose();
    }
}
