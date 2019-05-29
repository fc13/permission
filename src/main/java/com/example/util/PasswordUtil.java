package com.example.util;

import java.util.Date;
import java.util.Random;

public class PasswordUtil {
    private final static String[] UPPER_CASE_WORD = {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private final static String[] LOWER_CASE_WORD = {"a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    private final static String[] WORD = {"a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private final static String[] NUM = {"2", "3", "4", "5", "6", "7", "8", "9"};

    private static String[] WORD_LIST = {"UPPER_CASE_WORD", "LOWER_CASE_WORD"};

    public static String randomPassword() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random(new Date().getTime());
        boolean flag = false;
        int length = random.nextInt(3) + 8;
        String wordFlag = WORD_LIST[random.nextInt(WORD_LIST.length)];
        for (int i = 0; i < length; i++) {
            if (flag) {
                sb.append(wordFlag.equals("UPPER_CASE_WORD") ? UPPER_CASE_WORD[random.nextInt(UPPER_CASE_WORD.length)] : LOWER_CASE_WORD[random.nextInt(LOWER_CASE_WORD.length)]);
            } else {
                sb.append(NUM[random.nextInt(NUM.length)]);
            }
            flag = !flag;
        }
        return sb.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(randomPassword());
        Thread.sleep(100);
        System.out.println(randomPassword());
        Thread.sleep(100);
        System.out.println(randomPassword());
    }
}
