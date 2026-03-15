package com.run;

import org.springframework.data.jpa.repository.JpaRepository;

// handles saving/loading TestEntity from the database
public interface TestRepository extends JpaRepository<TestEntity, Long> {
}