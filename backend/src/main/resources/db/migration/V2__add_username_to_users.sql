set @schema_name = database();
set @has_username = (select count(*)
                     from information_schema.columns
                     where table_schema = @schema_name
                       and table_name = 'users'
                       and column_name = 'username');

set @add_column_sql = if(
  @has_username = 0,
  'alter table users add column username varchar(50)',
  'select 1'
                      );
prepare add_column_stmt from @add_column_sql;
execute add_column_stmt;
deallocate prepare add_column_stmt;

update users
set username = left(substring_index(email, '@', 1), 50)
where username is null
   or trim(username) = '';

alter table users
  modify column username varchar(50) not null;
