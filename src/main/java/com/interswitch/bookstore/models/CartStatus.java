package com.interswitch.bookstore.models;

public enum CartStatus {

    // Transition: "Ongoing" or "Pending"
    processed,

    //Transition: ongoing
    pending,

    //Transition: "Ongoing" or "Pending"
    failed,

    // initial default
    ongoing;
}
