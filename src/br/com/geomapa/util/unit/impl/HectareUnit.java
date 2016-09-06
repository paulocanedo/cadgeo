/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.unit.impl;

import br.com.geomapa.util.unit.specs.AreaUnit;
import br.com.geomapa.util.unit.specs.AbstractUnit;

/**
 *
 * @author paulocanedo
 */
public class HectareUnit extends AbstractUnit<Double> implements AreaUnit {

    @Override
    public String getDescription() {
        return "Hectare";
    }

    @Override
    public String getSymbol() {
        return "ha";
    }

    @Override
    public Double getValue(Double valueSI) {
        return valueSI / 10000;
    }

    @Override
    public Double toSI(Double value) {
        return value * 10000;
    }
}
