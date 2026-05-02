create table if not exists users
(
  id       bigint primary key auto_increment,
  email    varchar(255) not null unique,
  password varchar(255) not null,
  role     varchar(20)  not null
);
insert into users(email, password, role)
values ('admin@example.com', '$2a$10$B3YdE6ZlYfS9Y2bn4s7Dg.vVJ8zjFKw0SJxA6nQ5jY0x9E6fY7h1W', 'ADMIN'),
       ('user@example.com', '$2a$10$7QeM3eYuTsr8xeUlR68Y8.z3WFoK5zwxGLpIvFbcv1qzN.QI8g4Qe', 'USER');
