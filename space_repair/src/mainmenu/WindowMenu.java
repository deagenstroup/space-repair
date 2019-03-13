package mainmenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class WindowMenu extends JPanel {
    private static final int BUTTONS = 4;
    private static final double BUTTON_RATIO = 0.5;
    
    private Window rootFrame;
    private Dimension dim;
    private JButton gameButton, makerButton, reflectorButton, optionsButton;
    
    public WindowMenu(Window inWin, int width) {
        rootFrame = inWin;
        dim = new Dimension();
        dim.setSize(width, width*BUTTON_RATIO*BUTTONS);
        
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        this.setPreferredSize(dim);
        this.setLayout(new GridLayout(BUTTONS, 1));
        
        Font buttonFont = new Font( "Courier New", Font.BOLD, (int)((double)width*(22.0/200.0)) );
        Dimension buttonDimen = new Dimension( width, (int)((double)width*BUTTON_RATIO) );
        gameButton = new JButton("Play Game");
        makerButton = new JButton("Launch Editor");
        reflectorButton = new JButton("Reflector Tool");
        optionsButton = new JButton("Options");
        gameButton.setPreferredSize(buttonDimen);
        makerButton.setPreferredSize(buttonDimen);
        reflectorButton.setPreferredSize(buttonDimen);
        optionsButton.setPreferredSize(buttonDimen);
        gameButton.setFont(buttonFont);
        makerButton.setFont(buttonFont);
        reflectorButton.setFont(buttonFont);
        optionsButton.setFont(buttonFont);
        gameButton.addActionListener(rootFrame);
        makerButton.addActionListener(rootFrame);
        reflectorButton.addActionListener(rootFrame);
        optionsButton.addActionListener(rootFrame);
        this.add(gameButton);
        this.add(makerButton);
        this.add(reflectorButton);
        this.add(optionsButton);
    }
}
