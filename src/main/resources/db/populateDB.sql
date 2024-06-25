DELETE FROM user_role;
DELETE FROM meals;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (date_time, description, calories, user_id)
VALUES ('2020-01-30 10:00', 'USER Завтрак', 500, 100000),
       ('2020-01-30 13:00', 'USER Обед', 1000, 100000),
       ('2020-01-30 20:00', 'USER Ужин', 500, 100000),
       ('2020-01-31 00:00', 'USER Еда на граничное значение', 100, 100000),
       ('2020-01-31 10:00', 'USER Завтрак', 1000, 100000),
       ('2020-01-31 13:00', 'USER Обед', 500, 100000),
       ('2020-01-31 20:00', 'USER Ужин', 410, 100000),
       ('2020-01-30 10:00', 'ADMIN Завтрак', 500, 100001),
       ('2020-01-30 13:00', 'ADMIN Обед', 1000, 100001),
       ('2020-01-30 20:00', 'ADMIN Ужин', 500, 100001);