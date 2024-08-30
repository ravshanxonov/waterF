package org.example.waterf.repo;

import org.example.waterf.entity.DeliveryTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryTimeRepository extends JpaRepository<DeliveryTime, Integer> {
}