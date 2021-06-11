package com.dailyUpdate.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;


@Data
@AllArgsConstructor
@NoArgsConstructor

@Component
@Validated
public class User {

    //@Email(message = "")
    private String name;
    private int age;


}
