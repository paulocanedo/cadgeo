/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic;

import br.com.geomapa.util.NumberFloatUtils;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Properties;

/**
 *
 * @author paulocanedo
 */
public final class PolygonalMetadata {

    private String nome;
    private String descricao;
    private String nomeProprietario;
    private String numeroMatricula;
    private String codigoSncr;
    private String cpfCnpj;
    private int zonaUtm;
    private String sat1;
    private String sat2;
    private float escala = 1;
    private File file;
    public static final String PROP_ESCALA = "escala";
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public float getEscala() {
        return escala;
    }

    public void setEscala(float escala) {
        float oldEscala = this.escala;
        this.escala = escala;
        propertyChangeSupport.firePropertyChange(PROP_ESCALA, oldEscala, escala);
    }

    public PolygonalMetadata() {
    }

    public PolygonalMetadata(Properties prop) {
        nome = prop.getProperty("nome");
        descricao = prop.getProperty("descricao");
        nomeProprietario = prop.getProperty("proprietario");
        numeroMatricula = prop.getProperty("numero_matricula");
        codigoSncr = prop.getProperty("codigo_sncr");
        cpfCnpj = prop.getProperty("cpf_cnpj");

        sat1 = prop.getProperty("sat1");
        sat2 = prop.getProperty("sat2");
        setZonaUtm(prop.getProperty("zona_utm"));
        setEscala(parseFloat(prop.getProperty("escala")));
    }

    public Properties toProperties() {
        Properties prop = new Properties();
        prop.put("nome", notNull(nome));
        prop.put("descricao", notNull(descricao));
        prop.put("proprietario", notNull(nomeProprietario));
        prop.put("numero_matricula", notNull(numeroMatricula));
        prop.put("codigo_sncr", notNull(codigoSncr));
        prop.put("cpf_cnpj", notNull(cpfCnpj));
        prop.put("sat1", notNull(sat1));
        prop.put("sat2", notNull(sat2));
        prop.put("zona_utm", String.valueOf(zonaUtm));
        prop.put("escala", String.valueOf(escala));
        return prop;
    }

    private String notNull(Object o) {
        return o == null ? "" : o.toString();
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private float parseFloat(String s) {
        try {
            return NumberFloatUtils.parseFloat(s);
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNomeProprietario() {
        return nomeProprietario;
    }

    public void setNomeProprietario(String nomeProprietario) {
        this.nomeProprietario = nomeProprietario;
    }

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(String numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }

    public String getCodigoSncr() {
        return codigoSncr;
    }

    public void setCodigoSncr(String sncrImovel) {
        this.codigoSncr = sncrImovel;
    }

    public void setEscala(String escala) {
        setEscala(parseFloat(escala));
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getSat1() {
        return sat1;
    }

    public void setSat1(String sat1) {
        this.sat1 = sat1;
    }

    public String getSat2() {
        return sat2;
    }

    public void setSat2(String sat2) {
        this.sat2 = sat2;
    }

    public int getZonaUtm() {
        return zonaUtm;
    }

    public void setZonaUtm(String zonaUtm) {
        this.zonaUtm = parseInt(zonaUtm);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
