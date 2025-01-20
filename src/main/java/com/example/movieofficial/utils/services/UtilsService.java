package com.example.movieofficial.utils.services;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
public class UtilsService {

    private static final String DIGITS = "0123456789";

    public String generateOtp(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("OTP length must be greater than 0");
        }

        SecureRandom secureRandom = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(DIGITS.length());
            otp.append(DIGITS.charAt(index));
        }

        return otp.toString();
    }

    public String toSlug(String input) {
        if (input == null) {
            return "";
        }
        input = input.replaceAll("[đĐ]", "d");
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("");
        slug = slug.toLowerCase().replaceAll(" ", "-");
        slug = slug.replaceAll("[^a-z0-9\\-]", "");
        return slug;
    }

    public boolean isValidVietnamesePhoneNumber(String phone) {
        String vietnamesePhoneRegex = "^(0[3|5|7|8|9])[0-9]{8}$";
        return phone.matches(vietnamesePhoneRegex);
    }
}
