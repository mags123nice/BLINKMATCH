package blinkmatch.panels;

import java.awt.*;
import javax.swing.*;

/**
 * Abstract base for every game screen (Start, Game, Help).
 *
 * ABSTRACTION:  declares the lifecycle contract without knowing what each
 *               screen will show.
 * INHERITANCE:  all three concrete panels extend this class and inherit
 *               the shared styling helpers.
 */
public abstract class BasePanel extends JPanel {

    protected CardLayout cardLayout;
    
    protected Cursor customHoverCursor;

    public BasePanel(CardLayout cardLayout)
    {
        this.cardLayout = cardLayout;

    
                Image cursorImage = new ImageIcon("src/resources/images/cursorIcon.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        Point hotspot = new java.awt.Point(0, 0); 
        Cursor customCursor = java.awt.Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, hotspot, "Custom Cursor");
        this.setCursor(customCursor);

        // --- 2. Build the Hover Cursor ONCE here ---
         Image hoverImg = new ImageIcon("src/resources/images/hoverIcon.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        customHoverCursor = java.awt.Toolkit.getDefaultToolkit().createCustomCursor(hoverImg, hotspot, "Hover Cursor");
    }

    // ---------- abstract lifecycle ----------

    /** Build all Swing components for this screen. */
    public abstract void initComponents();

    /** Called by GameWindow right before this panel becomes visible. */
    public abstract void onEnter();

    /** Called by GameWindow right before this panel is hidden. */
    public abstract void onExit();

    // ---------- shared access ----------

    /** Returns the underlying JPanel so GameWindow can add it to CardLayout. */

    // ---------- shared styling helpers (inherited by all panels) ----------

    protected JButton makeButtonGame (ImageIcon icon) {
        //buttons for gamepanel
        JButton btn = new JButton(icon);
        

        return btn; 
    }

    protected JButton makeButton(ImageIcon icon, int xSize, int ySize) {
    JButton btn = new JButton(icon);
    
    // Set both sizes to guide the Layout Manager
    Dimension size = new Dimension(xSize, ySize);
    btn.setPreferredSize(size);
    btn.setMinimumSize(size); 
    
    if (xSize > 0 && ySize > 0) {
        Image scaledImage = icon.getImage().getScaledInstance(
            xSize, ySize, Image.SCALE_SMOOTH
        );
        btn.setIcon(new ImageIcon(scaledImage));
    }

    btn.setBorder(null);
    btn.setContentAreaFilled(false); // Important: hides the grey background

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
