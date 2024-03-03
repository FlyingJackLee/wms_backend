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


CREATE TABLE IF NOT EXISTS category(
           cate_id serial primary key not null UNIQUE,
           parent_cate_id integer not null check (parent_cate_id >= 0),
           name varchar(50) not null,
           own_id integer not null,
           UNIQUE (own_id, parent_cate_id, name, own_id),
           constraint fk_cate_user foreign key(own_id) references users(id)
);

CREATE TABLE IF NOT EXISTS merchandise(
          me_id serial primary key not null UNIQUE,
          cate_id integer not null,
          cost decimal(10, 2) not null,
          price decimal(10, 2) not null,
          imei varchar(50) unique,
          create_time timestamp not null DEFAULT CURRENT_TIMESTAMP,
          sold boolean not null default false,
          own_id integer not null,
          constraint fk_me_cate foreign key(cate_id) references category(cate_id),
          constraint fk_me_user foreign key(own_id) references users(id)
);

CREATE TABLE IF NOT EXISTS orders(
        order_id serial primary key not null UNIQUE,
        me_id integer not null,
        selling_price decimal(10, 2) not null,
        returned boolean not null default false,
        remark VARCHAR(100) null,
        selling_time timestamp not null DEFAULT CURRENT_TIMESTAMP,
        own_id integer not null,
        constraint fk_order_user foreign key(own_id) references users(id),
        constraint fk_order_me foreign key(me_id) references merchandise(me_id)
);
-- 当returned为false（未退回订单），不允许二次销售
CREATE UNIQUE INDEX idx_me_re on orders(me_id, returned) where (returned = false);

INSERT INTO category(parent_cate_id, name, own_id) values (0, '华为/HUAWEI', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (0, 'OPPO', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (0, '荣耀/HONOR', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (0, 'VIVO', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (0, '三星/SAMSUNG', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (0, '苹果/Apple', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (0, '魅族/MEIZU', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (0, '一加/ONEPLUS', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (0, '中兴', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (0, '小米/XIAOMI', 1);