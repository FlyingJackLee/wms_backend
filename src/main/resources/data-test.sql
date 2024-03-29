INSERT INTO users(username, password) VALUES ('test001', '$2a$10$4ESEs548mQfkBe1v.yVJ0eBvh1QmocYJCC.UN8lDVEmLG6RgWN1x6');
INSERT INTO users_detail(user_id, email, phone_number) VALUES (currval(pg_get_serial_sequence('users','id')), 'test@test.com', '13012341234');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'ROLE_USER');

INSERT INTO users(username, password) VALUES ('testmodified', '$2a$10$4ESEs548mQfkBe1v.yVJ0eBvh1QmocYJCC.UN8lDVEmLG6RgWN1x6');
INSERT INTO users_detail(user_id, email, phone_number) VALUES (currval(pg_get_serial_sequence('users','id')), 'testmodified@test.com', '13212341234');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'ROLE_USER');

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

INSERT INTO category(parent_cate_id, name, own_id) values (2, 'A97', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (2, 'RENO 11', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (2, 'RENO 10', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (3, 'X100', 1);
INSERT INTO category(parent_cate_id, name, own_id) values (3, 'X40', 1);


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
