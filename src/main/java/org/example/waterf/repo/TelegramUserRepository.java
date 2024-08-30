package org.example.waterf.repo;

import org.example.waterf.entity.TelegramUser;
import org.example.waterf.entity.enums.TelegramState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, UUID> {

  Optional<TelegramUser> findByChatId(Long chatId);

  List<TelegramUser> findAllByActiveIsFalseAndState(TelegramState telegramState);

}