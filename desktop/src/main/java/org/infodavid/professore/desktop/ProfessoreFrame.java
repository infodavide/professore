package org.infodavid.professore.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.professore.core.NoteEnum;
import org.infodavid.professore.core.VoicePlayer;
import org.infodavid.professore.core.midi.MidiPlayer;
import org.infodavid.util.concurrency.Executors;
import org.infodavid.util.logging.LoggingUtils;
import org.infodavid.util.swing.ComponentsMap;
import org.infodavid.util.swing.Constants;
import org.infodavid.util.swing.SwingAppender;
import org.infodavid.util.swing.SwingApplicationContext;
import org.infodavid.util.swing.SwingUtils;
import org.infodavid.util.swing.component.CustomCheckBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ProfessoreFrame.
 */
public class ProfessoreFrame extends JFrame implements Closeable { // NOSONAR Inheritance

    /** The Constant SWING_APPENDER. */
    public static final String SWING_APPENDER = "SWING";

    /** The Constant KEY_OFFSET. */
    private static final byte KEY_OFFSET = 24;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessoreFrame.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3877045971356741778L;

    /** The application context. */
    private final transient SwingApplicationContext applicationContext;

    /** The channel. */
    private transient MidiChannel channel;

    /** The executor. */
    private final transient ScheduledExecutorService executor;

    /** The file player panel. */
    private FilePlayerPanel filePlayerPanel;

    /** The log pane. */
    private final JTextPane logPane = new JTextPane();

    /** The main panel. */
    private JTabbedPane mainPanel;

    private final transient MidiPlayer midiPlayer;

    /** The pianoforte panel. */
    private PianofortePanel pianofortePanel;

    /** The professore panel. */
    private ProfessorePanel professorePanel;

    /** The recorder panel. */
    private RecorderPanel recorderPanel;

    /** The sound controller. */
    private final transient SoundControllerImpl soundController;

    /** The synthesizer. */
    private final transient Synthesizer synthesizer;

    /** The voice note player. */
    private final transient VoicePlayer voicePlayer;

    /**
     * Instantiates a new professore frame.
     * @param applicationContext the application context
     * @param properties the properties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public ProfessoreFrame(final SwingApplicationContext applicationContext, final Properties properties) throws IOException {
        super();

        this.applicationContext = applicationContext;
        executor = Executors.newScheduledExecutorService(getClass(), LOGGER, 20, Thread.MAX_PRIORITY);
        soundController = new SoundControllerImpl(this);
        midiPlayer = new MidiPlayer(soundController);

        try {
            synthesizer = MidiSystem.getSynthesizer();
        }
        catch (final MidiUnavailableException e) {
            throw new IOException(e);
        }

        voicePlayer = new VoicePlayer();

        voicePlayer.setStacked(false);
        applicationContext.setMainFrame(this);
        setName("professoreFrame");
        initialize(properties);
    }

    /*
     * (non-javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public synchronized void close() {
        LOGGER.info("Closing synthesizer...");

        synthesizer.close();

        channel = null;

        LOGGER.info("Closing voice note player...");

        voicePlayer.close();
    }

    /**
     * Open.
     * @param voice the voice
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public synchronized void open(final boolean voice) throws IOException {
        close();

        if (voice) {
            LOGGER.info("Opening voice note player...");

            voicePlayer.open();
        }
        else {
            LOGGER.info("Opening synthesizer...");

            try {
                synthesizer.open();

                final Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();

                for (int i = 0; i < instruments.length; i++) {
                    final Instrument instrument = instruments[i];

                    if (StringUtils.containsIgnoreCase(instrument.getName(), "grand piano")) {
                        LOGGER.info("Using instrument: {}", instrument.getName());

                        synthesizer.loadInstrument(instrument);

                        break;
                    }
                }

                final MidiChannel[] channels = synthesizer.getChannels();

                if (channels != null) {
                    for (final MidiChannel channel : channels) { // NOSONAR Name of item
                        if (channel != null && !channel.getMute()) {
                            this.channel = channel;

                            channel.programChange(0);

                            LOGGER.info("Using channel: {}", channel);

                            break;
                        }
                    }
                }
            }
            catch (final MidiUnavailableException e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Exit.
     */
    protected void exit() {
        close();
        Executors.shutdown(executor);
        System.exit(0);
    }

    /**
     * Gets the application context.
     * @return the applicationContext
     */
    protected SwingApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Gets the executor.
     * @return the executor
     */
    protected ScheduledExecutorService getExecutor() {
        return executor;
    }

    /**
     * Initialize.
     * @param properties the properties
     * @return the map
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected ComponentsMap initialize(final Properties properties) throws IOException { // NOSONAR
        final SwingUtils utils = SwingUtils.getInstance();
        final ComponentsMap componentsMap = new ComponentsMap();

        getContentPane().setLayout(new BorderLayout());
        utils.addDefaultWindowIcon(this, properties);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(final WindowEvent e) {
                super.windowClosed(e);
                exit();
            }
        });

        initializeMenuBar(properties, componentsMap);

        final JToolBar toolBar = initializeToolBar(properties, componentsMap);
        final JButton quitButton = componentsMap.get(toolBar.getName() + ".quit", JButton.class);

        quitButton.addActionListener(e -> exit());

        final CustomCheckBox playModeButton = componentsMap.get(toolBar.getName() + ".playMode", CustomCheckBox.class);

        playModeButton.addActionListener(e -> {
            final JToggleButton source = (JToggleButton)e.getSource();

            close();

            if (source.isSelected()) {
                try {
                    open(false);
                }
                catch (final IOException e1) {
                    LOGGER.error("Cannot open synthetiser", e1);
                    JOptionPane.showMessageDialog(applicationContext.getMainFrame(), "WARNING.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
            else {
                try {
                    open(true);
                }
                catch (final IOException e1) {
                    LOGGER.error("Cannot open voice player", e1);
                    JOptionPane.showMessageDialog(applicationContext.getMainFrame(), "WARNING.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        initializeTabbedPane(properties, componentsMap);

        logPane.setEditable(false);
        logPane.setComponentPopupMenu(utils.createPopupMenu(properties, applicationContext, "outputPanel.popup", componentsMap));
        logPane.setPreferredSize(new Dimension(600, 200));

        final JScrollPane logScrollPane = new JScrollPane(logPane);

        logScrollPane.setPreferredSize(new Dimension(600, 200));

        final Box outputPanel = Box.createVerticalBox();

        outputPanel.setBorder(utils.createTitledBorder(properties, applicationContext, "outputPanel", TitledBorder.CENTER));
        outputPanel.add(logScrollPane);

        try {
            final SwingAppender appender = (SwingAppender)LoggingUtils.getInstance().getAppender(SWING_APPENDER);

            appender.setup(logScrollPane, logPane);
        }
        catch (final IllegalAccessException e) {
            LOGGER.error("Cannot retrieve logging appender", e);
        }

        pianofortePanel = new PianofortePanel(applicationContext, properties, "pianofortePanel");

        pianofortePanel.getPianoforte().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                final PianoforteComponent pianoforte = pianofortePanel.getPianoforte();
                final byte key = pianoforte.getKeyAtPoint(e.getPoint());

                LOGGER.debug("{} pressed", String.valueOf(key));

                pianoforte.setKey(key, true);

                if (channel == null) {
                    try {
                        voicePlayer.play(NoteEnum.values()[key % 12]);
                    }
                    catch (final IOException e1) {
                        LOGGER.warn("Cannot play note", e1);
                    }
                }
                else {
                    channel.noteOn(key + KEY_OFFSET, pianofortePanel.getVelocitySlider().getValue());
                }
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                final PianoforteComponent pianoforte = pianofortePanel.getPianoforte();
                final byte key = pianoforte.getKeyAtPoint(e.getPoint());

                LOGGER.debug("{} released", String.valueOf(key));

                pianoforte.setKey(key, false);

                if (channel != null) {
                    channel.noteOff(key + KEY_OFFSET);
                }
            }
        });

        pianofortePanel.getVolumeSlider().addChangeListener(e -> {
            final JSlider source = (JSlider)e.getSource();

            LOGGER.info("Volume changed to: {}", String.valueOf(source.getValue()));

            if (channel == null) {
                voicePlayer.setVolume((byte)(source.getValue() / 100.0 * source.getMaximum()));
            }
            else {
                channel.controlChange(org.infodavid.professore.core.Constants.VOLUME_CONTROL_CHANGE, (int)(source.getValue() / 100.0 * source.getMaximum()));
                channel.setMute(source.getValue() == 0);
            }
        });

        voicePlayer.setVolume((byte)(pianofortePanel.getVolumeSlider().getValue() / 100.0 * pianofortePanel.getVolumeSlider().getMaximum()));

        pianofortePanel.getPitchSlider().addChangeListener(e -> {
            final JSlider source = (JSlider)e.getSource();

            LOGGER.info("Pitch changed to: {}", String.valueOf(source.getValue()));

            if (channel != null) {
                channel.controlChange(org.infodavid.professore.core.Constants.PITCH_CONTROL_CHANGE, source.getValue());
            }
        });

        try {
            open(true);
        }
        catch (final IOException e1) {
            LOGGER.error("Cannot open voice player", e1);
            JOptionPane.showMessageDialog(applicationContext.getMainFrame(), "WARNING.", "Warning", JOptionPane.WARNING_MESSAGE);
        }

        final JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pianofortePanel, outputPanel);

        splitPane2.setOneTouchExpandable(true);
        splitPane2.setDividerLocation(200);
        splitPane2.addPropertyChangeListener("dividerLocation", e -> {
            final int location = ((Integer)e.getNewValue()).intValue();
            final int max = pianofortePanel.getMaximumSize().height + 20;

            if (location > max) {
                ((JSplitPane)e.getSource()).setDividerLocation(max);
            }
        });

        final JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainPanel, splitPane2);

        splitPane1.setOneTouchExpandable(true);
        splitPane1.setDividerLocation(500);

        getContentPane().add(splitPane1, BorderLayout.CENTER);

        setMinimumSize(new Dimension(600, 800));
        setSize(900, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        return componentsMap;
    }

    /**
     * Initialize menu bar.
     * @param properties the properties
     * @param components the components
     * @return the menu bar
     */
    protected JMenuBar initializeMenuBar(final Properties properties, final ComponentsMap components) {
        final SwingUtils utils = SwingUtils.getInstance();
        final JMenuBar result = utils.createMenuBar(properties, applicationContext, "menuBar", components);

        setJMenuBar(result);

        return result;
    }

    /**
     * Initialize tabbed pane.
     * @param properties the properties
     * @param components the components
     * @return the j tabbed pane
     */
    protected JTabbedPane initializeTabbedPane(final Properties properties, final ComponentsMap components) { // NOSONAR Argument can be used when overriding method
        final SwingUtils utils = SwingUtils.getInstance();

        mainPanel = new JTabbedPane();

        mainPanel.setPreferredSize(new Dimension(600, 500));
        mainPanel.setName("tabbedPane");

        filePlayerPanel = new FilePlayerPanel(applicationContext, properties, mainPanel.getName() + ".filePlayerPanel", midiPlayer);

        mainPanel.addTab(utils.getCapitalizedString(properties, applicationContext, filePlayerPanel.getName(), Constants.TITLE_SUFFIX), filePlayerPanel);

        professorePanel = new ProfessorePanel(applicationContext, properties, mainPanel.getName() + ".professorePanel");

        mainPanel.addTab(utils.getCapitalizedString(properties, applicationContext, professorePanel.getName(), Constants.TITLE_SUFFIX), professorePanel);

        recorderPanel = new RecorderPanel(applicationContext, properties, mainPanel.getName() + ".recorderPanel");

        mainPanel.addTab(utils.getCapitalizedString(properties, applicationContext, recorderPanel.getName(), Constants.TITLE_SUFFIX), recorderPanel);

        return mainPanel;
    }

    /**
     * Initialize tool bar.
     * @param properties the properties
     * @param components the components
     * @return the tool bar
     */
    protected JToolBar initializeToolBar(final Properties properties, final ComponentsMap components) {
        final SwingUtils utils = SwingUtils.getInstance();
        final JToolBar result = utils.createToolBar(properties, applicationContext, "toolBar", components);

        getContentPane().add(result, BorderLayout.PAGE_START);

        return result;
    }

    /**
     * Gets the pianoforte.
     * @return the pianoforte
     */
    public PianoforteComponent getPianoforte() {
        return pianofortePanel.getPianoforte();
    }
}
