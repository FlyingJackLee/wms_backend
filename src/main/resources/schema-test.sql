DROP TABLE users_detail;
DROP TABLE authorities;
DROP TABLE users;

CREATE TABLE IF NOT EXISTS users(
        id serial primary key not null UNIQUE ,
        username varchar(50) not null UNIQUE CHECK (length(username) >= 5),
        password varchar(500) not null CHECK ( length(password) >= 8 ),
        accountNonExpired boolean default true,
        accountNonLocked boolean default true,
        credentialsNonExpired boolean default true,
        enabled boolean default true
);

CREATE TABLE IF NOT EXISTS users_detail(
        user_id integer not null unique,
        email email null unique,
        phone_number cn_phone_number null unique,
        constraint fk_users_detail_users foreign key(user_id) references users(id)
);

CREATE TABLE IF NOT EXISTS authorities (
                             user_id integer not null,
                             authority varchar(50) not null CHECK ( length(authority) >= 3 ),
                             constraint fk_authorities_users foreign key(user_id) references users(id)
);
CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username on authorities (user_id,authority);