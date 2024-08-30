package org.example.waterf.repo;

import org.example.waterf.entity.Courier;
import org.example.waterf.entity.Order;
import org.example.waterf.entity.enums.OrderStatus;
import org.example.waterf.projections.OrderProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<OrderProjection> findAllByStartTimeAndOrderStatus(LocalDateTime localDateTime, OrderStatus orderStatus);
    List<Order> findAllByCourierAndOrderStatus(Courier courier, OrderStatus orderStatus);

}