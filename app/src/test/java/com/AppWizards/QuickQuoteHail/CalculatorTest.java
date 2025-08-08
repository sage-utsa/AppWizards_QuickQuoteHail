package com.AppWizards.QuickQuoteHail;

import models.Calculator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the Calculator model.
 */
public class CalculatorTest {

    @Test
    public void test_standard_cost_calculation() {
        Calculator calculator = new Calculator();
        String cost = calculator.getEstimatedCost("HOOD", "D", 3, false);
        assertEquals("$125.00", cost);
    }

    @Test
    public void test_different_panel_cost() {
        Calculator calculator = new Calculator();
        String cost = calculator.getEstimatedCost("LFF", "N", 10, false);
        assertEquals("$175.00", cost);
    }

    @Test
    public void test_aluminum_multiplier() {
        Calculator calculator = new Calculator();
        String cost = calculator.getEstimatedCost("HOOD", "D", 3, true);
        assertEquals("$187.50", cost); // $125 * 1.5
    }

    @Test
    public void test_cr_for_custom_repair() {
        Calculator calculator = new Calculator();
        String cost = calculator.getEstimatedCost("HOOD", "H", 80, false);
        assertEquals("CR: Custom Repair Needed", cost);
    }

    @Test
    public void test_invalid_panel_type() {
        Calculator calculator = new Calculator();
        String cost = calculator.getEstimatedCost("FENDER", "D", 5, false);
        assertTrue(cost.startsWith("N/A"));
    }

    @Test
    public void test_invalid_dent_count() {
        Calculator calculator = new Calculator();
        String cost = calculator.getEstimatedCost("HOOD", "D", 0, false);
        assertTrue(cost.startsWith("N/A"));
    }

    @Test
    public void test_out_of_range_dent_count() {
        Calculator calculator = new Calculator();
        String cost = calculator.getEstimatedCost("HOOD", "D", 1000000, false);
        assertTrue(cost.startsWith("N/A"));
    }
}
