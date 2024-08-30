package org.example.waterf.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.waterf.entity.Location;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DistrictUtil {
    private final String apiKey = "AIzaSyBW2VQF8bZ1r6IpZ4Imtqaydl4ohyyrOsQ";
    private final RestTemplate restTemplate;

    public String getDistrictName(Location location) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s&language=%s&region=UZ",
                location.getLatitude(), location.getLongitude(), apiKey, "en");

        String response = restTemplate.getForObject(url, String.class);
        return parseDistrictFromResponse(response).split(" ")[0];
    }

    private String parseDistrictFromResponse(String response) {
        String target = "sublocality_level_1";
        int index1 = response.indexOf(target);
        response = response.substring(index1 + target.length());
        int index = response.indexOf(target);
        if (index != -1) {
            int start = response.lastIndexOf("long_name", index) + "long_name".length() + 5;
            int end = response.indexOf("\"", start);
            return response.substring(start, end);
        }
        return "District not found";
    }
}
