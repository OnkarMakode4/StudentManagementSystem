package com.StudentManagementSystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private int marks;
    private String grade; // A, B, C, D, F

    public Grade() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}