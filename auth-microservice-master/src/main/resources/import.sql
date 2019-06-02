INSERT INTO t_role VALUES ('Grant', 1, '3813c1aa-d329-4043-997f-1f94e7b3cef4', 0, 'Allows reading (1) parts from db.', 'read1', 0);
INSERT INTO t_role VALUES ('Grant', 2, '446d1b33-4b79-428d-8afe-99ec11fb4c94', 0, 'Allows reading (2) parts from db.', 'read2', 0);
INSERT INTO t_role VALUES ('Role', 3, '8ad8bd00-3d91-4164-9d45-1ceb9d9eeda0', 0, 'This role allows restricted access to the system', 'Default-System_user', 0);
INSERT INTO t_user VALUES ('User', 1, '40c82e3d-8194-4b30-afc0-cdf27c158be3', 0, 1, '2021-03-22 14:49:01', 0, 'Default-System_user', '2016-03-22 14:49:01', 0, 'My@123Password', 'This is a default system-user.', NULL , NUll, 'MALE', 'Default-System_user');
INSERT INTO t_user_password VALUES (1, '4f058635-c356-442b-8f8d-b83a5b8f80a5', 0, 'bomc-1234', '2016-03-22 14:49:01', 1);
INSERT INTO t_user_password_join VALUES (1, 1);
INSERT INTO cor_role_role_join VALUES (3, 1);
INSERT INTO cor_role_role_join VALUES (3, 2);
INSERT INTO cor_role_user_join VALUES (3, 1);
