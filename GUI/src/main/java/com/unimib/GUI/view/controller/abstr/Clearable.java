package com.unimib.GUI.view.controller.abstr;

/**
 * Contract for UI controllers that can be reset to their default state.
 * <p>
 * Implementations should restore both their internal model state and the
 * visible UI fields to whatever they would show right after initialization
 * (e.g. "now" for a date/time picker), without requiring re-instantiation
 * of the component.
 */
public interface Clearable {

    /**
     * Resets this component to its default state.
     * Safe to call multiple times.
     */
    void clear();
}