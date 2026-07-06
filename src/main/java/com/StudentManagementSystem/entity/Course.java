package com.StudentManagementSystem.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String courseName;
    private String courseCode;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();

    public Course() {}

    public Course(String courseName, String courseCode) {
        this.courseName = courseName;
        this.courseCode = courseCode;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public Set<Student> getStudents() { return students; }
    public void setStudents(Set<Student> students) { this.students = students; }
}