/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.unit.impl;

import br.com.geomapa.util.AngleValue;
import br.com.geomapa.util.InvalidAngleValue;
import br.com.geomapa.util.RhumbValue;
import br.com.geomapa.util.unit.specs.DirectionUnit;

/**
 *
 * @author paulocanedo
 */
public class RhumbUnit implements DirectionUnit {

    @Override
    public String getSymbol() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Rumo";
    }

    @Override
    public RhumbValue getValue(AngleValue valueSI) {
        double valueDecimal = valueSI.toDegreeDecimal();
        if (valueDecimal > 0 && valueDecimal < 90) {
            return new RhumbValue(valueDecimal, RhumbValue.Quadrant.NE);
        } else if (valueDecimal > 90 && valueDecimal < 180) {
            return new RhumbValue(180 - valueDecimal, RhumbValue.Quadrant.SE);
        } else if (valueDecimal > 180 && valueDecimal < 270) {
            return new RhumbValue(valueDecimal - 180, RhumbValue.Quadrant.SW);
        } else if (valueDecimal > 270 && valueDecimal < 360) {
            return new RhumbValue(360 - valueDecimal, RhumbValue.Quadrant.NW);
        } else {
            RhumbValue.Quadrant quadrant = null;
            if (valueDecimal == 0) {
                quadrant = RhumbValue.Quadrant.N;
            } else if (valueDecimal == 90) {
                quadrant = RhumbValue.Quadrant.E;
            } else if (valueDecimal == 180) {
                quadrant = RhumbValue.Quadrant.S;
            } else if (valueDecimal == 270) {
                quadrant = RhumbValue.Quadrant.W;
            }
            return new RhumbValue(0, quadrant);
        }
    }

    @Override
    public AngleValue toSI(AngleValue value) {
        if (!(value instanceof RhumbValue)) {
            throw new InvalidAngleValue("O valor informado não é um rumo.");
        }

        RhumbValue rhumb = (RhumbValue) value;
        double valueDecimal = value.toDegreeDecimal();
        double azimuth = 0;
        if (rhumb.getQuadrant() == RhumbValue.Quadrant.NE) { //primeiro quadrante
            azimuth = valueDecimal;
        } else if (rhumb.getQuadrant() == RhumbValue.Quadrant.SE) { //segundo quadrante
            azimuth = 180 - valueDecimal;
        } else if (rhumb.getQuadrant() == RhumbValue.Quadrant.SW) { //terceiro quadrante
            azimuth = 180 + valueDecimal;
        } else if (rhumb.getQuadrant() == RhumbValue.Quadrant.NW) { //quarto quadrante
            azimuth = 360 - valueDecimal;
        } else if (rhumb.getQuadrant() == RhumbValue.Quadrant.N) {
            azimuth = 0;
        } else if (rhumb.getQuadrant() == RhumbValue.Quadrant.E) {
            azimuth = 90;
        } else if (rhumb.getQuadrant() == RhumbValue.Quadrant.S) {
            azimuth = 180;
        } else if (rhumb.getQuadrant() == RhumbValue.Quadrant.W) {
            azimuth = 270;
        }
        return new AngleValue(azimuth);
    }

    @Override
    public String toString(AngleValue value, int precision) {
        return getValue(value).toString("dd" + AngleValue.UNICODE_DEGREE + "mm'ss\"", precision);
    }
}
