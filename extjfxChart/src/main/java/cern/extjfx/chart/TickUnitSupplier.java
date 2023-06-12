/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.extjfx.chart;

/**
 * Supplier of Axis tick units that is expected to compute a tick unit that is equal or grater than the one given as
 * argument. In case the calculated tick labels don't fit on the axis, the {@link #computeTickUnit(double)} method is
 * called again with a grater reference unit so that the supplier can calculate next bigger value of the tick unit.
 * <p>
 * If the {@link #computeTickUnit(double)} returns value that is smaller than the reference unit, the value will be used
 * without further checks which may result in tick labels overlapping.
 * <p>
 * If the {@link #computeTickUnit(double)} returns value smaller than or equal to zero, IllegalArgumentException will be
 * thrown at runtime.
 * 
 * @see NumericAxis#setTickUnitSupplier(TickUnitSupplier)
 */
@FunctionalInterface
public interface TickUnitSupplier {

    /**
     * Should return tick unit that is equal or grater to the given reference tick unit.
     * 
     * @param referenceTickUnit reference tick unit
     * @return the computed unit that is equal or grater to the specified one
     */
    double computeTickUnit(double referenceTickUnit);
}
