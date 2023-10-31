package com.CSCI32006.CLI.Games;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Game {
    private int gameId;
    private String title;
    private String esrbRating;
    private String developerName;
    private String publisherName;
}
