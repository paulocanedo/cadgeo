/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.unit.impl;

import br.com.geomapa.util.unit.specs.AbstractUnit;
import br.com.geomapa.util.unit.specs.DistanceUnit;

/**
 *
 * @author paulocanedo
 */
public class Meter extends AbstractUnit<Double> implements DistanceUnit {

    @Override
    public String getDescription() {
        return "Metro";
    }

    @Override
    public String getSymbol() {
        return "m";
    }

    @Override
    public Double getValue(Double valueSI) {
        return valueSI;
    }

    @Override
    public Double toSI(Double value) {
        return value;
    }
}
