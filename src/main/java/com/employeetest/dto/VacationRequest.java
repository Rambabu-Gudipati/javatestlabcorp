package com.employeetest.dto;

import jakarta.validation.constraints.DecimalMin;

/**
 * Request body for the {@code POST /api/employees/{id}/vacation} endpoint.
 */
public class VacationRequest {

    @DecimalMin(value = "0.0", inclusive = true, message = "Vacation days cannot be negative.")
    private double days;

    public VacationRequest() {}

    public VacationRequest(double days) {
        this.days = days;
    }

    public double getDays() {
        return days;
    }

    public void setDays(double days) {
        this.days = days;
    }
}
