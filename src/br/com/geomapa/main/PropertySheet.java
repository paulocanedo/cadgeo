/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.main;

import br.com.geomapa.graphic.cad.primitives.Line;
import br.com.paulocanedo.pc9.util.LafUtils;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
A component filled with editors for all editable properties 
of an object.
 */
public class PropertySheet extends JPanel {
    
    private Insets insets = new Insets(1, 8, 1, 6);

    /**
    Constructs a property sheet that shows the editable
    properties of a given object.
     */
    public PropertySheet() {
    }
    
    public void setObject(Object bean) {
        removeAll();
        
        try {
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            setLayout(new GridBagLayout());
            GridBagConstraints cons;
            for (int i = 0; i < descriptors.length; i++) {
                PropertyEditor editor = getEditor(bean, descriptors[i]);
                if (editor != null) {
                    cons = new GridBagConstraints();
                    cons.gridx = 0;
                    cons.gridy = i+1;
                    cons.anchor = GridBagConstraints.WEST;
                    cons.insets = insets;
                    add(new JLabel(descriptors[i].getDisplayName()), cons);

                    cons = new GridBagConstraints();
                    cons.gridx = 1;
                    cons.gridy = i+1;
                    cons.anchor = GridBagConstraints.WEST;
                    cons.fill = GridBagConstraints.HORIZONTAL;
                    add(getEditorComponent(editor), cons);
                }
            }
        } catch (IntrospectionException exception) {
            exception.printStackTrace();
        }
    }

    /**
    Gets the property editor for a given property,
    and wires it so that it updates the given object.
    @param bean the object whose properties are being edited
    @param descriptor the descriptor of the property to
    be edited
    @return a property editor that edits the property
    with the given descriptor and updates the given object
     */
    public PropertyEditor getEditor(final Object bean,
            PropertyDescriptor descriptor) {
        try {
            Method getter = descriptor.getReadMethod();
            if (getter == null) {
                return null;
            }
            final Method setter = descriptor.getWriteMethod();
//            if (setter == null) {
//                return null;
//            }
            final PropertyEditor editor;
            Class editorClass = descriptor.getPropertyEditorClass();
            if (editorClass != null) {
                editor = (PropertyEditor) editorClass.newInstance();
            } else {
                editor = PropertyEditorManager.findEditor(
                        descriptor.getPropertyType());
            }
            if (editor == null) {
                return null;
            }

            Object value = getter.invoke(bean, new Object[]{});
            editor.setValue(value);
            editor.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent event) {
                    try {
                        if(setter != null)
                        setter.invoke(bean,
                                new Object[]{editor.getValue()});
                    } catch (IllegalAccessException exception) {
                    } catch (InvocationTargetException exception) {
                    }
                }
            });
            return editor;
        } catch (InstantiationException exception) {
            return null;
        } catch (IllegalAccessException exception) {
            return null;
        } catch (InvocationTargetException exception) {
            return null;
        }
    }

    /**
    Wraps a property editor into a component.
    @param editor the editor to wrap
    @return a button (if there is a custom editor), 
    combo box (if the editor has tags), or text field (otherwise)
     */
    public Component getEditorComponent(final PropertyEditor editor) {
        String[] tags = editor.getTags();
        String text = editor.getAsText();
        if (editor.supportsCustomEditor()) {
            // Make a button that pops up the custom editor
            final JButton button = new JButton();
            button.setContentAreaFilled(false);
            button.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            // if the editor is paintable, have it paint an icon
            if (editor.isPaintable()) {
                button.setIcon(new Icon() {

                    public int getIconWidth() {
                        return WIDTH;
                    }

                    public int getIconHeight() {
                        return HEIGHT;
                    }

                    public void paintIcon(Component c, Graphics g,
                            int x, int y) {
                        g.translate(x, y);
                        Rectangle r = new Rectangle(0, 0,
                                getIconWidth(), getIconHeight());
                        Color oldColor = g.getColor();
                        g.setColor(Color.BLACK);
                        editor.paintValue(g, r);
                        g.setColor(oldColor);
                        g.translate(-x, -y);
                    }
                });
            } else {
                button.setText(buttonText(text));
            }
            // pop up custom editor when button is clicked
            button.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    JOptionPane.showMessageDialog(Main.getInstance(),
                            editor.getCustomEditor());
                    if (editor.isPaintable()) {
                        button.repaint();
                    } else {
                        button.setText(buttonText(editor.getAsText()));
                    }
                }
            });
            return button;
        } else if (tags != null) {
            // make a combo box that shows all tags
            final JComboBox comboBox = new JComboBox(tags);
            comboBox.setSelectedItem(text);
            comboBox.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent event) {
                    if (event.getStateChange() == ItemEvent.SELECTED) {
                        editor.setAsText(
                                (String) comboBox.getSelectedItem());
                    }
                }
            });
            return comboBox;
        } else {
            final JTextField textField = new JTextField(text, 10);
            textField.getDocument().addDocumentListener(new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    try {
                        editor.setAsText(textField.getText());
                    } catch (IllegalArgumentException exception) {
                    }
                }

                public void removeUpdate(DocumentEvent e) {
                    try {
                        editor.setAsText(textField.getText());
                    } catch (IllegalArgumentException exception) {
                    }
                }

                public void changedUpdate(DocumentEvent e) {
                }
            });
            return textField;
        }
    }

    /**
    Formats text for the button that pops up a
    custom editor.
    @param text the property value as text
    @return the text to put on the button
     */
    private static String buttonText(String text) {
        if (text == null || text.equals("")) {
            return " ";
        }
        if (text.length() > MAX_TEXT_LENGTH) {
            return text.substring(0, MAX_TEXT_LENGTH) + "...";
        }
        return text;
    }
    private ArrayList changeListeners = new ArrayList();
    private static final int WIDTH = 100;
    private static final int HEIGHT = 25;
    private static final int MAX_TEXT_LENGTH = 15;

}
