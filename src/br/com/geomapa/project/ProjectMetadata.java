/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.project;

import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.geodesic.datum.Datum;
import br.com.geomapa.geodesic.datum.SAD69Datum;
import br.com.geomapa.geodesic.datum.SIRGASDatum;
import br.com.geomapa.geodesic.datum.WGS84Datum;
import br.com.geomapa.geodesic.point.GeodesicPointType;
import br.com.geomapa.geodesic.rbmc.BaseRBMC;
import br.com.geomapa.geodesic.rbmc.UtilRBMC;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.ui.panels.options.ResponsavelTecnico;
import br.com.geomapa.util.UnidadeFederativa;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author paulocanedo
 */
public final class ProjectMetadata {

    private File folder;
    private String nome;
    private String nomeImovel;
    private String comarca;
    private String circunscricao;
    private String artNumero;
    private String municipio;
    private UnidadeFederativa uf;
    private Integer zonaUtm;
    private Hemisphere hemisferio;
    private ResponsavelTecnico responsavelTecnico;
    private Datum datum;
    private List<BaseRBMC> rbmc = new ArrayList<BaseRBMC>();
    private float version = 0f;

    public ProjectMetadata() {
    }

    public ProjectMetadata(Properties prop) {
        nome = prop.getProperty("nome");
        nomeImovel = prop.getProperty("nome_imovel");
        comarca = prop.getProperty("comarca");
        circunscricao = prop.getProperty("circunscricao");
        artNumero = prop.getProperty("art_numero");
        municipio = prop.getProperty("municipio");
        uf = UnidadeFederativa.valueOf(prop.getProperty("unidade_federativa"));
        zonaUtm = Integer.valueOf(prop.getProperty("zona_utm"));
        hemisferio = Hemisphere.valueOf(prop.getProperty("hemisferio"));
        responsavelTecnico = ResponsavelTecnico.valueOf(prop.getProperty("responsavel_tecnico"));

        String sversion = prop.getProperty("version");
        version = Float.parseFloat(sversion == null || sversion.isEmpty() ? "0f" : sversion);

        String datumProp = prop.getProperty("datum");
        if (datumProp.equals("SAD69")) {
            datum = new SAD69Datum();
        } else if (datumProp.equals("WGS84")) {
            datum = new WGS84Datum();
        } else {
            datum = new SIRGASDatum();
        }

        try {
            rbmc.clear();
            addToRBMC(prop.getProperty("rbmc"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public Properties toProperties() {
        Properties prop = new Properties();
        prop.put("nome", nome);
        prop.put("nome_imovel", nomeImovel);
        prop.put("comarca", comarca);
        prop.put("circunscricao", circunscricao);
        prop.put("art_numero", artNumero);
        prop.put("municipio", municipio);
        prop.put("unidade_federativa", uf.name());
        prop.put("datum", datum.toString());
        prop.put("zona_utm", String.valueOf(zonaUtm));
        prop.put("hemisferio", hemisferio.name());
        prop.put("responsavel_tecnico", responsavelTecnico == null ? "" : responsavelTecnico.toSerializableString());
        prop.put("rbmc", rbmc.toString());
        prop.put("version", String.valueOf(version));

        return prop;
    }

    public File getRootFolder() {
        return folder;
    }

    public void setRootFolder(File folder) {
        this.folder = folder;
    }

    public File getRinexFolder() {
        return new File(getRootFolder(), "rinex");
    }

    public File getTemplateFolder() {
        File file = new File(getRootFolder(), "modelos");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public File getMemoFolder() {
        return new File(getRootFolder(), "memorial");
    }

    public File getTechnicalReport() {
        File file = new File(getRootFolder(), "relatorio_tecnico");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public File getAreaCalcFolder() {
        return new File(getRootFolder(), "calculo_area");
    }

    public File getCartographicDataFolder() {
        return new File(getRootFolder(), "dados_cartograficos");
    }

    public File getMapFolder() {
        return new File(getRootFolder(), "mapas");
    }

    public File getMetadataFolder() {
        return new File(getRootFolder(), ProjectUtils.metadataDirName);
    }

    public File getHistoryFolder() {
        return new File(getRootFolder(), "historico");
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeImovel() {
        return nomeImovel;
    }

    public void setNomeImovel(String nomeImovel) {
        this.nomeImovel = nomeImovel;
    }

    public String getCircunscricao() {
        return circunscricao;
    }

    public void setCircunscricao(String circunscricao) {
        this.circunscricao = circunscricao;
    }

    public String getComarca() {
        return comarca;
    }

    public void setComarca(String comarca) {
        this.comarca = comarca;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public UnidadeFederativa getUf() {
        return uf;
    }

    public void setUf(UnidadeFederativa uf) {
        this.uf = uf;
    }

    public String getArtNumero() {
        return artNumero;
    }

    public void setArtNumero(String artNumero) {
        this.artNumero = artNumero;
    }

    /**
     * @return the datum
     */
    public Datum getDatum() {
        return datum;
    }

    /**
     * @param datum the datum to set
     */
    public void setDatum(Datum datum) {
        this.datum = datum;
    }

    public Hemisphere getHemisferio() {
        return hemisferio;
    }

    public void setHemisferio(Hemisphere hemisferio) {
        this.hemisferio = hemisferio;
    }

    public Integer getZonaUtm() {
        return zonaUtm;
    }

    public void setZonaUtm(Integer zonaUtm) {
        this.zonaUtm = zonaUtm;
    }

    public ResponsavelTecnico getResponsavelTecnico() {
        return responsavelTecnico;
    }

    public void setResponsavelTecnico(ResponsavelTecnico responsavelTecnico) {
        this.responsavelTecnico = responsavelTecnico;
    }

    public List<BaseRBMC> getRbmc() {
        return rbmc;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public boolean isPersisted() {
        return getRootFolder() != null && getRootFolder().exists();
    }

    public void addToRBMC(String rbmcList) throws IOException {
        if (rbmcList != null && rbmcList.length() > 3) {
            String[] split = rbmcList.replaceAll("\\[", "").replaceAll("\\]", "").split(",");

            for (String s : split) {
                s = s.trim().toUpperCase();

                File rbmcFile = UtilRBMC.getRbmcFile(s);
                BaseRBMC baseRBMC = new BaseRBMC(s, UtilRBMC.getPropertiesFromFile(rbmcFile));
                if (rbmc.contains(baseRBMC)) {
                    throw new IllegalArgumentException(String.format("A base RBMC %s já foi adicionada.", baseRBMC.toString()));
                }

                rbmc.add(baseRBMC);
            }
        }
    }

    public static String deriveNextPointName(String name) {
        String splitCharNumber = splitCharNumber(name);
        if (splitCharNumber.contains("!")) {
            String[] split = splitCharNumber.split("!");
            String prefix = split[0];
            String number = split[1];
            if (number != null) {
                Integer i = Integer.parseInt(number) + 1;
                return nameWithMinDigits(prefix, i, name.length());
            }
        }

        return name + "1";
    }

    public String newPointName(GeodesicPointType type) {
        return newPointName(type, 1);
    }

    public String newPointName(GeodesicPointType type, int mindigits) {
        String prefix = "";
        try {
            String codigoIncra = getResponsavelTecnico().getCodigoIncra();
            if (codigoIncra != null && !codigoIncra.isEmpty()) {
                prefix = codigoIncra + "-";
            }
        } catch (Throwable ex) {
        }

        if (type == GeodesicPointType.X) {
            prefix += "A-";
        } else {
            prefix += type.name() + "-";
        }

        return nameWithMinDigits(prefix, DataManagement.newPointValue(type), mindigits);
    }

    private static String nameWithMinDigits(String prefix, int value, int mindigits) {
        String newPointValue = String.valueOf(value);
        while (prefix.length() + newPointValue.length() < mindigits) {
            newPointValue = "0" + newPointValue;
        }
        return prefix + newPointValue;
    }

    @Override
    public String toString() {
        return getNome();
    }

    public static String splitCharNumber(String value) {
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        for (int i = value.length() - 1; i >= 0; i--) {
            char c = value.charAt(i);
            if (c < '0' || c > '9') {
                if (!flag) {
                    sb.append("!");
                }
                flag = true;
            }
            sb.append(c);

        }
        return sb.reverse().toString();
    }

}
