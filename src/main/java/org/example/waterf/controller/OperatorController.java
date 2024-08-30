package org.example.waterf.controller;

import lombok.RequiredArgsConstructor;
import org.example.waterf.bot.BotService;
import org.example.waterf.dto.VerifyUserDTO;
import org.example.waterf.entity.District;
import org.example.waterf.entity.Location;
import org.example.waterf.entity.TelegramUser;
import org.example.waterf.repo.DistrictRepository;
import org.example.waterf.repo.TelegramUserRepository;
import org.example.waterf.service.OperatorService;
import org.example.waterf.utils.DistrictUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/operator")
@RequiredArgsConstructor
public class OperatorController {
    private final OperatorService operatorService;
    private final TelegramUserRepository telegramUserRepository;
    private final BotService botService;
    private final DistrictUtil districtUtil;
    private final DistrictRepository districtRepository;

    @PostMapping("/verify")
    public String verifyUser(@ModelAttribute VerifyUserDTO verifyUserDTO) {
        TelegramUser telegramUser = telegramUserRepository.findById(verifyUserDTO.getId()).orElseThrow(() -> new RuntimeException("user not found"));
        telegramUser.setLocation(new Location(verifyUserDTO.getLongitude(), verifyUserDTO.getLatitude()));
        telegramUser.setAddress(verifyUserDTO.getAddress());
        telegramUser.setActive(true);
        District district = districtRepository.findById(verifyUserDTO.getDistrict()).orElseThrow(() -> new RuntimeException("district not found"));
        telegramUser.setDistrict(district);
        telegramUserRepository.save(telegramUser);
        botService.openCabinetForUser(telegramUser);
        return "redirect:/operator/not-accepted-users";
    }


    @GetMapping("/not-accepted-users")
    public String getNotAcceptedUsersPage(Model model) {
        List<TelegramUser> notAcceptedUsers = operatorService.getNotAcceptedUsers();
        model.addAttribute("users", notAcceptedUsers);
        return "operator";
    }

    @GetMapping("/not-accepted-user/{id}")
    public String getTelegramUserInfo(Model model, @PathVariable UUID id) {
        TelegramUser telegramUser = telegramUserRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
        model.addAttribute("userInfo", telegramUser);
        model.addAttribute("districtName", districtUtil.getDistrictName(telegramUser.getLocation()));
        model.addAttribute("districts",districtRepository.findAll());
        return "info";
    }

    @PostMapping("/kill-user")
    public String killUser(@RequestParam UUID userId) {
        TelegramUser telegramUser = telegramUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("not found"));
        botService.beforeKill(telegramUser);
        telegramUserRepository.deleteById(userId);
        return "redirect:/operator/not-accepted-users";
    }


    @ResponseBody
    @GetMapping("district-name")
    public String getDistrictName(@RequestParam Double longitude, @RequestParam Double latitude) {
        return districtUtil.getDistrictName(new Location(longitude, latitude));
    }
}
