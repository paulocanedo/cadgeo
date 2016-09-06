/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.unit.impl;

import br.com.geomapa.util.AngleValue;
import br.com.geomapa.util.unit.specs.DirectionUnit;

/**
 *
 * @author paulocanedo
 */
public class AzimuthUnit implements DirectionUnit {

    @Override
    public String getSymbol() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Azimute";
    }

    @Override
    public AngleValue getValue(AngleValue valueSI) {
        return valueSI;
    }

    @Override
    public AngleValue toSI(AngleValue value) {
        return value;
    }

    @Override
    public String toString(AngleValue value, int precision) {
        return getValue(value).toString("dd" + AngleValue.UNICODE_DEGREE + "mm'ss\"", precision);
    }
}
