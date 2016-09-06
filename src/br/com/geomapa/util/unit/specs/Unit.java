/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.unit.specs;

/**
 *
 * @author paulocanedo
 */
public interface Unit<T> {

    public String getSymbol();

    public String getDescription();

    public T getValue(T valueSI);

    public String toString(T value, int precision);

    public T toSI(T value);
}
