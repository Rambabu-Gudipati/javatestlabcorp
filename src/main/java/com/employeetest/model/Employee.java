package com.employeetest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract base class representing a generic Employee.
 *
 * <p>
 * Business rules enforced here:
 * <p>
 * Subclasses must implement {@link #getVacationDaysPerYear()} to declare how
 * many
 * vacation days they accumulate across the full 260-day work year.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "employeeType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HourlyEmployee.class, name = "HOURLY"),
        @JsonSubTypes.Type(value = SalariedEmployee.class, name = "SALARIED"),
        @JsonSubTypes.Type(value = Manager.class, name = "MANAGER")
})
public abstract class Employee {

    /** Total number of workdays in a standard work year. */
    public static final int WORK_YEAR_DAYS = 260;

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    @JsonProperty("id")
    private long id;
    @JsonProperty("name")
    private String name;

    /**
     * Cumulative days worked in the current work year.
     * Capped at {@value #WORK_YEAR_DAYS}.
     */
    @JsonProperty("totalDaysWorked")
    private int totalDaysWorked;

    /**
     * Vacation days accumulated so far.
     * Private to prevent external mutation; exposed through a read-only getter.
     */
    @JsonProperty("vacationDaysAccumulated")
    private double vacationDaysAccumulated;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates an Employee with the given id and name.
     * Vacation days and days-worked are both initialised to 0.
     *
     * @param id   unique identifier
     * @param name employee display name
     */
    /** No-arg constructor required by Jackson for JSON deserialization. */
    protected Employee() {
        this.id = 0L;
        this.name = "";
        this.totalDaysWorked = 0;
        this.vacationDaysAccumulated = 0.0;
    }

    protected Employee(long id, String name) {
        this.id = id;
        this.name = name;
        this.totalDaysWorked = 0;
        this.vacationDaysAccumulated = 0.0;
    }

    // -------------------------------------------------------------------------
    // Abstract
    // -------------------------------------------------------------------------

    /**
     * Returns the total number of vacation days this employee type accumulates
     * across the full {@value #WORK_YEAR_DAYS}-day work year.
     *
     * @return vacation days entitled for a full work year
     */
    public abstract int getVacationDaysPerYear();

    /**
     * Returns the concrete employee type label (HOURLY / SALARIED / MANAGER).
     */
    public abstract String getEmployeeType();

    // -------------------------------------------------------------------------
    // Business methods
    // -------------------------------------------------------------------------

    /**
     * Records the number of days the employee worked and accrues vacation
     * days proportionally.
     *
     * <p>
     * Formula:
     * {@code vacationAccrued = daysWorked * (vacationDaysPerYear / WORK_YEAR_DAYS)}
     *
     * @param days number of days worked; must be 0–260 and must not push
     *             cumulative days worked beyond {@value #WORK_YEAR_DAYS}
     * @throws IllegalArgumentException if {@code days} is out of range or
     *                                  would exceed the annual work-day limit
     */
    public void work(int days) {
        if (days < 0 || days > WORK_YEAR_DAYS) {
            throw new IllegalArgumentException(
                    "Days worked must be between 0 and " + WORK_YEAR_DAYS + ". Provided: " + days);
        }
        if (totalDaysWorked + days > WORK_YEAR_DAYS) {
            int remaining = WORK_YEAR_DAYS - totalDaysWorked;
            throw new IllegalArgumentException(
                    "Employee has already worked " + totalDaysWorked + " days. "
                            + "Only " + remaining + " workday(s) remain in this work year.");
        }

        double accrualRate = (double) getVacationDaysPerYear() / WORK_YEAR_DAYS;
        vacationDaysAccumulated += days * accrualRate;
        totalDaysWorked += days;
    }

    /**
     * Deducts the specified number of vacation days from the accumulated balance.
     *
     * @param days number of vacation days to take; must be ≥ 0 and ≤ accumulated
     *             balance
     * @throws IllegalArgumentException if {@code days} is negative or exceeds the
     *                                  balance
     */
    public void takeVacation(double days) {
        if (days < 0) {
            throw new IllegalArgumentException(
                    "Vacation days to take cannot be negative. Provided: " + days);
        }
        if (days > vacationDaysAccumulated) {
            throw new IllegalArgumentException(
                    String.format("Cannot take %.2f vacation day(s). Only %.2f day(s) available.",
                            days, vacationDaysAccumulated));
        }
        vacationDaysAccumulated -= days;
    }

    // -------------------------------------------------------------------------
    // Getters (no public setter for vacationDaysAccumulated)
    // -------------------------------------------------------------------------

    public long getId() {
        return id;
    }

    /** Setter used by Jackson during deserialization only. */
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    /** Setter used by Jackson during deserialization only. */
    public void setName(String name) {
        this.name = name;
    }

    public int getTotalDaysWorked() {
        return totalDaysWorked;
    }

    /** Setter used by Jackson during deserialization only. */
    public void setTotalDaysWorked(int totalDaysWorked) {
        this.totalDaysWorked = totalDaysWorked;
    }

    /** Vacation days balance. */
    public double getVacationDaysAccumulated() {
        return vacationDaysAccumulated;
    }

    /** Setter used by Jackson during deserialization only. */
    public void setVacationDaysAccumulated(double vacationDaysAccumulated) {
        this.vacationDaysAccumulated = vacationDaysAccumulated;
    }

    /** Remaining workdays this employee can still log in the current work year. */
    public int getRemainingWorkdays() {
        return WORK_YEAR_DAYS - totalDaysWorked;
    }

    @Override
    public String toString() {
        return String.format("%s{id=%d, name='%s', daysWorked=%d, vacationAccumulated=%.2f}",
                getEmployeeType(), id, name, totalDaysWorked, vacationDaysAccumulated);
    }
}
