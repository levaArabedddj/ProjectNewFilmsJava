package com.example.Service;

import com.example.Repository.VisitorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitorService {
/*
* 1. Создать возможность просматривать детальную информацию юзера про фильмы
* 2. Узнавать детали каждого фильма
* 3. Смотреть детали про актера и режисера фильма
* 4. Оставлять отзывы о фильмах которые были сняты
* 5. Лайкать трейлеры к фильмам или наоборот
* 6. Комментировать трейлера
* 7. Интеграция с социальными сетями(возможность авторизации через гугл)
*
*
*
* */
    public final VisitorRepo visitorRepo;

    @Autowired
    public VisitorService(VisitorRepo visitorRepo) {
        this.visitorRepo = visitorRepo;
    }



}
