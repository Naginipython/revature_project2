package com.revature.project2.repositories;

import com.revature.project2.models.Envelope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnvelopeRepository extends JpaRepository<Envelope, Integer> {
    List<Envelope> findByUser_UserId(Integer userId);
}
