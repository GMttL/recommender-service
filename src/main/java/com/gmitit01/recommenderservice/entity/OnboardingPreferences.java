package com.gmitit01.recommenderservice.entity;


import lombok.Data;

@Data
public class OnboardingPreferences extends UuidIdentifiedEntity {
    private Boolean smokersOK;
    private Boolean petsOK;
    private String Occupation;
    private Integer minAge;
    private Integer maxAge;
    private String genderRequired;
    private String personalDescription;
}
