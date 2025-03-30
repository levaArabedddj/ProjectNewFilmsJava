package com.example.Repository;


import com.example.Entity.DirectorProfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectorProfilesRepo extends JpaRepository<DirectorProfiles, Long> {


    @Query("SELECT dp FROM DirectorProfiles dp WHERE dp.director.id = :directorId")
    Optional<DirectorProfiles> findByDirectorId(@Param("directorId") Long directorId);}