package org.example.repository;

import org.example.model.Cours;

import java.util.List;
import java.util.Optional;

public interface IRepository {
    public Optional<List<Cours>> getCourseBy(
            String param,
            String value,
            String includeScheduleBool,
            String semester
    ) throws Exception;
    public Optional<List<String>> getAllCoursesId() throws Exception;
    public String checkCourseEligibility(String courseId, List<String> completedCourses) throws Exception;

}
