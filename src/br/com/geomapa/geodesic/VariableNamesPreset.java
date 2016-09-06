/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic;

/**
 *
 * @author paulocanedo
 */
public enum VariableNamesPreset implements Comparable<VariableNamesPreset> {

    PROPRIEDADE_ID,
    PROPRIEDADE,
    PROPRIETARIO,
    PROP_CPF_CNPJ,
    GLEBA,
    AREA,
    PERIMETRO,
    DATA_LEVANTAMENTO,
    MUNICIPIO,
    UF,
    UNIDADE_FEDERATIVA,
    SIS_GEODESICO,
    ESCALA,
    SIS_PROJECAO,
    MERIDIANO_CENTRAL,
    REF_PONTO,
    REF_PONTO_LAT,
    REF_PONTO_LONG,
    REF_CONV_MERIDIANA,
    REF_FATOR_ESCALA,
    RT_NOME,
    RT_PROFISSAO,
    RT_CODIGO_INCRA,
    RT_CREA,
    ART_NUMERO;

    @Override
    public String toString() {
        return "$" + name();
    }
}
