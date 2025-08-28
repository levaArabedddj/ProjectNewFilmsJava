package org.example.servicefilm.RabbitMqUpdateMovie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.servicefilm.Entity.Genre;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDtoUpdateRM {
    private long movieId;
    private String title;
    private String description;
    private Genre genreFilm;
    private Long userId;
    private String email;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Genre getGenreFilm() {
        return genreFilm;
    }

    public void setGenreFilm(Genre genreFilm) {
        this.genreFilm = genreFilm;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

