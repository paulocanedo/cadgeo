/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.model;

import br.com.geomapa.importer.rinex.RinexFile;
import br.com.geomapa.importer.rinex.RinexFileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author paulocanedo
 */
public class RinexListModel extends AbstractListModel {

    private List<RinexFile> list = new ArrayList<RinexFile>();
    private File folder;

    public RinexListModel(File folder) {
        setFolder(folder);
    }

    public final File getFolder() {
        return folder;
    }

    public final void setFolder(File folder) {
        int oldSize = list.size();
        clear();
        
        if (folder == null) {
            return;
        }

        File[] listFiles = folder.listFiles(RinexFileFilter.FILE_FILTER);
        if (listFiles.length == 0) {
            clear();
            return;
        }
        for (File f : listFiles) {
            list.add(new RinexFile(f));
        }
        Collections.sort(list);

        fireIntervalRemoved(this, 0, oldSize);
        fireIntervalAdded(this, 0, list.size());

        this.folder = folder;
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public Object getElementAt(int index) {
        return list.get(index);
    }

    private void clear() {
        int oldSize = list.size();

        list.clear();
        folder = null;
        fireIntervalRemoved(this, 0, oldSize);
    }
}
