package com.example.lovable_clone.repository;

import com.example.lovable_clone.entity.Plan;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan,Long> {
    Optional<Plan> findById(Long aLong);

    Optional<Plan> findByStripePriceId(String id);
}
