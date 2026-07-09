package com.employeetest.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Employee class hierarchy and business rules.
 */
class EmployeeTest {

    private HourlyEmployee hourly;
    private SalariedEmployee salaried;
    private Manager manager;

    @BeforeEach
    void setUp() {
        hourly = new HourlyEmployee(1L, "Alice");
        salaried = new SalariedEmployee(2L, "Bob");
        manager = new Manager(3L, "Carol");
    }

    // Vacation entitlements

    @Test
    @DisplayName("Hourly employee accumulates 10 vacation days over a full work year")
    void hourlyFullYear() {
        hourly.work(260);
        assertEquals(10.0, hourly.getVacationDaysAccumulated(), 1e-9);
    }

    @Test
    @DisplayName("Salaried employee accumulates 15 vacation days over a full work year")
    void salariedFullYear() {
        salaried.work(260);
        assertEquals(15.0, salaried.getVacationDaysAccumulated(), 1e-9);
    }

    @Test
    @DisplayName("Manager accumulates 30 vacation days over a full work year")
    void managerFullYear() {
        manager.work(260);
        assertEquals(30.0, manager.getVacationDaysAccumulated(), 1e-9);
    }

    // -------------------------------------------------------------------------
    // Initial state
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Vacation days start at 0 on creation")
    void vacationStartsAtZero() {
        assertEquals(0.0, hourly.getVacationDaysAccumulated());
        assertEquals(0.0, salaried.getVacationDaysAccumulated());
        assertEquals(0.0, manager.getVacationDaysAccumulated());
    }

    // -------------------------------------------------------------------------
    // Work() validation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Work() accumulates vacation proportionally for partial year")
    void workPartialYear() {
        hourly.work(130); // half year
        assertEquals(5.0, hourly.getVacationDaysAccumulated(), 1e-9);
    }

    @Test
    @DisplayName("Work() rejects negative days")
    void workNegativeDays() {
        assertThrows(IllegalArgumentException.class, () -> hourly.work(-1));
    }

    @Test
    @DisplayName("Work() rejects more than 260 days in a single call")
    void workExceedsYearSingleCall() {
        assertThrows(IllegalArgumentException.class, () -> hourly.work(261));
    }

    @Test
    @DisplayName("Work() rejects cumulative days exceeding 260")
    void workExceedsYearCumulative() {
        hourly.work(200);
        assertThrows(IllegalArgumentException.class, () -> hourly.work(61));
    }

    @Test
    @DisplayName("Work() with 0 days is a no-op")
    void workZeroDays() {
        hourly.work(0);
        assertEquals(0.0, hourly.getVacationDaysAccumulated());
        assertEquals(0, hourly.getTotalDaysWorked());
    }

    // -------------------------------------------------------------------------
    // TakeVacation() validation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("TakeVacation() correctly reduces the balance")
    void takeVacationReducesBalance() {
        salaried.work(260); // accumulates 15 days
        salaried.takeVacation(5.0);
        assertEquals(10.0, salaried.getVacationDaysAccumulated(), 1e-9);
    }

    @Test
    @DisplayName("TakeVacation() allows using the full accumulated balance")
    void takeVacationFullBalance() {
        manager.work(260); // accumulates 30 days
        manager.takeVacation(30.0);
        assertEquals(0.0, manager.getVacationDaysAccumulated(), 1e-9);
    }

    @Test
    @DisplayName("TakeVacation() rejects more days than accumulated")
    void takeVacationExceedsBalance() {
        hourly.work(130); // 5 days accrued
        assertThrows(IllegalArgumentException.class, () -> hourly.takeVacation(6.0));
    }

    @Test
    @DisplayName("TakeVacation() rejects negative days")
    void takeVacationNegative() {
        assertThrows(IllegalArgumentException.class, () -> hourly.takeVacation(-1.0));
    }

    @Test
    @DisplayName("Vacation balance never goes negative")
    void vacationNeverNegative() {
        hourly.work(26); // ~1 day accrued
        assertThrows(IllegalArgumentException.class, () -> hourly.takeVacation(5.0));
        assertTrue(hourly.getVacationDaysAccumulated() >= 0.0);
    }

    // -------------------------------------------------------------------------
    // Manager is-a SalariedEmployee
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Manager is an instance of SalariedEmployee")
    void managerIsSalaried() {
        assertTrue(manager instanceof SalariedEmployee);
    }

    @Test
    @DisplayName("Manager type label is MANAGER not SALARIED")
    void managerTypeLabel() {
        assertEquals("MANAGER", manager.getEmployeeType());
    }

    // -------------------------------------------------------------------------
    // Remaining workdays
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Remaining workdays decreases correctly after work()")
    void remainingWorkdays() {
        hourly.work(100);
        assertEquals(160, hourly.getRemainingWorkdays());
    }
}
