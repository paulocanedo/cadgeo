/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.panels.options;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

/**
 *
 * @author paulocanedo
 */
public class ResponsavelTecnico {

    private int id;
    private String nome;
    private String codigoIncra;
    private String codigoCrea;
    private String profissao;

    public ResponsavelTecnico() {
    }

    public ResponsavelTecnico(String nome, String codigoIncra, String codigoCrea, String profissao) {
        this.nome = nome;
        this.codigoIncra = codigoIncra;
        this.codigoCrea = codigoCrea;
        this.profissao = profissao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoCrea() {
        return codigoCrea;
    }

    public void setCodigoCrea(String codigoCrea) {
        this.codigoCrea = codigoCrea;
    }

    public String getCodigoIncra() {
        return codigoIncra;
    }

    public void setCodigoIncra(String codigoIncra) {
        this.codigoIncra = codigoIncra;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public boolean isInvalid() {
        return (nome.isEmpty() || codigoIncra.isEmpty() || codigoCrea.isEmpty() || profissao.isEmpty());
    }

    public String toSerializableString() {
        return String.format("%s,%s,%s,%s", nome, codigoIncra, codigoCrea, profissao);
    }

    public static ResponsavelTecnico valueOf(String s) {
        try {
            StringTokenizer st = new StringTokenizer(s, ",");
            ResponsavelTecnico rt = new ResponsavelTecnico();

            rt.setNome(st.nextToken());
            rt.setCodigoIncra(st.nextToken());
            rt.setCodigoCrea(st.nextToken());
            if (st.hasMoreTokens()) {
                rt.setProfissao(st.nextToken());
            }
            return rt;
        } catch (Exception ex) {
            return new ResponsavelTecnico("INVALIDO", "AAA", "0000", "PROFISSAO");
        }
    }

    @Override
    public String toString() {
        return String.format("%s (INCRA: %S; CREA: %s)", getNome(), getCodigoIncra(), getCodigoCrea());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResponsavelTecnico) {
            ResponsavelTecnico other = (ResponsavelTecnico) obj;
            return getCodigoIncra().equals(other.getCodigoIncra()) && getCodigoCrea().equals(other.getCodigoCrea());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.codigoIncra != null ? this.codigoIncra.hashCode() : 0);
        hash = 79 * hash + (this.codigoCrea != null ? this.codigoCrea.hashCode() : 0);
        return hash;
    }

    public static List<ResponsavelTecnico> allRTs() {
        rts.clear();
        Preferences pref = OptionsPanel.pref;
        for (int i = 0; i < 10; i++) {
            String s = pref.get("rt_" + i, null);
            if (s == null) {
                return rts;
            }
            ResponsavelTecnico rt = valueOf(s);
            rt.setId(i);
            rts.add(rt);
        }
        return rts;
    }
    private static final List<ResponsavelTecnico> rts = new ArrayList<ResponsavelTecnico>();
}
