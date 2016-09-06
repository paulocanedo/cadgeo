/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.point;

/**
 *
 * @author paulocanedo
 */
public enum LineDivisionType {

        LA1("Limite por vértice do tipo M"),
        LA2("Limite artificial por barragem"),
        LA3("Limite artificial por canal"),
        LA4("Limite artificial por estrada"),
        LA5("Limite artificial não categorizada"),
        LN1("Limite natural por água corrente"),
        LN2("Limite natural por água dormente"),
        LN3("Limite natural por terreno alagado ou alagável"),
        LN4("Limite natural por encosta ou cânion"),
        LN5("Limite natural não categorizada");

        private LineDivisionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            String name = super.toString();
            return name + " - " + getDescription();
        }
        private String description;
    }
