package org.infodavid.util.swing.component;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicCheckBoxUI;

/**
 * The Class CustomCheckBoxUI.
 */
class CustomCheckBoxUI extends BasicCheckBoxUI implements PropertyChangeListener, ComponentListener, MouseListener { // NOSONAR Inheritance

    /** The Constant BACKGROUND_COLORS. */
    private static final Color[] BACKGROUND_COLORS = {
            new Color(91, 91, 91, 255), new Color(138, 138, 138, 255), new Color(124, 124, 124, 255)
    };

    /** The Constant BACKGROUND_FRACTIONS. */
    private static final float[] BACKGROUND_FRACTIONS = {
            0.0f, 0.96f, 1.0f
    };

    /** The Constant BACKGROUND_FRAME_COLORS. */
    private static final Color[] BACKGROUND_FRAME_COLORS = {
            new Color(68, 68, 68, 255), new Color(105, 105, 106, 255), new Color(216, 217, 218, 255)
    };

    /** The Constant BACKGROUND_FRAME_FRACTIONS. */
    private static final float[] BACKGROUND_FRAME_FRACTIONS = {
            0.0f, 0.51f, 1.0f
    };

    /** The Constant FOREGROUND_COLORS. */
    private static final Color[] FOREGROUND_COLORS = {
            new Color(241, 242, 242, 255), new Color(224, 225, 226, 255), new Color(166, 169, 171, 255), new Color(124, 124, 124, 255)
    };

    /** The Constant FOREGROUND_FRACTIONS. */
    private static final float[] FOREGROUND_FRACTIONS = {
            0.0f, 0.03f, 0.94f, 1.0f
    };

    /** The Constant FRACTIONS. */
    private static final float[] FRACTIONS = {
            0.0f, 40.0f, 90.0f, 140.0f, 220.0f, 270.0f, 320.0f
    };

    /** The Constant FRAME_COLORS. */
    private static final Color[] FRAME_COLORS = {
            new Color(90, 91, 92, 255), new Color(127, 127, 128, 255), new Color(81, 82, 83, 255), new Color(104, 105, 105, 255), new Color(63, 64, 65, 255)
    };

    /** The Constant FRAME_FRACTIONS. */
    private static final float[] FRAME_FRACTIONS = {
            0.0f, 0.25f, 0.51f, 0.76f, 1.0f
    };

    /** The Constant PRESSED_COLORS. */
    private static final Color[] PRESSED_COLORS = new Color[] {
            new Color(0xC2C2C2), new Color(0x727678), new Color(0xC2C2C2), new Color(0x727678), new Color(0xC2C2C2), new Color(0x727678), new Color(0xC2C2C2)
    };

    /** The Constant RELEASED_COLORS. */
    private static final Color[] RELEASED_COLORS = new Color[] {
            new Color(0xF2F2F2), new Color(0x8F9396), new Color(0xF2F2F2), new Color(0x8F9396), new Color(0xF2F2F2), new Color(0x8F9396), new Color(0xF2F2F2)
    };

    /** The Constant SIZE. */
    private static final Dimension SIZE = new Dimension(26, 13);

    /**
     * Gets the centered text position.
     * @param g2d the graphics 2D
     * @param boundary the boundary
     * @param font the font
     * @param text the text
     * @param orientation the orientation
     * @return the centered text position
     */
    public static Point2D getCenteredTextPosition(final Graphics2D g2d, final Rectangle2D boundary, final Font font, final String text, final int orientation) {
        final double centerX = boundary.getWidth() / 2.0;
        final double centerY = boundary.getHeight() / 2.0;
        final Rectangle2D textBoundary = new TextLayout(text, font, g2d.getFontRenderContext()).getBounds();
        final double x;
        final double y;

        switch (orientation) {
            case javax.swing.SwingConstants.LEFT:
                x = boundary.getMinX();
                y = centerY - textBoundary.getHeight() / 2.0 + textBoundary.getHeight();

                break;

            case javax.swing.SwingConstants.RIGHT:
                x = boundary.getMaxX() - textBoundary.getWidth();
                y = centerY - textBoundary.getHeight() / 2.0 + textBoundary.getHeight();

                break;

            default:
                x = centerX - textBoundary.getWidth() / 2.0;
                y = centerY - textBoundary.getHeight() / 2.0 + textBoundary.getHeight();

                break;
        }

        return new Point2D.Double(x, y);
    }

    /**
     * Creates the background image.
     * @param width the width
     * @return the buffered image
     */
    private static BufferedImage createBackgroundImage(final int width) {
        if (width <= 0) {
            return null;
        }

        final BufferedImage result = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, (int)(0.5384615384615384 * width), Transparency.TRANSLUCENT);
        final Graphics2D g2d = result.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        final int w = result.getWidth();
        final int h = result.getHeight();
        final RoundRectangle2D frame = new RoundRectangle2D.Double(w * 0.0, h * 0.0, w * 1.0, h * 1.0, h, h);
        final LinearGradientPaint frameGradient = new LinearGradientPaint(new Point2D.Double(0, frame.getBounds2D().getMinY()), new Point2D.Double(0, frame.getBounds2D().getMaxY()), BACKGROUND_FRAME_FRACTIONS, BACKGROUND_FRAME_COLORS);

        g2d.setPaint(frameGradient);
        g2d.fill(frame);

        final RoundRectangle2D background = new RoundRectangle2D.Double(w * 0.03846153989434242, h * 0.0714285746216774, w * 0.923076868057251, h * 0.8571428060531616, h, h);
        final Point2D start = new Point2D.Double(0, background.getBounds2D().getMinY());
        final Point2D stop = new Point2D.Double(0, background.getBounds2D().getMaxY());

        if (start.distance(stop) > 0) {
            g2d.setPaint(new LinearGradientPaint(start, stop, BACKGROUND_FRACTIONS, BACKGROUND_COLORS));
            g2d.fill(background);
        }

        g2d.dispose();

        return result;
    }

    /**
     * Creates the image.
     * @param width the width
     * @param pressed the pressed
     * @return the buffered image
     */
    private static BufferedImage createImage(final int width, final boolean pressed) {
        if (width <= 0) {
            return null;
        }

        final BufferedImage result = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, width, Transparency.TRANSLUCENT);
        final Graphics2D g2d = result.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int w = result.getWidth();
        final int h = result.getHeight();
        final Ellipse2D frame = new Ellipse2D.Double(w * 0.0, h * 0.0, w * 1.0, h * 1.0);
        final Point2D start = new Point2D.Double(0, frame.getBounds2D().getMinY());
        final Point2D stop = new Point2D.Double(0, frame.getBounds2D().getMaxY());

        if (start.distance(stop) > 0) {
            g2d.setPaint(new LinearGradientPaint(start, stop, FRAME_FRACTIONS, FRAME_COLORS));
            g2d.fill(frame);
        }

        final Ellipse2D ellipse = new Ellipse2D.Double(w * 0.07692307978868484, h * 0.07692307978868484, w * 0.8461538553237915, h * 0.8461538553237915);
        final Color[] colors;

        if (pressed) {
            colors = PRESSED_COLORS;
        }
        else {
            colors = RELEASED_COLORS;
        }

        g2d.setPaint(new ConicalGradientPaint(true, new Point2D.Double(ellipse.getCenterX(), ellipse.getCenterY()), 0f, FRACTIONS, colors));
        g2d.fill(ellipse);
        g2d.dispose();

        return result;
    }

    /** The background image. */
    private final BufferedImage backgroundImage = createBackgroundImage(SIZE.width);

    /** The component. */
    private final CustomCheckBox component;

    /** The foreground. */
    private RoundRectangle2D foreground;

    /** The foreground colors. */
    private Color[] foregroundColors = FOREGROUND_COLORS;

    /** The foreground gradient. */
    private LinearGradientPaint foregroundGradient;

    /** The foreground start. */
    private Point2D foregroundStart;

    /** The foreground stop. */
    private Point2D foregroundStop;

    /** The knob pressed image. */
    private final BufferedImage knobPressedImage = createImage(SIZE.height, true);

    /** The knob standard image. */
    private final BufferedImage knobStandardImage = createImage(SIZE.height, false);

    /** The mouse over. */
    private final AtomicBoolean mouseOver = new AtomicBoolean(false);

    /** The mouse pressed. */
    private final AtomicBoolean mousePressed = new AtomicBoolean(false);

    /** The position. */
    private final Point position = new Point(0, 0);

    /**
     * Instantiates a new custom check box UI.
     * @param component the component
     */
    public CustomCheckBoxUI(final CustomCheckBox component) {
        super();

        this.component = component;
        this.component.addComponentListener(this);
        this.component.addMouseListener(this);
        this.component.addPropertyChangeListener(this);

        init();
    }

    /*
     * (non-javadoc)
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentHidden(final ComponentEvent event) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentMoved(final ComponentEvent event) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentResized(final ComponentEvent event) {
        init();
        position.setLocation(0, (event.getComponent().getHeight() - SIZE.height) / 2.0);
    }

    /*
     * (non-javadoc)
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentShown(final ComponentEvent event) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.swing.plaf.basic.BasicButtonUI#installUI(javax.swing.JComponent)
     */
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);

        this.component.addComponentListener(this);
    }

    /*
     * (non-javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(final MouseEvent event) {
        mousePressed.set(false);

        component.repaint();
    }

    /*
     * (non-javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(final MouseEvent event) {
        mouseOver.set(true);

        component.repaint();
    }

    /*
     * (non-javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(final MouseEvent event) {
        mouseOver.set(false);

        component.repaint();
    }

    /*
     * (non-javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(final MouseEvent event) {
        mousePressed.set(true);

        component.repaint();
    }

    /*
     * (non-javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(final MouseEvent event) {
        mouseClicked(event);
    }

    /*
     * (non-javadoc)
     * @see javax.swing.plaf.basic.BasicRadioButtonUI#paint(java.awt.Graphics, javax.swing.JComponent)
     */
    @Override
    public synchronized void paint(final Graphics graphics, final JComponent component) {
        final Graphics2D g2d = (Graphics2D)graphics.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        position.setLocation(0, (this.component.getPreferredSize().height - SIZE.height) / 2.0);

        if (!this.component.isEnabled()) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        // Draw the background
        g2d.drawImage(backgroundImage, position.x, position.y, null);

        // Draw the foreground and knob
        if (this.component.isSelected()) {
            if (this.component.isColored()) {
                if (this.component.isRised()) {
                    foregroundColors = new Color[] {
                            this.component.getSelectedColor().getLight().brighter(), this.component.getSelectedColor().getLight(), this.component.getSelectedColor().getMedium(), this.component.getSelectedColor().getDark()
                    };
                }
                else {
                    foregroundColors = new Color[] {
                            this.component.getSelectedColor().getDark(), this.component.getSelectedColor().getDark(), this.component.getSelectedColor().getLight(), this.component.getSelectedColor().getMedium()
                    };
                }
            }
            else {
                foregroundColors = FOREGROUND_COLORS;
            }

            foregroundGradient = new LinearGradientPaint(foregroundStart, foregroundStop, FOREGROUND_FRACTIONS, foregroundColors);

            g2d.setPaint(foregroundGradient);
            g2d.fill(foreground);

            if (mouseOver.get() && mousePressed.get()) {
                g2d.drawImage(knobPressedImage, position.x + backgroundImage.getWidth() / 2, position.y, null);
            }
            else {
                g2d.drawImage(knobStandardImage, position.x + backgroundImage.getWidth() / 2, position.y, null);
            }
        }
        else if (mouseOver.get() && mousePressed.get()) {
            g2d.drawImage(knobPressedImage, position.x, position.y, null);
        }
        else {
            g2d.drawImage(knobStandardImage, position.x, position.y, null);
        }

        g2d.setColor(this.component.getForeground());
        g2d.setFont(this.component.getFont());

        final Rectangle2D bounds = new TextLayout(this.component.getText(), g2d.getFont(), new FontRenderContext(null, true, true)).getBounds();

        g2d.drawString(this.component.getText(), backgroundImage.getWidth() + 5, (this.component.getBounds().height - bounds.getBounds().height) / 2 + bounds.getBounds().height);
        g2d.dispose();
    }

    /*
     * (non-javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        init();
        component.repaint();
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CustomCheckBoxUI";
    }

    /*
     * (non-javadoc)
     * @see javax.swing.plaf.basic.BasicButtonUI#uninstallUI(javax.swing.JComponent)
     */
    @Override
    public void uninstallUI(final JComponent component) {
        super.uninstallUI(component);

        component.removeComponentListener(this);
    }

    /**
     * Inits the.
     */
    private void init() {
        foreground = new RoundRectangle2D.Double(position.x + backgroundImage.getWidth() * 0.03846153989434242, position.y + backgroundImage.getHeight() * 0.0714285746216774, backgroundImage.getWidth() * 0.923076868057251, backgroundImage.getHeight() * 0.8571428060531616, backgroundImage.getHeight() * 0.8571428571, backgroundImage.getHeight() * 0.8571428571);
        foregroundStart = new Point2D.Double(position.x, foreground.getBounds2D().getMinY());
        foregroundStop = new Point2D.Double(position.x, foreground.getBounds2D().getMaxY());
        foregroundGradient = new LinearGradientPaint(foregroundStart, foregroundStop, FOREGROUND_FRACTIONS, foregroundColors);
    }
}
