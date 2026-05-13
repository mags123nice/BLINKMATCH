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
public abstract class BasePanel {

    protected JPanel panel;

    // ---------- abstract lifecycle ----------

    /** Build all Swing components for this screen. */
    public abstract void initComponents();

    /** Called by GameWindow right before this panel becomes visible. */
    public abstract void onEnter();

    /** Called by GameWindow right before this panel is hidden. */
    public abstract void onExit();

    // ---------- shared access ----------

    /** Returns the underlying JPanel so GameWindow can add it to CardLayout. */
    public JPanel getPanel() { return panel; }

    // ---------- shared styling helpers (inherited by all panels) ----------

    protected JButton makeButton(ImageIcon icon) {
        JButton btn = new JButton(icon);
        // btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(150, 100));
        
        if (btn.getWidth() > 0 && btn.getHeight() > 0) {
                    Image scaledImage = icon.getImage().getScaledInstance(
                        btn.getWidth(), 
                        btn.getHeight(), 
                        Image.SCALE_SMOOTH
                    );
                    btn.setIcon(new ImageIcon(scaledImage));
        }
        // btn.setBackground(new Color(173, 216, 230));
        // btn.setFocusPainted(false);
        // btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // btn.setBorder(BorderFactory.createCompoundBorder(
        //         BorderFactory.createLineBorder(new Color(100, 149, 237), 1),
        //         BorderFactory.createEmptyBorder(6, 18, 6, 18)
        // ));
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

    protected Color bgColor() { return new Color(235, 245, 255); }
}
