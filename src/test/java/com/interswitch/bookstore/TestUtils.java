package com.interswitch.bookstore;

import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.utils.BasicUtil;

public class TestUtils {

    public static User createUser(){
        User user = new User();
        user.setFullName(BasicUtil.generateRandomAlphet(5) +" "+BasicUtil.generateRandomAlphet(12));
        user.setEmail(BasicUtil.generateRandomAlphet(6)+"@domain.com");
        user.setPhoneNumber("+234"+BasicUtil.generateRandomNumeric(10));
        user.setPassword(BasicUtil.generateRandomAlphet(10));

        return user;
    }

}
