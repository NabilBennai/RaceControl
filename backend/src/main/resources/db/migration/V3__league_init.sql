create table if not exists leagues
(
  id            bigint primary key auto_increment,
  name          varchar(120)  not null,
  description   varchar(2000) not null,
  game_platform varchar(30)   not null,
  visibility    varchar(20)   not null,
  created_at    timestamp     not null default current_timestamp
);

create table if not exists league_members
(
  id        bigint primary key auto_increment,
  league_id bigint      not null,
  user_id   bigint      not null,
  role      varchar(20) not null,
  constraint fk_league_members_league foreign key (league_id) references leagues (id) on delete cascade,
  constraint fk_league_members_user foreign key (user_id) references users (id) on delete cascade,
  constraint uk_league_members_league_user unique (league_id, user_id)
);
