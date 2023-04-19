package com.gmitit01.recommenderservice.entity;

import lombok.Data;


@Data
public class OnboardingProfile extends UuidIdentifiedEntity {
    private String firstName;
    private String lastName;
    private Integer age;
    private String gender;
    private String orientation;
    private String nationality;
    private String occupation;
    private String language;
}
