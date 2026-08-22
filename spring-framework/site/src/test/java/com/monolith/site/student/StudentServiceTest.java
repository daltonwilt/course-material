package com.monolith.site.student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    // Which service we want to test
    @InjectMocks
    private StudentService service;

    // Declare dependencies
    @Mock
    private StudentRepository repository;
    @Mock
    private StudentMapper mapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void saveStudent_Success() {
        // Given
        StudentDTO dto = new StudentDTO(
                "John",
                "Doe",
                "john@mail.com",
                1
        );

        Student student = new Student(
                "John",
                "Doe",
                "john@mail.com",
                29
        );

        Student savedStudent = new Student(
                "John",
                "Doe",
                "john@mail.com",
                29
        );
        savedStudent.setId(1);

        // Mock the calls
        when(mapper.toStudent(dto))
                .thenReturn(student);
        when(repository.save(student))
                .thenReturn(savedStudent);
        when(mapper.toStudentResponseDTO(savedStudent)).
                thenReturn(new StudentResponseDTO(
                        "John",
                        "Doe",
                        "john@mail.com"
                ));

        // When
        StudentResponseDTO responseDTO = service.saveStudent(dto);

        // Then
        assertEquals(dto.firstName(), responseDTO.firstName());
        assertEquals(dto.lastName(), responseDTO.lastName());
        assertEquals(dto.email(), responseDTO.email());

        verify(mapper, times(1)).toStudent(dto);
        verify(repository, times(1)).save(student);
        verify(mapper, times(1)).toStudentResponseDTO(savedStudent);
    }

    @Test
    void findAllStudents_Success() {
        // Given
        List<Student> students = new ArrayList<>();
        students.add(new Student(
                "John",
                "Doe",
                "john@mail.com",
                20
        ));

        // Mock the calls
        when(repository.findAll())
                .thenReturn(students);
        when(mapper.toStudentResponseDTO(any(Student.class)))
                .thenReturn(new StudentResponseDTO(
                        "John",
                        "Doe",
                        "john@mail.com"
                ));


        // When
        List<StudentResponseDTO> responseDTOS = service.findAllStudents();

        // Then
        assertEquals(students.size(), responseDTOS.size());
        verify(repository, times(1)).findAll();

    }

    @Test
    void findStudentById_Success() {
        // Given
        Student student = new Student(
                "John",
                "Doe",
                "john@mail.com",
                29
        );
        student.setId(1);

        // Mock
        when(repository.findById(student.getId()))
                .thenReturn(Optional.of(student));
        when(mapper.toStudentResponseDTO(any(Student.class)))
                .thenReturn(new StudentResponseDTO(
                        "John",
                        "Doe",
                        "john@mail.com"
                ));

        // When
        StudentResponseDTO responseDTO = service.findStudentById(student.getId());

        // Then
        assertEquals(responseDTO.firstName(), student.getFirstName());
        assertEquals(responseDTO.lastName(), student.getLastName());
        assertEquals(responseDTO.email(), student.getEmail());
        verify(repository, times(1)).findById(student.getId());
    }

    @Test
    void findStudentsByName_Success() {
        // Given
        String name = "John";
        List<Student> students = new ArrayList<>();
        students.add(new Student(
                "John",
                "Doe",
                "john@mail.com",
                20
        ));

        // Mock the calls
        when(repository.findAllByFirstNameContaining(name))
                .thenReturn(students);
        when(mapper.toStudentResponseDTO(any(Student.class)))
                .thenReturn(new StudentResponseDTO(
                        "John",
                        "Doe",
                        "john@mail.com"
                ));

        // When
        List<StudentResponseDTO> responseDTOS = service.findStudentsByName(name);

        // Then
        assertEquals(responseDTOS.size(), students.size());
        verify(repository, times(1)).findAllByFirstNameContaining(name);
    }

    @Test
    void delete_Success() {
        // Given
        Student student = new Student(
                "John",
                "Doe",
                "john@mail.com",
                29
        );
        student.setId(1);

        // Mock
        // Don't need to do because there is no return

        // When
        service.deleteStudent(student.getId());

        // Then
    }
}