package org.example.waterf.bot;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendLocation;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.example.waterf.entity.Courier;
import org.example.waterf.entity.Location;
import org.example.waterf.entity.Order;
import org.example.waterf.entity.enums.OrderStatus;
import org.example.waterf.repo.CourierRepository;
import org.example.waterf.repo.OrderRepository;
import org.example.waterf.repo.TelegramUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepository;
    private final TelegramUserRepository telegramUserRepository;
    private final TelegramBot telegramBot;
    private final OrderRepository orderRepository;


    public Boolean UserOrCourier(Contact contact) {
        Courier byPhone = courierRepository.findByPhone(contact.phoneNumber());
        return byPhone != null;
    }

    public void acceptPhoneAskForOrders(Message message) {
        Courier courier = courierRepository.findByPhone(message.contact().phoneNumber());
        courier.setChatId(message.from().id());
        courierRepository.save(courier);
        SendMessage sendMessage = new SendMessage(courier.getChatId(), "Buyurtmalaringizbi ko'rishingiz mumkun");
        sendMessage.replyMarkup(new ReplyKeyboardMarkup("Mening Buyurtmalarim").resizeKeyboard(true));
        telegramBot.execute(sendMessage);
    }

    public void sendAllOrders(Long id) {
        Courier courier = courierRepository.findByChatId(id);
        List<Order> allOrders = orderRepository.findAllByCourierAndOrderStatus(courier,OrderStatus.ACCEPTED);
        SendMessage sendMessage = new SendMessage(courier.getChatId(), "Order List");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        if (allOrders.isEmpty()) {
            SendMessage sendMessage1=new SendMessage(courier.getChatId(), "Buyurtmalar yo'q");
            sendMessage1.replyMarkup(new ReplyKeyboardMarkup("Mening Buyurtmalarim").resizeKeyboard(true));
            telegramBot.execute(sendMessage1);
        }else {
            for (Order order : allOrders) {
                InlineKeyboardButton button = new InlineKeyboardButton(order.getDistrict().getName() + " " + order.getBottleCount()).callbackData("order_" + order.getId());
                inlineKeyboardMarkup.addRow(button);
            }
            sendMessage.replyMarkup(inlineKeyboardMarkup);
            telegramBot.execute(sendMessage);
        }
    }

    public Boolean getUserFromChatId(Update update) {
        if (update.callbackQuery() != null) {
            Courier courier1 = courierRepository.findByChatId(update.callbackQuery().from().id());
            return courier1 == null;
        } else {
            Courier courier2 = courierRepository.findByChatId(update.message().from().id());
            return courier2 == null;
        }

    }

    public String createOrderMessage(Order order) {
        return String.format("Buyurtma %s ta %s lik \n%s: %s",
                order.getBottleCount(),
                order.getBottleTypes().getType(),
                order.getDistrict().getName(),
                order.getPhone());
    }

    public void sendOrder(String data, Courier courier) {
        String substring = data.substring(6);
        Order order = orderRepository.findById(UUID.fromString(substring))
                .orElseThrow(() -> new IllegalArgumentException("Order not found for ID: " + substring));
        String msg = createOrderMessage(order);
        SendMessage sendMessage = new SendMessage(courier.getChatId(), msg);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("Boshlash").callbackData("boshlash_" + order.getId()),
                new InlineKeyboardButton("Keyingisi").callbackData("keyingisi_" + order.getId()));
        sendMessage.replyMarkup(inlineKeyboardMarkup);
        telegramBot.execute(sendMessage);
    }

    public void deliveryOrder(String data, Courier courier) {
        String substring = data.substring(9);
        Order order = orderRepository.findById(UUID.fromString(substring)).orElseThrow();
        order.setOrderStatus(OrderStatus.DELIVERY);
        orderRepository.save(order);
        Location location = order.getLocation();
        SendLocation sendLocation = new SendLocation(courier.getChatId(), (float) location.getLatitude(), (float) location.getLongitude());
        telegramBot.execute(sendLocation);
        InlineKeyboardButton deliveredButton = new InlineKeyboardButton("Buyurtma egasiga topshirildi").callbackData("delivered_" + order.getId());
        InlineKeyboardButton notHomeButton = new InlineKeyboardButton("Mijoz uyda yo'q").callbackData("not_home_" + order.getId());
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                {deliveredButton, notHomeButton}
        });
        String msg = "Buyurtma holatini tanlang:";
        SendMessage sendMessage = new SendMessage(courier.getChatId(), msg)
                .replyMarkup(inlineKeyboardMarkup);

        telegramBot.execute(sendMessage);
    }

    public void sendOrderFromOrder(Order order, Courier courier) {
        String msg = createOrderMessage(order);
        SendMessage sendMessage = new SendMessage(courier.getChatId(), msg);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("Boshlash").callbackData("boshlash_" + order.getId()),
                new InlineKeyboardButton("Keyingisi").callbackData("keyingisi_" + order.getId()));
        sendMessage.replyMarkup(inlineKeyboardMarkup);
        telegramBot.execute(sendMessage);
    }

    public void nextOrder( String data, Courier courier) {
        String substring = data.substring("keyingisi_".length());
        Order order = orderRepository.findById(UUID.fromString(substring)).orElseThrow();
        Courier byChatId = courierRepository.findByChatId(courier.getChatId());
        List<Order> allOrders = orderRepository.findAllByCourierAndOrderStatus(byChatId,OrderStatus.ACCEPTED);
        if (allOrders.isEmpty()) {
            SendMessage sendMessage = new SendMessage(courier.getChatId(), "Buyurtmalar yo'q");
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("Mening Buyurtmalarim").resizeKeyboard(true));
            telegramBot.execute(sendMessage);
        } else {
            int index = -1;
            for (int i = 0; i < allOrders.size(); i++) {
                if (allOrders.get(i).getId().equals(order.getId())) {
                    index = i;
                    break;
                }
            }
            Order order1;
            if (index + 1 < allOrders.size()) {
                order1 = allOrders.get(index + 1);
            } else {
                order1 = allOrders.get(0);
            }
            sendOrderFromOrder(order1, courier);
        }
    }

    public void noteHome(String data, Courier courier) {
        String substring = data.substring("not_home_".length());
        Order order = orderRepository.findById(UUID.fromString(substring)).orElseThrow();
        order.setOrderStatus(OrderStatus.REJECTED);
        orderRepository.save(order);
        nextOrder(data,courier);
    }
}