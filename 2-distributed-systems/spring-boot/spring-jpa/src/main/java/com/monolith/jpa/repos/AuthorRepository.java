package com.monolith.jpa.repos;

import com.monolith.jpa.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthorRepository extends JpaRepository<Author,Integer> {
    
}
