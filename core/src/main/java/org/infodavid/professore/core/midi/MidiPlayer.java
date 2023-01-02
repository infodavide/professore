package org.infodavid.professore.core.midi;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.infodavid.professore.core.Note;
import org.infodavid.professore.core.NotePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MidiPlayer.
 */
public class MidiPlayer implements Closeable {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MidiPlayer.class);

    /** The runnable. */
    private final MidiPlayerRunnable runnable;

    /** The executor. */
    private final ThreadPoolExecutor executor;

    /**
     * Instantiates a new player.
     * @param controller the controller
     */
    public MidiPlayer(final SoundController controller) {
        super();
        runnable = new MidiPlayerRunnable(this, controller);
        LOGGER.debug("Creating thread");
        executor = (ThreadPoolExecutor) Executors.newSingleThreadExecutor();
    }

    /*
     * (non-javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        stop();

        if (executor != null) {
            executor.shutdownNow();
        }
    }

    /**
     * Gets the bpm.
     * @return the bpm
     */
    public short getBpm() {
        return runnable.getBpm();
    }

    /**
     * Gets the listener.
     * @return the listener
     */
    public MidiPlayerListener getListener() {
        return runnable.getListener();
    }

    /**
     * Gets the microsecond position.
     * @return the microsecond position
     */
    @SuppressWarnings("resource")
    public long getMicrosecondPosition() {
        if (runnable.getSequencer() != null) {
            return runnable.getSequencer().getMicrosecondPosition();
        }

        return 0;
    }

    /**
     * Gets the play list size.
     * @return the play list size
     */
    public int getPlayListSize() {
        return runnable.getPlayList().size();
    }

    /**
     * Gets the sound controller.
     * @return the soundController
     */
    public SoundController getSoundController() {
        return runnable.getSoundController();
    }

    /**
     * Checks if is paused.
     * @return true, if is paused
     */
    public boolean isPaused() {
        return runnable.isPaused();
    }

    /**
     * Checks if is playing.
     * @return true, if is playing
     */
    public boolean isPlaying() {
        return runnable.isPlaying();
    }

    /**
     * Pause.
     */
    public void pause() {
        runnable.pause();
    }

    /**
     * Play.
     * @param file the file
     * @throws IOException              Signals that an I/O exception has occurred.
     * @throws InvalidMidiDataException the invalid midi data exception
     */
    public void play(final Path file) throws IOException, InvalidMidiDataException {
        LOGGER.info("Reading file: {}", file.toAbsolutePath());
        Sequence sequence;

        try (InputStream in = Files.newInputStream(file)) {
            if (in == null) {
                throw new FileNotFoundException(file.toAbsolutePath().toString());
            }

            sequence = MidiSystem.getSequence(in);
        }

        runnable.getPlayList().add(new ImmutablePair<>(file.getFileName().toString(), sequence));
        play();
    }

    /**
     * Read.
     * @param file the file
     * @return the list
     * @throws IOException              Signals that an I/O exception has occurred.
     * @throws InvalidMidiDataException the invalid midi data exception
     */
    @SuppressWarnings("resource")
    public List<Note> read(final Path file) throws IOException, InvalidMidiDataException {
        LOGGER.info("Reading file: {}", file.toAbsolutePath());
        final List<Note> results = new ArrayList<>();
        Sequence sequence;

        try (InputStream in = Files.newInputStream(file)) {
            if (in == null) {
                throw new FileNotFoundException(file.toAbsolutePath().toString());
            }

            sequence = MidiSystem.getSequence(in);
        }

        LOGGER.info("Extracting notes from file: {}", file.toAbsolutePath());
        final Track[] tracks = sequence.getTracks();
        byte trackNumber = 0;

        for (final Track track : tracks) {
            trackNumber++;

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Reading track {}/{} having size: {}", String.valueOf(trackNumber), String.valueOf(tracks.length), String.valueOf(track.size()));
            }

            for (int i = 0; i < track.size(); i++) {
                final MidiEvent event = track.get(i);
                final MidiMessage message = event.getMessage();

                if (message instanceof ShortMessage) {
                    final Note note = NotePool.getInstance().borrowObject((ShortMessage) message);

                    if (note != null) {
                        LOGGER.debug("Adding note: {}", note);
                        results.add(note);
                    }
                } else {
                    LOGGER.debug("Not a short message: {}", message);
                }
            }
        }

        LOGGER.info("Completed");
        return results;
    }

    /**
     * Reset.
     */
    public void reset() {
        runnable.reset();
    }

    /**
     * Resume.
     */
    public void resume() {
        runnable.resume();
    }

    /**
     * Sets the bpm.
     * @param value the bpm to set
     */
    public void setBpm(final short value) {
        if (value <= 0) {
            runnable.setBpm(MidiPlayerRunnable.DEFAULT_BPM);
        } else {
            runnable.setBpm(value);
        }
    }

    /**
     * Sets the listener.
     * @param listener the new listener
     */
    public void setListener(final MidiPlayerListener listener) {
        runnable.setListener(listener);
    }

    /**
     * Stop.
     */
    public void stop() {
        runnable.stop();
    }

    /**
     * Play.
     */
    private void play() {
        synchronized (this) {
            if (executor.getActiveCount() == 0) {
                executor.submit(runnable);
            } else {
                LOGGER.debug("Using existing thread");
            }
        }
    }

    /**
     * Checks if is connect device.
     * @return the connectDevice
     */
    public boolean isConnectDevice() {
        return runnable.isConnectDevice();
    }

    /**
     * Sets the connect device.
     * @param connectDevice the connectDevice to set
     */
    public void setConnectDevice(final boolean connectDevice) {
        runnable.setConnectDevice(connectDevice);
    }
}
