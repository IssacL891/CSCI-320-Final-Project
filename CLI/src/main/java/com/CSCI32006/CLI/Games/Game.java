package com.CSCI32006.CLI.Games;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Game {
    private int gameId;
    private String title;
    private String esrbRating;
    private String developerName;
    private String publisherName;

    @Override
    public String toString() {

        return title + " | " + developerName + " | "
                + publisherName + " | " + esrbRating;
    }
}
