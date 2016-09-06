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
public class SquareMeterUnit extends AbstractUnit<Double> implements AreaUnit {

    @Override
    public String getDescription() {
        return "Metro quadrado";
    }

    @Override
    public String getSymbol() {
        return "m\u00b2";
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
