package org.infodavid.professore.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.commons.io.FileUtils;
import org.infodavid.professore.core.Constants;
import org.infodavid.professore.core.midi.MidiPlayer;
import org.infodavid.professore.core.midi.MidiPlayerListener;
import org.infodavid.util.swing.ComponentsMap;
import org.infodavid.util.swing.SwingApplicationContext;
import org.infodavid.util.swing.SwingUtils;
import org.infodavid.util.swing.renderer.PathRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import humanize.Humanize;

/**
 * The Class FilePlayerPanel.
 */
public class FilePlayerPanel extends JPanel {

    /** The Constant DIRECTORY. */
    private static final String DIRECTORY = ".directory";

    /**
     * The Class DragUpdate.
     */
    protected class DragUpdate implements Runnable {

        /** The drag over. */
        private final boolean dragOver;

        /** The drag point. */
        private final Point dragPoint;

        /**
         * Instantiates a new drag update.
         * @param dragOver the drag over
         * @param dragPoint the drag point
         */
        public DragUpdate(final boolean dragOver, final Point dragPoint) {
            super();

            this.dragOver = dragOver;
            this.dragPoint = dragPoint;
        }

        /*
         * (non-javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            FilePlayerPanel.this.dragOver = dragOver;
            FilePlayerPanel.this.dragPoint = dragPoint;
        }
    }

    /**
     * The Class DropTargetHandler.
     */
    protected class DropTargetHandler implements DropTargetListener {

        /*
         * (non-javadoc)
         * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dragEnter(final DropTargetDragEvent dtde) {
            processDrag(dtde);
            SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
        }

        /*
         * (non-javadoc)
         * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
         */
        @Override
        public void dragExit(final DropTargetEvent dte) {
            SwingUtilities.invokeLater(new DragUpdate(false, null));
        }

        /*
         * (non-javadoc)
         * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dragOver(final DropTargetDragEvent dtde) {
            dragEnter(dtde);
        }

        /*
         * (non-javadoc)
         * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
         */
        @SuppressWarnings({
                "unchecked", "rawtypes"
        })
        @Override
        public void drop(final DropTargetDropEvent dtde) {
            SwingUtilities.invokeLater(new DragUpdate(false, null));

            final Transferable transferable = dtde.getTransferable();

            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrop(dtde.getDropAction());

                try {
                    final List<File> transferData = (List)transferable.getTransferData(DataFlavor.javaFileListFlavor);

                    for (final File file : transferData) { // NOSONAR Number of break
                        if (file.isFile() && file.getName().toLowerCase().endsWith(Constants.MID_EXTENSION)) {
                            addSelectableFiles(file.getParentFile().toPath());
                            filesCombo.setSelectedItem(file.toPath());

                            break;
                        }
                        else if (file.isDirectory()) {
                            addSelectableFiles(file.getParentFile().toPath());

                            break;
                        }
                    }
                }
                catch (final Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    JOptionPane.showMessageDialog(applicationContext.getMainFrame(), "WARNING.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
            else {
                dtde.rejectDrop();
            }
        }

        /*
         * (non-javadoc)
         * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dropActionChanged(final DropTargetDragEvent dtde) {
            // noop
        }

        /**
         * Process drag.
         * @param dtde the dtde
         */
        protected void processDrag(final DropTargetDragEvent dtde) {
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrag(DnDConstants.ACTION_COPY);
            }
            else {
                dtde.rejectDrag();
            }
        }
    }

    /**
     * The PlayerListener class.
     * @see PlayerEvent
     */
    private class PlayerListener implements MidiPlayerListener, ActionListener {

        /** The timer. */
        private Timer timer;

        /** The pattern. */
        private String pattern = "%s / ";

        /*
         * (non-javadoc)
         * @see org.infodavid.professore.core.midi.MidiPlayerListener#paused(org.infodavid.professore.core.midi.MidiPlayer)
         */
        @Override
        public void paused(final MidiPlayer player) {
            LOGGER.debug("Player paused");

            beginningButton.setEnabled(true);
            playButton.setEnabled(true);
            stopButton.setEnabled(true);

            if (timer != null) {
                timer.stop();
            }
        }

        /*
         * (non-javadoc)
         * @see org.infodavid.professore.core.midi.MidiPlayerListener#playing(org.infodavid.professore.core.midi.MidiPlayer, java.lang.String, javax.sound.midi.Sequence)
         */
        @Override
        public void playing(final MidiPlayer player, final String title, final Sequence sequence) {
            LOGGER.debug("Player playing: {}", title);

            beginningButton.setEnabled(true);
            playButton.setEnabled(true);
            stopButton.setEnabled(true);
            progressBar.setValue(0);

            pattern = "%s / " + Humanize.duration(Integer.valueOf((int)(sequence.getMicrosecondLength() / 1E6)));

            progressBar.setMaximum((int)(sequence.getMicrosecondLength() / 1E3));
            progressBar.setToolTipText(title);
            progressBar.setStringPainted(true);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Progress of player: 0 -> {}", String.valueOf(progressBar.getMaximum()));
            }

            timer = new Timer(100, this);

            timer.start();
        }

        /*
         * (non-javadoc)
         * @see org.infodavid.professore.core.midi.MidiPlayerListener#resumed(org.infodavid.professore.core.midi.MidiPlayer)
         */
        @Override
        public void resumed(final MidiPlayer player) {
            LOGGER.debug("Player resumed");

            beginningButton.setEnabled(true);
            playButton.setEnabled(true);
            stopButton.setEnabled(true);

            if (timer != null) {
                timer.restart();
            }
        }

        /*
         * (non-javadoc)
         * @see org.infodavid.professore.core.midi.MidiPlayerListener#stopped(org.infodavid.professore.core.midi.MidiPlayer)
         */
        @Override
        public void stopped(final MidiPlayer player) {
            LOGGER.debug("Player stopped");

            SwingUtilities.invokeLater(() -> {
                beginningButton.setEnabled(true);
                playButton.setEnabled(true);
                playButton.setSelected(false);
                stopButton.setEnabled(false);
            });

            if (timer != null) {
                timer.stop();
            }
        }

        /*
         * (non-javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            final int value = (int)(player.getMicrosecondPosition() / 1E3);

            progressBar.getModel().setValue(value);
            progressBar.setString(String.format(pattern, Humanize.duration(Integer.valueOf((int)(value / 1E3)))));
        }
    }

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FilePlayerPanel.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2638586494781388942L;

    /** The application context. */
    private final transient SwingApplicationContext applicationContext;

    /** The drag over. */
    private boolean dragOver = false;

    /** The drag point. */
    private Point dragPoint;

    /** The files combo. */
    private JComboBox<Path> filesCombo;

    /** The player. */
    private final transient MidiPlayer player;

    /** The progress bar. */
    private JProgressBar progressBar;

    /**
     * Instantiates a new file player panel.
     * @param applicationContext the application context
     * @param properties the properties
     * @param name the name
     * @param player the player
     */
    public FilePlayerPanel(final SwingApplicationContext applicationContext, final Properties properties, final String name, final MidiPlayer player) {
        super();

        this.applicationContext = applicationContext;
        this.player = player;

        setName(name);
        initialize(properties);
        addSelectableFiles(Paths.get(applicationContext.getPreferences().get(getName() + DIRECTORY, FileUtils.getUserDirectoryPath())));
    }

    /**
     * Adds the selectable files.
     * @param selectedDirectory the selected directory
     */
    private void addSelectableFiles(final Path selectedDirectory) {
        final DefaultComboBoxModel<Path> model = (DefaultComboBoxModel<Path>)filesCombo.getModel();

        directoryField.setText(selectedDirectory.toAbsolutePath().toString());
        applicationContext.getPreferences().put(getName() + DIRECTORY, directoryField.getText());
        model.removeAllElements();

        try (Stream<Path> walk = Files.walk(selectedDirectory, 1)) {
            final List<Path> files = walk.filter(f -> Files.isRegularFile(f) && f.getFileName().toString().endsWith(Constants.MID_EXTENSION)).collect(Collectors.toList());

            for (final Path file : files) {
                model.addElement(file);
            }
        }
        catch (final IOException e) {
            LOGGER.error("Cannot list directory: " + selectedDirectory, e);
            JOptionPane.showMessageDialog(applicationContext.getMainFrame(), "WARNING.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** The beginning button. */
    private JButton beginningButton;

    /** The directory field. */
    private JTextField directoryField;

    /** The play button. */
    private JToggleButton playButton;

    /** The stop button. */
    private JButton stopButton;

    /**
     * Initialize.
     * @param properties the properties
     * @return the map
     */
    protected ComponentsMap initialize(final Properties properties) {
        final SwingUtils utils = SwingUtils.getInstance();
        final ComponentsMap componentsMap = new ComponentsMap();
        final Box toolBarPanel = Box.createHorizontalBox();
        final JToolBar tooBar = utils.createToolBar(properties, applicationContext, getName() + ".toolBar", componentsMap);
        beginningButton = componentsMap.get(getName() + ".toolBar.beginning", JButton.class);
        playButton = componentsMap.get(getName() + ".toolBar.play", JToggleButton.class);
        stopButton = componentsMap.get(getName() + ".toolBar.stop", JButton.class);

        beginningButton.addActionListener(e -> player.reset());
        beginningButton.setEnabled(false);

        playButton.addActionListener(e -> {
            if (filesCombo.getSelectedItem() == null) {
                return;
            }

            try {
                // value is before change
                if (playButton.isSelected()) {
                    if (player.isPaused()) {
                        player.resume();
                    }
                    else {
                        player.play((Path)filesCombo.getSelectedItem());
                    }
                }
                else {
                    player.pause();
                }
            }
            catch (IOException | InvalidMidiDataException e1) {
                LOGGER.error("Cannot open play file", e1);
                JOptionPane.showMessageDialog(applicationContext.getMainFrame(), "WARNING.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        playButton.setEnabled(false);

        stopButton.addActionListener(e -> player.stop());
        stopButton.setEnabled(false);

        toolBarPanel.add(tooBar);

        final JPanel fieldsPanel = new JPanel(new GridBagLayout());

        fieldsPanel.setName(getName());
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        final GridBagConstraints constraints = utils.createGridBagConstraints();
        byte row = 0;

        directoryField = utils.createTextField(properties, applicationContext, fieldsPanel, constraints, row, getName() + DIRECTORY, "");

        directoryField.setEditable(false);

        final JButton directoryButton = new JButton(applicationContext.getResources().getCapitalizedString("browse"));

        directoryButton.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();

            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);

            if (fileChooser.showOpenDialog(applicationContext.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
                addSelectableFiles(fileChooser.getSelectedFile().toPath());
            }
        });

        constraints.gridy = ++row;
        fieldsPanel.add(directoryButton, constraints);

        filesCombo = utils.createComboBox(properties, applicationContext, fieldsPanel, constraints, ++row, getName() + ".filesCombo", Path.class);

        filesCombo.setEditable(false);
        filesCombo.setRenderer(new PathRenderer(filesCombo));
        filesCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.DESELECTED || e.getStateChange() == ItemEvent.SELECTED) {
                final boolean enabled = e.getItem() != null;

                beginningButton.setEnabled(enabled);
                playButton.setEnabled(enabled);
                stopButton.setEnabled(false);
            }
        });

        constraints.gridx = 0;
        constraints.gridy = ++row;
        constraints.gridwidth = 2;

        progressBar = utils.createProgressBar(properties, applicationContext, fieldsPanel, constraints, ++row, getName() + ".progressBar", 0, 100);

        progressBar.setPreferredSize(new Dimension(progressBar.getPreferredSize().width, filesCombo.getPreferredSize().height));
        progressBar.addChangeListener(e -> progressBar.setString(String.valueOf(progressBar.getValue())));
        player.setListener(new PlayerListener());

        constraints.gridx = 0;
        constraints.gridy = ++row;
        constraints.gridwidth = 2;

        final JSlider bpmSlider = utils.createSlider(properties, applicationContext, fieldsPanel, constraints, ++row, getName() + ".bpmSlider", SwingConstants.HORIZONTAL, 5, 180, 60);

        bpmSlider.setMajorTickSpacing(50);
        bpmSlider.setMinorTickSpacing(10);
        bpmSlider.setPaintTicks(true);
        bpmSlider.setPaintLabels(true);

        setAlignmentX(Component.LEFT_ALIGNMENT);
        setLayout(new BorderLayout());

        add(toolBarPanel, BorderLayout.PAGE_START);
        add(fieldsPanel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(600, 320));

        return componentsMap;
    }
}
