create table if not exists seasons
(
  id         bigint primary key auto_increment,
  league_id  bigint       not null,
  name       varchar(120) not null,
  start_date date         not null,
  end_date   date         not null,
  status     varchar(20)  not null,
  created_at timestamp    not null default current_timestamp,
  constraint fk_seasons_league foreign key (league_id) references leagues (id) on delete cascade
);
