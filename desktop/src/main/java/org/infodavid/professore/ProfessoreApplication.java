package org.infodavid.professore;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;

import org.infodavid.professore.desktop.ProfessoreFrame;
import org.infodavid.util.swing.Constants;
import org.infodavid.util.swing.SwingApplicationContext;
import org.infodavid.util.swing.SwingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.formdev.flatlaf.FlatLightLaf;

/**
 * The Class ProfessoreApplication.
 */
public class ProfessoreApplication {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessoreApplication.class);

    /**
     * The main method.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        FlatLightLaf.install();
        EventQueue.invokeLater(() -> {
            try {
                final ProfessoreApplication app = new ProfessoreApplication();

                app.getFrame().setVisible(true);
            }
            catch (final Exception e) {
                LOGGER.error("Cannot start application", e);
                e.printStackTrace(); // NOSONAR Launch of application
            }
        });
    }

    /** The frame. */
    private final JFrame frame;

    /**
     * Instantiates a new application.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public ProfessoreApplication() throws IOException {
        super();

        frame = initialize();
    }

    /**
     * Initialize.
     * @return the frame
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private JFrame initialize() throws IOException {
        final SwingUtils utils = SwingUtils.getInstance();
        final SwingApplicationContext context = utils.createApplicationContext(ProfessoreApplication.class, Constants.DEFAULT_RESOURCE_PATH + "professore_resources.properties");
        final Properties properties = utils.loadProperties(ProfessoreFrame.class);
        final JFrame result = new ProfessoreFrame(context, properties);

        result.setTitle("Professore per pianoforte");

        return result;
    }

    /**
     * Gets the frame.
     * @return the frame
     */
    protected JFrame getFrame() {
        return frame;
    }
}
