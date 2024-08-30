package org.example.waterf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.waterf.entity.enums.BottleTypes;
import org.example.waterf.entity.enums.TelegramState;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class TelegramUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private TelegramState state = TelegramState.START;

    private Long chatId;

    private String phone;

    @Embedded
    private Location location;

    private boolean active = false;

    @ManyToOne
    private District district;

    @Column(length = 500)
    private String address;

    private BottleTypes bottleTypes;

    @Builder.Default
    private Integer bottleCount=2;

    private Integer editingMessageId;

    private UUID lastOrderId;
}
