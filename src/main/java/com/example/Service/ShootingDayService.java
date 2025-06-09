package com.example.Service;

import com.example.DTO.DtoShootingDay;
import com.example.Entity.MoviesPackage.Movies;
import com.example.Entity.MoviesPackage.ShootingDay;
import com.example.Exception.ApiException;
import com.example.Repository.MoviesRepo;
import com.example.Repository.ShootingDayRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShootingDayService {


    private final ShootingDayRepo repo;
    private final MoviesRepo repoMovies;

    @Autowired
    public ShootingDayService(ShootingDayRepo repo, MoviesRepo repoMovies) {
        this.repo = repo;
        this.repoMovies = repoMovies;
    }

    public DtoShootingDay createShootingDay(Long filmId, DtoShootingDay dto) {
        boolean exists = repo.existsByShootingDateAndMovieId(dto.getShootingDate(), filmId);
        if (exists) {
            throw new ApiException("Shooting already exists on this day");
        }


        Movies movie = repoMovies.findById(filmId)
                .orElseThrow(() -> new ApiException("Film not found"));


        ShootingDay shootingDay = new ShootingDay();
        shootingDay.setShootingDate(dto.getShootingDate());
        shootingDay.setShootingTime(dto.getShootingTime());
        shootingDay.setShootingLocation(dto.getShootingLocation());
        shootingDay.setEstimatedDurationHours(dto.getEstimatedDurationHours());
        shootingDay.setMovie(movie);


        repo.save(shootingDay);


        DtoShootingDay result = new DtoShootingDay();
        result.setShootingDate(shootingDay.getShootingDate());
        result.setShootingTime(shootingDay.getShootingTime());
        result.setShootingLocation(shootingDay.getShootingLocation());
        result.setEstimatedDurationHours(shootingDay.getEstimatedDurationHours());

        return result;
    }
}
