-- Пользователи
/*INSERT INTO users (email, name)
VALUES ('alice@example.com', 'Alice'),
       ('bob@example.com', 'Bob'),
       ('charlie@example.com', 'Charlie');

-- Категории
INSERT INTO categories (name)
VALUES ('Концерты'),
       ('Выставки'),
       ('Спорт');

-- События
INSERT INTO events (annotation, category_id, created_on, description, event_date, initiator_id,
                    location_lat, location_lon, paid, participant_limit, published_on, request_moderation, state, title)
VALUES ('Краткое описание 1', 1, now(), 'Описание события 1', now() + interval '5 days', 1,
        55.75, 37.61, true, 100, now(), true, 'PUBLISHED', 'Концерт группы X'),

       ('Краткое описание 2', 2, now(), 'Описание события 2', now() + interval '10 days', 2,
        59.93, 30.31, false, 50, now(), false, 'PUBLISHED', 'Выставка Y');

-- Заявки
INSERT INTO requests (event_id, requester_id, status, created)
VALUES (1, 2, 'CONFIRMED', now()),
       (1, 3, 'PENDING', now()),
       (2, 1, 'CONFIRMED', now());

-- Подборки
INSERT INTO compilations (title, pinned)
VALUES ('Лучшие события недели', true),
       ('Рекомендуем посетить', false);

-- Связи подборок и событий
INSERT INTO compilation_events (compilation_id, event_id)
VALUES (1, 1),
       (1, 2),
       (2, 2);
/*