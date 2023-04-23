package com.gmitit01.recommenderservice.entity;


import com.gmitit01.recommenderservice.entity.utils.UuidIdentifiedEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnboardingSelf extends UuidIdentifiedEntity {
    private Boolean smoker;
    private Boolean pets;
    private Integer budget;
    private String roomWanted;
    private Set<String> areas;
    private Integer minTerm;
    private Integer maxTerm;
    private Set<String> amenities;
    private Set<String> interests;
}
