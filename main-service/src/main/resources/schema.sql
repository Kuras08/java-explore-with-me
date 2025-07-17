-- Таблица пользователей
CREATE TABLE users
(
    id    BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name  VARCHAR(255) NOT NULL
);

-- Таблица категорий
CREATE TABLE categories
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Таблица событий
CREATE TABLE events
(
    id                 BIGSERIAL PRIMARY KEY,
    annotation         TEXT         NOT NULL,
    category_id        BIGINT       NOT NULL REFERENCES categories (id),
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description        TEXT         NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id       BIGINT       NOT NULL REFERENCES users (id),
    location_lat       FLOAT8       NOT NULL,
    location_lon       FLOAT8       NOT NULL,
    paid               BOOLEAN      NOT NULL,
    participant_limit  INTEGER      NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN      NOT NULL,
    state              VARCHAR(50)  NOT NULL,
    title              VARCHAR(255) NOT NULL
);

-- Таблица заявок на участие
CREATE TABLE requests
(
    id           BIGSERIAL PRIMARY KEY,
    event_id     BIGINT      NOT NULL REFERENCES events (id),
    requester_id BIGINT      NOT NULL REFERENCES users (id),
    status       VARCHAR(50) NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT uq_event_requester UNIQUE (event_id, requester_id)
);

-- Таблица подборок (compilations)
CREATE TABLE compilations
(
    id     BIGSERIAL PRIMARY KEY,
    title  VARCHAR(255) NOT NULL,
    pinned BOOLEAN      NOT NULL DEFAULT FALSE
);

-- Таблица связей подборок и событий
CREATE TABLE compilation_events
(
    compilation_id BIGINT NOT NULL REFERENCES compilations (id) ON DELETE CASCADE,
    event_id       BIGINT NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);
