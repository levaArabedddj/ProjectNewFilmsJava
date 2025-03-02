package com.example.Entity;

public class PlanIdea {
 // ТУТ ОПИСАНО ИДЕЮ СЬЕМОК
    // АКТЕРОВ И ИХ ЗАЯВОК ДЛЯ ДАЛЬНЕЙШЕЙ РАБОТЫ ДЛЯ МЕНЯ

    /*
    тут я буду описывать сущность для
    работы с кастингом актеров на роль
    где режиссер будет выбирать кто пойдет на роль
    Выглядеть это будет так , актер отправляет свой заявку на кастинг
    Режиссер осматривает все заявки на эту роль , осматривая заявку
    В случае если заявки понравилась , режиссер добавляет ее в одобренную и указывает день пробных съемок и ему приходит сообщение на почту
    и актер идет на пробные съемки в указанный день , после если режиссеру понравился актер , он в пункте кастинга изменяет строку на
    На пробных съемках изменяет на принят
    код
    CREATE TABLE castings (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    role_name VARCHAR(255), -- Наприклад, "Головний герой"
    description TEXT, -- Опис ролі
    requirements TEXT, -- Вимоги до актора
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

    код промежной таблицы
    CREATE TABLE casting_applications (
    id BIGSERIAL PRIMARY KEY,
    casting_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    status VARCHAR(50) CHECK (status IN ('Pending', 'Approved', 'Rejected', 'Accepted')),
    message TEXT, -- Повідомлення режисера акторові
    FOREIGN KEY (casting_id) REFERENCES castings(id),
    FOREIGN KEY (actor_id) REFERENCES users(user_id)
);
 таблица пробных сьемок
    CREATE TABLE trial_shootings (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    trial_date DATE NOT NULL, -- Дата пробних зйомок
    trial_time TIME NOT NULL, -- Час початку пробних зйомок
    location VARCHAR(255), -- Локація, де проводяться пробні зйомки
    description TEXT, -- Додаткова інформація (наприклад, що брати з собою)
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

 промежуточная таблица Оскільки в одному
 кастингу може брати участь кілька
 акторів та кілька ролей, потрібно створити
 проміжну таблицю.
    CREATE TABLE trial_participants (
    id BIGSERIAL PRIMARY KEY,
    trial_id BIGINT NOT NULL,
    casting_application_id BIGINT NOT NULL,
    casting_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    role_name VARCHAR(255), -- На яку роль пробується актор
    result VARCHAR(50) CHECK (result IN ('Pending', 'Passed', 'Failed')), -- Результат проб
    feedback TEXT, -- Коментар режисера
    FOREIGN KEY (trial_id) REFERENCES trial_shootings(id),
    FOREIGN KEY (casting_application_id) REFERENCES casting_applications(id),
    FOREIGN KEY (casting_id) REFERENCES castings(id),
    FOREIGN KEY (actor_id) REFERENCES users(user_id)
);



    */

}
