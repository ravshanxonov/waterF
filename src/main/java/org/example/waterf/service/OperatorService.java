package org.example.waterf.service;

import lombok.RequiredArgsConstructor;
import org.example.waterf.entity.TelegramUser;
import org.example.waterf.entity.enums.TelegramState;
import org.example.waterf.repo.TelegramUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final TelegramUserRepository telegramUserRepository;

    public List<TelegramUser> getNotAcceptedUsers(){
        return telegramUserRepository.findAllByActiveIsFalseAndState(TelegramState.WAITING);
    }


}
