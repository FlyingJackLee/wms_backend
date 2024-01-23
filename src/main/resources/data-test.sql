INSERT INTO users(username, password) VALUES ('test001', '$2a$10$4ESEs548mQfkBe1v.yVJ0eBvh1QmocYJCC.UN8lDVEmLG6RgWN1x6');
INSERT INTO users_detail(user_id, email, phone_number) VALUES (currval(pg_get_serial_sequence('users','id')), 'test@test.com', '13012341234');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'ROLE_USER');

INSERT INTO users(username, password) VALUES ('testmodified', '$2a$10$4ESEs548mQfkBe1v.yVJ0eBvh1QmocYJCC.UN8lDVEmLG6RgWN1x6');
INSERT INTO users_detail(user_id, email, phone_number) VALUES (currval(pg_get_serial_sequence('users','id')), 'testmodified@test.com', '13212341234');
INSERT INTO authorities(user_id, authority) VALUES (currval(pg_get_serial_sequence('users','id')), 'ROLE_USER');

