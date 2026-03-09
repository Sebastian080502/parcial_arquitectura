package com.iglesia.dto.response;

import com.iglesia.entity.Course;
import java.math.BigDecimal;

public record CourseResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    boolean active
) {
    public static CourseResponse from(Course course) {
        return new CourseResponse(
            course.getId(),
            course.getName(),
            course.getDescription(),
            course.getPrice(),
            course.isActive()
        );
    }
}