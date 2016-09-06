/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.theme;

import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 *
 * @author paulocanedo
 */
public class PointDescriptorButtonUI extends BasicButtonUI {

    private Color pressedColor = new Color(0x5e5ebf);
    private Color color = new Color(0xcfcfcf);
    private Color hoverColor = new Color(0x5eaeaf);
    private static ComponentUI buttonUI;
    private boolean oldOpaque;

    public static ComponentUI createUI(JComponent c) {
        if (buttonUI == null) {
            buttonUI = new PointDescriptorButtonUI();
        }
        return buttonUI;
    }

    public PointDescriptorButtonUI() {
    }

    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);

        oldOpaque = b.isOpaque();
        b.setText(".");
        b.setOpaque(false);
        b.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        c.setOpaque(oldOpaque);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton ab = ((AbstractButton) c);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (ab.isOpaque()) {
            g2.setColor((ab.isSelected()) ? pressedColor : color);
            g2.fillRect(0, 0, ab.getWidth(), ab.getHeight());
        }

        if (ab.getModel().isRollover()) {
            customPaintBackground(g2, ab, false);
        } else if(ab.getModel().isSelected()) {
            g2.setColor(pressedColor);
            g2.fillRect(0, 0, ab.getWidth(), ab.getHeight());
        }

        super.paint(g2, c);
    }

    private void customPaintBackground(Graphics2D g2, AbstractButton ab, boolean isPressed) {
        Composite composite = g2.getComposite();
        g2.setComposite(AlphaComposite.SrcOver.derive(0.4f));

        if (isPressed) {
            g2.setColor(pressedColor);
        } else {
            g2.setColor(hoverColor);
        }
        g2.fillRect(0, 0, ab.getWidth(), ab.getHeight());
        g2.setComposite(composite);
    }

    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        customPaintBackground((Graphics2D) g, b, true);
    }

    @Override
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
        Graphics2D g2 = (Graphics2D) g.create();
        Object desktopProperty = Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        if (desktopProperty != null) {
            g2.addRenderingHints((Map) desktopProperty);
        }

        ButtonModel model = b.getModel();
        Font font = b.getFont();
        Font boldFont = font.deriveFont(Font.BOLD);
        FontMetrics fm = b.getFontMetrics(b.getFont());
        int mnemonicIndex = b.getDisplayedMnemonicIndex();

        GeodesicPoint gpoint = (GeodesicPoint) b.getClientProperty("JButton.GeodesicPoint");
        if (gpoint == null) {
            if (model.isEnabled()) {
                g2.setColor(b.getForeground());

                if (model.isPressed()) {
                    BasicGraphicsUtils.drawStringUnderlineCharAt(g2, b.getText(), mnemonicIndex,
                            textRect.x + getTextShiftOffset() + 1,
                            textRect.y + fm.getAscent() + getTextShiftOffset() + 1);
                } else {
                    BasicGraphicsUtils.drawStringUnderlineCharAt(g2, text, mnemonicIndex,
                            textRect.x + getTextShiftOffset(),
                            textRect.y + fm.getAscent() + getTextShiftOffset());
                }
            } else {
                g2.setColor(b.getBackground().brighter());
                BasicGraphicsUtils.drawStringUnderlineCharAt(g2, text, mnemonicIndex,
                        textRect.x + getTextShiftOffset(),
                        textRect.y + fm.getAscent() + getTextShiftOffset());

                g2.setColor(b.getBackground().darker());
                BasicGraphicsUtils.drawStringUnderlineCharAt(g2, b.getText(), mnemonicIndex,
                        textRect.x + getTextShiftOffset() - 1,
                        textRect.y + fm.getAscent() + getTextShiftOffset() - 1);
            }
        } else {
            g2.setColor(Color.BLACK);
            g2.setFont(boldFont);
            UTMCoordinate coord = gpoint.getCoordinate().toUTM();
            String string1 = String.format("E:   %.3f", coord.getEast());
            String string2 = String.format("N: %.3f", coord.getNorth());

            int i = 1;
            int height = fm.getHeight();
            int stringWidth = fm.stringWidth(string2);
            int x = (b.getWidth() - stringWidth) / 2;

            g2.drawString(gpoint.getName(), x, b.getHeight() / 2 - height * (i--));

            g2.setFont(font);
            g2.drawString(string1, x, b.getHeight() / 2 - height * (i--));
            g2.drawString(string2, x, b.getHeight() / 2 - height * (i--));
        }
    }
}
