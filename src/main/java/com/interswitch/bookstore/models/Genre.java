package com.interswitch.bookstore.models;


import java.util.Random;

public enum Genre {
    FICTION,
    THRILLER,
    MYSTERY,
    POETRY,
    HORROR,
    SATIRE;

    public static Genre findRandomGenre(){
        Genre[] genres = Genre.values();

        Random rand = new Random();
        int randomInt = rand.nextInt(genres.length);

        return genres[randomInt];
    }

    public static Genre fixGenreFromName(String name){
        for(Genre genre : Genre.values()){
            if(name.equalsIgnoreCase(genre.name())){
                return genre;
            }
        }
        return null;
    }
}
