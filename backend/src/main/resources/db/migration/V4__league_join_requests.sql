create table if not exists league_join_requests
(
  id         bigint primary key auto_increment,
  league_id  bigint      not null,
  user_id    bigint      not null,
  status     varchar(20) not null,
  created_at timestamp   not null default current_timestamp,
  constraint fk_join_requests_league foreign key (league_id) references leagues (id) on delete cascade,
  constraint fk_join_requests_user foreign key (user_id) references users (id) on delete cascade,
  constraint uk_join_request_league_user unique (league_id, user_id)
);

create table if not exists league_invitation_codes
(
  id         bigint primary key auto_increment,
  league_id  bigint      not null,
  code       varchar(32) not null unique,
  active     boolean     not null default true,
  created_at timestamp   not null default current_timestamp,
  constraint fk_invitation_codes_league foreign key (league_id) references leagues (id) on delete cascade
);
