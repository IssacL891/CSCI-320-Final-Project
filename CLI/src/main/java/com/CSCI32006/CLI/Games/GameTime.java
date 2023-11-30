package com.CSCI32006.CLI.Games;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class GameTime {
    private String title;
    private int time_played;

    public String toString() {
        return title + " | Time Played: " + time_played;
    }
}

