package com.zharker.netty.serialize;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
    private long orderNumber;
    private Customer customer;
    private Address billTo;
    private Shipping shipping;
    private Address shiipTo;
    private Float total;
}
