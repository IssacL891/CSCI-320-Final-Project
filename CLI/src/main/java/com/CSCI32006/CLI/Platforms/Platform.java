package com.CSCI32006.CLI.Platforms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Platform {
    private int platformId;
    private String name;
    private Date releaseDate;
    private double price;
}
