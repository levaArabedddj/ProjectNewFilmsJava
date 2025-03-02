package com.example.Entity;

public class movie_chats {
    /*

    Тут режиссер или другой будет иметь
    возможность создать чат пример идеи на скл

    CREATE TABLE movie_chats (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    chat_type VARCHAR(50) CHECK (chat_type IN ('Actors', 'Crew', 'General')),
    chat_link VARCHAR(500) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

     */
}
