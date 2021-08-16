package com.zharker.netty.serialize;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Customer {
    private long customerNumber;
    private String firstName;
    private String lastName;
    private List<String> middleNames;
}
