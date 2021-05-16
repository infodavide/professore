package org.infodavid.professore.desktop;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.infodavid.util.swing.SwingApplicationContext;
import org.infodavid.util.swing.SwingUtils;

/**
 * The Class RecorderPanel.
 */
public class RecorderPanel extends JPanel {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6901646253537795216L;

    /** The application context. */
    private final SwingApplicationContext applicationContext;

    /**
     * Instantiates a new recorder panel.
     * @param applicationContext the application context
     * @param properties the properties
     * @param name the name
     */
    public RecorderPanel(final SwingApplicationContext applicationContext, final Properties properties, final String name) {
        super();

        this.applicationContext = applicationContext;

        setName(name);
        initialize(properties);
    }

    /**
     * Initialize.
     * @param properties the properties
     * @return the map
     */
    protected void initialize(final Properties properties) {
        final SwingUtils utils = SwingUtils.getInstance();
        final JPanel fieldsPanel = new JPanel(new GridBagLayout());

        fieldsPanel.setName(getName());
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        final GridBagConstraints constraints = utils.createGridBagConstraints();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(fieldsPanel);
        setPreferredSize(new Dimension(600, 320));
    }
}
