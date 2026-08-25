package com.monolith.site.student;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentMapperTest {

    private StudentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new StudentMapper();
    }

    @Test
    public void toStudent_Success() {
        StudentDTO dto = new StudentDTO(
                "John",
                "Doe",
                "john@mail.com",
                1
        );

        Student student = mapper.toStudent(dto);
        assertEquals(dto.firstName(), student.getFirstName());
        assertEquals(dto.lastName(), student.getLastName());
        assertEquals(dto.email(), student.getEmail());
        assertNotNull(student.getSchool());
        assertEquals(dto.schoolId(), student.getSchool().getId());
    }

    @Test
    public void should_throw_null_pointer_exception_when_studentDTO_is_null() {
        var msg = assertThrows(NullPointerException.class, () -> mapper.toStudent(null));
        assertEquals("The Student DTO should not be null.", msg.getMessage());
    }

    @Test
    public void toStudentResponseDTO_Success() {
        //Given
        Student student = new Student(
                "John",
                "Doe",
                "john@mail.com",
                29
        );

        //When
        StudentResponseDTO responseDTO = mapper.toStudentResponseDTO(student);

        //Then
        assertEquals(responseDTO.firstName(), student.getFirstName());
        assertEquals(responseDTO.lastName(), student.getLastName());
        assertEquals(responseDTO.email(), student.getEmail());
    }
}