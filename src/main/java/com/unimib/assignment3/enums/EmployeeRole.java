package com.unimib.assignment3.enums;

public enum EmployeeRole {
    JUNIOR(2400.00),
    SENIOR(2800.00),
    SENIOR_SW_ENGINEER(3000.00),
    SW_ARCHITECT(3600.00),
    SENIOR_SW_ARCHITECT(4000.00),
    MANAGER(7000.00);

    private final Double monthlySalary;

    EmployeeRole(Double monthlySalary) {
        this.monthlySalary = monthlySalary;
    }

    public Double getMonthlySalary() {
        return monthlySalary;
    }
}
