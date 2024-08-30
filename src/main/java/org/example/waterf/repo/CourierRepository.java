package org.example.waterf.repo;

import org.example.waterf.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourierRepository extends JpaRepository<Courier, UUID> {
    Courier findByChatId(Long chatId);
    Courier findByPhone(String phone);
}