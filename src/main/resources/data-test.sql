INSERT INTO users(username, password) VALUES ('test001', '$2a$10$4ESEs548mQfkBe1v.yVJ0eBvh1QmocYJCC.UN8lDVEmLG6RgWN1x6');
INSERT INTO users_detail(user_id, email, phone_number) VALUES (currval(pg_get_serial_sequence('users','id')), 'test@test.com', '13012341234');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'ROLE_USER');

INSERT INTO users(username, password) VALUES ('testmodified', '$2a$10$4ESEs548mQfkBe1v.yVJ0eBvh1QmocYJCC.UN8lDVEmLG6RgWN1x6');
INSERT INTO users_detail(user_id, email, phone_number) VALUES (currval(pg_get_serial_sequence('users','id')), 'testmodified@test.com', '13212341234');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'ROLE_USER');

INSERT INTO category(parent_cate_id, name) values (0, '华为/HUAWEI');
INSERT INTO category(parent_cate_id, name) values (0, 'OPPO');
INSERT INTO category(parent_cate_id, name) values (0, '荣耀/HONOR');
INSERT INTO category(parent_cate_id, name) values (0, 'VIVO');
INSERT INTO category(parent_cate_id, name) values (0, '三星/SAMSUNG');
INSERT INTO category(parent_cate_id, name) values (0, '苹果/Apple');
INSERT INTO category(parent_cate_id, name) values (0, '魅族/MEIZU');
INSERT INTO category(parent_cate_id, name) values (0, '一加/ONEPLUS');
INSERT INTO category(parent_cate_id, name) values (0, '中兴');
INSERT INTO category(parent_cate_id, name) values (0, '小米/XIAOMI');

INSERT INTO category(parent_cate_id, name) values (2, 'A97');
INSERT INTO category(parent_cate_id, name) values (2, 'RENO 11');
INSERT INTO category(parent_cate_id, name) values (2, 'RENO 10');
INSERT INTO category(parent_cate_id, name) values (3, 'X100');
INSERT INTO category(parent_cate_id, name) values (3, 'X40');


-- A97
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id)
values (11, 1750.00, 1999.00 , 123456789012340,'2024-02-14  10:23:54'  , 1) ;
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id)
values (11, 1750.00, 1999.00 , 123456789012341,'2024-02-14  10:23:54'  , 1) ;
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id)
values (11, 1750.00, 1999.00 , 123456789012342,'2024-02-14  10:23:54'  , 1) ;
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id)
values (11, 1750.00, 1999.00 , 123456789012343,'2024-02-14  10:23:54'  , 1) ;
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id)
values (11, 1750.00, 1999.00 , 123456789012344,'2024-02-14  10:23:54'  , 1) ;


-- RENO 11
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id)
values (12, 1800.00, 2299.00 , 123456789000000,'2024-02-12  10:23:54'  , 1) ;
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id)
values (12, 1800.00, 2299.00 , 123456789000001,'2024-02-12  10:23:54'  , 1) ;
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id)
values (12, 1800.00, 2299.00 , 123456789000002,'2024-02-12  10:23:54'  , 1);

-- X100
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id)
values (14, 2000.00, 2399.00 , 123456789000010,'2024-02-12  10:23:54'  , 1) ;
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id)
values (14, 2000.00, 2399.00 , 123456789000011,'2024-02-12  10:23:54'  , 1) ;
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id)
values (14, 2000.00, 2399.00 , 123456789000012,'2024-02-12  10:23:54'  , 1);
INSERT INTO merchandise(cate_id, cost, price, sold, imei, create_time, own_id)
values (14, 2000.00, 2399.00, true , 123456789000013,'2024-02-12  10:23:54'  , 1);
