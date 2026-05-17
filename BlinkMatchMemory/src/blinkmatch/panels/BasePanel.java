package blinkmatch.panels;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
// import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
// import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JButton;
// import javax.swing.JLabel;
import javax.swing.JPanel;
// import javax.swing.SwingConstants;

public abstract class BasePanel extends JPanel {

    protected CardLayout cardLayout;
    // private Image backgroundImage = null;
    protected Cursor customHoverCursor;

    public BasePanel(CardLayout cardLayout)
    {
        this.cardLayout = cardLayout;

    //Create Cursors
                Image cursorImage = new ImageIcon("resources/images/cursorIcon.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        Point hotspot = new Point(0, 0); 
        Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, hotspot, "Custom Cursor");
        this.setCursor(customCursor);


         Image hoverImg = new ImageIcon("resources/images/hoverIcon.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
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

    // public void setConstantBackground(String imagePath) {
    // this.backgroundImage = new ImageIcon(imagePath).getImage();
    // this.repaint(); // Forces the panel to refresh and show the new image immediately
    // }

    // @Override
    // protected void paintComponent(Graphics g) {
    // super.paintComponent(g); // Paints standard background color first
    
    // // If the method was called and an image exists, draw it to fill the screen
    // if (backgroundImage != null) {
    //     g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    // }
    // }


        protected Color bgColor() { return new Color(170, 205, 255); }
}
