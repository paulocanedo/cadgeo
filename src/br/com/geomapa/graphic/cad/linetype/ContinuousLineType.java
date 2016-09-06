/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.linetype;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author paulocanedo
 */
public class ContinuousLineType implements LineType {

    private static Icon icon;

    public ContinuousLineType() {
    }

    @Override
    public String getName() {
        return "Linha contínua";
    }

    @Override
    public short getStipple() {
        return 0x0000;
    }

    @Override
    public boolean isContinuous() {
        return true;
    }

    @Override
    public String getDxfName() {
        return "CONTINUOUS";
    }

    @Override
    public String toString() {
        return getDxfName();
    }

    @Override
    public Icon getIcon() {
        if (icon == null) {
            icon = createContinuousLineTypeIcon();
        }
        return icon;
    }

    private Icon createContinuousLineTypeIcon() {
        int size = 48;
        BufferedImage image = new BufferedImage(size, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();

        g2.setColor(Color.BLACK);
        g2.drawLine(0, 8, size, 8);
        return new ImageIcon(image);
    }
}
