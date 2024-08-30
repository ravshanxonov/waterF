package org.example.waterf.utils;

public class PhoneUtil {

    public static String repairPhone(String phone) {
        if (phone.startsWith("+")) {
            return phone;
        } else {
            return "+" + phone;
        }
    }



}
