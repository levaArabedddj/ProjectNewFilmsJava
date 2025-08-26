package org.example.servicefilm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MovieCreatedEvent {
    private long movieId;
    private String title;
    private String description;
    private String genre;



    public MovieCreatedEvent() {}
    public MovieCreatedEvent(long movieId,String title, String description, String genre) {
        this.movieId = movieId;
        this.title = title;
        this.description = description;
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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
}

