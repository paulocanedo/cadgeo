/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic;

/**
 * <p>Enumeração para representar os possíveis campos para se formar um ponto
 * geodésico.</p>
 *
 * <p><b>ATENÇÃO:</b> A ordem dos elementos desta enum é de extrema importância,
 * nunca modifique a ordem das mesmas nem sequer remova qualquer elemento desta 
 * classe. Caso seja necessário terminar a utilização de qualquer campo desta
 * enum, apenas declare-o como Deprecated.</p>
 * 
 * @author paulocanedo
 */
public enum GeodesicEnum {

    NAME,
    FIXED_AMBIGUITY,
    EAST,
    NORTH,
    LATITUDE,
    LONGITUDE,
    ELIPSOIDAL_HEIGHT,
    QUALITY_X,
    QUALITY_Y,
    QUALITY_Z,
    QUALITY_HOR,
    QUALITY_VER,
    NEXT_POINT,
    AZIMUTH,
    DISTANCE,
    FACTOR_K,
    MEASUREMENT_METHOD,
    LIMIT_TYPE,
    BORDERING,
    TIME_FIRST_OBSERVATION,
    TIME_LAST_OBSERVATION,
    UNKOWN
}
