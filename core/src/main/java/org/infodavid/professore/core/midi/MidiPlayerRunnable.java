package org.infodavid.professore.core.midi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MidiPlayerRunnable.
 */
class MidiPlayerRunnable implements Runnable {

    /** The Constant DEFAULT_BPM. */
    public static final short DEFAULT_BPM = 80;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MidiPlayerRunnable.class);

    /** The bpm. */
    private short bpm = 80;

    /** The connect device. */
    private final AtomicBoolean connectDevice = new AtomicBoolean(true);

    /** The current. */
    private Pair<String,Sequence> current;

    /** The listener. */
    private MidiPlayerListener listener;

    /** The paused. */
    private final AtomicBoolean paused = new AtomicBoolean(false);

    /** The player. */
    private final MidiPlayer player;

    /** The play list. */
    private final BlockingQueue<Pair<String,Sequence>> playList = new LinkedBlockingQueue<>(Byte.MAX_VALUE);

    /** The sequencer. */
    private Sequencer sequencer;

    /** The sound controller. */
    private final SoundController soundController;

    /**
     * Instantiates a new midi player runnable.
     * @param player the player
     * @param controller the controller
     */
    public MidiPlayerRunnable(final MidiPlayer player, final SoundController controller) {
        super();

        this.player = player;
        soundController = controller;
    }

    /**
     * Gets the bpm.
     * @return the bpm
     */
    public short getBpm() {
        return bpm;
    }

    /**
     * Gets the listener.
     * @return the listener
     */
    public MidiPlayerListener getListener() {
        return listener;
    }

    /**
     * Gets the play list.
     * @return the play list
     */
    public BlockingQueue<Pair<String,Sequence>> getPlayList() {
        return playList;
    }

    /**
     * Gets the sequencer.
     * @return the sequencer
     */
    public Sequencer getSequencer() {
        return sequencer;
    }

    /**
     * Gets the sound controller.
     * @return the soundController
     */
    public SoundController getSoundController() {
        return soundController;
    }

    /**
     * Checks if is connect device.
     * @return the connectDevice
     */
    public boolean isConnectDevice() {
        return connectDevice.get();
    }

    /**
     * Checks if is paused.
     * @return true, if is paused
     */
    public boolean isPaused() {
        return paused.get();
    }

    /**
     * Checks if is playing.
     * @return true, if is playing
     */
    public boolean isPlaying() {
        synchronized (this) {
            return sequencer != null && sequencer.isRunning();
        }
    }

    /**
     * Pause.
     */
    public void pause() {
        synchronized (this) {
            paused.set(true);

            if (sequencer != null && sequencer.isOpen()) {
                sequencer.stop();
            }
        }

        if (listener != null) {
            listener.paused(player);
        }
    }

    /**
     * Reset.
     */
    public void reset() {
        synchronized (this) {
            pause();

            if (current != null) {
                final List<Pair<String,Sequence>> entries = new ArrayList<>(playList);

                entries.add(0, current);

                playList.clear();
                playList.addAll(entries);
            }
        }
    }

    /**
     * Resume.
     */
    public void resume() {
        synchronized (this) {
            if (sequencer != null && sequencer.isOpen()) {
                sequencer.start();
            }

            paused.set(false);
        }

        if (listener != null) {
            listener.resumed(player);
        }
    }

    /*
     * (non-javadoc)
     * @see java.lang.Runnable#run()
     */
    @SuppressWarnings("resource")
    @Override
    public void run() {
        try { // NOSONAR sequencer is used in the object and close is handled in the finally block
            if (sequencer == null) {
                sequencer = MidiSystem.getSequencer(connectDevice.get());

                if (sequencer == null) {
                    throw new MidiUnavailableException("Sequencer device not supported");
                }

                sequencer.getTransmitter().setReceiver(new ReceiverBridge(soundController, sequencer.getTransmitter().getReceiver()));
                sequencer.addMetaEventListener(soundController::meta);

                final int[] controllerTypes = new int[128];

                for (int i = 0; i < controllerTypes.length; i++) {
                    controllerTypes[i] = i;
                }

                sequencer.addControllerEventListener(soundController::controlChange, controllerTypes);
            }

            if (!sequencer.isOpen()) {
                sequencer.open();
            }

            while (sequencer.isOpen()) { // NOSONAR break and continue
                synchronized (this) {
                    current = playList.poll(1000, TimeUnit.MILLISECONDS);
                }

                if (current == null) {
                    continue;
                }

                if (current.getValue() == null) {
                    break;
                }

                LOGGER.info("Playing: {}", current.getKey());

                if (listener != null) {
                    listener.playing(player, current.getKey(), current.getValue());
                }

                sequencer.setSequence(current.getValue());
                sequencer.setTempoInBPM(bpm);
                sequencer.start();

                while (sequencer.isRunning() || paused.get()) {
                    try {
                        Thread.sleep(500);
                    }
                    catch (final InterruptedException e) {
                        LOGGER.debug("Thread interrupted", e);

                        Thread.currentThread().interrupt();
                    }
                }

                sequencer.stop();

                if (playList.isEmpty()) {
                    break;
                }
            }
        }
        catch (final MidiUnavailableException e) {
            LOGGER.error("Cannot open sequencer", e);
        }
        catch (final InvalidMidiDataException e) {
            LOGGER.error("Cannot play sequence", e);
        }
        catch (final InterruptedException e) {
            LOGGER.warn("Thread interruped");

            if (sequencer != null) {
                sequencer.close();
            }

            Thread.currentThread().interrupt();
        }
        finally {
            paused.set(false);

            if (sequencer != null && sequencer.isOpen()) {
                sequencer.close();
            }

            if (listener != null) {
                listener.stopped(player);
            }
        }
    }

    /**
     * Sets the bpm.
     * @param bpm the bpm to set
     */
    public void setBpm(final short bpm) {
        this.bpm = bpm;
    }

    /**
     * Sets the connect device.
     * @param connectDevice the connectDevice to set
     */
    public void setConnectDevice(final boolean connectDevice) {
        this.connectDevice.set(connectDevice);
    }

    /**
     * Sets the listener.
     * @param listener the new listener
     */
    public void setListener(final MidiPlayerListener listener) {
        this.listener = listener;
    }

    /**
     * Stop.
     */
    public void stop() {
        synchronized (this) {
            paused.set(false);

            if (sequencer != null && sequencer.isOpen()) {
                sequencer.stop();
            }
        }

        if (listener != null) {
            listener.stopped(player);
        }
    }
}
