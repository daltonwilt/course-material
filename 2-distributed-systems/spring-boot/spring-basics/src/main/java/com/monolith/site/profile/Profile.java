package com.monolith.site.profile;

import com.monolith.site.student.Student;
import jakarta.persistence.*;

@Entity
public class Profile {
    @Id
    @GeneratedValue
    private Integer id;
    private String bio;

    @OneToOne()
    @JoinColumn(name = "student_id")
    private Student student;

    public Profile () {}

    public Profile(String bio) {
        this.bio = bio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

}
