package com.employeetest.service;

import com.employeetest.model.Employee;
import com.employeetest.model.HourlyEmployee;
import com.employeetest.model.Manager;
import com.employeetest.model.SalariedEmployee;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service layer for employee management.
 *
 * <p>
 * All employee instances are stored in an in-memory {@link ConcurrentHashMap};
 * no
 * database is required. On application startup ({@link #init()}), 10 instances
 * of each
 * employee type are created automatically.
 *
 * <p>
 * This service is the single source of truth for business logic delegation:
 * validation of business rules is enforced in the model; the service
 * coordinates
 * lookup, delegation, and returning results to the controller.
 */
@Service
public class EmployeeService {

    // -------------------------------------------------------------------------
    // In-memory store
    // -------------------------------------------------------------------------

    /** Monotonically increasing ID generator. */
    private final AtomicLong idSequence = new AtomicLong(1);

    /** Thread-safe map of employee id → employee instance. */
    private final Map<Long, Employee> store = new ConcurrentHashMap<>();

    // -------------------------------------------------------------------------
    // Startup initialization
    // -------------------------------------------------------------------------

    /**
     * Populates the in-memory store with 10 {@link HourlyEmployee}s,
     * 10 {@link SalariedEmployee}s, and 10 {@link Manager}s on application startup.
     */
    @PostConstruct
    public void init() {
        for (int i = 1; i <= 10; i++) {
            long id = idSequence.getAndIncrement();
            store.put(id, new HourlyEmployee(id, "Hourly Employee " + i));
        }
        for (int i = 1; i <= 10; i++) {
            long id = idSequence.getAndIncrement();
            store.put(id, new SalariedEmployee(id, "Salaried Employee " + i));
        }
        for (int i = 1; i <= 10; i++) {
            long id = idSequence.getAndIncrement();
            store.put(id, new Manager(id, "Manager " + i));
        }
    }

    // -------------------------------------------------------------------------
    // Query operations
    // -------------------------------------------------------------------------

    /**
     * Returns all employees sorted by id.
     */
    public List<Employee> getAllEmployees() {
        return store.values().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Returns all employees whose type matches the given string (case-insensitive).
     *
     * @param type one of HOURLY, SALARIED, MANAGER
     */
    public List<Employee> getEmployeesByType(String type) {
        String upper = type.toUpperCase();
        return store.values().stream()
                .filter(e -> e.getEmployeeType().equals(upper))
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Returns a single employee by id.
     *
     * @param id employee id
     * @throws NoSuchElementException if no employee exists with the given id
     */
    public Employee getEmployeeById(long id) {
        Employee employee = store.get(id);
        if (employee == null) {
            throw new NoSuchElementException("No employee found with id: " + id);
        }
        return employee;
    }

    // -------------------------------------------------------------------------
    // Business operations
    // -------------------------------------------------------------------------

    /**
     * Records {@code days} worked for the employee with the given id and accrues
     * vacation days accordingly.
     *
     * @param id   employee id
     * @param days number of days to work (0–260, subject to annual cap)
     * @return updated employee
     * @throws NoSuchElementException   if no employee found
     * @throws IllegalArgumentException if business rules are violated (delegated
     *                                  from model)
     */
    public Employee recordWork(long id, int days) {
        Employee employee = getEmployeeById(id);
        employee.work(days);
        return employee;
    }

    /**
     * Deducts {@code days} vacation days from the employee's accumulated balance.
     *
     * @param id   employee id
     * @param days vacation days to use (must not exceed accumulated balance)
     * @return updated employee
     * @throws NoSuchElementException   if no employee found
     * @throws IllegalArgumentException if business rules are violated (delegated
     *                                  from model)
     */
    public Employee recordVacation(long id, double days) {
        Employee employee = getEmployeeById(id);
        employee.takeVacation(days);
        return employee;
    }

    // -------------------------------------------------------------------------
    // Summary
    // -------------------------------------------------------------------------

    /**
     * Returns a simple count summary of employees by type.
     */
    @SuppressWarnings("null")
    public Map<String, Long> getSummary() {
        return store.values().stream()
                .collect(Collectors.groupingBy(Employee::getEmployeeType, Collectors.counting()));
    }
}
