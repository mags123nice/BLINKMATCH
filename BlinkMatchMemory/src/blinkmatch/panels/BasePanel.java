package blinkmatch.panels;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public abstract class BasePanel extends JPanel {

    protected CardLayout cardLayout;
    
    protected Cursor customHoverCursor;

    public BasePanel(CardLayout cardLayout)
    {
        this.cardLayout = cardLayout;

    //Create Cursors
                Image cursorImage = new ImageIcon("src/resources/images/cursorIcon.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        Point hotspot = new Point(0, 0); 
        Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, hotspot, "Custom Cursor");
        this.setCursor(customCursor);


         Image hoverImg = new ImageIcon("src/resources/images/hoverIcon.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        customHoverCursor = Toolkit.getDefaultToolkit().createCustomCursor(hoverImg, hotspot, "Hover Cursor");
    }


    
    public abstract void initComponents();
    public abstract void onEnter();
    public abstract void onExit();

    

    // ---------- Shared Styling Helpers ----------

    protected JButton makeButtonGame (ImageIcon icon) {
        //buttons for gamepanel
        JButton btn = new JButton(icon);
        

        return btn; 
    }

    protected JButton makeButton(ImageIcon icon, int xSize, int ySize) {
        JButton btn = new JButton(icon);
        
        // Set Sizes
        Dimension size = new Dimension(xSize, ySize);
        btn.setPreferredSize(size);
        btn.setMaximumSize(size); 
        btn.setMinimumSize(size); 
        
        if (xSize > 0 && ySize > 0) {
            Image scaledImage = icon.getImage().getScaledInstance(
                xSize, ySize, Image.SCALE_SMOOTH
            );
            btn.setIcon(new ImageIcon(scaledImage));
        }

        btn.setBorder(null);
        btn.setContentAreaFilled(false); 

        btn.setCursor(customHoverCursor);
        
        return btn;
    }

    protected JLabel makeTitle(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 28));
        lbl.setForeground(new Color(60, 60, 120));
        return lbl;
    }

    protected JLabel makeSubtitle(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }

        protected Color bgColor() { return new Color(220, 245, 230); }
}
