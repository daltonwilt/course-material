package com.monolith.site.student;

import com.monolith.site.school.School;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class StudentMapper {

    public Student toStudent(StudentDTO dto) {
        if(dto == null)
            throw new NullPointerException("The Student DTO should not be null.");

        var student = new Student(
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                null
        );
        var school = new School();

        school.setId(dto.schoolId());
        student.setSchool(school);

        return student;
    }

    public StudentResponseDTO toStudentResponseDTO(Student student) {
        return new StudentResponseDTO(
                student.getFirstName(),
                student.getLastName(),
                student.getEmail()
        );
    }

}
