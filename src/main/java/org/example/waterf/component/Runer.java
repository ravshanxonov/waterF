package org.example.waterf.component;

import lombok.RequiredArgsConstructor;
import org.example.waterf.entity.DeliveryTime;
import org.example.waterf.entity.District;
import org.example.waterf.repo.CourierRepository;
import org.example.waterf.repo.DeliveryTimeRepository;
import org.example.waterf.repo.DistrictRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Runer implements CommandLineRunner {

    private final DeliveryTimeRepository deliveryTimeRepository;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;
    private final DistrictRepository districtRepository;
    private final CourierRepository courierRepository;

    @Override
    public void run(String... args) throws Exception {
        if (ddl.equals("create")) {
            deliveryTimeRepository.saveAll(List.of(
                    new DeliveryTime(1, LocalTime.of(9, 0), LocalTime.of(12, 0)),
                    new DeliveryTime(2, LocalTime.of(13, 0), LocalTime.of(18, 0)),
                    new DeliveryTime(3, LocalTime.of(18, 0), LocalTime.of(23, 59))
            ));
            districtRepository.saveAll(
                    List.of(
                            new District(null, "Shayxontohur"),
                            new District(null, "Olmazor"),
                            new District(null, "Yunusobod"),
                            new District(null, "Mirobod"),
                            new District(null, "Chilonzor"),
                            new District(null, "Yashnobod"),
                            new District(null, "Mirzo ulugbek")
                    )
            );

        }

    }
}
