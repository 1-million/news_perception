package com.qr.np.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Person {
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String email;
    private String address;

}
