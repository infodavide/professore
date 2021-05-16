package org.infodavid.util.swing.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.FocusManager;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

/**
 * The Class ClearAction.
 */
public class ClearAction extends TextAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5738587776777636756L;

    /**
     * Instantiates a new clear action.
     * @param name the name
     */
    public ClearAction(final String name) {
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
            ((JTextComponent)component).setText("");
        }
        else if (component instanceof JList) {
            final JList list = (JList)component;

            if (list.getModel() instanceof DefaultListModel) {
                ((DefaultListModel)list.getModel()).clear();
            }
        }
        else if (component instanceof JTable) {
            final JTable table = (JTable)component;

            if (table.getModel() instanceof DefaultTableModel) {
                final DefaultTableModel model = (DefaultTableModel)table.getModel();

                for (int i = 0; i < model.getRowCount(); i++) {
                    model.removeRow(i);
                    table.revalidate();
                }
            }
        }
    }

}
