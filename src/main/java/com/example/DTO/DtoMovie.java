package com.example.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoMovie {
    private long id;
    private String title;
    private String description ;
    private String genre;
//    private Set<DtoActor> actors;
//    private Set<DtoCrewMember> crewMembers;
    private LocalDateTime dateTimeCreated;
    private Set<DtoShootingDay> shootingDays;
    private DtoScript script;
    private DtoFinance finance;
}
