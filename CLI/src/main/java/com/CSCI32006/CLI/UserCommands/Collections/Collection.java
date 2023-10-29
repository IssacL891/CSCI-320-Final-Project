package com.CSCI32006.CLI.UserCommands.Collections;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.shell.component.support.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Collection {
    private int userId;
    private int collectionId;
    private String collectionName;

}
