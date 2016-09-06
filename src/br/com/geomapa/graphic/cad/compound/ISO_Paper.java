/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic.cad.compound;

import br.com.geomapa.controller.LayerController;
import br.com.geomapa.importer.pcgeocad.VisualObjectParser;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.importer.pcgeocad.VisualObjectParserException;
import br.com.geomapa.main.Bus;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

/**
 *
 * @author paulocanedo
 */
public final class ISO_Paper extends AbstractVisualObjectCompound {

    private static VisualObjectParser voparser = VisualObjectParser.getInstance();
    private DescriptorPaper dpaper;

    public ISO_Paper(double x, double y, DescriptorPaper paper) throws VisualObjectParserException {
        this(x, y, Bus.getScale(), paper);
    }

    public ISO_Paper(double x, double y, double scale, DescriptorPaper paper) throws VisualObjectParserException {
        this.dpaper = paper;

        delegate.addAll(voparser.parse(dpaper.getSourceAsStream(), scale, scale));
        for (VisualObject vo : delegate) {
            vo.setLayer(LayerController.find("Legenda"));
        }
        setLayer(LayerController.find("Legenda"));
    }

    @Override
    public void write(PrintStream stream) throws IOException {
        stream.println(String.format(Locale.ENGLISH, "%s %s %s %s %.3f %.3f %s",
                getVisualObjectName(), getLayer(), getColorAsString(), getLineTypeAsString(), getX(), getY(), dpaper.getName().replaceAll(" ", "_").toLowerCase()));
    }

    @Override
    public String getVisualObjectName() {
        return "paper";
    }

    @Override
    public String toString() {
        return dpaper.getName();
    }

    @Override
    public void writeToDxf(PrintStream stream) throws IOException {
        refresh();
        super.writeToDxf(stream);
    }

    @Override
    public void refresh() {
        double x = getX(), y = getY();
        delegate.clear();
        double scale = Bus.getScale();

        try {
            delegate.addAll(VisualObjectParser.getInstance().parse(dpaper.getSourceAsStream(), scale, scale));
        } catch (VisualObjectParserException ex) {
        }

        setLocation(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ISO_Paper other = (ISO_Paper) obj;
        if (this.oid != other.oid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.oid;
        return hash;
    }
    public static final String FILE_NAME_A1 = "formato_a1.vot";
    public static final String FILE_NAME_A4 = "formato_a4.vot";
}
