package com.iglesia.service;

import com.iglesia.entity.Church;
import com.iglesia.entity.Course;
import com.iglesia.repository.CourseRepository;
import com.iglesia.dto.request.CourseRequest;
import com.iglesia.dto.response.CourseResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final ChurchService churchService;

    public CourseService(CourseRepository courseRepository, ChurchService churchService) {
        this.courseRepository = courseRepository;
        this.churchService = churchService;
    }

    public CourseResponse create(CourseRequest request) {
        Church church = churchService.getRequiredChurch();

        Course course = new Course();
        course.setName(request.name());
        course.setDescription(request.description());
        course.setPrice(request.price());
        course.setChurch(church);

        courseRepository.save(course);
        return CourseResponse.from(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> listAll() {
        Church church = churchService.getRequiredChurch();

        return courseRepository.findAllByChurchId(church.getId())
                .stream()
                .map(CourseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new com.iglesia.exception.CourseNotFoundException(id));
        return CourseResponse.from(course);
    }
}