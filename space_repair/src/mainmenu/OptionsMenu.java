package mainmenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OptionsMenu extends JPanel implements ActionListener {
    private static final double BUTTON_RATIO = 0.5;
    
    private Dimension dim, buttonDimen;
    private Window rootFrame;
    private Font buttonFont;
    private Options opt;
    private int numButtons;
    
    private JButton changeResolution, backButton;
    private JTextField resBox;
    
    public OptionsMenu(Window inWin, int width) {
        rootFrame = inWin;
        dim = new Dimension();
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        
        buttonFont = new Font( "Courier New", Font.BOLD, (int)((double)width*(14.0/200.0)) );
        buttonDimen = new Dimension( width, (int)((double)width*BUTTON_RATIO) );
        
        resBox = new JTextField((int)((double)width*BUTTON_RATIO));
        this.add(resBox);
        this.addOptionsButtons(width);
        
        opt = new Options();
        
        opt.readInOptions();
        //System.out.println("resWidth: " + opt.resWidth + "   resHeight: " + opt.resHeight);
    }
    
    public void addOptionsButtons(int width) {
        this.removeAll();
        numButtons = 2;
        
        dim.setSize(width, width*BUTTON_RATIO*numButtons);
        this.setPreferredSize(dim);
        this.setLayout(new GridLayout(numButtons, 1));
        
        changeResolution = new JButton("Change Resolution");
        backButton = new JButton("Back");
        changeResolution.setFont(buttonFont);
        backButton.setFont(buttonFont);
        changeResolution.setPreferredSize(buttonDimen);
        backButton.setPreferredSize(buttonDimen);
        changeResolution.addActionListener(this);
        backButton.addActionListener(this);
        this.add(changeResolution);
        this.add(backButton);
        
        this.revalidate();
        this.repaint();
    }
    public void actionPerformed(ActionEvent e) {
        String bt = e.getActionCommand();
        if(bt.equals("Change Resolution")) {
            this.changeResolution();
        } else if(bt.equals("Back")) {
            rootFrame.setOptions(opt);
            opt.writeOutOptions();
            rootFrame.addMenu();
        }
    }
    
    public void changeResolution() {
        String input, temp;
        int a, b;
        input = JOptionPane.showInputDialog("Resolution Width: ");
        if(input == null)
            return;
        
        try {
            a = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            System.out.println("Error: input was not in correct format");
            return;
        }
        
        temp = JOptionPane.showInputDialog("Resolution Height: ");
        if(temp == null)
            return;
        
        try { 
            b = Integer.parseInt(temp);
        } catch(NumberFormatException e) {
            System.out.println("Error: input was not in correct format");
            return;
        }
        if(a >= 0 && b >= 0) {
            opt.resWidth = a;
            opt.resHeight = b; 
        } else {
            System.out.println("Error: input provided was negative");
        }
    }
}
