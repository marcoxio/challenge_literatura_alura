package com.alura.challenges.repository;

import com.alura.challenges.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {
    List<BookEntity> findByAuthorsNameContainingIgnoreCase(String name);
}
