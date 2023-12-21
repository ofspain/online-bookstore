package com.interswitch.bookstore.models;

public enum CartStatus {

    // Transition: "Ongoing" or "Pending"
    PROCESSED,

    //Transition: ongoing
    PENDING,

    //Transition: "Ongoing" or "Pending"
    FAILED,

    // initial default
    ONGOING;
}
