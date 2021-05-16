package org.infodavid.util;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

/**
 * The Class NumberUtils.
 */
public final class NumberUtils {

    /** The Constant BINARY_PREFIX. */
    public static final String BINARY_PREFIX = "0b";

    /** The Constant HEX_PREFIX. */
    public static final String HEX_PREFIX = "0x";

    /** The singleton. */
    private static WeakReference<NumberUtils> instance = null;

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized NumberUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new NumberUtils());
        }

        return instance.get();
    }

    /** The Constant powers. */
    private final long[] powers = new long[] { // NOSONAR Allow unallocation
            1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000
    };

    /**
     * Instantiates a new util.
     */
    private NumberUtils() {
        super();
    }

    /**
     * Double to byte array.
     * @param value the value
     * @return the byte[]
     */
    public byte[] doubleToByteArray(final double value) {
        final byte[] bytes = new byte[8];

        ByteBuffer.wrap(bytes).putDouble(value);

        return bytes;
    }

    /**
     * Float to byte array.
     * @param value the value
     * @return the byte[]
     */
    public byte[] floatToByteArray(final float value) {
        final byte[] bytes = new byte[4];

        ByteBuffer.wrap(bytes).putFloat(value);

        return bytes;
    }

    /**
     * Round.
     * @param value the value
     * @param decimals the number of decimals
     * @return the double
     */
    public double round(final double value, final int decimals) {
        return Math.round(value * powers[decimals]) / (double)powers[decimals];
    }

    /**
     * To binary.
     * @param bits the bits
     * @return the string
     */
    public String toBinary(final BitSet bits) {
        if (bits == null) {
            return toBinary(0);
        }

        if (bits.length() <= Integer.BYTES * 4) {
            return toBinary(toInt(bits));
        }

        return toBinary(toLong(bits));
    }

    /**
     * To binary.
     * @param bits the bits
     * @return the string
     */
    public String toBinary(final boolean[] bits) {
        if (bits == null) {
            return toBinary(0);
        }

        if (bits.length <= Integer.BYTES * 4) {
            return toBinary(toInt(bits));
        }

        return toBinary(toLong(bits));
    }

    /**
     * To binary.
     * @param bits the bits
     * @return the string
     */
    public String toBinary(final int bits) {
        return BINARY_PREFIX + Integer.toBinaryString(bits);
    }

    /**
     * To binary.
     * @param bits the bits
     * @return the string
     */
    public String toBinary(final long bits) {
        return BINARY_PREFIX + Long.toBinaryString(bits);
    }

    /**
     * Convert.
     * @param value the value
     * @return the bit set
     */
    public boolean[] toBits(final byte value) {
        final boolean[] bits = new boolean[Byte.SIZE];

        Arrays.fill(bits, false);

        for (int i = 0; i < Byte.SIZE; i++) {
            if ((value >> i & 1) == 1) {
                bits[i] = true;
            }
        }

        return bits;
    }

    /**
     * Convert.
     * @param value the value
     * @return the bit set
     */
    public boolean[] toBits(final int value) {
        final boolean[] bits = new boolean[Integer.SIZE];

        Arrays.fill(bits, false);

        for (int i = 0; i < Integer.SIZE; i++) {
            if ((value >> i & 1) == 1) {
                bits[i] = true;
            }
        }

        return bits;
    }

    /**
     * Convert.
     * @param value the value
     * @return the bit set
     */
    public boolean[] toBits(final long value) {
        final boolean[] bits = new boolean[Long.SIZE];

        Arrays.fill(bits, false);

        for (int i = 0; i < Long.SIZE; i++) {
            if ((value >> i & 1) == 1) {
                bits[i] = true;
            }
        }

        return bits;
    }

    /**
     * Convert.
     * @param value the value
     * @return the bit set
     */
    public boolean[] toBits(final short value) {
        final boolean[] bits = new boolean[Short.SIZE];

        Arrays.fill(bits, false);

        for (int i = 0; i < Short.SIZE; i++) {
            if ((value >> i & 1) == 1) {
                bits[i] = true;
            }
        }

        return bits;
    }

    /**
     * Convert.
     * @param value the value
     * @return the bit set
     */
    public BitSet toBitSet(final byte value) {
        final BitSet bits = new BitSet();

        for (int i = 0; i < Byte.SIZE; i++) {
            if ((value >> i & 1) == 1) {
                bits.set(i);
            }
        }

        return bits;
    }

    /**
     * Convert.
     * @param value the value
     * @return the bit set
     */
    public BitSet toBitSet(final int value) {
        final BitSet bits = new BitSet();

        for (int i = 0; i < Integer.SIZE; i++) {
            if ((value >> i & 1) == 1) {
                bits.set(i);
            }
        }

        return bits;
    }

    /**
     * Convert.
     * @param value the value
     * @return the bit set
     */
    public BitSet toBitSet(final short value) {
        final BitSet bits = new BitSet();

        for (int i = 0; i < Short.SIZE; i++) {
            if ((value >> i & 1) == 1) {
                bits.set(i);
            }
        }

        return bits;
    }

    /**
     * Convert.
     * @param value the value
     * @return the bit set
     */
    public BitSet toBitsSet(final long value) {
        final BitSet bits = new BitSet();

        for (int i = 0; i < Long.SIZE; i++) {
            if ((value >> i & 1) == 1) {
                bits.set(i);
            }
        }

        return bits;
    }

    /**
     * To byte.
     * @param bits the bits
     * @return the byte
     */
    public byte toByte(final BitSet bits) {
        byte value = 0;

        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? 1 << i : 0;
        }

        return value;
    }

    /**
     * To byte.
     * @param bits the bits
     * @return the byte
     */
    public byte toByte(final boolean[] bits) {
        byte value = 0;

        for (int i = 0; i < bits.length; ++i) {
            value += bits[i] ? 1 << i : 0;
        }

        return value;
    }

    /**
     * To bytes.
     * @param hex the hex
     * @return the byte[]
     */
    public byte[] toBytes(final String hex) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(hex)) {
            return new byte[0];
        }

        final int len = hex.length();
        final byte[] result = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            result[i / 2] = (byte)((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }

        return result;
    }

    /**
     * To double.
     * @param bytes the bytes
     * @return the double
     */
    public double toDouble(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    /**
     * To float.
     * @param bytes the bytes
     * @return the float
     */
    public float toFloat(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).getFloat();
    }

    /**
     * To hex.
     * @param bits the bits
     * @return the string
     */
    public String toHex(final BitSet bits) {
        if (bits == null) {
            return toHex(0);
        }

        if (bits.length() <= Integer.BYTES * 4) {
            return toHex(toInt(bits));
        }

        return toHex(toLong(bits));
    }

    /**
     * To hex.
     * @param bits the bits
     * @return the string
     */
    public String toHex(final boolean[] bits) {
        if (bits == null) {
            return toHex(0);
        }

        if (bits.length <= Integer.BYTES * 4) {
            return toHex(toInt(bits));
        }

        return toHex(toLong(bits));
    }

    /**
     * To hex.
     * @param bits the bits
     * @return the string
     */
    public String toHex(final int bits) {
        return HEX_PREFIX + Integer.toHexString(bits);
    }

    /**
     * To hex.
     * @param bits the bits
     * @return the string
     */
    public String toHex(final long bits) {
        return HEX_PREFIX + Long.toHexString(bits);
    }

    /**
     * Human readable bytes count.
     * @param bytes the bytes
     * @param si the si
     * @return the string
     */
    @SuppressWarnings("boxing")
    public String toHumanReadableByteCount(final long bytes, final boolean si) {
        final int unit = si ? 1000 : 1024;

        if (bytes < unit) {
            return bytes + " B";
        }

        final int exp = (int)(Math.log(bytes) / Math.log(unit));
        final String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");

        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * To int.
     * @param bits the bits
     * @return the int
     */
    public int toInt(final BitSet bits) {
        int value = 0;

        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? 1 << i : 0;
        }

        return value;
    }

    /**
     * To int.
     * @param bits the bits
     * @return the int
     */
    public int toInt(final boolean[] bits) {
        int value = 0;

        for (int i = 0; i < bits.length; ++i) {
            value += bits[i] ? 1 << i : 0;
        }

        return value;
    }

    /**
     * To int.
     * @param bytes the bytes
     * @return the int
     */
    public int toInt(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * To long.
     * @param bits the bits
     * @return the long
     */
    public long toLong(final BitSet bits) {
        long value = 0L;

        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? 1L << i : 0L;
        }

        return value;
    }

    /**
     * To long.
     * @param bits the bits
     * @return the long
     */
    public long toLong(final boolean[] bits) {
        long value = 0L;

        for (int i = 0; i < bits.length; ++i) {
            value += bits[i] ? 1L << i : 0L;
        }

        return value;
    }

    /**
     * To long.
     * @param bytes the bytes
     * @return the long
     */
    public long toLong(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    /**
     * To short.
     * @param bits the bits
     * @return the short
     */
    public short toShort(final BitSet bits) {
        short value = 0;

        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? 1 << i : 0;
        }

        return value;
    }

    /**
     * To short.
     * @param bits the bits
     * @return the short
     */
    public short toShort(final boolean[] bits) {
        short value = 0;

        for (int i = 0; i < bits.length; ++i) {
            value += bits[i] ? 1 << i : 0;
        }

        return value;
    }

    /**
     * To short.
     * @param bytes the bytes
     * @return the short
     */
    public short toShort(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }

    /**
     * Unsigned to bytes.
     * @param b the b
     * @return the int
     */
    public int unsignedToBytes(final byte b) {
        return b & 0xFF;
    }
}
