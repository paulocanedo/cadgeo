/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geografico.ztest.opengl;

import br.com.geomapa.graphic.RenderContext;
import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.geomapa.graphic.cad.primitives.VisualText;
import br.com.geomapa.graphic.cad.text.FontLoader;
import br.com.geomapa.main.Bus;
import br.com.geomapa.util.PGLUtil;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

/**
 *
 * @author paulocanedo
 */
public class HelloWorld {

    public static void main(String[] args) {
        // setup OpenGL Version 2
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        // The canvas is the widget that's drawn in the JFrame
        GLCanvas glcanvas = new GLCanvas(capabilities);
        glcanvas.addGLEventListener(new Renderer());
        glcanvas.setSize(1500, 900);

        FPSAnimator animator = new FPSAnimator(glcanvas, 0);
        animator.start();

        JFrame frame = new JFrame("Hello World");
        frame.getContentPane().add(glcanvas);

        // shutdown the program on windows close event
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });

        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);
    }
}

class Renderer implements GLEventListener {

    private static int frames = 0;
    private GLU glu = new GLU();
    private long initialtime;
    private Map<Character, FloatBuffer> charVertex;
    private RenderContext rendererContext;
    private FloatBuffer buffer;

    public Renderer() {
        try {
            this.charVertex = FontLoader.reloadCharVertex();
        } catch (IOException ex) {
            Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private boolean initialized = false;

    public void display(GLAutoDrawable gLDrawable) {
        final GL2 gl = gLDrawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        PGLUtil.setGlColor(gl, Color.WHITE);
        gl.glPushMatrix();
//        gl.glTranslated(60, 60, 0);
//        gl.glScaled(1, 1, 1);
//        gl.glScaled(0.5, 0.5, 1);

        String text = "Paulo Canedo Costa Rodrigues. Acentuação";
//        if (!initialized) {
//            Bus.getScale();
//            gl.glRotated(180, 0, 0, 1);
//            VisualText vtext = new VisualText(100, 100, text);
////            vtext.setRotation(220);
//            RenderContext.add(vtext);
//            RenderContext.createBufferDisplayList(gl);
//            initialized = true;
//        }
//        RenderContext.drawWithDisplayList(gl);
//        int displaylist = gl.glGenLists(1);
//        gl.glNewList(displaylist, GL2.GL_COMPILE_AND_EXECUTE);
//        drawText(gl, text, 0, 0, 10);
//        gl.glEndList();
//        for (int i = 0; i < 50; i++) {
//            gl.glCallList(displaylist);
//            drawText(gl, text, 0, 0, 10);
//            drawTextByVBO(gl, text);
////            gl.glTranslated(0, 15, 0);
//        }
//        drawLinesByVBO(gl);
//        drawLines(gl);
//        drawFromBuffer(gl, null);

//        rendererContext.add(new Line(100, 100, 300, 700));
//        rendererContext.add(new Line(150, 150, 500, 240));
//        rendererContext.add(new Line(100, 1200, 20, 100));
//        rendererContext.add(new Line(800, 100, 300, 800));

//        drawFromBuffer(gl, buffer);

        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslated(20, 20, 0);
        gl.glScaled(0.1, 0.1, 1);
        gl.glFlush();

        frames++;
        long timeSpent = (System.currentTimeMillis() - initialtime);
        double fps = frames / (double) timeSpent * 1000;
        String sfps = String.format("FPS: %.2f", fps);
        PGLUtil.setGlColor(gl, Color.WHITE);
        PGLUtil.fillRect(gl, -20, -20, 800, 160);

        PGLUtil.setGlColor(gl, Color.BLUE);
        new GLUT().glutStrokeString(GLUT.STROKE_ROMAN, sfps);
        gl.glPopMatrix();

        if (timeSpent > 1000) {
            initialtime = System.currentTimeMillis();
            frames = 0;
        }

    }

    private void drawTextByVBO(GL2 gl, String text) {
        int size = 0;
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                continue;
            }
            size += charVertex.get(c).capacity();
        }

        FloatBuffer bufferText = FloatBuffer.allocate(size);
        float space = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                space += 10;
                continue;
            }
            FloatBuffer buffer = charVertex.get(c);
            buffer.rewind();
            for (int j = 0; j < buffer.capacity(); j++) {
                bufferText.put(buffer.get() + (j % 2 == 0 ? (i * 10 + space) : 0));
            }
        }
        int lineNumbers = bufferText.capacity();
        int nbVBO = 1;

        // Array to hold Vertex Buffers Objects (VBOs).
        int[] VBO = new int[nbVBO];
        gl.glGenBuffers(nbVBO, VBO, 0);

        // Enable same as for vertex buffers.
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

        // Init VBOs and transfer data.
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBO[0]);
        // Copy data to the server into the VBO.

        bufferText.rewind();

        gl.glBufferData(GL2.GL_ARRAY_BUFFER,
                bufferText.capacity() * 4, bufferText,
                GL.GL_STATIC_DRAW);
        // Draw.
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO[0]);
        gl.glVertexPointer(2, GL.GL_FLOAT, 0, 0);
        gl.glDrawArrays(GL.GL_LINES, 0, lineNumbers * 2);

        gl.glDeleteBuffers(nbVBO, VBO, 0);
    }

    private void drawLinesByVBO(GL2 gl) {
        int lineNumbers = 100000;
        // Buffer for the vertex data
        FloatBuffer points = FloatBuffer.allocate(lineNumbers * 4);
        int nbVBO = 1;

        // Array to hold Vertex Buffers Objects (VBOs).
        int[] VBO = new int[nbVBO];
        gl.glGenBuffers(nbVBO, VBO, 0);

        // Enable same as for vertex buffers.
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

        // Init VBOs and transfer data.
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBO[0]);
        // Copy data to the server into the VBO.


        //desenha linha
        Random random = new Random();
        for (int i = 0; i < lineNumbers; i++) {
            int x = random.nextInt(1500);
            int y = random.nextInt(900);

            int x2 = random.nextInt(40);
            int y2 = random.nextInt(30);

//            System.out.println(String.format("%d %d %d %d", x, y, x2, y2));
            points.put(x).put(y).put(x + x2).put(y + y2);
        }
        points.rewind();

        gl.glBufferData(GL2.GL_ARRAY_BUFFER,
                points.capacity() * 4, points,
                GL.GL_STATIC_DRAW);
        // Draw.
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO[0]);
        gl.glVertexPointer(2, GL.GL_FLOAT, 0, 0);
        gl.glDrawArrays(GL.GL_LINES, 0, lineNumbers * 2);

        gl.glDeleteBuffers(nbVBO, VBO, 0);
    }

    private void drawFromBuffer(GL2 gl, FloatBuffer points) {
        int lineNumbers = points.capacity() / 4;
        // Buffer for the vertex data
        int nbVBO = 1;

        // Array to hold Vertex Buffers Objects (VBOs).
        int[] VBO = new int[nbVBO];
        gl.glGenBuffers(nbVBO, VBO, 0);

        // Enable same as for vertex buffers.
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

        // Init VBOs and transfer data.
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBO[0]);
        // Copy data to the server into the VBO.

        points.rewind();

        gl.glBufferData(GL2.GL_ARRAY_BUFFER,
                points.capacity() * 4, points,
                GL.GL_STATIC_DRAW);
        // Draw.
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO[0]);
        gl.glVertexPointer(2, GL.GL_FLOAT, 0, 0);
        gl.glDrawArrays(GL.GL_LINES, 0, lineNumbers * 2);

        gl.glDeleteBuffers(nbVBO, VBO, 0);
    }

    private void drawLines(GL2 gl) {
        Random random = new Random();
//        gl.glBegin(GL.GL_LINE_STRIP);
        for (int i = 0; i < 100000; i++) {
            int x = random.nextInt(1500);
            int y = random.nextInt(900);

            int x2 = random.nextInt(40);
            int y2 = random.nextInt(30);

            gl.glBegin(GL.GL_LINES);
            gl.glVertex2d(x, y);
            gl.glVertex2d(x + x2, y + y2);
            gl.glEnd();

//            gl.glVertex2d(x, y);
//            gl.glVertex2d(x2, y2);
        }
//        gl.glEnd();
    }

    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {
        System.out.println("displayChanged called");
    }

    public void init(GLAutoDrawable gLDrawable) {
        GL2 gl = gLDrawable.getGL().getGL2();

        gl.setSwapInterval(0);
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        gl.glShadeModel(GL2.GL_FLAT);				// Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);			// Black Background
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glEnable(GL.GL_DEPTH_TEST);				// Enables Depth Testing
    }

    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
        final GL2 gl = gLDrawable.getGL().getGL2();

        if (height <= 0) // avoid a divide by zero error!
        {
            height = 1;
        }

        final float h = (float) width / (float) height;

//        gl.glViewport(0, 0, width, height);
//        gl.glMatrixMode(GL2.GL_PROJECTION);
//        gl.glLoadIdentity();
//        glu.gluPerspective(45.0f, h, 1.0, 20.0);
//        gl.glMatrixMode(GL2.GL_MODELVIEW);
//        gl.glLoadIdentity();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();
        gl.glOrtho(0, width, 0, height, -1, 1);
    }

    public void dispose(GLAutoDrawable arg0) {
        System.out.println("dispose() called");
    }
}