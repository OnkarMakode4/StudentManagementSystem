package com.StudentManagementSystem.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.StudentManagementSystem.entity.Timetable;

public interface TimetableRepository extends JpaRepository<Timetable, Integer> {

    @Query("SELECT t FROM Timetable t JOIN FETCH t.course WHERE t.dayOfWeek = :day ORDER BY t.startTime ASC")
    List<Timetable> findByDayOfWeekWithCourse(@Param("day") String day);

    @Query("SELECT t FROM Timetable t JOIN FETCH t.course ORDER BY t.dayOfWeek ASC, t.startTime ASC")
    List<Timetable> findAllWithCourse();
}