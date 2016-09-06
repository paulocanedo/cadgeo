/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package br.com.geomapa.controller.command.cad.spec;

import br.com.geomapa.controller.CadCommandController;
import br.com.geomapa.controller.CommandException;
import br.com.geomapa.controller.LayerController;
import br.com.geomapa.geodesic.CoordinateException;
import br.com.geomapa.geodesic.MainPolygonal;
import br.com.geomapa.geodesic.Polygonal;
import br.com.geomapa.geodesic.PolygonalUtils;
import br.com.geomapa.geodesic.coordinate.UTMCoordinate;
import br.com.geomapa.graphic.Layer;
import br.com.geomapa.graphic.RenderContext;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.main.DataManagement;
import br.com.geomapa.main.Main;
import br.com.geomapa.ui.panels.PointManagerFormPanel;
import br.com.geomapa.ui.panels.topographic.GLTopographicPanel;
import br.com.geomapa.ui.panels.topographic.PolygonalException;
import br.com.geomapa.util.AngleValue;
import br.com.geomapa.util.ArraysUtil;
import br.com.geomapa.util.NumberFloatUtils;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;
import java.util.TreeSet;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 *
 * @author paulocanedo
 */
public abstract class AbstractCadCommand implements CadCommand {

    protected static Stack<CadCommand> stackUndo = new Stack<CadCommand>();
    protected GLTopographicPanel displayPanel;
    protected String message;
    protected boolean finished = false;
    protected boolean canDraw = false;
    protected final static FileDialog fileDialog = new FileDialog(Main.getInstance());

    {
        CadCommandController.ERROR_MESSAGE = null;
    }

    public AbstractCadCommand(String message) {
        this.message = message;
    }

    public AbstractCadCommand(GLTopographicPanel displayPanel, String message) {
        this.displayPanel = displayPanel;
        this.message = message;
    }

    @Override
    public final String getMessageStatus() {
        return message;
    }

    @Override
    public final boolean wasFinished() {
        return finished;
    }

    @Override
    public final boolean canDraw() {
        return canDraw;
    }

    public final void finish() {
        this.finished = true;
        this.canDraw = false;

        if (isUndoable()) {
            stackUndo.add(this);
        }

        if (!CadCommandController.restoreOldCommand()) {
            CadCommandController.setCommand(null);
        }
    }

    @Override
    public final boolean nextState(String text) {
        try {
            return transitToNextState(text);
        } catch (Throwable ex) {
            message = ">" + ex.getMessage();
        }
        return false;
    }

    @Override
    public final boolean nextState(Point2D point) {
        try {
            return transitToNextState(point);
        } catch (Throwable ex) {
            message = ">" + ex.getMessage();
        }
        return false;
    }

    public final GLTopographicPanel getDisplayPanel() {
        return displayPanel;
    }

    @Override
    public void setDisplayPanel(GLTopographicPanel displayPanel) throws CommandException {
        this.displayPanel = displayPanel;
    }

    public final Polygonal getPolygonal() {
        if (getDisplayPanel() == null) {
            System.out.println(getClass().getSimpleName());
        }
        return getDisplayPanel().getPolygonal();
    }

    protected void setCurrentPositionInternal(Point2D point) {
    }
    private Point2D currentPositionAux = new Point2D.Double();

    @Override
    public final void setCurrentPosition(Point2D point) {
        setCurrentPositionInternal(prepareForOrtho(point));
    }

    @Override
    public final Point2D prepareForOrtho(Point2D point) {
        double x = point.getX(), y = point.getY();
        if (CadCommandController.isOrthoActivated() && lastPoint.getX() != 0 && lastPoint.getY() != 0) {
            double dx = Math.abs(point.getX() - lastPoint.getX());
            double dy = Math.abs(point.getY() - lastPoint.getY());

            if (dx > dy) {
                y = lastPoint.getY();
            } else {
                x = lastPoint.getX();
            }
        }
        point.setLocation(x, y);
        return point;
    }
    private Point2D lastPoint = new Point2D.Double();

    @Override
    public final void setLastPoint(double x, double y) {
        lastPoint.setLocation(x, y);
    }

    public static GeodesicPoint findGeoPoint(Point2D point) {
        for (GeodesicPoint gpoint : DataManagement.getAllPoints()) {
            Point2D location = gpoint.getLocation();

            if (PolygonalUtils.horizontalDistance(location.getX(), location.getY(), point.getX(), point.getY()) < 0.1) {
                return gpoint;
            }
        }
        return null;
    }

    public final Point2D parsePoint(String text) throws CoordinateException {
        Point2D point = UTMCoordinate.parsePoint2D(text);
        setLastPoint(point.getX(), point.getY());
        return point;
    }

    public static Point2D derivatePoint(Point2D point, String text) throws CoordinateException {
        return UTMCoordinate.derivatePoint2D(point, text);
    }

    public static Double getDouble(String value, String templateError) {
        try {
            return Double.parseDouble(value);
        } catch (Throwable ex) {
            throw new NumberFormatException(String.format(templateError, value));
        }
    }

    public static AngleValue getAngleValue(String value, String templateError) {
        try {
            return NumberFloatUtils.parseDirection(value);
        } catch (Throwable ex) {
            throw new NumberFormatException(String.format(templateError, value));
        }
    }

    public static Double getPositiveDouble(String value, String templateError) {
        Double aDouble = getDouble(value, templateError);
        if (aDouble <= 0) {
            throw new NumberFormatException(String.format(templateError, value));
        }
        return aDouble;
    }

    public static Integer getPositiveInteger(String value, String templateError) {
        Integer i = Integer.parseInt(value);
        if (i < 0) {
            throw new NumberFormatException(String.format(templateError, value));
        }
        return i;
    }

    public static Character getCharacter(String value, String templateError, char... possibilites) throws CommandException {
        for (char c : possibilites) {
            if (value.length() == 1 && Character.toUpperCase(value.charAt(0)) == Character.toUpperCase(c)) {
                return Character.toUpperCase(c);
            }
        }
        throw new CommandException(String.format(templateError, value));
    }

    public static Collection<VisualObject> findPath(GeodesicPoint from, GeodesicPoint to) {
        return Collections.EMPTY_LIST;
    }

    public static Layer chooseLayer() {
        Collection<Layer> allLayers = LayerController.getAllLayers();
        JList list = new JList(allLayers.toArray(new Layer[0]));
        list.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        String[] options = new String[]{"Confirmar", "Não atribuir"};

        int result = JOptionPane.showOptionDialog(null, scrollPane, "Escolha um layer para atribuir", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (result == JOptionPane.CANCEL_OPTION) {
            return null;
        }

        return (Layer) list.getSelectedValue();
    }

    public static Polygonal chooseOpenedPolygonal() {
        MainPolygonal mainPolygonal = DataManagement.getMainPolygonal();

        if (mainPolygonal.getChildrenSize() == 0 && !mainPolygonal.isClosed()) {
            return mainPolygonal;
        } else {
            Collection collection = new TreeSet();
            if (!mainPolygonal.isClosed()) {
                collection.add(mainPolygonal);
            }

            for (Polygonal p : mainPolygonal.values()) {
                if (!p.isClosed()) {
                    collection.add(p);
                }
            }

            JList list = new JList(collection.toArray(new Polygonal[0]));
            list.setFont(list.getFont().deriveFont(Font.BOLD, 22f));
            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.setPreferredSize(new Dimension(700, 450));
            JOptionPane.showMessageDialog(Main.getInstance(),
                    collection.isEmpty() ? "Nenhuma parcela sem definição de perímetro." : scrollPane,
                    "Escolha a parcela a definir",
                    JOptionPane.PLAIN_MESSAGE);

            Object selectedValue = list.getSelectedValue();

            return (Polygonal) selectedValue;
        }
    }

    public final GeodesicPoint searchPoint(String name) throws PolygonalException {
        return searchPoint(name, getPolygonal());
    }

    public final GeodesicPoint searchPoint(String name, Polygonal polygonal) throws PolygonalException {
        if (polygonal.isMain()) {
            return DataManagement.findPointByAprox(name.toUpperCase());
        } else {
            Collection<GeodesicPoint> collect = ArraysUtil.collect(polygonal.getVisualObjects(), GeodesicPoint.class);
            return DataManagement.findPointByAprox(collect, name);
        }
    }

    @Override
    public CadCommand newInstance(GLTopographicPanel displayPanel) throws CommandException {
        throw new UnsupportedOperationException("must be overrided.");
    }

    public final void checkIfPolygonalIsMain() throws CommandException {
        if (!getPolygonal().isMain()) {
            JOptionPane.showMessageDialog(Main.getInstance(), "Esta ação só pode ser executada a partir do mapa da Planta Geral.", "Aviso", JOptionPane.ERROR_MESSAGE);
            throw new CommandException();
        }
    }

    @Override
    public boolean canSelect() {
        return false;
    }

    @Override
    public boolean singleSelect() {
        return false;
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public boolean acceptSpaceBar() {
        return false;
    }

    @Override
    public boolean acceptAnyChar() {
        return true;
    }

    @Override
    public void canceled() {
    }

    protected final void redraw() {
        RenderContext.getInstance().requestToRedraw();
        RenderContext.getInstance(true).requestToRedraw();

        for (VisualObject vo : getDisplayPanel().getVisualObjects()) {
            vo.refresh();
        }
        getDisplayPanel().requestRepaint();
    }

    @Override
    public boolean hasToBeExecuted() {
        return false;
    }

    @Override
    public boolean canUseMagnetic() {
        return !canSelect();
    }

    protected JDialog getPointDialog() {
        if (pointFormDialog == null) {
            pointFormDialog = new JDialog(Main.getInstance(), true);
            pointFormDialog.add(pointFormPanel);
        }
        int dialogWidth = 800, dialogHeight = 450;
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        pointFormDialog.setBounds((screenSize.width - dialogWidth) / 2, (screenSize.height - dialogHeight) / 2, dialogWidth, dialogHeight);
        return pointFormDialog;
    }
    protected static final PointManagerFormPanel pointFormPanel = new PointManagerFormPanel();
    protected static JDialog pointFormDialog;
}
