package org.example.waterf.controller;

import lombok.RequiredArgsConstructor;
import org.example.waterf.entity.Courier;
import org.example.waterf.repo.CourierRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/couriers")
public class CourierController {


    private final CourierRepository courierRepository;

    @GetMapping
    public String listCouriers(Model model) {
        List<Courier> couriers = courierRepository.findAll();
        model.addAttribute("couriers", couriers);
        return "couriers";
    }

    @GetMapping("/new")
    public String newCourierForm(Model model) {
        model.addAttribute("courier", new Courier());
        return "courier_form";
    }

    @PostMapping
    public String saveCourier(@ModelAttribute Courier courier) {
        if (courier.getId()==null) {
            courierRepository.save(courier);
        } else {
            updateCourier(courier.getId(), courier);
        }
        return "redirect:/couriers";
    }

    @GetMapping("/edit/{id}")
    public String editCourierForm(@PathVariable UUID id, Model model) {
        Courier courier = courierRepository.findById(id).orElseThrow();
        model.addAttribute("courier", courier);
        return "courier_form";
    }

    public void updateCourier(UUID id, Courier courier) {
        Courier courier1 = courierRepository.findById(id).orElseThrow();
        courier1.setName(courier.getName());
        courier1.setPhone(courier.getPhone());
        courier1.setCarType(courier.getCarType());
        courierRepository.save(courier1);
    }

    @GetMapping("/delete/{id}")
    public String deleteCourier(@PathVariable UUID id) {
        courierRepository.deleteById(id);
        return "redirect:/couriers";
    }
}