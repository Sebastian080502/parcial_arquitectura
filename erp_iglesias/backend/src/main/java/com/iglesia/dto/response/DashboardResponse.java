package com.iglesia.dto.response;

public record DashboardResponse(
    long totalPeople,
    long activeCourses,
    long offeringsMonth,
    long pendingPayments
) {}