package org.infodavid.professore.core;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class VoicePlayer.
 */
public class VoicePlayer implements Closeable, Runnable {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VoicePlayer.class);

    /** The Constant RESOURCES_PATH. */
    private static final String RESOURCES_PATH = "/sounds/"; // NOSONAR Keep as it is

    /** The format. */
    private AudioFormat format;

    /** The opened. */
    private final AtomicBoolean opened = new AtomicBoolean(false);

    /** The queue. */
    private final BlockingQueue<Pair<NoteEnum,byte[]>> queue = new LinkedBlockingQueue<>();

    /** The sounds. */
    private final EnumMap<NoteEnum,byte[]> sounds = new EnumMap<>(NoteEnum.class);

    /** The thread. */
    private Thread thread;

    /** The volume. */
    private byte volume;

    /** The volume changed. */
    private final AtomicBoolean volumeChanged = new AtomicBoolean(true);

    /** The stacked. */
    private final AtomicBoolean stacked = new AtomicBoolean(true);

    /**
     * Instantiates a new player.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public VoicePlayer() throws IOException {
        super();

        for (final NoteEnum note : NoteEnum.values()) {
            try (InputStream in = getClass().getResourceAsStream(RESOURCES_PATH + note.getItalianName().toLowerCase() + ".wav")) {
                if (in == null) {
                    LOGGER.info("No sound found for note: {}", note);

                    continue;
                }

                sounds.put(note, IOUtils.toByteArray(in));

                if (format == null) {
                    try (AudioInputStream ain = AudioSystem.getAudioInputStream(new ByteArrayInputStream(sounds.get(note)))) {
                        format = ain.getFormat();
                    }
                    catch (final UnsupportedAudioFileException e) {
                        throw new IOException(e);
                    }
                }
            }
        }

        LOGGER.info("{} sounds loaded", String.valueOf(sounds.size())); // NOSONAR Always written
    }

    /*
     * (non-javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() {
        if (opened.get()) {
            opened.set(false);
            queue.add(new ImmutablePair<>(null, null));
        }
    }

    public boolean isStacked() {
        return stacked.get();
    }

    public void setStacked(final boolean stacked) {
        this.stacked.set(stacked);
    }

    /**
     * Gets the sound.
     * @param note the note
     * @return the sound
     */
    public byte[] getSound(final NoteEnum note) {
        return sounds.get(note);
    }

    /**
     * Gets the volume.
     * @return the volume
     */
    public byte getVolume() {
        return volume;
    }

    /**
     * Checks if is open.
     * @return true, if is open
     */
    public boolean isOpen() {
        return opened.get();
    }

    /**
     * Open.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void open() throws IOException {
        LOGGER.info("Opening voice player...");

        if (!opened.get()) {
            if (thread != null) {
                thread.interrupt();
            }

            thread = new Thread(this, getClass().getName());

            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        }
    }

    /**
     * Play.
     * @param notes the notes
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void play(final NoteEnum... notes) throws IOException {
        if (notes == null || notes.length == 0) {
            return;
        }

        for (final NoteEnum note : notes) {
            play(note);
        }
    }

    /**
     * Play.
     * @param note the note
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void play(final NoteEnum note) throws IOException {
        final byte[] data = getSound(note);

        if (data == null) {
            LOGGER.info("No sound to play for note: {}", note);

            return;
        }

        LOGGER.debug("Adding note: {}", note);

        queue.add(new ImmutablePair<>(note, data));
    }

    /*
     * (non-javadoc)
     * @see java.lang.Runnable#run()
     */
    @SuppressWarnings("resource")
    @Override
    public void run() {
        opened.set(true);

        final byte[] buffer = new byte[4096 * 4];
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, buffer.length);

        try (SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info)) {
            line.open(format);
            line.start();

            LOGGER.debug("Ready");

            while (isOpen()) { // NOSONAR break and continue
                Pair<NoteEnum,byte[]> entry = queue.poll(1000, TimeUnit.MILLISECONDS);

                if (!stacked.get()) {
                    while (queue.size() > 1) {
                        entry = queue.poll(1000, TimeUnit.MILLISECONDS);
                    }
                }

                if (entry == null) {
                    continue;
                }

                if (entry.getKey() == null) {
                    break;
                }

                if (volumeChanged.get()) {
                    volumeChanged.set(false);
                    applyVolume(line, volume);
                }

                LOGGER.debug("Playing: {}", entry.getKey());

                int read = 0;

                try (final ByteArrayInputStream bais = new ByteArrayInputStream(entry.getValue()); AudioInputStream ais = AudioSystem.getAudioInputStream(bais)) {
                    while ((stacked.get() || queue.isEmpty()) && (read = ais.read(buffer)) > 0) {
                        line.write(buffer, 0, read);
                    }
                }
            }
        }
        catch (IOException | LineUnavailableException | UnsupportedAudioFileException | NullPointerException e) {
            LOGGER.warn("Cannot play sound", e);
        }
        catch (final InterruptedException e) {
            LOGGER.warn("Thread interrupted");

            Thread.currentThread().interrupt();
        }
        finally {
            opened.set(false);
        }

        LOGGER.info("Voice player closed");
    }

    /**
     * Sets the volume.
     * @param value the new volume
     */
    public void setVolume(final byte value) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Setting volume to: {}", String.valueOf(value));
        }

        volume = value;
        volumeChanged.set(true);
    }

    /**
     * Apply volume.
     * @param line the line
     * @param volume the volume
     */
    private void applyVolume(final SourceDataLine line, final byte volume) {
        if (line.isControlSupported(FloatControl.Type.VOLUME)) {
            final FloatControl control = (FloatControl)line.getControl(FloatControl.Type.VOLUME);
            final float v = (float)(volume / 100.0);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Using volume value: {}", String.valueOf(v));
            }

            control.setValue(v);
        }
        else {
            LOGGER.debug("Volume control is not available");

            final FloatControl control = (FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
            final float v = (float)(Math.log(volume / 100.0) / Math.log(10.0) * 20.0);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Using gain value: {}", String.valueOf(v));
            }

            control.setValue(v);
        }

        if (line.isControlSupported(BooleanControl.Type.MUTE)) {
            final BooleanControl control = (BooleanControl)line.getControl(BooleanControl.Type.MUTE);

            control.setValue(volume <= 0);
        }
    }
}
