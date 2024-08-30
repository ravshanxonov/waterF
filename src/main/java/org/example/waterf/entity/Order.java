package org.example.waterf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.waterf.entity.enums.BottleTypes;
import org.example.waterf.entity.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private UUID telegramUserId;

    @ManyToOne
    private District district;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Integer bottleCount;

    @Enumerated(EnumType.STRING)
    private BottleTypes bottleTypes;

    private String phone;

    @Embedded
    private Location location;

    @ManyToOne
    private Courier courier;






}
