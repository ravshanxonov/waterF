package org.example.waterf.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotListener implements CommandLineRunner {

    private final TelegramBot telegramBot;
    private final BotActions botActions;

    @Override
    public void run(String... args) throws Exception {
        telegramBot.setUpdatesListener(updates -> {
            try {
                for (Update update : updates) {
                    botActions.handle(update);
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            } catch (Exception e) {
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
    }
}
