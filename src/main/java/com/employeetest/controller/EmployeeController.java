package com.employeetest.controller;

import com.employeetest.dto.ApiResponse;
import com.employeetest.dto.VacationRequest;
import com.employeetest.dto.WorkRequest;
import com.employeetest.model.Employee;
import com.employeetest.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller exposing the Employee Management API.
 *
 * <p>All endpoints are prefixed with {@code /api/employees} and return a
 * consistent {@link ApiResponse} envelope so that a UI can always handle
 * responses the same way.
 *
 * <p>Base URL: {@code http://localhost:8080/api/employees}
 *
 * <table border="1" cellpadding="4">
 *   <tr><th>Method</th><th>Path</th><th>Description</th></tr>
 *   <tr><td>GET</td><td>/api/employees</td><td>List all employees</td></tr>
 *   <tr><td>GET</td><td>/api/employees/summary</td><td>Count by employee type</td></tr>
 *   <tr><td>GET</td><td>/api/employees/type/{type}</td><td>List by type (HOURLY/SALARIED/MANAGER)</td></tr>
 *   <tr><td>GET</td><td>/api/employees/{id}</td><td>Get a single employee</td></tr>
 *   <tr><td>POST</td><td>/api/employees/{id}/work</td><td>Record days worked</td></tr>
 *   <tr><td>POST</td><td>/api/employees/{id}/vacation</td><td>Take vacation days</td></tr>
 * </table>
 */
@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")   // allows a frontend on any origin to call this API
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // -------------------------------------------------------------------------
    // GET /api/employees
    // -------------------------------------------------------------------------

    /**
     * Returns all 30 employees sorted by id.
     *
     * <pre>
     * GET /api/employees
     * </pre>
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Employee>>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(
                ApiResponse.ok("Retrieved " + employees.size() + " employee(s).", employees));
    }

    // -------------------------------------------------------------------------
    // GET /api/employees/summary
    // -------------------------------------------------------------------------

    /**
     * Returns a count of employees grouped by type.
     *
     * <pre>
     * GET /api/employees/summary
     * Response: { "HOURLY": 10, "SALARIED": 10, "MANAGER": 10 }
     * </pre>
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getSummary() {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.getSummary()));
    }

    // -------------------------------------------------------------------------
    // GET /api/employees/type/{type}
    // -------------------------------------------------------------------------

    /**
     * Returns all employees of a specific type.
     *
     * <pre>
     * GET /api/employees/type/HOURLY
     * GET /api/employees/type/SALARIED
     * GET /api/employees/type/MANAGER
     * </pre>
     *
     * @param type employee type string (case-insensitive)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<Employee>>> getByType(@PathVariable String type) {
        List<Employee> employees = employeeService.getEmployeesByType(type);
        return ResponseEntity.ok(
                ApiResponse.ok("Retrieved " + employees.size() + " " + type.toUpperCase()
                        + " employee(s).", employees));
    }

    // -------------------------------------------------------------------------
    // GET /api/employees/{id}
    // -------------------------------------------------------------------------

    /**
     * Returns a single employee by their unique id.
     *
     * <pre>
     * GET /api/employees/1
     * </pre>
     *
     * @param id employee id (1-based)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> getById(@PathVariable long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.ok(employee));
    }

    // -------------------------------------------------------------------------
    // POST /api/employees/{id}/work
    // -------------------------------------------------------------------------

    /**
     * Records the number of days an employee worked and accrues vacation days.
     *
     * <pre>
     * POST /api/employees/1/work
     * Content-Type: application/json
     *
     * { "days": 20 }
     * </pre>
     *
     * <p>Business rules enforced:
     * <ul>
     *   <li>{@code days} must be 0–260.</li>
     *   <li>Cumulative days worked cannot exceed 260 in a work year.</li>
     * </ul>
     *
     * @param id      employee id
     * @param request request body containing the number of days
     */
    @PostMapping("/{id}/work")
    public ResponseEntity<ApiResponse<Employee>> recordWork(
            @PathVariable long id,
            @Valid @RequestBody WorkRequest request) {

        Employee updated = employeeService.recordWork(id, request.getDays());
        return ResponseEntity.ok(
                ApiResponse.ok(
                        String.format("Recorded %d day(s) worked. Vacation balance: %.4f day(s).",
                                request.getDays(), updated.getVacationDaysAccumulated()),
                        updated));
    }

    // -------------------------------------------------------------------------
    // POST /api/employees/{id}/vacation
    // -------------------------------------------------------------------------

    /**
     * Deducts vacation days from an employee's accumulated balance.
     *
     * <pre>
     * POST /api/employees/1/vacation
     * Content-Type: application/json
     *
     * { "days": 2.5 }
     * </pre>
     *
     * <p>Business rules enforced:
     * <ul>
     *   <li>{@code days} must be ≥ 0.</li>
     *   <li>{@code days} cannot exceed the current accumulated balance.</li>
     * </ul>
     *
     * @param id      employee id
     * @param request request body containing the number of vacation days
     */
    @PostMapping("/{id}/vacation")
    public ResponseEntity<ApiResponse<Employee>> takeVacation(
            @PathVariable long id,
            @Valid @RequestBody VacationRequest request) {

        Employee updated = employeeService.recordVacation(id, request.getDays());
        return ResponseEntity.ok(
                ApiResponse.ok(
                        String.format("Took %.2f vacation day(s). Remaining balance: %.4f day(s).",
                                request.getDays(), updated.getVacationDaysAccumulated()),
                        updated));
    }
}
