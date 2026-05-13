package blinkmatch.panels;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * Help / instructions screen.
 *
 * INHERITANCE:  extends BasePanel — reuses shared helpers.
 * POLYMORPHISM: overrides lifecycle methods.
 */
public class HelpPanel extends BasePanel {

    private final CardLayout cardLayout;
    private final JPanel     container;

    public HelpPanel(CardLayout cardLayout, JPanel container) {
        this.cardLayout = cardLayout;
        this.container  = container;
        initComponents();
    }

    @Override
    public void initComponents() {
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // --- title ---
        JLabel title = makeTitle("How to Play");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        // --- content ---
        JPanel content = new JPanel(new GridLayout(1, 2, 16, 0));
        content.setBackground(bgColor());

        //imageIcons
        ImageIcon backIcon = new ImageIcon("src/resources/images/backIcon.png");

        content.add(makeSection("Mouse / Keyboard",
                new String[]{
                    "Left Click — flip a card",
                    "S — start game",
                    "R — restart",
                    "E — exit to start"
                }));

        content.add(makeSection("Weather System",
                new String[]{
                    "\u2600 Sunny — normal gameplay",
                    "\u26c8 Stormy — cards shuffle!",
                    "Adapt fast to the storm.",
                    "Match all pairs to win."
                }));

        panel.add(content, BorderLayout.CENTER);

        // --- back button ---
        //MAKE ICONIMAGE
        JButton backBtn = makeButton(backIcon);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            onExit();
            cardLayout.show(container, "START");
        });

        JPanel south = new JPanel();
        south.setBackground(bgColor());
        south.add(backBtn);
        panel.add(south, BorderLayout.SOUTH);
    }

    private JPanel makeSection(String title, String[] lines) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 1),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13), new Color(60, 60, 120)));

        for (String line : lines) {
            JLabel lbl = new JLabel(line);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            p.add(lbl);
        }
        return p;
    }

    @Override public void onEnter() { /* nothing special needed */ }
    @Override public void onExit()  { /* nothing special needed */ }
}
