package blinkmatch.panels;

import java.awt.*;
import javax.swing.*;

/**
 * The opening/title screen.
 *
 * INHERITANCE:  extends BasePanel — reuses makeTitle(), makeButton(), bgColor().
 * POLYMORPHISM: overrides onEnter() / onExit() with its own behaviour.
 */
public class StartPanel extends BasePanel {

    private final CardLayout cardLayout;
    private final JPanel     container;

    public StartPanel(CardLayout cardLayout, JPanel container) {
        this.cardLayout = cardLayout;
        this.container  = container;
        initComponents();
    }

    @Override
    public void initComponents() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bgColor());
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        //button imageicon
        ImageIcon startIcon = new ImageIcon("src/resources/images/startIcon.png");
        ImageIcon helpIcon = new ImageIcon("src/resources/images/helpIcon.png");
        

        // --- title ---
        JLabel title = makeTitle("Blink Match Memory");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = makeSubtitle("Match all pairs before the storm hits!");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- buttons ---
        JButton startBtn =  makeButton(startIcon);
        JButton helpBtn  = makeButton(helpIcon);

        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        helpBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        startBtn.addActionListener(e -> {
            onExit();
            cardLayout.show(container, "GAME");
        });
        helpBtn.addActionListener(e -> {
            onExit();
            cardLayout.show(container, "HELP");
        });

        // keyboard: S = start
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
             .put(KeyStroke.getKeyStroke('S'), "start");
        panel.getActionMap().put("start", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                onExit();
                cardLayout.show(container, "GAME");
            }
        });

        // --- layout ---
        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(40));
        panel.add(startBtn);
        panel.add(Box.createVerticalStrut(12));
        panel.add(helpBtn);
        panel.add(Box.createVerticalGlue());
    }

    @Override public void onEnter() { /* nothing special needed */ }
    @Override public void onExit()  { /* nothing special needed */ }
}
