-- 注意应当与src中的一致
DO
$$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM   pg_catalog.pg_database
            WHERE  datname = 'wms'
        ) THEN
            RAISE NOTICE 'Creating database: %', 'wms';
            EXECUTE 'CREATE DATABASE wms';
        ELSE
            RAISE NOTICE 'Database % already exists.', 'wms';
        END IF;
    END
$$;

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

CREATE TABLE IF NOT EXISTS groups(
     group_id serial primary key not null UNIQUE,
     store_name varchar(100) null ,
     address varchar(200) null ,
     contact varchar(50) null,
     create_time timestamp not null DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users(
    id serial primary key not null UNIQUE ,
    username varchar(50) not null UNIQUE CHECK (length(username) >= 5),
    password varchar(500) not null CHECK ( length(password) >= 8 ),
    accountNonExpired boolean default true,
    accountNonLocked boolean default true,
    credentialsNonExpired boolean default true,
    enabled boolean default true,
    group_id integer not null default 0,
    constraint fk_user_groups foreign key(group_id) references groups(group_id)
);

CREATE TABLE IF NOT EXISTS receipts(
    re_id serial primary key not null UNIQUE,
    remark varchar(200) null,
    group_id integer not null,
    constraint fk_receipts_groups foreign key(group_id) references groups(group_id)
);

CREATE TABLE IF NOT EXISTS users_detail(
    user_id integer not null unique,
    nickname varchar(20) null default '默认名称',
    email email null unique,
    phone_number cn_phone_number null unique,
    avatar varchar(500) default 'default',
    constraint fk_users_detail_users foreign key(user_id) references users(id)
);


CREATE TABLE IF NOT EXISTS group_request(
    group_id integer not null,
--      用户一次只能提交一个申请
    user_id integer not null UNIQUE,
    constraint fk_request_users foreign key(user_id) references users(id),
    constraint fk_request_groups foreign key(group_id) references groups(group_id)
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
           group_id integer not null,
           UNIQUE (group_id, parent_cate_id, name),
           constraint fk_category_groups foreign key(group_id) references groups(group_id),
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
          group_id integer not null,
          constraint fk_merchandise_groups foreign key(group_id) references groups(group_id),
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
        group_id integer not null,
        constraint fk_orders_groups foreign key(group_id) references groups(group_id),
        constraint fk_order_user foreign key(own_id) references users(id),
        constraint fk_order_me foreign key(me_id) references merchandise(me_id)
);
-- 当returned为false（未退回订单），不允许二次销售
CREATE UNIQUE INDEX idx_me_re on orders(me_id, returned) where (returned = false);

CREATE TABLE IF NOT EXISTS notices(
      id serial primary key not null UNIQUE ,
      type varchar(10) not null DEFAULT 'warn',
      publish_time timestamp not null DEFAULT CURRENT_TIMESTAMP,
      content varchar(5000) not null DEFAULT '无'
);

INSERT INTO groups(group_id, store_name, address) VALUES (0, '默认', '默认地址');