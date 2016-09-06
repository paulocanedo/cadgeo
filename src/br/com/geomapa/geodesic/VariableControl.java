/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic;

import br.com.geomapa.geodesic.coordinate.Longitude;
import br.com.geomapa.geodesic.point.LineDivisionType;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.main.Bus;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.ui.panels.options.ResponsavelTecnico;
import br.com.geomapa.util.AngleValue;
import br.com.geomapa.util.receitafederal.Cnpj;
import br.com.geomapa.util.receitafederal.Cpf;
import br.com.geomapa.util.receitafederal.PessoaRFException;
import br.com.geomapa.util.unit.impl.HectareUnit;
import br.com.geomapa.util.unit.impl.Meter;
import br.com.geomapa.util.unit.specs.AreaUnit;
import br.com.geomapa.util.unit.specs.DistanceUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author paulocanedo
 */
public class VariableControl {

    private static PolygonalMetadata getCurrentPolygonalMetadata() {
        return Bus.getCurrentPolygonal().getMetadata();
    }

    private static ProjectMetadata getCurrentProjectMetadata() {
        return Bus.getCurrentProjectMetadata();
    }

    private static Polygonal getCurrentPolygonal() {
        return Bus.getCurrentPolygonal();
    }

    public static String getProprietarioFromId(String borderName) {
        MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();
        if (mainPolygonal.containsPolygonalName(borderName)) {
            Polygonal p = mainPolygonal.createOrGetPolygonal(borderName);
            borderName = p.getMetadata().getNomeProprietario();

            if (borderName == null || borderName.isEmpty()) {
                borderName = "Campo Proprietário em branco";
            }
        }
        return borderName;
    }
    
    public static String getSNCRFromId(String borderName) {
        MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();
        if (mainPolygonal.containsPolygonalName(borderName)) {
            Polygonal p = mainPolygonal.createOrGetPolygonal(borderName);
            borderName = p.getMetadata().getCodigoSncr();

            if (borderName != null && !borderName.isEmpty()) {
                borderName = "";
            }
        }
        return borderName;
    }

    public static String replaceText(String textWithVar) {
        if (textWithVar == null || textWithVar.isEmpty()) {
            return "";
        }
        try {
            String s = textWithVar;

            Matcher matcher = pattern.matcher(s);
            while (matcher.find()) {
                String what = matcher.group(1);
                String param = matcher.group(2);

                if (param != null) {
                    String[] split = param.split(",");
                    if (split.length > 1) {
                        GeodesicPoint from = DataManagement.findPoint(split[0].substring(1));
                        GeodesicPoint to = DataManagement.findPoint(split[1].substring(0, split[1].length() - 1));
                        if (from != null && to != null) {
                            LineDivision ld = LineDivision.getInstance(from, to);
                            String borderName = (ld.getBorderName() == null || ld.getBorderName().isEmpty()) ? "CONFRONTANTE SEM PREENCHER" : ld.getBorderName();

                            MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();
                            if (mainPolygonal.containsPolygonalName(borderName)) {
                                borderName = getProprietarioFromId(borderName);
                            }

                            s = s.replace(what + param, borderName);

                            if (ld.getType() == LineDivisionType.LN1) {
                                if (ld.isWaterCourseClockwiseDirection()) {
                                    s = "<- " + s;
                                } else {
                                    s = s + " ->";
                                }
                            }
                            continue;
                        }
                    }
                }

                String var = matcher.group();
                try {
                    VariableNamesPreset variable = VariableNamesPreset.valueOf(var.substring(1));

                    switch (variable) {
                        case PROPRIEDADE_ID:
                            s = s.replaceAll("\\" + var, getPropriedadeId());
                            break;
                        case PROPRIEDADE:
                            s = s.replaceAll("\\" + var, getNomePropriedade());
                            break;
                        case PROPRIETARIO:
                            s = s.replaceAll("\\" + var, getProprietario());
                            break;
                        case GLEBA:
                            s = s.replaceAll("\\" + var, getGleba());
                            break;
                        case MUNICIPIO:
                            s = s.replaceAll("\\" + var, getMunicipio());
                            break;
                        case DATA_LEVANTAMENTO:
                            s = s.replaceAll("\\" + var, getDataLevantamento());
                            break;
                        case AREA:
                            s = s.replaceAll("\\" + var, getArea());
                            break;
                        case PERIMETRO:
                            s = s.replaceAll("\\" + var, getPerimetro());
                            break;
                        case SIS_GEODESICO:
                            s = s.replaceAll("\\" + var, getSistemaGeodesico());
                            break;
                        case PROP_CPF_CNPJ:
                            s = s.replaceAll("\\" + var, getCpfCnpj());
                            break;
                        case RT_NOME:
                            s = s.replaceAll("\\" + var, getNomeRT());
                            break;
                        case RT_PROFISSAO:
                            s = s.replaceAll("\\" + var, getProfissaoRT());
                            break;
                        case RT_CODIGO_INCRA:
                            s = s.replaceAll("\\" + var, getCredenciamentoIncra());
                            break;
                        case RT_CREA:
                            s = s.replaceAll("\\" + var, getCREA());
                            break;
                        case ESCALA:
                            s = s.replaceAll("\\" + var, getEscala());
                            break;
                        case SIS_PROJECAO:
                            s = s.replaceAll("\\" + var, getSistemaProjecao());
                            break;
                        case UF:
                            s = s.replaceAll("\\" + var, getUF());
                            break;
                        case UNIDADE_FEDERATIVA:
                            s = s.replaceAll("\\" + var, getUnidadeFederativa());
                            break;
                        case REF_CONV_MERIDIANA:
                            s = s.replaceAll("\\" + var, getConvMeridiana());
                            break;
                        case REF_FATOR_ESCALA:
                            s = s.replaceAll("\\" + var, getFatorEscala());
                            break;
                        case MERIDIANO_CENTRAL:
                            s = s.replaceAll("\\" + var, getMeridianoCentral());
                            break;
                        case REF_PONTO:
                            s = s.replaceAll("\\" + var, getPontoReferencia().toString());
                            break;
                        case REF_PONTO_LAT:
                            s = s.replaceAll("\\" + var, getPontoReferencia().getCoordinate().toGeodesic().getLatitude().toString());
                            break;
                        case REF_PONTO_LONG:
                            s = s.replaceAll("\\" + var, getPontoReferencia().getCoordinate().toGeodesic().getLongitude().toString());
                            break;
                        case ART_NUMERO: {
                            s = s.replaceAll("\\" + var, getArtNumero());
                            break;
                        }
                        default:
                            throw new AssertionError();
                    }
                } catch (Exception ex) {
                }

            }

            return s;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return textWithVar;
        }
    }
    private static AreaUnit aUnit = new HectareUnit();
    private static DistanceUnit dUnit = new Meter();
    private static Pattern pattern = Pattern.compile("(\\$\\w+_*\\w*\\d*)(\\[.+\\])*");

    public static void main(String... args) {
//        String s = "Teste: $confrontante";
        String s = "Teste: $confrontante[ABCM1,ABCM2] ->";
        System.out.println(s.replace("$confrontante[ABCM1,ABCM2]", "teste"));
    }

    private static String getProprietario() {
        return getCurrentPolygonalMetadata().getNomeProprietario();
    }

    private static String getGleba() {
        return getCurrentProjectMetadata().getNome();
    }

    private static String getDataLevantamento() {
        return "";
    }

    private static String getMunicipio() {
        return getCurrentProjectMetadata().getMunicipio();
    }

    private static String getArea() {
        Polygonal polygonal = getCurrentPolygonal();
        if (!polygonal.isClosed()) {
            return aUnit.toString(0d, 4);
        }
        return aUnit.toString(polygonal.area(), 4);
    }

    private static String getPerimetro() {
        Polygonal polygonal = getCurrentPolygonal();
        if (!polygonal.isClosed()) {
            return dUnit.toString(0d, 2);
        }
        return dUnit.toString(polygonal.perimeter(), 2);
    }

    private static String getSistemaGeodesico() {
        return getCurrentProjectMetadata().getDatum().toString();
    }

    private static String getCpfCnpj() {
        String s = getCurrentPolygonalMetadata().getCpfCnpj();
        s = Cnpj.retiraSimbolos(s);

        try {
            if (s.length() == 14) {
                if (Cnpj.isValid(s)) {
                    return (Cnpj.format(s));
                }
            } else if (s.length() == 11) {
                if (Cpf.isValid(s)) {
                    return (Cpf.format(s));
                }
            }
        } catch (PessoaRFException ex) {
        }
        return s;
    }

    private static String getNomeRT() {
        ResponsavelTecnico rt = getCurrentProjectMetadata().getResponsavelTecnico();
        if (rt == null) {
            return "";
        }
        return rt.getNome();
    }

    private static String getProfissaoRT() {
        ResponsavelTecnico rt = getCurrentProjectMetadata().getResponsavelTecnico();
        if (rt == null) {
            return "";
        }
        return rt.getProfissao();
    }

    private static String getCredenciamentoIncra() {
        ResponsavelTecnico rt = getCurrentProjectMetadata().getResponsavelTecnico();
        if (rt == null) {
            return "";
        }
        return rt.getCodigoIncra();
    }

    private static String getCREA() {
        ResponsavelTecnico rt = getCurrentProjectMetadata().getResponsavelTecnico();
        if (rt == null) {
            return "";
        }
        return rt.getCodigoCrea();
    }

    private static String getEscala() {
        return String.format("1/%.0f", getCurrentPolygonalMetadata().getEscala() * 1000);
    }

    private static String getSistemaProjecao() {
        return "Zona UTM " + getZonaUtm();
    }

    private static String getUF() {
        return getCurrentProjectMetadata().getUf().getSigla();
    }

    private static String getUnidadeFederativa() {
        return getCurrentProjectMetadata().getUf().getNome();
    }

    private static String getConvMeridiana() {
        Polygonal polygonal = getCurrentPolygonal();
        if (polygonal == null) {
            return "";
        }

        GeodesicPoint referencePoint = polygonal.referencePoint();
        if (referencePoint == null) {
            return "";
        }
        AngleValue meridianConvergence = referencePoint.getCoordinate().toGeodesic().getMeridianConvergence();
        return meridianConvergence.toString("dd" + AngleValue.UNICODE_DEGREE + "mm'ss\"", 0);
    }

    private static String getNomePropriedade() {
        return getCurrentPolygonal().getMetadata().getDescricao();
    }

    private static String getPropriedadeId() {
        return getCurrentPolygonal().getName();
    }

    private static String getFatorEscala() {
        return String.format("%.8f", getCurrentPolygonal().referencePoint().getCoordinate().toGeodesic().getScaleCorrection());
    }

    private static String getMeridianoCentral() {
        return Longitude.getCentralMeridian(getZonaUtm()).toMeridianCentralString();
    }

    private static GeodesicPoint getPontoReferencia() {
        return getCurrentPolygonal().referencePoint();
    }

    private static String getArtNumero() {
        return getCurrentProjectMetadata().getArtNumero();
    }

    public static int getZonaUtm() {
        Polygonal currentPolygonal = getCurrentPolygonal();
        int zonaUtm = currentPolygonal.getMetadata().getZonaUtm();
        ProjectMetadata currentProjectMetadata = getCurrentProjectMetadata();

        if (currentPolygonal.isMain() || zonaUtm == 0) {
            zonaUtm = currentProjectMetadata.getZonaUtm();
        }
        return zonaUtm;
    }
}