package org.infodavid.professore.desktop;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import org.infodavid.professore.core.NoteEnum;

import com.gs.collections.api.set.primitive.MutableByteSet;
import com.gs.collections.impl.set.mutable.primitive.ByteHashSet;

/**
 * The Class PianoforteComponent.
 */
public class PianoforteComponent extends JComponent {

    /**
     * The Class KeyShape.
     */
    private static class KeyShape implements Serializable {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -6599411715444940440L;

        /**
         * The color.</br>
         * true to use white or fals to use black.
         */
        private final boolean color;

        /** The shape. */
        private final transient Shape shape;

        /**
         * Instantiates a new key shape.
         * @param shape the shape
         * @param color the color, true to use white or false to use black
         */
        KeyShape(final Shape shape, final boolean color) {
            super();

            this.shape = shape;
            this.color = color;
        }
    }

    /** The Constant BLACK_KEY_HEIGHT. */
    public static final float BLACK_KEY_HEIGHT = 3.5f / 6f;

    /** The Constant WHITE_KEY_ASPECT. */
    public static final float WHITE_KEY_ASPECT = 7f / 8f / 5.7f;

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6897889426232476057L;

    /** The first note. */
    private NoteEnum firstNote;

    /** The key shapes. */
    private List<KeyShape> keyShapes;

    /** The keys. */
    private final MutableByteSet keys = new ByteHashSet();

    /** The white key count. */
    private byte whiteKeyCount;

    /** The white key height. */
    private int whiteKeyHeight;

    /** The white key width. */
    private int whiteKeyWidth;

    /**
     * Instantiates a new pianoforte.
     */
    public PianoforteComponent() {
        super();

        setFirstNote(NoteEnum.DO);
        setWhiteKeyCount((byte)(7 * 7 + 1));
        setWhiteKeySize(Math.round(220 * WHITE_KEY_ASPECT), 220);
    }

    /**
     * Clear lit keys.
     */
    public void clearLitKeys() {
        keys.clear();
        repaint();
    }

    /**
     * Gets the key at point.
     * @param p the p
     * @return the key at point
     */
    public byte getKeyAtPoint(final Point2D p) {
        final List<KeyShape> shapes = getKeyShapes();

        for (byte i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).shape.contains(p)) {
                return i;
            }
        }

        return -1;
    }

    /*
     * (non-javadoc)
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(whiteKeyCount * whiteKeyWidth, whiteKeyHeight);
    }

    /*
     * (non-javadoc)
     * @see java.awt.Container#invalidate()
     */
    @Override
    public void invalidate() {
        super.invalidate();

        keyShapes = null;
    }

    /**
     * Checks if is a key.
     * @param index the index
     * @return true, if is a key
     */
    public boolean isKey(final byte index) {
        return keys.contains(index);
    }

    /*
     * (non-javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(final Graphics g1) {
        final Graphics2D g2d = (Graphics2D)g1;
        final Rectangle clipRect = g2d.getClipBounds();

        g2d.setColor(Color.BLACK);
        g2d.fill(clipRect);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(1f));

        final List<KeyShape> shapes = getKeyShapes();

        for (byte i = 0; i < shapes.size(); i++) {
            final KeyShape shape = shapes.get(i);
            final Rectangle bounds = shape.shape.getBounds();

            if (!bounds.intersects(clipRect)) {
                continue;
            }

            final Color color;

            if (isKey(i)) {
                color = shape.color ? new Color(0xFF5050) : new Color(0xDF3030);
            }
            else {
                color = shape.color ? Color.WHITE : Color.BLACK;
            }

            g2d.setColor(color);
            g2d.fill(shape.shape);

            if (true) { // gradient
                if (shape.color) {
                    g2d.setPaint(new LinearGradientPaint(bounds.x, bounds.y, bounds.x, bounds.y + bounds.height, new float[] {
                            0, 0.02f, 0.125f, 0.975f, 1
                    }, new Color[] {
                            new Color(0xA0000000, true), new Color(0x30000000, true), new Color(0x00000000, true), new Color(0x00000000, true), new Color(0x30000000, true),
                    }));
                    g2d.fill(shape.shape);
                }
                else {
                    bounds.setRect(bounds.getX() + bounds.getWidth() * 0.15f, bounds.getY() + bounds.getHeight() * 0.03f, bounds.getWidth() * 0.7f, bounds.getHeight() * 0.97f);
                    g2d.setPaint(new GradientPaint(bounds.x, bounds.y, new Color(0x60FFFFFF, true), bounds.x, bounds.y + bounds.height * 0.5f, new Color(0x00FFFFFF, true)));
                    g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 4, 4);
                    g2d.setPaint(new LinearGradientPaint(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y, new float[] {
                            0, 0.2f, 0.8f, 1
                    }, new Color[] {
                            new Color(0x60FFFFFF, true), new Color(0x00FFFFFF, true), new Color(0x00FFFFFF, true), new Color(0x60FFFFFF, true),
                    }));
                    g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 4, 4);
                }
            }

            g2d.setColor(Color.BLACK);
            g2d.draw(shape.shape);
        }
    }

    /**
     * Sets the first note.
     * @param n the new first note
     */
    public void setFirstNote(final NoteEnum note) {
        if (note == null) {
            throw new IllegalArgumentException();
        }

        firstNote = note;

        revalidate();
    }

    /**
     * Sets the key.
     * @param index the index
     * @param flag the state
     */
    public void setKey(final byte index, final boolean flag) {
        if (index < 0 || index > getKeyShapes().size()) {
            return;
        }

        if (flag) {
            keys.add(index);
        }
        else {
            keys.remove(index);
        }

        repaint(getKeyShapes().get(index).shape.getBounds());
    }

    /**
     * Sets the white key count.
     * @param count the new white key count
     */
    public void setWhiteKeyCount(final byte count) {
        if (count < 0) {
            throw new IllegalArgumentException();
        }

        whiteKeyCount = count;

        revalidate();
    }

    /**
     * Sets the white key size.
     * @param width the width
     * @param height the height
     */
    public void setWhiteKeySize(final int width, final int height) {
        if (width < 0) {
            throw new IllegalArgumentException();
        }

        if (height < 0) {
            throw new IllegalArgumentException();
        }

        whiteKeyWidth = width;
        whiteKeyHeight = height;

        revalidate();
    }

    /**
     * Creates the black key.
     * @param x the x
     * @return the shape
     */
    private Shape createBlackKey(final float x) {
        return new Rectangle2D.Float(x, 0, whiteKeyWidth * 14f / 24, whiteKeyHeight * BLACK_KEY_HEIGHT);
    }

    /**
     * Creates the white key.
     * @param x the x
     * @param cutLeft the cut left
     * @param cutRight the cut right
     * @return the shape
     */
    private Shape createWhiteKey(final float x, final float cutLeft, final float cutRight) {
        final float width = whiteKeyWidth;
        final float height = whiteKeyHeight;
        final Path2D.Float path = new Path2D.Float();

        path.moveTo(x + cutLeft * width, 0);
        path.lineTo(x + width - width * cutRight, 0);

        if (cutRight != 0) {
            path.lineTo(x + width - width * cutRight, height * BLACK_KEY_HEIGHT);
            path.lineTo(x + width, height * BLACK_KEY_HEIGHT);
        }

        final float bevel = 0.15f;

        path.lineTo(x + width, height - width * bevel - 1);

        if (bevel != 0) {
            path.quadTo(x + width, height, x + width * (1 - bevel), height - 1);
        }

        path.lineTo(x + width * bevel, height - 1);

        if (bevel != 0) {
            path.quadTo(x, height, x, height - width * bevel - 1);
        }

        if (cutLeft != 0) {
            path.lineTo(x, height * BLACK_KEY_HEIGHT);
            path.lineTo(x + width * cutLeft, height * BLACK_KEY_HEIGHT);
        }

        path.closePath();

        return path;
    }

    /**
     * Generate key shapes.
     * @return the list
     */
    @SuppressWarnings("incomplete-switch")
    private List<KeyShape> generateKeyShapes() {
        final List<KeyShape> shapes = new ArrayList<>();
        int x = 0;
        NoteEnum note = firstNote;

        for (int w = 0; w < whiteKeyCount; w++) {
            float cutLeft = 0;
            float cutRight = 0;

            switch (note) { // NOSONAR Alteration no used here
                case DO:
                    cutLeft = 0 / 24f;
                    cutRight = 9 / 24f;

                    break;
                case RE:
                    cutLeft = 5 / 24f;
                    cutRight = 5 / 24f;

                    break;
                case MI:
                    cutLeft = 9 / 24f;

                    break;
                case FA:
                    cutRight = 11 / 24f;

                    break;
                case SOL:
                    cutLeft = 3 / 24f;
                    cutRight = 7 / 24f;

                    break;
                case LA:
                    cutLeft = 7 / 24f;
                    cutRight = 3 / 24f;

                    break;
                case SI:
                    cutLeft = 11 / 24f;
                    cutRight = 0 / 24f;

                    break;
            }

            if (w == 0) {
                cutLeft = 0;
            }

            if (w == whiteKeyCount - 1) {
                cutRight = 0;
            }

            shapes.add(new KeyShape(createWhiteKey(x, cutLeft, cutRight), true));

            if (cutRight != 0) {
                shapes.add(new KeyShape(createBlackKey(x + whiteKeyWidth - whiteKeyWidth * cutRight), false));
            }

            x += whiteKeyWidth;

            note = note.next(false);
        }

        return Collections.unmodifiableList(shapes);
    }

    /**
     * Gets the key shapes.
     * @return the key shapes
     */
    private List<KeyShape> getKeyShapes() {
        if (keyShapes == null) {
            keyShapes = generateKeyShapes();
        }

        return keyShapes;
    }
}
