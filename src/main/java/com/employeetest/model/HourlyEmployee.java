package com.employeetest.model;

/**
 * An hourly employee who accumulates <strong>10 vacation days</strong> per 260-day work year.
 *
 * <p>Accrual rate: 10 / 260 ≈ 0.03846 vacation days per day worked.
 */
public class HourlyEmployee extends Employee {

    /** Vacation days entitled for a full work year. */
    public static final int VACATION_DAYS_PER_YEAR = 10;

    /** No-arg constructor required by Jackson for JSON deserialization. */
    protected HourlyEmployee() {
        super();
    }

    public HourlyEmployee(long id, String name) {
        super(id, name);
    }

    @Override
    public int getVacationDaysPerYear() {
        return VACATION_DAYS_PER_YEAR;
    }

    @Override
    public String getEmployeeType() {
        return "HOURLY";
    }
}
