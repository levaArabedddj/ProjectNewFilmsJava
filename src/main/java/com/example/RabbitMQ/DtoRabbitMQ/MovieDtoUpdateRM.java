package com.example.RabbitMQ.DtoRabbitMQ;

import com.example.Enum.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
