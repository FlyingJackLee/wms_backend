DROP TABLE IF EXISTS users_detail cascade;
DROP TABLE IF EXISTS authorities cascade;
DROP TABLE IF EXISTS merchandise cascade;
DROP TABLE IF EXISTS orders cascade;
DROP TABLE IF EXISTS category cascade;
DROP TABLE IF EXISTS users cascade;


CREATE TABLE users(
        id serial primary key not null UNIQUE ,
        username varchar(50) not null UNIQUE CHECK (length(username) >= 5),
        password varchar(500) not null CHECK ( length(password) >= 8 ),
        accountNonExpired boolean default true,
        accountNonLocked boolean default true,
        credentialsNonExpired boolean default true,
        enabled boolean default true
);

CREATE TABLE users_detail(
        user_id integer not null unique,
        email email null unique,
        phone_number cn_phone_number null unique,
        constraint fk_users_detail_users foreign key(user_id) references users(id)
);

CREATE TABLE authorities (
                             user_id integer not null,
                             authority varchar(50) not null CHECK ( length(authority) >= 3 ),
                             constraint fk_authorities_users foreign key(user_id) references users(id)
);
CREATE UNIQUE INDEX ix_auth_username on authorities (user_id,authority);


CREATE TABLE category(
                         cate_id serial primary key not null UNIQUE,
                         parent_cate_id integer not null check (parent_cate_id >= 0),
                         name varchar(50) not null,
                         own_id integer not null,
                         UNIQUE (own_id, parent_cate_id, name),
                         constraint fk_cate_user foreign key(own_id) references users(id)
);

CREATE TABLE merchandise(
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

CREATE TABLE orders(
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