package com.StudentManagementSystem.serviceImpl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.StudentManagementSystem.entity.Student;
import com.StudentManagementSystem.repository.AttendanceRepository;
import com.StudentManagementSystem.repository.GradeRepository;
import com.StudentManagementSystem.repository.Studentrepository;
import com.StudentManagementSystem.service.StudentService;

@Service
public class ServiceImpl implements StudentService {

    @Autowired
    private Studentrepository studentrepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Override
    public List<Student> getAllStudents() {
        return studentrepository.findAll();
    }

    @Override
    public Student saveStudent(Student student) {
        return studentrepository.save(student);
    }

    @Override
    public Student getById(int id) {
        return studentrepository.findById(id).get();
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        Student student = studentrepository.findById(id).get();

        // Delete grades first
        gradeRepository.deleteAll(gradeRepository.findByStudent(student));

        // Delete attendance records
        attendanceRepository.deleteAll(attendanceRepository.findByStudent(student));

        // Now safe to delete the student
        studentrepository.deleteById(id);
    }

    @Override
    public List<Student> searchStudents(String keyword) {
        return studentrepository.searchStudents(keyword);
    }

    @Override
    public Page<Student> getAllStudents(Pageable pageable) {
        return studentrepository.findAll(pageable);
    }

    @Override
    public Page<Student> searchStudents(String keyword, Pageable pageable) {
        return studentrepository.searchStudents(keyword, pageable);
    }
}