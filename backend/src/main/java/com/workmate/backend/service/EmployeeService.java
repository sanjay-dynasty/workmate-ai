package com.workmate.backend.service;

import com.workmate.backend.dto.response.DocumentResponse;
import com.workmate.backend.dto.response.EmployeeProfileResponse;
import com.workmate.backend.dto.response.LeaveBalanceResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    private final Map<String, LeaveBalanceResponse> leaveBalances = Map.of(
            "EMP001",
            new LeaveBalanceResponse(
                    "EMP001", 24, 6, 18, 2),
            "EMP002",
            new LeaveBalanceResponse(
                    "EMP002", 24, 10, 14, 1));

    private final Map<String, EmployeeProfileResponse> employees = Map.of(
            "EMP001",
            new EmployeeProfileResponse(
                    "EMP001",
                    "Rahul Sharma",
                    "rahul@workmate.com",
                    "Engineering",
                    "Software Engineer",
                    "Amit Verma",
                    "Pune"),
            "EMP002",
            new EmployeeProfileResponse(
                    "EMP002",
                    "Priya Singh",
                    "priya@workmate.com",
                    "Human Resources",
                    "HR Executive",
                    "Neha Gupta",
                    "Bengaluru"));

    private final List<DocumentResponse> documents = List.of(
            new DocumentResponse(
                    "DOC001",
                    "Employee Leave Policy",
                    "Company leave rules and entitlement details",
                    "HR",
                    "Employees receive 24 annual leaves. Leave requests " +
                            "must be submitted through the HR portal. " +
                            "Manager approval is required."),
            new DocumentResponse(
                    "DOC002",
                    "Work From Home Policy",
                    "Guidelines for remote working",
                    "HR",
                    "Employees can request work from home according " +
                            "to their team's policy and manager approval."),
            new DocumentResponse(
                    "DOC003",
                    "Expense Reimbursement Policy",
                    "Rules for employee expense claims",
                    "Finance",
                    "Employees can submit eligible business expenses " +
                            "with supporting bills and manager approval."),
            new DocumentResponse(
                    "DOC004",
                    "Engineering Development Guidelines",
                    "Software engineering standards",
                    "Engineering",
                    "Engineering teams should follow code review, " +
                            "testing, documentation and deployment standards."));

    public LeaveBalanceResponse getLeaveBalance(String employeeId) {

        LeaveBalanceResponse response = leaveBalances.get(employeeId);

        if (response == null) {
            throw new IllegalArgumentException(
                    "Employee not found: " + employeeId);
        }

        return response;
    }

    public EmployeeProfileResponse getEmployeeProfile(String employeeId) {

        EmployeeProfileResponse response = employees.get(employeeId);

        if (response == null) {
            throw new IllegalArgumentException(
                    "Employee not found: " + employeeId);
        }

        return response;
    }

    public List<DocumentResponse> searchDocuments(String query) {

        if (query == null || query.isBlank()) {
            return documents;
        }

        String keyword = query.toLowerCase();

        return documents.stream()
                .filter(document -> document.title().toLowerCase().contains(keyword)
                        || document.description().toLowerCase().contains(keyword)
                        || document.category().toLowerCase().contains(keyword)
                        || document.content().toLowerCase().contains(keyword))
                .toList();
    }
}