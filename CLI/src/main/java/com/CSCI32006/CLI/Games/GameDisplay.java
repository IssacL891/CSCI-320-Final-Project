package com.CSCI32006.CLI.Games;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
public class GameDisplay {
    private int gameId;
    private String title;
    private String esrbRating;
    private String developerName;
    private String publisherName;
    private String platformName;
    private double avgHours;
    private double avgMinutes;
    private double avgRating;

    @Override
    public String toString() {
        return title + " | " + platformName + " | " + developerName + " | "
                + publisherName + " | " + esrbRating + " | Average Time Played: " + avgHours + " hours and " + avgMinutes +
                " minutes | Average Rating: " + avgRating;
    }
}
