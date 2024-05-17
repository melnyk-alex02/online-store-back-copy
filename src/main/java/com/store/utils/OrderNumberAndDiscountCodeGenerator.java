package com.store.utils;

import java.util.Random;

public class OrderNumberAndDiscountCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ORDER_NUMBER_LENGTH = 8;

    private static final int DISCOUNT_CODE_LENGTH = 6;

    public static String generateOrderNumber() {
        return getString(ORDER_NUMBER_LENGTH);
    }

    public static String generateDiscountCode() {
        return getString(DISCOUNT_CODE_LENGTH);
    }

    private static String getString(int length) {
        StringBuilder orderNumber = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            orderNumber.append(randomChar);
        }

        return orderNumber.toString();
    }


}