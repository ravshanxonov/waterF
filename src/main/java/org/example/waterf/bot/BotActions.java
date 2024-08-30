package org.example.waterf.bot;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendLocation;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.example.waterf.entity.Courier;
import org.example.waterf.entity.Order;
import org.example.waterf.entity.TelegramUser;
import org.example.waterf.entity.enums.OrderStatus;
import org.example.waterf.entity.enums.TelegramState;
import org.example.waterf.repo.CourierRepository;
import org.example.waterf.repo.OrderRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BotActions {

    private final BotService botService;
    private final CourierService courierService;
    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final TelegramBot telegramBot;

    @Async
    public void handle(Update update) {


        Boolean userOrCourier = courierService.getUserFromChatId(update);
        if (userOrCourier) {
            if (update.message() != null) {
                Message message = update.message();
                TelegramUser tgUser = botService.getOrCreateUser(message.chat().id());
                if (message.text() != null) {
                    String text = message.text();
                    if (text.equals("/start")) {
                        if (tgUser.isActive()) {
                            botService.openCabinetForUser(tgUser);
                        } else {
                            botService.acceptStartAskForContact(tgUser);
                        }
                    } else if (tgUser.getState().equals(TelegramState.SHARING_CONTACT)) {
                        botService.acceptPhoneAskForLocation(text, tgUser);
                    }
                } else if (message.contact() != null) {
                    if (tgUser.getState().equals(TelegramState.SHARING_CONTACT)) {
                        if (courierService.UserOrCourier(message.contact())) {
                            courierService.acceptPhoneAskForOrders(message);
                        }else {
                        botService.acceptPhoneAskForLocation(message.contact().phoneNumber(), tgUser);
                        }
                    }
                } else if (message.location() != null) {
                    botService.acceptLocationAskToWait(message.location(), tgUser);
                }
            } else if (update.callbackQuery() != null) {
                CallbackQuery callbackQuery = update.callbackQuery();
                TelegramUser tgUser = botService.getOrCreateUser(callbackQuery.from().id());
                if (tgUser.getState().equals(TelegramState.CABINET)) {
                    if (callbackQuery.data().equals(BotConstant.ORDER_WATER)) {
                        botService.startOrderingAskForBottleType(tgUser);
                    }
                } else if (tgUser.getState().equals(TelegramState.SELECTING_BOTTLE_TYPES)) {
                    botService.acceptBottleTypeAskForAmount(callbackQuery, tgUser);
                } else if (tgUser.getState().equals(TelegramState.ENTERING_BOTTLES_AMOUNT)) {
                    if (callbackQuery.data().equals(BotConstant.PLUS)) {
                        botService.changeCount("+", tgUser);
                    } else if (callbackQuery.data().equals(BotConstant.MINUS)) {
                        botService.changeCount("-", tgUser);
                    } else if (callbackQuery.data().equals(BotConstant.ACCEPT_COUNT)) {
                        botService.acceptBottleCountAskForTime(tgUser);
                    }
                } else if (tgUser.getState().equals(TelegramState.SELECTING_TIME)) {
                    botService.acceptTimeAndCreateOrder(callbackQuery, tgUser);
                } else if (tgUser.getState().equals(TelegramState.ACCEPT_ORDER)) {
                    if (callbackQuery.data().equals(BotConstant.ACCEPT_ORDER)) {
                        botService.acceptOrderAndWait(tgUser);
                    } else if (callbackQuery.data().equals(BotConstant.CANCEL_ORDER)) {
                        botService.cancelOrderAndGoToCabinet(tgUser);
                    }
                }
            }
        } else {
            if (update.message() != null) {
                Message message = update.message();
                courierRepository.findByChatId(message.from().id());
                if (message.text()!=null) {
                    String text = message.text();
                    if (text.equals("Mening Buyurtmalarim")) {
                        courierService.sendAllOrders(message.from().id());
                    }
                }
            } else if (update.callbackQuery()!=null) {
                String data = update.callbackQuery().data();
                Courier courier = courierRepository.findByChatId(update.callbackQuery().from().id());
                if (data.startsWith("order_")) {
                    courierService.sendOrder(data,courier);
                }else if (data.startsWith("boshlash_")) {
                  courierService.deliveryOrder(data,courier);
                } else if (data.startsWith("delivered_")) {
                    String substring = data.substring("delivered_".length());
                    Order order = orderRepository.findById(UUID.fromString(substring)).orElseThrow();
                    order.setOrderStatus(OrderStatus.DELIVERED);
                    orderRepository.save(order);
                    courierService.nextOrder(data,courier);
                } else if (data.startsWith("keyingisi_")) {
                  courierService.nextOrder(data,courier);
                }else if (data.startsWith("not_home_")){
                  courierService.noteHome(data,courier);
                }

            }
        }
    }
}