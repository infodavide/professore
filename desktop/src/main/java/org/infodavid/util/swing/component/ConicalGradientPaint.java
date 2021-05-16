package org.infodavid.util.swing.component;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gs.collections.api.iterator.MutableFloatIterator;
import com.gs.collections.api.list.primitive.MutableFloatList;
import com.gs.collections.api.map.primitive.FloatObjectMap;
import com.gs.collections.api.map.primitive.MutableFloatObjectMap;
import com.gs.collections.impl.list.mutable.primitive.FloatArrayList;
import com.gs.collections.impl.map.mutable.primitive.FloatObjectHashMap;

/**
 * A paint class that creates conical gradients around a given center point.</br>
 * It could be used in the same way as LinearGradientPaint and RadialGradientPaint and follows the same syntax.</br>
 * You could use floats from 0.0 to 1.0 for the fractions which is standard but it's also possible to use angles from 0.0 to 360 degrees which is most of the times much easier to handle.</br>
 * Gradients always start at the top with a clockwise direction and you could rotate the gradient around the center by given offset.</br>
 * The offset could also be defined from -0.5 to +0.5 or -180 to +180 degrees.</br>
 * If you would like to use degrees instead of values from 0 to 1 you have to use the full constructor and set the degrees variable to true.
 */
final class ConicalGradientPaint implements Paint {

    /**
     * The Class ConicalGradientPaintContext.
     */
    private final class ConicalGradientPaintContext implements PaintContext {

        /** The center. */
        private final Point2D center;

        /**
         * Instantiates a new conical gradient paint context.
         * @param value the value
         */
        public ConicalGradientPaintContext(final Point2D value) {
            center = new Point2D.Double(value.getX(), value.getY());
        }

        /*
         * (non-javadoc)
         * @see java.awt.PaintContext#dispose()
         */
        @Override
        public void dispose() {
            // noop
        }

        /*
         * (non-javadoc)
         * @see java.awt.PaintContext#getColorModel()
         */
        @Override
        public ColorModel getColorModel() {
            return ColorModel.getRGBdefault();
        }

        /*
         * (non-javadoc)
         * @see java.awt.PaintContext#getRaster(int, int, int, int)
         */
        @Override
        public Raster getRaster(final int x, final int y, final int tileWidth, final int tileHeight) {
            final double rotationCenterX = -x + center.getX();
            final double rotationCenterY = -y + center.getY();
            final WritableRaster result = getColorModel().createCompatibleWritableRaster(tileWidth, tileHeight);
            // Create data array with place for red, green, blue and alpha values
            final int[] data = new int[tileWidth * tileHeight * 4];

            for (int py = 0; py < tileHeight; py++) {
                for (int px = 0; px < tileWidth; px++) {
                    // Calculate the distance between the current position and the rotation angle
                    final double dx = px - rotationCenterX;
                    final double dy = py - rotationCenterY;
                    final double distance = Math.sqrt(dx * dx + dy * dy);
                    double currentRed = 0;
                    double currentGreen = 0;
                    double currentBlue = 0;
                    double currentAlpha = 0;
                    double angle;

                    // Avoid division by zero
                    // 0 degree on top
                    if (distance == 0) {
                        angle = Math.abs(Math.toDegrees(Math.acos(dx / 1)));
                    }
                    else {
                        angle = Math.abs(Math.toDegrees(Math.acos(dx / distance)));
                    }

                    if (dx >= 0 && dy <= 0) {
                        angle = 90.0 - angle;
                    }
                    else if (dx >= 0 && dy >= 0) {
                        angle += 90.0;
                    }
                    else if (dx <= 0 && dy >= 0) {
                        angle += 90.0;
                    }
                    else if (dx <= 0 && dy <= 0) {
                        angle = 450.0 - angle;
                    }

                    // Check for each angle in fractionAngles array
                    for (int i = 0; i < fractionAngles.length - 1; i++) {
                        final double value = angle - fractionAngles[i];

                        if (value >= 0) {
                            currentRed = colors[i].getRed() * INT_TO_FLOAT + value * redStepLookup[i];
                            currentGreen = colors[i].getGreen() * INT_TO_FLOAT + value * greenStepLookup[i];
                            currentBlue = colors[i].getBlue() * INT_TO_FLOAT + value * blueStepLookup[i];
                            currentAlpha = colors[i].getAlpha() * INT_TO_FLOAT + value * alphaStepLookup[i];

                            break;
                        }
                    }

                    // Fill data array with calculated color values
                    final int base = (py * tileWidth + px) * 4;
                    data[base + 0] = (int)(currentRed * 255);
                    data[base + 1] = (int)(currentGreen * 255);
                    data[base + 2] = (int)(currentBlue * 255);
                    data[base + 3] = (int)(currentAlpha * 255);
                }
            }

            // Fill the raster with the data
            result.setPixels(0, 0, tileWidth, tileHeight, data);

            return result;
        }
    }

    /** The Constant INT_TO_FLOAT. */
    private static final float INT_TO_FLOAT = 1f / 255f;

    /**
     * With the source at the beginning and the target at the end of the given range the method will calculate and return the color that equals the given value.</br>
     * For example: a source of BLACK (R:0, G:0, B:0, A:255) and a target of WHITE(R:255, G:255, B:255, A:255) with a given range of 100 and a given value of 50 will return the color that is exactly in the middle of the gradient between black and white which is gray(R:128, G:128, B:128, A:255).</br>
     * So this method is really useful to calculate colors in gradients between two given colors.
     * @param source the source color
     * @param target the target color
     * @param range the range
     * @param value the value
     * @return Color calculated from a range of values by given value
     */
    public static Color getColorFromFraction(final Color source, final Color target, final int range, final int value) {
        final float redSource = source.getRed() * INT_TO_FLOAT;
        final float greenSource = source.getGreen() * INT_TO_FLOAT;
        final float blueSource = source.getBlue() * INT_TO_FLOAT;
        final float alphaSource = source.getAlpha() * INT_TO_FLOAT;

        return new Color(redSource + (target.getRed() * INT_TO_FLOAT - redSource) / range * value, greenSource + (target.getGreen() * INT_TO_FLOAT - greenSource) / range * value, blueSource + (target.getBlue() * INT_TO_FLOAT - blueSource) / range * value, alphaSource + (target.getAlpha() * INT_TO_FLOAT - alphaSource) / range * value);
    }

    /**
     * Recalculates the fractions and their associated colors in the colors with a given offset.</br>
     * Because the conical gradients always starts with 0 at the top and clockwise direction you could rotate the defined conical gradient from -180 to 180 degrees which equals values from -0.5 to +0.5.
     * @param fractions the fractions
     * @param colors the colors
     * @param offset the offset
     * @return Hashmap that contains the recalculated fractions and colors after a given rotation
     */
    private static FloatObjectMap<Color> recalculate(final MutableFloatList fractions, final List<Color> colors, final float offset) {
        // Recalculate the fractions and colors with the given offset
        final int max = fractions.size();
        final MutableFloatObjectMap<Color> results = new FloatObjectHashMap<>(max);

        for (int i = 0; i < max; i++) {
            // Add offset to fraction
            final float fraction = fractions.get(i) + offset;
            final Color color = colors.get(i);

            // Check each fraction for limits (0...1)
            if (fraction <= 0) {
                results.put(1.0f + fraction + 0.0001f, color);

                final float nextFraction;
                final Color nextColor;

                if (i < max - 1) {
                    nextFraction = fractions.get(i + 1) + offset;
                    nextColor = colors.get(i + 1);
                }
                else {
                    nextFraction = 1 - fractions.get(0) + offset;
                    nextColor = colors.get(0);
                }

                if (nextFraction > 0) {
                    final Color newColor = getColorFromFraction(color, nextColor, (int)((nextFraction - fraction) * 10000), (int)(-fraction * 10000));

                    results.put(0.0f, newColor);
                    results.put(1.0f, newColor);
                }
            }
            else if (fraction >= 1) {
                results.put(fraction - 1.0f - 0.0001f, color);

                final float previousFraction;
                final Color previousColor;

                if (i > 0) {
                    previousFraction = fractions.get(i - 1) + offset;
                    previousColor = colors.get(i - 1);
                }
                else {
                    previousFraction = fractions.get(max - 1) + offset;
                    previousColor = colors.get(max - 1);
                }

                if (previousFraction < 1) {
                    final Color newColor = getColorFromFraction(color, previousColor, (int)((fraction - previousFraction) * 10000), (int)(fraction - 1.0f) * 10000);

                    results.put(1.0f, newColor);
                    results.put(0.0f, newColor);
                }
            }
            else {
                results.put(fraction, color);
            }
        }

        // Clear the original lists
        fractions.clear();
        colors.clear();

        return results;
    }

    /** The alpha step lookup. */
    private final double[] alphaStepLookup;

    /** The blue step lookup. */
    private final double[] blueStepLookup;

    /** The center. */
    private final Point2D center;

    /** The colors. */
    private final Color[] colors;

    /** The fraction angles. */
    private final double[] fractionAngles;

    /** The green step lookup. */
    private final double[] greenStepLookup;

    /** The red step lookup. */
    private final double[] redStepLookup;

    /**
     * Enhanced constructor which takes the fractions in degress from 0.0f to 360.0f and also an offset in degrees around the rotation center.
     * @param degrees the degrees
     * @param center the center
     * @param offset the offset
     * @param fractions the fractions
     * @param colors the colors
     * @throws IllegalArgumentException the illegal argument exception
     */
    public ConicalGradientPaint(final boolean degrees, final Point2D center, final float offset, final float[] fractions, final Color[] colors) throws IllegalArgumentException {
        // Check that fractions and colors are of the same size
        if (fractions.length != colors.length) {
            throw new IllegalArgumentException("Fractions and colors must be equal in size");
        }

        final MutableFloatList fractionsList = new FloatArrayList(fractions.length);
        final float computedOffset;

        if (degrees) {
            final double fraction = 1f / 360f;

            if (Double.compare(offset * fraction, -0.5) == 0) {
                computedOffset = -0.5f;
            }
            else if (Double.compare(offset * fraction, 0.5) == 0) {
                computedOffset = 0.5f;
            }
            else {
                computedOffset = (float)(offset * fraction);
            }

            for (final float item : fractions) {
                fractionsList.add((float)(item * fraction));
            }
        }
        else {
            computedOffset = offset;

            for (final float item : fractions) {
                fractionsList.add(item);
            }
        }

        // Check for valid offset
        if (computedOffset > 0.5f || computedOffset < -0.5f) {
            throw new IllegalArgumentException("Offset has to be in the range of -0.5 to 0.5");
        }

        // Adjust fractions and colors array in the case where startvalue != 0.0f and/or endvalue != 1.0f
        final List<Color> colorsList = new ArrayList<>();

        colorsList.addAll(Arrays.asList(colors));

        // Assure that fractions start with 0.0f
        if (fractionsList.get(0) != 0.0f) {
            fractionsList.addAllAtIndex(0, 0.0f);
            colorsList.add(0, colorsList.get(0));
        }

        // Assure that fractions end with 1.0f
        if (fractionsList.get(fractionsList.size() - 1) != 1.0f) {
            fractionsList.add(1.0f);
            colorsList.add(colors[0]);
        }

        // Recalculate the fractions and colors with the given offset
        final FloatObjectMap<Color> computed = recalculate(fractionsList, colorsList, computedOffset);

        // Clear the original lists
        fractionsList.clear();
        colorsList.clear();

        // Sort the hashmap by fraction and add the values to the FRACION_LIST and COLOR_LIST
        final MutableFloatIterator ite = computed.keySet().floatIterator();

        while (ite.hasNext()) {
            final float current = ite.next();

            fractionsList.add(current);
            colorsList.add(computed.get(current));
        }

        // Set the values
        this.center = center;
        this.colors = colorsList.toArray(new Color[] {});
        // Prepare lookup table for the angles of each fraction
        fractionAngles = new double[fractionsList.size()];

        for (int i = 0; i < fractionAngles.length; i++) {
            fractionAngles[i] = fractionsList.get(i) * 360;
        }

        // Prepare lookup tables for the color stepsize of each color
        redStepLookup = new double[this.colors.length];
        greenStepLookup = new double[this.colors.length];
        blueStepLookup = new double[this.colors.length];
        alphaStepLookup = new double[this.colors.length];

        for (int i = 0; i < this.colors.length - 1; i++) {
            redStepLookup[i] = (this.colors[i + 1].getRed() - this.colors[i].getRed()) * INT_TO_FLOAT / (fractionAngles[i + 1] - fractionAngles[i]);
            greenStepLookup[i] = (this.colors[i + 1].getGreen() - this.colors[i].getGreen()) * INT_TO_FLOAT / (fractionAngles[i + 1] - fractionAngles[i]);
            blueStepLookup[i] = (this.colors[i + 1].getBlue() - this.colors[i].getBlue()) * INT_TO_FLOAT / (fractionAngles[i + 1] - fractionAngles[i]);
            alphaStepLookup[i] = (this.colors[i + 1].getAlpha() - this.colors[i].getAlpha()) * INT_TO_FLOAT / (fractionAngles[i + 1] - fractionAngles[i]);
        }
    }

    /**
     * Standard constructor which takes the fractions in values from 0.0f to 1.0f.
     * @param center the center
     * @param fractions the fractions
     * @param colors the colors
     * @throws IllegalArgumentException the illegal argument exception
     */
    public ConicalGradientPaint(final Point2D center, final float[] fractions, final Color[] colors) throws IllegalArgumentException {
        this(false, center, 0.0f, fractions, colors);
    }

    /*
     * (non-javadoc)
     * @see java.awt.Paint#createContext(java.awt.image.ColorModel, java.awt.Rectangle, java.awt.geom.Rectangle2D, java.awt.geom.AffineTransform, java.awt.RenderingHints)
     */
    @Override
    public java.awt.PaintContext createContext(final ColorModel model, final Rectangle deviceBounds, final Rectangle2D userBounds, final AffineTransform transform, final RenderingHints hints) {
        return new ConicalGradientPaintContext(transform.transform(center, null));
    }

    /*
     * (non-javadoc)
     * @see java.awt.Transparency#getTransparency()
     */
    @Override
    public int getTransparency() {
        return Transparency.TRANSLUCENT;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ConicalGradientPaint";
    }
}
