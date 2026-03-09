package com.iglesia.controller;
import com.iglesia.dto.request.CourseRequest;
import com.iglesia.dto.response.CourseResponse;
import com.iglesia.entity.Church;
import com.iglesia.entity.Course;
import com.iglesia.exception.CourseNotFoundException;
import com.iglesia.repository.CourseRepository;
import com.iglesia.service.ChurchService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseRepository courseRepository;
    private final ChurchService churchService;

    public CourseController(CourseRepository courseRepository, ChurchService churchService) {
        this.courseRepository = courseRepository;
        this.churchService = churchService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CourseResponse create(@Valid @RequestBody CourseRequest request) {
        Church church = churchService.getRequiredChurch();
        Course course = new Course();
        course.setName(request.name());
        course.setDescription(request.description());
        course.setPrice(request.price());
        course.setChurch(church);
        courseRepository.save(course);
        return CourseResponse.from(course);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public List<CourseResponse> list() {
        Church church = churchService.getRequiredChurch();
        return courseRepository.findAllByChurchId(church.getId())
                .stream()
                .map(CourseResponse::from)
                .toList();

    }

  @PreAuthorize("hasRole('ADMIN')")
@GetMapping("/{id}") 
    public CourseResponse getById(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        return CourseResponse.from(course);
    }

  
}