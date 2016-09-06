/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.unit.specs;

/**
 *
 * @author paulocanedo
 */
public abstract class AbstractUnit<T> implements Unit<T> {

    @Override
    public String toString(T value, int precision) {
        Object print = getValue(value);
        if (!(value instanceof Number)) {
            print = value.toString();
        }
        return String.format("%." + precision + "f %s", print, getSymbol());
    }
}
