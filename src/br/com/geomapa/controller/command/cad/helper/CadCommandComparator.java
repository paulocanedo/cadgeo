/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.controller.command.cad.helper;

import br.com.geomapa.controller.command.cad.spec.CadCommand;
import java.util.Comparator;

/**
 *
 * @author paulocanedo
 */
public class CadCommandComparator implements Comparator<CadCommand> {

    @Override
    public int compare(CadCommand o1, CadCommand o2) {
        return o1.toString().compareToIgnoreCase(o2.toString());
    }
}
