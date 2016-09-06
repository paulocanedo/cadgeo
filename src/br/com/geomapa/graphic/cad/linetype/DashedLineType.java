/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.linetype;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author paulocanedo
 */
public class DashedLineType implements LineType {

    private static Icon icon;

    public DashedLineType() {
    }

    @Override
    public String getName() {
        return "Linha tracejada";
    }

    @Override
    public short getStipple() {
        return 0x00FF;
    }

    @Override
    public boolean isContinuous() {
        return false;
    }

    @Override
    public String getDxfName() {
        return "DASHED";
    }

    @Override
    public String toString() {
        return getDxfName();
    }

    @Override
    public Icon getIcon() {
        if (icon == null) {
            icon = createDashedLineTypeIcon();
        }
        return icon;
    }

    private Icon createDashedLineTypeIcon() {
        int size = 48;
        BufferedImage image = new BufferedImage(size, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();

        float f = size / 8;
        float[] dashPattern = {f, f, f, f};
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1,
                dashPattern, 0));
        g2.setColor(Color.BLACK);
        g2.drawLine(0, 8, size, 8);
        return new ImageIcon(image);
    }
}
