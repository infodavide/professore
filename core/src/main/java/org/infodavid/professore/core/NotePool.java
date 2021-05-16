package org.infodavid.professore.core;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.midi.ShortMessage;

import org.apache.commons.pool2.impl.SoftReferenceObjectPool;
import org.infodavid.util.exception.PoolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class NotePool.
 */
public class NotePool extends SoftReferenceObjectPool<Note> {

    /** The Constant INSTANCE. */
    private static final NotePool INSTANCE;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NotePool.class);

    static {
        INSTANCE = new NotePool((short)64);
    }

    /**
     * Gets the single instance of NotePool.
     * @return the instance
     */
    public static NotePool getInstance() {
        return INSTANCE;
    }

    /** The check lock. */
    private final Lock checkLock = new ReentrantLock();

    /** The prefill count. */
    private short prefillCount;

    /** The prefill limit. */
    private float prefillLimit = 0.10f;

    /**
     * Instantiates a new pool.
     * @param prefillCount the prefill count
     */
    private NotePool(final short prefillCount) {
        super(new NoteFactory());

        this.prefillCount = prefillCount;

        addObjects(prefillCount);
    }

    /*
     * (non-javadoc)
     * @see org.apache.commons.pool2.ObjectPool#addObjects(int)
     */
    @Override
    public void addObjects(final int count) {
        try {
            super.addObjects(count);
        }
        catch (final Exception e) {
            throw new PoolException(e);
        }
    }

    /*
     * (non-javadoc)
     * @see org.apache.commons.pool2.impl.GenericObjectPool#borrowObject()
     */
    @Override
    public synchronized Note borrowObject() {
        try {
            return super.borrowObject();
        }
        catch (final Exception e) {
            throw new PoolException(e);
        }
        finally {
            prefill();
        }
    }

    /**
     * Borrow object.
     * @param channel the channel
     * @param key the key
     * @param pressed the pressed
     * @param velocity the velocity
     * @return the note
     */
    public Note borrowObject(final byte channel, final byte key, final boolean pressed, final short velocity) {
        final Note result = borrowObject();

        result.setChannel(channel);
        result.setKey(key);
        result.setPressed(velocity > 0 && pressed);

        if (key <= 0) {
            result.setOctave((byte)0);
            result.setBaseNote(null);
        }
        else {
            result.setOctave((byte)(key / 12 - 1));
            result.setBaseNote(NoteEnum.values()[key % 12]);
        }

        return result;
    }

    /**
     * Borrow object.
     * @param message the message
     * @return the note
     */
    public Note borrowObject(final ShortMessage message) {
        if (message.getCommand() == ShortMessage.NOTE_ON) {
            return borrowObject((byte)message.getChannel(), (byte)message.getData1(), true, (short)message.getData2());
        }
        else if (message.getCommand() == ShortMessage.NOTE_OFF) {
            return borrowObject((byte)message.getChannel(), (byte)message.getData1(), false, (short)message.getData2());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Note is not ON nor OFF, ignoring message: {}", String.valueOf(message.getCommand()));
        }

        return null;
    }

    /**
     * Gets the prefill count.
     * @return the prefillCount
     */
    public short getPrefillCount() {
        return prefillCount;
    }

    /**
     * Gets the prefill limit.
     * @return the prefillLimit
     */
    public float getPrefillLimit() {
        return prefillLimit;
    }

    /**
     * Sets the prefill count.
     * @param prefillCount the prefillCount to set
     */
    public void setPrefillCount(final short prefillCount) {
        this.prefillCount = prefillCount;
    }

    /**
     * Sets the prefill limit.
     * @param prefillLimit the prefillLimit to set
     */
    public void setPrefillLimit(final float prefillLimit) {
        this.prefillLimit = prefillLimit;
    }

    /**
     * Prefill.
     */
    private void prefill() {
        final float value = getNumIdle() / (float)prefillCount;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Available objects: {}%", String.valueOf(value * 100));
        }

        if (value < prefillLimit) {
            new Thread(() -> {
                checkLock.lock();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Checking number of available objects");
                }

                try {
                    final int count = prefillCount - getNumIdle();

                    if (count > 0) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Filling with {} objects", String.valueOf(count));
                        }

                        addObjects(count);
                    }
                }
                finally {
                    checkLock.unlock();

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Check completed");
                    }
                }
            }).start();
        }
    }
}
