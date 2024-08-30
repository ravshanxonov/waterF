package org.example.waterf.controller;


import org.example.waterf.bot.BotService;
import org.example.waterf.dto.MoveDTO;
import org.example.waterf.entity.Courier;
import org.example.waterf.entity.DeliveryTime;
import org.example.waterf.entity.Order;
import org.example.waterf.entity.enums.OrderStatus;
import org.example.waterf.projections.OrderProjection;
import org.example.waterf.repo.CourierRepository;
import org.example.waterf.repo.DeliveryTimeRepository;
import org.example.waterf.repo.OrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DeliveryTimeRepository deliveryTimeRepository;
    private final BotService botService;
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;

    public AdminController(DeliveryTimeRepository deliveryTimeRepository, BotService botService, OrderRepository orderRepository, CourierRepository courierRepository) {
        this.deliveryTimeRepository = deliveryTimeRepository;
        this.botService = botService;
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
    }

    @GetMapping
    public String getAdminPage(Model model) {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        model.addAttribute("times", deliveryTimeRepository.findAll(sort));
        return "admin";
    }

    @ResponseBody
    @GetMapping("/orders")
    public List<OrderProjection> getOrdersByTime(@RequestParam Integer timeId, @RequestParam String day) {
        DeliveryTime deliveryTime = deliveryTimeRepository.findById(timeId).orElseThrow(() -> new RuntimeException("time not found"));
        LocalDate date = botService.getDate(day);
        LocalDateTime ordersStartTime = LocalDateTime.of(date, deliveryTime.getStart());
        return orderRepository.findAllByStartTimeAndOrderStatus(ordersStartTime, OrderStatus.ACCEPTED);
    }

    @ResponseBody
    @GetMapping("/couriers")
    public List<Courier> getCouriers() {
        return courierRepository.findAll();
    }

    @ResponseBody
    @PostMapping("/move")
    public void moveOrder(@RequestBody MoveDTO moveDTO) {
        Order order = orderRepository.findById(moveDTO.orderId()).orElseThrow(() -> new RuntimeException("order not found"));
        Courier courier = courierRepository.findById(moveDTO.courierId()).orElseThrow(() -> new RuntimeException("courier not found"));
        order.setCourier(courier);
        orderRepository.save(order);
    }

}
