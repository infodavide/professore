package org.infodavid.professore.desktop;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.infodavid.util.swing.SwingApplicationContext;
import org.infodavid.util.swing.SwingUtils;

/**
 * The Class PianofortePanel.
 */
public class PianofortePanel extends Box {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7684918388904038538L;

    /** The application context. */
    private final transient SwingApplicationContext applicationContext;

    /** The pianoforte. */
    private PianoforteComponent pianoforte;

    /** The pitch slider. */
    private JSlider pitchSlider;

    /** The velocity slider. */
    private JSlider velocitySlider;

    /** The volume slider. */
    private JSlider volumeSlider;

    /**
     * Instantiates a new professore panel.
     * @param applicationContext the application context
     * @param properties the properties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public PianofortePanel(final SwingApplicationContext applicationContext, final Properties properties, final String name) throws IOException {
        super(BoxLayout.X_AXIS);

        this.applicationContext = applicationContext;

        setName(name);
        initialize(properties);
    }

    /**
     * Gets the pianoforte.
     * @return the pianoforte
     */
    public PianoforteComponent getPianoforte() {
        return pianoforte;
    }

    /**
     * Gets the pitch slider.
     * @return the pitchSlider
     */
    public JSlider getPitchSlider() {
        return pitchSlider;
    }

    /**
     * Gets the velocity slider.
     * @return the velocitySlider
     */
    public JSlider getVelocitySlider() {
        return velocitySlider;
    }

    /**
     * Gets the volume slider.
     * @return the volumeSlider
     */
    public JSlider getVolumeSlider() {
        return volumeSlider;
    }

    /**
     * Initialize.
     * @param properties the properties
     * @return the map
     */
    protected void initialize(final Properties properties) {
        final SwingUtils utils = SwingUtils.getInstance();
        pianoforte = new PianoforteComponent();

        pianoforte.setName(getName() + ".keyboard");
        pianoforte.setMaximumSize(pianoforte.getPreferredSize());

        volumeSlider = utils.createSlider(properties, applicationContext, null, null, (byte)0, getName() + ".volumeSlider", SwingConstants.VERTICAL, 0, 100, 50);

        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setMinorTickSpacing(2);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        Hashtable<Integer,JLabel> labelsTable = new Hashtable<>(); // NOSONAR Dictionary

        labelsTable.put(Integer.valueOf(0), new JLabel("Min"));
        labelsTable.put(Integer.valueOf(100), new JLabel("Max"));
        volumeSlider.setLabelTable(labelsTable);

        velocitySlider= utils.createSlider(properties, applicationContext, null, null, (byte)0, getName() + ".velocitySlider", SwingConstants.VERTICAL, 0, 127, 100);

        velocitySlider.setMajorTickSpacing(10);
        velocitySlider.setMinorTickSpacing(2);
        velocitySlider.setPaintTicks(true);
        velocitySlider.setPaintLabels(true);

        labelsTable = new Hashtable<>(); // NOSONAR Dictionary

        labelsTable.put(Integer.valueOf(0), new JLabel("Min"));
        labelsTable.put(Integer.valueOf(127), new JLabel("Max"));
        velocitySlider.setLabelTable(labelsTable);

        pitchSlider = utils.createSlider(properties, applicationContext, null, null, (byte)0, getName() + ".pitchSlider", SwingConstants.VERTICAL, 0, 127, 60);

        pitchSlider.setMajorTickSpacing(10);
        pitchSlider.setMinorTickSpacing(2);
        pitchSlider.setPaintTicks(true);
        pitchSlider.setPaintLabels(true);

        labelsTable = new Hashtable<>(); // NOSONAR Dictionary

        labelsTable.put(Integer.valueOf(0), new JLabel("Min"));
        labelsTable.put(Integer.valueOf(127), new JLabel("Max"));
        pitchSlider.setLabelTable(labelsTable);

        final JScrollPane pianoforteScrollPane = new JScrollPane(pianoforte);

        pianoforteScrollPane.setMaximumSize(pianoforte.getMaximumSize());

        setBorder(utils.createTitledBorder(properties, applicationContext, "pianofortePanel", TitledBorder.CENTER));
        setMaximumSize(pianoforteScrollPane.getMaximumSize());
        setAlignmentX(Component.CENTER_ALIGNMENT);
        add(volumeSlider);
        add(velocitySlider);
        add(pitchSlider);
        add(pianoforteScrollPane);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                if (e.getComponent().getWidth() > pianoforte.getMaximumSize().width) {
                    pianoforteScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                }
                else if (e.getComponent().getWidth() < pianoforte.getMaximumSize().width) {
                    pianoforteScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                }

                if (e.getComponent().getHeight() > pianoforte.getMaximumSize().height) {
                    pianoforteScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                }
                else if (e.getComponent().getHeight() < pianoforte.getMaximumSize().height) {
                    pianoforteScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                }
            }
        });
    }
}
