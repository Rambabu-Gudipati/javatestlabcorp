package com.employeetest.model;

/**
 * A manager – also a salaried employee – who accumulates <strong>30 vacation days</strong>
 * per 260-day work year.
 *
 * <p>Accrual rate: 30 / 260 ≈ 0.11538 vacation days per day worked.
 *
 * <p>Managers are a specialization of {@link SalariedEmployee}; they share all salaried
 * business rules but enjoy an elevated vacation entitlement.
 */
public class Manager extends SalariedEmployee {

    /** Vacation days entitled for a full work year (overrides salaried value). */
    public static final int VACATION_DAYS_PER_YEAR = 30;

    /** No-arg constructor required by Jackson for JSON deserialization. */
    protected Manager() {
        super();
    }

    public Manager(long id, String name) {
        super(id, name);
    }

    /**
     * Overrides the salaried value to return 30 vacation days per work year.
     */
    @Override
    public int getVacationDaysPerYear() {
        return VACATION_DAYS_PER_YEAR;
    }

    @Override
    public String getEmployeeType() {
        return "MANAGER";
    }
}
