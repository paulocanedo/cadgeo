/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.ui.model;

import br.com.geomapa.project.ProjectMetadata;
import javax.swing.AbstractListModel;

/**
 *
 * @author paulocanedo
 */
public class RbmcListModel extends AbstractListModel {

    private ProjectMetadata pMetadata;

    public RbmcListModel(ProjectMetadata projectMetadata) {
        this.pMetadata = projectMetadata;
    }

    @Override
    public int getSize() {
        return pMetadata.getRbmc().size();
    }

    @Override
    public Object getElementAt(int index) {
        return pMetadata.getRbmc().get(index);
    }   

    public void fireElementsAdded() {
        int size = getSize();
        
        fireIntervalAdded(this, size-1, size);
    }
    
    public void fireElementsRemoved(int index) {
        fireIntervalRemoved(this, index, index);
    }

    public void setProjectMetadata(ProjectMetadata projectMetadata) {
        fireIntervalRemoved(this, 0, projectMetadata.getRbmc().size());
        
        this.pMetadata = projectMetadata;
        fireIntervalAdded(this, 0, projectMetadata.getRbmc().size()); 
    }

}
