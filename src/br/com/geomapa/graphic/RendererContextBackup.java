/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.graphic;

import br.com.geomapa.graphic.cad.compound.VisualObjectCompound;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.primitives.Point;
import br.com.geomapa.graphic.cad.primitives.Rectangle;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.graphic.cad.spec.VisualObject;
import br.com.geomapa.graphic.cad.text.FontLoader;
import br.com.geomapa.main.Bus;
import br.com.geomapa.util.PGLUtil;
import java.awt.Color;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL2;

/**
 *
 * @author paulocanedo
 */
public class RendererContextBackup {

    private static Map<Character, FloatBuffer> charVertex;
    private Set<VisualObject> delegate = new HashSet<VisualObject>();
    private List<Float> pointsClientBuffered = new ArrayList<Float>();
    private int lines = 0;
    private int nVBO = 1;
    private int[] VBO = new int[nVBO];
    private FloatBuffer vertexBuffer;
    private final static FloatBuffer circleBuffer;

    static {
        try {
            charVertex = FontLoader.reloadCharVertex();
        } catch (IOException ex) {
            Logger.getLogger(RendererContextBackup.class.getName()).log(Level.SEVERE, null, ex);
        }

        int nLines = 12;
        circleBuffer = FloatBuffer.allocate(12 * 4);
        Float lastX = null, lastY = null;

        double amount = Math.PI * 2 / nLines;
        double angle = 0;
        for (int i = 0; i < nLines; i++) {
            float x = (float) ((Math.cos(angle)));
            float y = (float) ((Math.sin(angle)));

            if (lastX != null && lastY != null) {
                circleBuffer.put(x);
                circleBuffer.put(y);
                circleBuffer.put(lastX);
                circleBuffer.put(lastY);
            }

            angle += amount;
            lastX = x;
            lastY = y;
        }
    }
    private final Color color;
    private static final HashMap<Color, RendererContextBackup> mapInstance = new HashMap<Color, RendererContextBackup>();

    private RendererContextBackup(Color color) {
        this.color = color;
    }

    public static RendererContextBackup getInstance(Color color) {
        RendererContextBackup get = mapInstance.get(color);
        if (get == null) {
            mapInstance.put(color, get = new RendererContextBackup(color));
        }
        return get;
    }

    public static void add(VisualObject vo) {
        getInstance(vo.getColor()).delegate.add(vo);
    }

    public static void addAll(Collection<VisualObject> vos) {
        for (VisualObject vo : vos) {
            add(vo);
        }
    }
    
    public static void add(VisualObject vo, Color color) {
        getInstance(color).delegate.add(vo);
    }
    
    public static void addAll(Collection<VisualObject> vos, Color color) {
        for(VisualObject vo : vos) {
            getInstance(color).delegate.add(vo);
        }
    }

    private void addToCollection(Line line) {
        lines++;

        pointsClientBuffered.add((float) line.getX());
        pointsClientBuffered.add((float) line.getY());
        pointsClientBuffered.add((float) line.getEndX());
        pointsClientBuffered.add((float) line.getEndY());
    }

    private void addToCollection(Rectangle line) {
        lines += 4;

        pointsClientBuffered.add((float) line.getX());
        pointsClientBuffered.add((float) line.getY());
        pointsClientBuffered.add((float) line.getX());
        pointsClientBuffered.add((float) line.getEndY());

        pointsClientBuffered.add((float) line.getX());
        pointsClientBuffered.add((float) line.getEndY());
        pointsClientBuffered.add((float) line.getEndX());
        pointsClientBuffered.add((float) line.getEndY());

        pointsClientBuffered.add((float) line.getEndX());
        pointsClientBuffered.add((float) line.getEndY());
        pointsClientBuffered.add((float) line.getEndX());
        pointsClientBuffered.add((float) line.getY());

        pointsClientBuffered.add((float) line.getEndX());
        pointsClientBuffered.add((float) line.getY());
        pointsClientBuffered.add((float) line.getX());
        pointsClientBuffered.add((float) line.getY());
    }

    private void addToCollection(Point point) {
        lines += circleBuffer.capacity() / 4;
        
        float px = (float) point.getX();
        float py = (float) point.getY();

        float scale = Bus.getScale();

        circleBuffer.rewind();
        for (int i = 0; i < circleBuffer.capacity(); i += 4) {
            float x1 = circleBuffer.get() * scale * 0.5f;
            float y1 = circleBuffer.get() * scale * 0.5f;
            float x2 = circleBuffer.get() * scale * 0.5f;
            float y2 = circleBuffer.get() * scale * 0.5f;

            pointsClientBuffered.add(x1 + px);
            pointsClientBuffered.add(y1 + py);
            pointsClientBuffered.add(x2 + px);
            pointsClientBuffered.add(y2 + py);
        }
        
        lines += 2;
        addToCollection(px, py - scale, px, py + scale);
        addToCollection(px - scale, py, px + scale, py);
    }

    private void addToCollection(float x1, float y1, float x2, float y2) {
        pointsClientBuffered.add(x1);
        pointsClientBuffered.add(y1);
        pointsClientBuffered.add(x2);
        pointsClientBuffered.add(y2);
    }

    private void addToCollection(VisualText vtext) {
        String text = vtext.getText();
        double rot = Math.toRadians(vtext.getRotation());

        for (char c : text.toCharArray()) {
            if (c == ' ') {
                continue;
            }
            FloatBuffer get = charVertex.get(c);
            if (get == null) {
                lines += 0;
            } else {
                lines += charVertex.get(c).capacity();
            }
        }

        float space = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ') {
//                space += 10;
                continue;
            }
            FloatBuffer buffer = charVertex.get(c);
            if (buffer == null) {
                space += 10;
                continue;
            }
            buffer.rewind();

            Float x1 = null, y1 = null, x2 = null, y2 = null;
            for (int j = 0; j < buffer.capacity(); j++) {
                boolean isX = j % 2 == 0;
                float value = buffer.get() + (isX ? (i * 10 + space) : 0);
//                    x = x * cos(0) + y * sin(0);
//                    y = x * -sin(0) + y * cos(0);

                if (isX) {
                    if (x1 == null) {
                        x1 = value;
                    } else {
                        x2 = value;
                    }
//                    points.add((float) vtext.getX() + value);
                } else {
                    if (y1 == null) {
                        y1 = value;
                    } else {
                        y2 = value;
                    }
//                    points.add((float) vtext.getY() + value);
                }

                if (x1 != null && y1 != null && x2 != null && y2 != null) {
                    x1 = x1 * vtext.getScaledHeight() / 10;
                    y1 = y1 * vtext.getScaledHeight() / 10;
                    x2 = x2 * vtext.getScaledHeight() / 10;
                    y2 = y2 * vtext.getScaledHeight() / 10;

                    float rx1 = (float) (x1 * Math.cos(rot) - y1 * Math.sin(rot));
                    float ry1 = (float) (x1 * Math.sin(rot) + y1 * Math.cos(rot));
                    float rx2 = (float) (x2 * Math.cos(rot) - y2 * Math.sin(rot));
                    float ry2 = (float) (x2 * Math.sin(rot) + y2 * Math.cos(rot));

                    x1 = rx1 + (float) vtext.getX();
                    y1 = ry1 + (float) vtext.getY();
                    x2 = rx2 + (float) vtext.getX();
                    y2 = ry2 + (float) vtext.getY();

                    pointsClientBuffered.add(x1);
                    pointsClientBuffered.add(y1);
                    pointsClientBuffered.add(x2);
                    pointsClientBuffered.add(y2);

                    x1 = y1 = x2 = y2 = null;
                }
            }
        }
    }

    private void addToCollection(VisualObjectCompound voc) {
        for (VisualObject vo : voc) {
            addVOToCollection(vo);
        }
    }

    private void addVOToCollection(VisualObject vo) {
        if (vo instanceof Line) {
            addToCollection((Line) vo);
        } else if (vo instanceof Rectangle) {
            addToCollection((Rectangle) vo);
        } else if (vo instanceof VisualText) {
            addToCollection((VisualText) vo);
        } else if (vo instanceof VisualObjectCompound) {
            addToCollection((VisualObjectCompound) vo);
        } else if (vo instanceof Point) {
            addToCollection((Point) vo);
        }
    }

    public static void createBufferDisplayList(GL2 gl) {
        for (RendererContextBackup render : mapInstance.values()) {
            for (VisualObject vo : render.delegate) {
                render.addVOToCollection(vo);
            }

            render.displayList = gl.glGenLists(1);
            gl.glNewList(render.displayList, GL2.GL_COMPILE_AND_EXECUTE);
            gl.glBegin(GL2.GL_LINES);
            for (int i = 0; i < render.pointsClientBuffered.size(); i += 4) {
                gl.glVertex2f(render.pointsClientBuffered.get(i), render.pointsClientBuffered.get(i + 1));
                gl.glVertex2f(render.pointsClientBuffered.get(i + 2), render.pointsClientBuffered.get(i + 3));
            }
            gl.glEnd();
            gl.glEndList();
            
            render.pointsClientBuffered.clear();
        }
    }

    public static void createBufferSimple(GL2 gl) {
        for (RendererContextBackup render : mapInstance.values()) {
            PGLUtil.setGlColor(gl, render.color);
            for (VisualObject vo : render.delegate) {
                render.addVOToCollection(vo);
            }
        }
    }

    public static void createBufferVBO(GL2 gl) {
        for (RendererContextBackup render : mapInstance.values()) {
            for (VisualObject vo : render.delegate) {
                render.addVOToCollection(vo);
            }

            FloatBuffer buffer = FloatBuffer.allocate(render.lines * 4);
            for (Float f : render.pointsClientBuffered) {
                buffer.put(f);
            }
            render.vertexBuffer = buffer;
            gl.glGenBuffers(render.nVBO, render.VBO, 0);
        }
    }

    public static void drawWithVBO(GL2 gl) {
        for (RendererContextBackup render : mapInstance.values()) {
            PGLUtil.setGlColor(gl, render.color);
            int lineNumbers = render.vertexBuffer.capacity() / 4;


            gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, render.VBO[0]);

            render.vertexBuffer.rewind();

            gl.glBufferData(GL2.GL_ARRAY_BUFFER,
                    render.vertexBuffer.capacity() * 4, render.vertexBuffer,
                    GL2.GL_DYNAMIC_DRAW);
            gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, render.VBO[0]);
            gl.glVertexPointer(2, GL2.GL_FLOAT, 0, 0);
            gl.glDrawArrays(GL2.GL_LINES, 0, lineNumbers * 2);
        }
    }

    public static void drawWithDisplayList(GL2 gl) {
        for (RendererContextBackup render : mapInstance.values()) {
            PGLUtil.setGlColor(gl, render.color);

            gl.glCallList(render.displayList);
        }
    }

    public static void drawSimple(GL2 gl) {
        for (RendererContextBackup render : mapInstance.values()) {
            gl.glBegin(GL2.GL_LINES);
            for (int i = 0; i < render.pointsClientBuffered.size(); i += 4) {
                gl.glVertex2f(render.pointsClientBuffered.get(i), render.pointsClientBuffered.get(i + 1));
                gl.glVertex2f(render.pointsClientBuffered.get(i + 2), render.pointsClientBuffered.get(i + 3));
            }
            gl.glEnd();
        }
    }
    private int displayList = -1;

    public static void reset(GL2 gl) {
        for (RendererContextBackup render : mapInstance.values()) {
            render.lines = 0;
            render.delegate.clear();
            render.pointsClientBuffered.clear();

            gl.glDeleteLists(render.displayList, 1);
            render.displayList = -1;

            gl.glDeleteBuffers(render.nVBO, render.VBO, 0);
        }
    }
}
