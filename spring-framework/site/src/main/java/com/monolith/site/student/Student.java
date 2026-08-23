package com.monolith.site.student;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.monolith.site.profile.Profile;
import com.monolith.site.school.School;
import jakarta.persistence.*;

@Entity
@Table(name = "Student")
public class Student {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 20)
    private String firstName;
    @Column
    private String lastName;

    @Column(unique = true)
    private String email;
    private Integer age;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "school_id")
    @JsonBackReference // Tell that the Student object doesn't need to serialize the School
    private School school;

    public Student() {}

    public Student(String firstName, String lastName, String email, Integer age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

}
