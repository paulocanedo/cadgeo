/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.main;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

/**
 *
 * @author paulocanedo
 */
public final class GLConfigure {

    public final static GLProfile profile;
    public final static GLCapabilities caps;

    static {
        profile = GLProfile.getDefault();
        GLProfile.initSingleton(true);
        caps = new GLCapabilities(profile);
    }
}
