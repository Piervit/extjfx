/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.extjfx.chart.plugins;

import static cern.extjfx.chart.AxisMode.XY;

import java.util.Objects;
import java.util.function.Predicate;

import cern.extjfx.chart.Axes;
import cern.extjfx.chart.AxisMode;
import cern.extjfx.chart.NumericAxis;
import cern.extjfx.chart.XYChartPlugin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.input.MouseEvent;

/**
 * Allows dragging the visible plot area along X and/or Y axis, changing the visible axis range.
 * <p>
 * Reacts on {@link MouseEvent#DRAG_DETECTED} event accepted by {@link #getMouseFilter() mouse
 * filter}.
 * <p>
 * {@code Panner} works properly only if both X and Y axis are instances of {@link NumericAxis}.
 */
public class Panner extends XYChartPlugin<Number, Number> {
    /**
     * Default pan mouse filter passing on left mouse button with {@link MouseEvent#isControlDown()
     * control key down}.
     */
    public static final Predicate<MouseEvent> DEFAULT_MOUSE_FILTER = (event) -> {
        return MouseEvents.isOnlyPrimaryButtonDown(event) && MouseEvents.isOnlyCtrlModifierDown(event);
    };

    private Predicate<MouseEvent> mouseFilter = Panner.DEFAULT_MOUSE_FILTER;
    private Point2D previousMouseLocation = null;

    /**
     * Creates a new instance of Panner class with {@link AxisMode#XY XY} {@link #axisModeProperty()
     * axisMode}.
     */
    public Panner() {
        this(AxisMode.XY);
    }

    /**
     * Creates a new instance of Panner class.
     *
     * @param panMode
     *        initial value for the {@link #axisModeProperty() axisMode} property
     */
    public Panner(final AxisMode panMode) {
        this.setAxisMode(panMode);
        this.setDragCursor(Cursor.CLOSED_HAND);
        this.registerMouseHandlers();
    }

    private void registerMouseHandlers() {
        this.registerMouseEventHandler(MouseEvent.MOUSE_PRESSED, this.panStartHandler);
        this.registerMouseEventHandler(MouseEvent.MOUSE_DRAGGED, this.panDragHandler);
        this.registerMouseEventHandler(MouseEvent.MOUSE_RELEASED, this.panEndHandler);
    }

    /**
     * Returns MouseEvent filter triggering pan operation.
     *
     * @return filter used to test whether given MouseEvent should start panning operation
     * @see #setMouseFilter(Predicate)
     */
    public Predicate<MouseEvent> getMouseFilter() {
        return this.mouseFilter;
    }

    /**
     * Sets the filter determining whether given MouseEvent triggered on
     * {@link MouseEvent#DRAG_DETECTED event type} should start the panning operation.
     * <p>
     * By default it is initialized to {@link #DEFAULT_MOUSE_FILTER}.
     *
     * @param mouseFilter
     *        the mouse filter to be used. Can be set to {@code null} to start panning on any
     *        {@link MouseEvent#DRAG_DETECTED DRAG_DETECTED} event.
     */
    public void setMouseFilter(final Predicate<MouseEvent> mouseFilter) {
        this.mouseFilter = mouseFilter;
    }

    private final ObjectProperty<AxisMode> axisMode = new SimpleObjectProperty<AxisMode>(this, "axisMode", XY) {
        @Override
        protected void invalidated() {
            Objects.requireNonNull(this.get(), "The " + this.getName() + " must not be null");
        }
    };

    /**
     * The mode defining axis along which the pan operation is allowed. By default initialized to
     * {@link AxisMode#XY}.
     *
     * @return the axis mode property
     */
    public final ObjectProperty<AxisMode> axisModeProperty() {
        return this.axisMode;
    }

    /**
     * Sets the value of the {@link #axisModeProperty()}.
     *
     * @param mode
     *        the mode to be used
     */
    public final void setAxisMode(final AxisMode mode) {
        this.axisModeProperty().set(mode);
    }

    /**
     * Returns the value of the {@link #axisModeProperty()}.
     *
     * @return current mode
     */
    public final AxisMode getAxisMode() {
        return this.axisModeProperty().get();
    }

    private Cursor originalCursor;
    private final ObjectProperty<Cursor> dragCursor = new SimpleObjectProperty<>(this, "dragCursor");

    /**
     * Mouse cursor to be used during drag operation.
     *
     * @return the mouse cursor property
     */
    public final ObjectProperty<Cursor> dragCursorProperty() {
        return this.dragCursor;
    }

    /**
     * Sets value of the {@link #dragCursorProperty()}.
     *
     * @param cursor
     *        the cursor to be used by the plugin
     */
    public final void setDragCursor(final Cursor cursor) {
        this.dragCursorProperty().set(cursor);
    }

    /**
     * Returns the value of the {@link #dragCursorProperty()}
     *
     * @return the current cursor
     */
    public final Cursor getDragCursor() {
        return this.dragCursorProperty().get();
    }

    private void installCursor() {
        this.originalCursor = this.getChartPane().getCursor();
        if (this.getDragCursor() != null) {
            this.getChartPane().setCursor(this.getDragCursor());
        }
    }

    private void uninstallCursor() {
        this.getChartPane().setCursor(this.originalCursor);
    }

    private final EventHandler<MouseEvent> panStartHandler = (event) -> {
        if (this.mouseFilter == null || this.mouseFilter.test(event)) {
            this.panStarted(event);
            event.consume();
        }
    };

    private final EventHandler<MouseEvent> panDragHandler = (event) -> {
        if (this.panOngoing()) {
            this.panDragged(event);
            event.consume();
        }
    };

    private final EventHandler<MouseEvent> panEndHandler = (event) -> {
        if (this.panOngoing()) {
            this.panEnded();
            event.consume();
        }
    };

    private boolean panOngoing() {
        return this.previousMouseLocation != null;
    }

    private void panStarted(final MouseEvent event) {
        this.previousMouseLocation = this.getLocationInPlotArea(event);
        this.installCursor();
    }

    private void panDragged(final MouseEvent event) {
        final Point2D mouseLocation = this.getLocationInPlotArea(event);
        for (final XYChart<Number, Number> chart : this.getCharts()) {
            this.panChart(chart, mouseLocation);
        }
        this.previousMouseLocation = mouseLocation;
    }

    private void panChart(final XYChart<Number, Number> chart, final Point2D mouseLocation) {
        final Data<Number, Number> prevData = this.toDataPoint(chart.getYAxis(), this.previousMouseLocation);
        final Data<Number, Number> data = this.toDataPoint(chart.getYAxis(), mouseLocation);

        final double xOffset = prevData.getXValue().doubleValue() - data.getXValue().doubleValue();
        final double yOffset = prevData.getYValue().doubleValue() - data.getYValue().doubleValue();

        final ValueAxis<?> xAxis = Axes.toValueAxis(chart.getXAxis());
        if (!Axes.hasBoundedRange(xAxis) && this.getAxisMode().allowsX()) {
            xAxis.setAutoRanging(false);
            this.shiftBounds(xAxis, xOffset);
        }
        final ValueAxis<?> yAxis = Axes.toValueAxis(chart.getYAxis());
        if (!Axes.hasBoundedRange(yAxis) && this.getAxisMode().allowsY()) {
            yAxis.setAutoRanging(false);
            this.shiftBounds(yAxis, yOffset);
        }
    }

    /**
     * Depending if the offset is positive or negative, change first upper or lower bound to not
     * provoke lowerBound >= upperBound when offset >= upperBound - lowerBound.
     */
    private void shiftBounds(final ValueAxis<?> axis, final double offset) {
        if (offset < 0) {
            axis.setLowerBound(axis.getLowerBound() + offset);
            axis.setUpperBound(axis.getUpperBound() + offset);
        }
        else {
            axis.setUpperBound(axis.getUpperBound() + offset);
            axis.setLowerBound(axis.getLowerBound() + offset);
        }
    }

    private void panEnded() {
        this.previousMouseLocation = null;
        this.uninstallCursor();
    }
}
