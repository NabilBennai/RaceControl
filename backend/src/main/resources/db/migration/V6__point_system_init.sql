create table if not exists point_systems
(
  id         bigint primary key auto_increment,
  season_id  bigint       not null unique,
  name       varchar(120) not null,
  created_at timestamp    not null default current_timestamp,
  constraint fk_point_system_season foreign key (season_id) references seasons (id) on delete cascade
);

create table if not exists point_rules
(
  id              bigint primary key auto_increment,
  point_system_id bigint      not null,
  type            varchar(40) not null,
  position_rank   int         null,
  points          int         not null,
  constraint fk_point_rule_system foreign key (point_system_id) references point_systems (id) on delete cascade
);
