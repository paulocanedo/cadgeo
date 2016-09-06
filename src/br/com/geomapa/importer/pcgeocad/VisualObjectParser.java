/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.importer.pcgeocad;

import br.com.geomapa.graphic.cad.spec.VisualObject;
import java.io.InputStream;
import java.util.Collection;

/**
 *
 * @author paulocanedo
 */
public final class VisualObjectParser {

    public static final float LAST_VERSION = 1f;
    private VisualObjectImporter importer;
    private static final VisualObjectImporter betaVersionInstance = new VisualObjectImporterBeta();
    private static final VisualObjectImporter oneDotZeroInstance = new VisualObjectImporterOneDotZero();

    public static VisualObjectParser getInstance() {
        return getInstance(LAST_VERSION);
    }

    public static VisualObjectParser getInstance(float version) {
        VisualObjectParser visualObjectParser = new VisualObjectParser();
        if (version == 1.0f) {
            visualObjectParser.importer = oneDotZeroInstance;
        } else {
            visualObjectParser.importer = betaVersionInstance;
        }
        return visualObjectParser;
    }

    public Collection<VisualObject> parse(InputStream stream) throws VisualObjectParserException {
        return importer.parse(stream, 1, 1);
    }

    public Collection<VisualObject> parse(InputStream stream, double drawingScale, double objectsScale) throws VisualObjectParserException {
        return importer.parse(stream, drawingScale, objectsScale);
    }
}
