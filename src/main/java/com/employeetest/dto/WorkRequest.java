package com.employeetest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Request body for the {@code POST /api/employees/{id}/work} endpoint.
 */
public class WorkRequest {

    @Min(value = 0, message = "Days worked cannot be negative.")
    @Max(value = 260, message = "Days worked cannot exceed the work year (260 days).")
    private int days;

    public WorkRequest() {}

    public WorkRequest(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
