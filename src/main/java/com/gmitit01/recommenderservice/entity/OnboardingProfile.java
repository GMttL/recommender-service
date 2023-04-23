package com.gmitit01.recommenderservice.entity;

import com.gmitit01.recommenderservice.entity.utils.UuidIdentifiedEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
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
