-- create necessary data types
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname  = 'citext') THEN
        CREATE EXTENSION citext;
    END IF;
    -- phone number
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname  = 'cn_phone_number') THEN
        CREATE DOMAIN cn_phone_number AS varchar(11) CHECK ( value ~ '^1[3-9]\d{9}$');
    END IF;
    -- email
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname  = 'email') THEN
        CREATE DOMAIN email AS citext
            CHECK ( value ~ '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$' );
    END IF;
END$$;

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