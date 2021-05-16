package org.infodavid.util.swing.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.FocusManager;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

/**
 * The Class SelectAllAction.
 */
public class SelectAllAction extends TextAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 38030308887523532L;

    /**
     * Instantiates a new select all action.
     * @param name the name
     */
    public SelectAllAction(final String name) {
        super(name);
    }

    /*
     * (non-javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void actionPerformed(final ActionEvent e) {
        final Component component = FocusManager.getCurrentManager().getFocusOwner();

        if (component instanceof JTextComponent) {
            ((JTextComponent)component).selectAll();
        }
        else if (component instanceof JList) {
            final JList list = (JList)component;
            final int end = list.getModel().getSize() - 1;

            if (end >= 0) {
                list.setSelectionInterval(0, end);
            }
        }
        else if (component instanceof JTable) {
            final JTable table = (JTable)component;
            final int end = table.getModel().getRowCount() - 1;

            if (end >= 0) {
                table.setRowSelectionInterval(0, end);
            }
        }
    }
}
