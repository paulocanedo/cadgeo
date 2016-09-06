/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.geodesic.memorial;

import br.com.geomapa.geodesic.InvalidPolygonalException;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.VariableControl;
import br.com.geomapa.graphic.cad.geo.LineDivision;
import br.com.geomapa.geodesic.rbmc.BaseRBMC;
import br.com.geomapa.project.ProjectMetadata;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import br.com.geomapa.util.ArraysUtil;
import br.com.geomapa.util.unit.impl.AzimuthUnit;
import br.com.geomapa.util.unit.impl.Meter;
import br.com.geomapa.util.unit.specs.DirectionUnit;
import br.com.geomapa.util.unit.specs.DistanceUnit;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class Memorial {

    private Polygonal polygonal;
    private ProjectMetadata projectInfo;
    private DistanceUnit dUnit;
    private DirectionUnit dirUnit;
    private boolean aspasMarco = true;
    private boolean pontoNegrito = true;
    private boolean intermediarioNegrito = true;
    private boolean coordNegrito = true;

    public Memorial(Polygonal polygonal, ProjectMetadata projectInfo) {
        this.polygonal = polygonal;
        this.projectInfo = projectInfo;

        this.dUnit = new Meter();
        this.dirUnit = new AzimuthUnit();

        if (!polygonal.isClosed()) {
            throw new InvalidPolygonalException("O perímetro não está corretamente definido");
        }
    }

    public Memorial(Polygonal polygonal, ProjectMetadata projectInfo, DistanceUnit dUnit, DirectionUnit dirUnit) {
        this.polygonal = polygonal;
        this.projectInfo = projectInfo;
        this.dUnit = dUnit;
        this.dirUnit = dirUnit;

        if (!polygonal.isClosed()) {
            throw new InvalidPolygonalException("O perímetro não está corretamente definido");
        }
    }

    @Override
    public String toString() {
        if (polygonal == null || !polygonal.isClosed() || !polygonal.isBorderingFilled()) {
            return "";
        }
        List<BaseRBMC> rbmc = projectInfo.getRbmc();
        try {
            int zonaUtm = VariableControl.getZonaUtm();
            List<List<LineDivision>> lineDivisionsGrouped = ArraysUtil.collectLineDivisionsGrouped(polygonal);
            
            List<LineDivision> lastGroup = lineDivisionsGrouped.get(lineDivisionsGrouped.size()-1);
            String firstBorderName = lastGroup.get(lastGroup.size()-1).getBorderName();
            List<LineDivision> firstGroup = lineDivisionsGrouped.get(0);
            LineDivision firstBorder = firstGroup.get(0);

            boolean lastVertex = false;

            StringBuilder memorial = new StringBuilder();
            memorial.append("Inicia-se a descrição deste perímetro no vértice ");
            memorial.append(pontoNegrito ? "<b>" : "");
            memorial.append(getPointName(firstBorder.getStartPoint().getName(), aspasMarco));
            memorial.append(pontoNegrito ? "</b>" : "");
            memorial.append(", de coordenadas ");
            memorial.append(coordNegrito ? "<b>" : "");
            memorial.append(firstBorder.getStartPoint().getCoordinate(zonaUtm).toUTM());
            memorial.append(coordNegrito ? "</b>" : "");
            memorial.append(" no limite ");
            memorial.append(isFemaleWord(firstBorderName) ? "da" : "do");
            memorial.append(" ").append(firstBorderName);
            for (List<LineDivision> groupLD : lineDivisionsGrouped) {
                LineDivision firstLD = groupLD.get(0);

                memorial.append(memorial.charAt(memorial.length()-2) != ';' ? "; " : "");
                memorial.append("deste, segue confrontando com ");
                memorial.append(firstLD.getBorderName()).append(",");
                if (groupLD.size() == 1) {
                    memorial.append(" com o azimute de ");
                    memorial.append(dirUnit.toString(firstLD.azimuth(), 0));
                    memorial.append(" e a distânia de ");
                    memorial.append(dUnit.toString(firstLD.distance(), 2));
                    memorial.append(", ");
                    memorial.append("até o vértice ");
                    memorial.append(intermediarioNegrito ? "<b>" : "");
                    memorial.append(getPointName(firstLD.getEndPoint().getNameNoSeparators(), aspasMarco));
                    memorial.append(intermediarioNegrito ? "</b>" : "");
                    memorial.append(", de coordenadas ");
                    memorial.append(intermediarioNegrito && coordNegrito ? "<b>" : "");
                    memorial.append(firstLD.getEndPoint().getCoordinate(zonaUtm).toUTM());
                    memorial.append(intermediarioNegrito && coordNegrito ? "</b>" : "");
                    lastVertex = false;
                } else {
                    memorial.append(" com os seguintes azimutes e distâncias: ");
                    for (LineDivision ld : groupLD) {
                        memorial.append(dirUnit.toString(ld.azimuth(), 0));
                        memorial.append(" e ");
                        memorial.append(dUnit.toString(ld.distance(), 2));
                        memorial.append(" ");
                        memorial.append("até o vértice ");
                        memorial.append(intermediarioNegrito ? "<b>" : "");
                        memorial.append(getPointName(ld.getEndPoint().getNameNoSeparators(), aspasMarco));
                        memorial.append(intermediarioNegrito ? "</b>" : "");
                        memorial.append(", de coordenadas ");
                        memorial.append(intermediarioNegrito && coordNegrito ? "<b>" : "");
                        memorial.append(ld.getEndPoint().getCoordinate(zonaUtm).toUTM());
                        memorial.append(intermediarioNegrito && coordNegrito ? "</b>" : "");
                        memorial.append("; ");
                    }
                    lastVertex = true;

                }
            }

            if (lastVertex) {
                memorial.append("até o vértice ");
                memorial.append(pontoNegrito ? "<b>" : "");
                memorial.append(getPointName(firstBorder.getStartPoint().getName(), aspasMarco));
                memorial.append(pontoNegrito ? "</b>" : "");
            }
            memorial.append(", ponto inicial da descrição deste perímetro. Todas as coordenadas ");
            memorial.append("aqui descritas estão georreferenciadas ao Sistema Geodésico Brasileiro, ");

            if (rbmc.size() > 0) {
                memorial.append("a partir das estações ativas RBMC ");
                for (int i = 0; i < rbmc.size(); i++) {
                    BaseRBMC baseRbmc = rbmc.get(i);
                    memorial.append("de ");
                    memorial.append(String.format("<b>%s</b>", baseRbmc.getCidade()));
                    memorial.append(" de coordenadas: ");
                    memorial.append(String.format("<b>%s</b>", baseRbmc.getCoordinate().toUTM().toString()));
                    memorial.append(", MC: <b>").append(baseRbmc.getCoordinate().toGeodesic().getLongitude().getCentralMeridian().toMeridianCentralString()).append("</b>");
                    memorial.append(i == rbmc.size() - 2 ? " e " : ", ");
                }
            }

            memorial.append("e encontram-se ");
            memorial.append("representadas no Sistema UTM, referenciadas ao Meridiano Central no ");
            memorial.append(firstBorder.getStartPoint().getCoordinate(zonaUtm).toGeodesic().getLongitude().getCentralMeridian().toMeridianCentralString());
            memorial.append(", tendo ");
            memorial.append("como datum o ");
            memorial.append(projectInfo.getDatum());
            memorial.append(". Todos os azimutes e distâncias, área e perímetro foram ");
            memorial.append("calculados no plano de projeção UTM.");

            return memorial.toString();
        } catch (PolygonalException ex) {
            return "";
        }
    }

    private boolean isFemaleWord(String word) {
        int indexOf = word.indexOf(" ");
        if (indexOf > 0) {
            return Character.toLowerCase(word.charAt(indexOf - 1)) == 'a';
        }
        return word.toLowerCase().endsWith("a");
    }

    private String getPointName(String name, boolean quoted) {
        if (!quoted) {
            return name;
        } else {
            return String.format("'%s'", name);
        }
    }

    public boolean isAspasMarco() {
        return aspasMarco;
    }

    public void setAspasMarco(boolean aspasMarco) {
        this.aspasMarco = aspasMarco;
    }

    public boolean isCoordNegrito() {
        return coordNegrito;
    }

    public void setCoordNegrito(boolean coordNegrito) {
        this.coordNegrito = coordNegrito;
    }

    public boolean isIntermediarioNegrito() {
        return intermediarioNegrito;
    }

    public void setIntermediarioNegrito(boolean intermediarioNegrito) {
        this.intermediarioNegrito = intermediarioNegrito;
    }

    public boolean isPontoNegrito() {
        return pontoNegrito;
    }

    public void setPontoNegrito(boolean pontoNegrito) {
        this.pontoNegrito = pontoNegrito;
    }
}
