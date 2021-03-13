package com.unknownproject.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Component
@Validated
public class User {

    @Email(message = "")
    private String name;
    private int age;


}
