package com.aquabasilea.domain.admin.model;

import com.aquabasilea.domain.course.model.CourseDateComparator;

public class Course4AdminViewComparator implements java.util.Comparator<Course4AdminView> {
    @Override
    public int compare(Course4AdminView course4AdminView1, Course4AdminView course4AdminView2) {
        return new CourseDateComparator().compare(course4AdminView1.courseDate(), course4AdminView2.courseDate());
    }
}
