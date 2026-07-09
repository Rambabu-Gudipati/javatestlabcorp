package com.employeetest.model;

/**
 * A salaried employee who accumulates <strong>15 vacation days</strong> per 260-day work year.
 *
 * <p>Accrual rate: 15 / 260 ≈ 0.05769 vacation days per day worked.
 *
 * <p>{@link Manager} extends this class and overrides the vacation entitlement.
 */
public class SalariedEmployee extends Employee {

    /** Vacation days entitled for a full work year. */
    public static final int VACATION_DAYS_PER_YEAR = 15;

    /** No-arg constructor required by Jackson for JSON deserialization. */
    protected SalariedEmployee() {
        super();
    }

    public SalariedEmployee(long id, String name) {
        super(id, name);
    }

    @Override
    public int getVacationDaysPerYear() {
        return VACATION_DAYS_PER_YEAR;
    }

    @Override
    public String getEmployeeType() {
        return "SALARIED";
    }
}
