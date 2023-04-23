package com.gmitit01.recommenderservice.entity;


import com.gmitit01.recommenderservice.entity.utils.UuidIdentifiedEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnboardingPreferences extends UuidIdentifiedEntity {
    private Boolean smokersOK;
    private Boolean petsOK;
    private String Occupation;
    private Integer minAge;
    private Integer maxAge;
    private String genderRequired;
    private String personalDescription;
}
