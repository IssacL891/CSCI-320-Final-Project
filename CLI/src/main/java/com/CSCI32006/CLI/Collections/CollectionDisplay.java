package com.CSCI32006.CLI.Collections;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Time;

@Getter
@Setter
@AllArgsConstructor
public class CollectionDisplay {
    private int userId;
    private int collectionId;
    private String collectionName;
    private int countOfGames;
    private int hours;
    private int minutes;

    @Override
    public String toString() {
        return collectionName + " | Count: " + countOfGames + " | Play time: " + hours + ":" + minutes;
    }
}
