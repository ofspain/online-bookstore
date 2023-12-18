package com.interswitch.bookstore.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

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
}
