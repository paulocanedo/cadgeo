/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util;

/**
 * An immutable class to represent rhumb value
 * @author paulocanedo
 */
public class RhumbValue extends AngleValue {

    public enum Quadrant {

        NE, SE, SW, NW, N, E, S, W
    }
    private Quadrant quadrant;

    public RhumbValue(double degree, double minute, double second, boolean positive, Quadrant quadrant) {
        super(degree, minute, second, positive);
        this.quadrant = quadrant;

        if (degree < 0 || degree > 90) {
            throw new InvalidAngleValue("Valor angular rumo inválido: " + degree);
        }
    }

    public RhumbValue(double value, Quadrant quadrant) {
        super(value);
        this.quadrant = quadrant;

        if (value < 0 || value > 90) {
            throw new InvalidAngleValue("Valor angular rumo inválido: " + value);
        }
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }

    @Override
    public String toString(String pattern, int precision) {
        return super.toString(pattern, precision) + " " + quadrant.name();
    }
}
