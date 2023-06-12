/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.extjfx.chart.plugins;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;

/**
 * Plugin indicating a value range as a rectangle drawn on the plot area, with an optional {@link #textProperty() text
 * label} describing the range.
 * 
 * @param <X> type of X values
 * @param <Y> type of Y values
 * @author mhrabia
 */
public abstract class AbstractRangeValueIndicator<X, Y> extends AbstractValueIndicator<X, Y> {
    static final String STYLE_CLASS_LABEL = "range-indicator-label";
    static final String STYLE_CLASS_RECT = "range-indicator-rect";

    /**
     * Rectangle indicating the range.
     */
    protected final Rectangle rectangle = new Rectangle(0, 0, 0, 0);

    /**
     * Creates a new instance of the indicator.
     * 
     * @param lowerBound lower bound (min value) of the range
     * @param upperBound upper bound (max value) of the range
     * @param text the text to be shown by the label. Value of {@link #textProperty()}.
     */
    protected AbstractRangeValueIndicator(double lowerBound, double upperBound, String text) {
        super(text);
        setLowerBound(lowerBound);
        setUpperBound(upperBound);

        rectangle.setMouseTransparent(true);
        getChartChildren().addAll(rectangle, label);
    }

    private final DoubleProperty lowerBound = new SimpleDoubleProperty(this, "lowerBound") {
        @Override
        protected void invalidated() {
            layoutChildren();
        }
    };

    /**
     * Lower bound (min value) of the range to be indicated.
     * 
     * @return lowerBound property
     */
    public final DoubleProperty lowerBoundProperty() {
        return lowerBound;
    }

    /**
     * Returns the value of the {@link #lowerBoundProperty()}.
     * 
     * @return lower bound of the range to be indicated
     */
    public final double getLowerBound() {
        return lowerBoundProperty().get();
    }

    /**
     * Sets the value of the {@link #lowerBoundProperty()}
     * 
     * @param value the value for the lower bound of the indicator
     */
    public final void setLowerBound(double value) {
        lowerBoundProperty().set(value);
    }

    private final DoubleProperty upperBound = new SimpleDoubleProperty(this, "upperBound") {
        @Override
        protected void invalidated() {
            layoutChildren();
        }
    };

    /**
     * Upper bound (max value) of the range to be indicated.
     * 
     * @return upperBound property
     */
    public final DoubleProperty upperBoundProperty() {
        return upperBound;
    }

    /**
     * Returns the value of the {@link #upperBoundProperty()}.
     * 
     * @return upper bound (max value) of the range to be indicated
     */
    public final double getUpperBound() {
        return upperBoundProperty().get();
    }

    /**
     * Sets the value of {@link #upperBoundProperty()}
     * 
     * @param value upper bound (max value) of the range to be indicated
     */
    public final void setUpperBound(double value) {
        upperBoundProperty().set(value);
    }

    private final DoubleProperty labelHorizontalPosition = new SimpleDoubleProperty(this, "labelHorizontalPosition",
            0.5) {
        @Override
        protected void invalidated() {
            if (get() < 0 || get() > 1) {
                throw new IllegalArgumentException("labelHorizontalPosition must be in rage [0,1]");
            }
            layoutChildren();
        }
    };

    /**
     * Relative horizontal position of the {@link #textProperty() text label} on the plot area, with value between 0.0
     * (left) and 1.0 (right). Value 0.5 will position the label in the middle of the plot area.
     * 
     * @return labelHorizontalPosition property
     */
    public final DoubleProperty labelHorizontalPositionProperty() {
        return labelHorizontalPosition;
    }

    /**
     * Returns the value of the {@link #labelHorizontalPositionProperty()}.
     * 
     * @return relative horizontal position of the {@link #textProperty() text label}
     */
    public final double getLabelHorizontalPosition() {
        return labelHorizontalPositionProperty().get();
    }

    /**
     * Sets the new value of the {@link #labelHorizontalPositionProperty()}.
     * 
     * @param value the new horizontal position, between 0.0 and 1.0 (both inclusive)
     */
    public final void setLabelHorizontalPosition(double value) {
        labelHorizontalPositionProperty().set(value);
    }

    private final DoubleProperty labelVerticalPosition = new SimpleDoubleProperty(this, "labelVerticalPosition", 0.5) {
        @Override
        protected void invalidated() {
            if (get() < 0 || get() > 1) {
                throw new IllegalArgumentException("labelVerticalPosition must be in rage [0,1]");
            }
            layoutChildren();
        }
    };

    /**
     * Relative vertical position of the {@link #textProperty() text label} on the plot area, with value between 0.0
     * (bottom) and 1.0 (top). Value 0.5 will position the label in the middle of the plot area.
     * 
     * @return labelVerticalPosition property
     */
    public final DoubleProperty labelVerticalPositionProperty() {
        return labelVerticalPosition;
    }

    /**
     * Returns the value of the {@link #labelVerticalPositionProperty()}.
     * 
     * @return relative vertical position of the {@link #textProperty() text label}
     */
    public final double getLabelVerticalPosition() {
        return labelVerticalPositionProperty().get();
    }

    /**
     * Sets the new value of the {@link #labelVerticalPositionProperty()}.
     * 
     * @param value the new vertical position, between 0.0 and 1.0 (both inclusive)
     */
    public final void setLabelVerticalPosition(double value) {
        labelVerticalPositionProperty().set(value);
    }

    /**
     * Layouts the rectangle and label within given bounds.
     * 
     * @param bounds to be applied
     */
    protected void layout(Bounds bounds) {
        if (bounds.intersects(getChartPane().getPlotAreaBounds())) {
            rectangle.setX(bounds.getMinX());
            rectangle.setY(bounds.getMinY());
            rectangle.setWidth(bounds.getWidth());
            rectangle.setHeight(bounds.getHeight());
            addChildNodeIfNotPresent(rectangle);

            layoutLabel(bounds, getLabelHorizontalPosition(), getLabelVerticalPosition());
        } else {
            getChartChildren().clear();
        }
    }
}
