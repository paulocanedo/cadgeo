/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.memorial;

import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.util.AngleValue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class MemorialBorder {

    private String nome;
    private List<GeodesicPoint> points = new ArrayList<GeodesicPoint>();
    private List<AngleValue> azimutes = new ArrayList<AngleValue>();
    private List<Double> distancias = new ArrayList<Double>();

    public MemorialBorder(String nome, GeodesicPoint pontoInicial, AngleValue azimuteInicial, Double distInicial) {
        this.nome = nome;
        this.points.add(pontoInicial);
        this.azimutes.add(azimuteInicial);
        this.distancias.add(distInicial);
    }

    public String getNome() {
        return nome;
    }
    
    public void add(GeodesicPoint point) {
        points.add(point);
    }

    public void add(AngleValue azimute) {
        azimutes.add(azimute);
    }

    public void add(Double distancia) {
        distancias.add(distancia);
    }
    
    public List<GeodesicPoint> getPoints() {
        return points;
    }

    public List<AngleValue> getAzimutes() {
        return azimutes;
    }

    public List<Double> getDistancias() {
        return distancias;
    }
    
    @Override
    public String toString() {
        return nome + ": " + points.toString();
    }
}
