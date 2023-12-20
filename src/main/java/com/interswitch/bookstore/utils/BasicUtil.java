package com.interswitch.bookstore.utils;

import com.interswitch.bookstore.dtos.BankAccountDetail;
import com.interswitch.bookstore.dtos.BankDetail;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BasicUtil {


    private static String generateRandomCharacter(String characters, int length){

        return new Random()
                .ints(length, 0, characters.length())
                .mapToObj(characters::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    public static String generateRandomNumeric(int length) {
        String characters = "0123456789";

        return generateRandomCharacter(characters,length);
    }

    public static String generateRandomAlphet(int length) {
        String characters = "ABCDEFGHIJKLMOPQRSTUVWXYZ";

        return generateRandomCharacter(characters,length);
    }

    public static String generateRandomAlphaNumeric(int length) {
        String characters = "ABCDEFGHIJKLMOPQRSTUVWXYZ0123456789";

        return generateRandomCharacter(characters,length);
    }

    public static boolean validString(String s){
        return null != s && !s.isEmpty();
    }

    public static boolean checkPassword(String password_plaintext, String stored_hash) {

        if (null == stored_hash || !stored_hash.startsWith("$2a$")) {
            return false;
        }

        return  BCrypt.checkpw(password_plaintext, stored_hash);

    }

    public static String generateTranReferenceSuffix(){
        //20230521|A23BH76Z
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom RANDOM = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        Date date = new Date();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String now =  dateFormat.format(date);

        now = now.replace("-","");
        sb.append(now).append("|");
        for (int i = 0; i < 8; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public static List<BankAccountDetail> bankOptionToPayTo(){
        String accountName = "BOOK STORE ACCOUNT";
        BankAccountDetail detail1 = new BankAccountDetail();
        detail1.setAccName(accountName);
        detail1.setAccNumber("99998765430");
        BankDetail bankDetail = new BankDetail("Access Bank", "09876");
        detail1.setBankDetail(bankDetail);

        BankAccountDetail detail2 = new BankAccountDetail();
        detail2.setAccName(accountName);
        detail2.setAccNumber("00998765430");
        BankDetail bankDetail2 = new BankDetail("First Bank", "09876");
        detail2.setBankDetail(bankDetail2);

        return new ArrayList<> (){{add(detail1);
            add(detail2);
        }};

    }

    public static List<BankDetail> bankOptionToPayFrom(){
        BankDetail bankDetail1 = new BankDetail("Access Bank", "09876");
        BankDetail bankDetail2 = new BankDetail("First Bank", "09876");
        BankDetail bankDetail3 = new BankDetail("GT Bank", "09876");
        BankDetail bankDetail4 = new BankDetail("UBA", "09876");
        BankDetail bankDetail5 = new BankDetail("FCMB", "09876");
        BankDetail bankDetail6 = new BankDetail("Union Bank", "09876");
        BankDetail bankDetail7 = new BankDetail("Sterling Bank", "09876");
        BankDetail bankDetail8 = new BankDetail("Polaris Bank", "09876");
        BankDetail bankDetail9 = new BankDetail("Keystone Bank", "09876");
        BankDetail bankDetail10 = new BankDetail("Providus Bank", "09876");

        return new ArrayList<> (){{
            add(bankDetail1);
            add(bankDetail2);
            add(bankDetail3);
            add(bankDetail4);
            add(bankDetail5);
            add(bankDetail6);
            add(bankDetail7);
            add(bankDetail8);
            add(bankDetail9);
            add(bankDetail10);
        }};

    }
}
