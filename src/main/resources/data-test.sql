-- 注意自增序！！！不要！！！手动插入id，否则会导致mybatis insert获取id时发生错误，这里的group默认组为特殊情况，不可模仿
INSERT INTO groups(group_id, store_name, address) VALUES (0, '默认', '默认地址');
INSERT INTO groups(store_name, address) VALUES ('测试', '测试地址');
INSERT INTO groups(store_name, address) VALUES ('测试2', '测试地址2');

INSERT INTO users(username, password) VALUES ('test001', '$2a$10$4ESEs548mQfkBe1v.yVJ0eBvh1QmocYJCC.UN8lDVEmLG6RgWN1x6');
INSERT INTO users_detail(user_id, email, phone_number) VALUES (currval(pg_get_serial_sequence('users','id')), 'test@test.com', '13012341234');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'ROLE_DEFAULT');

INSERT INTO users(username, password) VALUES ('testmodified', '$2a$10$4ESEs548mQfkBe1v.yVJ0eBvh1QmocYJCC.UN8lDVEmLG6RgWN1x6');
INSERT INTO users_detail(user_id, email, phone_number) VALUES (currval(pg_get_serial_sequence('users','id')), 'testmodified@test.com', '13212341234');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'DEFAULT_STAFF');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'PERMISSION:shopping');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'PERMISSION:inventory');
UPDATE users SET group_id = 1 WHERE id = currval(pg_get_serial_sequence('users','id'));

INSERT INTO users(username, password) VALUES ('testunit', '$2a$10$4ESEs548mQfkBe1v.yVJ0eBvh1QmocYJCC.UN8lDVEmLG6RgWN1x6');
INSERT INTO users_detail(user_id, email, phone_number) VALUES (currval(pg_get_serial_sequence('users','id')), 'test002@test.com', '13212341236');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'DEFAULT_STAFF');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'PERMISSION:shopping');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'PERMISSION:inventory');
UPDATE users SET group_id = 1 WHERE id = currval(pg_get_serial_sequence('users','id'));

INSERT INTO category(parent_cate_id, name, own_id, group_id) values (0, '华为/HUAWEI', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (0, 'OPPO', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (0, '荣耀/HONOR', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (0, 'VIVO', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (0, '三星/SAMSUNG', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (0, '苹果/Apple', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (0, '魅族/MEIZU', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (0, '一加/ONEPLUS', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (0, '中兴', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (0, '小米/XIAOMI', 1, 1);

INSERT INTO category(parent_cate_id, name, own_id, group_id) values (2, 'A97', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (2, 'RENO 11', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (2, 'RENO 10', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (3, 'X100', 1, 1);
INSERT INTO category(parent_cate_id, name, own_id, group_id) values (3, 'X40', 1, 1);


-- A97
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id, group_id)
values (11, 1750.00, 1999.00 , 123456789012340,'2024-02-14  10:23:54'  , 1, 1);
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id, group_id)
values (11, 1750.00, 1999.00 , 123456789012341,'2024-02-14  10:23:54'  , 1, 1);
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id, group_id)
values (11, 1750.00, 1999.00 , 123456789012342,'2024-02-14  10:23:54'  , 1, 1);
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id, group_id)
values (11, 1750.00, 1999.00 , 123456789012343,'2024-02-14  10:23:54'  , 1, 1);
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id, group_id)
values (11, 1750.00, 1999.00 , 123456789012344,'2024-02-14  10:23:54'  , 1, 1);


-- RENO 11
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id, group_id)
values (12, 1800.00, 2299.00 , 123456789000000,'2024-02-12  10:23:54'  , 1, 1);
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id, group_id)
values (12, 1800.00, 2299.00 , 123456789000001,'2024-02-12  10:23:54'  , 1, 1);
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id, group_id)
values (12, 1800.00, 2299.00 , 123456789000002,'2024-02-12  10:23:54'  , 1, 1);

-- X100
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id, group_id)
values (14, 2000.00, 2399.00 , 123456789000010,'2024-02-12  10:23:54'  , 1, 1);
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id, group_id)
values (14, 2000.00, 2399.00 , 123456789000011,'2024-02-12  10:23:54'  , 1, 1);
INSERT INTO merchandise(cate_id, cost, price, imei, create_time, own_id, group_id)
values (14, 2000.00, 2399.00 , 123456789000012,'2024-02-12  10:23:54'  , 1, 1);
INSERT INTO merchandise(cate_id, cost, price, sold, imei, create_time, own_id, group_id)
values (14, 2000.00, 2399.00, true , 123456789000013,'2024-02-12  10:23:54', 1, 1);


INSERT INTO notices (type, publish_time, content) VALUES ('update', '2024-03-13 23:29:05.000000', '测试内容');
INSERT INTO notices (type, publish_time, content) VALUES ('update', '2024-03-10 23:29:05.000000', '测试内容');
INSERT INTO notices (type, publish_time, content) VALUES ('update', '2024-03-12 23:29:05.000000', '测试内容');
INSERT INTO notices (type, publish_time, content) VALUES ('update', '2024-03-11 23:29:05.000000', '测试内容');

INSERT INTO notices (type, publish_time, content) VALUES ('warn', '2024-03-10 23:29:05.000000', '测试内容');
INSERT INTO notices (type, publish_time, content) VALUES ('warn', '2024-03-11 23:29:05.000000', '测试内容');