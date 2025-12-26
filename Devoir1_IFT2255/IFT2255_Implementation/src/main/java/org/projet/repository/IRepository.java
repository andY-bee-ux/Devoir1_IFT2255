package org.projet.repository;

import org.projet.model.Cours;

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
    public String getCourseEligibility(String courseId, List<String> completedCourses) throws Exception;

}
