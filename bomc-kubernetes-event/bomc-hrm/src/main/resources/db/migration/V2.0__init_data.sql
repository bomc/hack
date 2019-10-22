-- 	Insert some customer test data.
insert into public.t_customer (c_id, c_createdatetime, c_createuser, c_modifydatetime, c_modifyuser, c_version, c_city, c_country, c_date_of_birth, c_email_address, c_first_name, c_house_number, c_last_name, c_phone_number, c_postal_code, c_street) values 
(1, '2019-05-07 11:00:00', 'bomc', null, null, 0, 'myCity1', 'CH', '2011-11-04', 'bomc1@bomc.org', 'myFirstName1', '42', 'myLastName1', '042-424242', '4242', 'myStreet1'),
(2, '2019-05-07 11:00:00', 'bomc', null, null, 0, 'myCity2', 'CH', '2011-11-13', 'bomc2@bomc.org', 'myFirstName2', '42', 'myLastName2', '042-424242', '4242', 'myStreet2');

-- Insert role and permission data.
insert into public.t_security_object (c_createdatetime, c_createuser, c_modifydatetime, c_modifyuser, c_version, c_description, c_name, TYPE, c_id) VALUES 
('2019-09-29 11:59:59', 'bomc', null, null, 0, 'a user with extended permissions', 'ADMIN_USER', 'ROLE', 3),
('2019-09-29 11:59:59', 'bomc', null, null, 0, 'a user with extended permissions', 'SYSTEM_USER', 'ROLE', 4),
('2019-09-29 11:59:59', 'bomc', null, null, 0, 'a user with extended permissions', 'APPLICATION_USER', 'ROLE', 5),
('2019-09-29 11:59:59', 'bomc', null, null, 0, 'a user with extended permissions', 'TEST_USER', 'ROLE', 6),
('2019-09-29 11:59:59', 'bomc', null, null, 0, 'a permissions that allows reading', 'READ', 'PERMISSION', 7),
('2019-09-29 11:59:59', 'bomc', null, null, 0, 'a permissions that allows editing', 'EDIT', 'PERMISSION', 8);

-- Insert user data.
insert into public.t_user (c_createdatetime, c_createuser, c_modifydatetime, c_modifyuser, c_version, c_enabled, c_expiration_date, c_extern, c_fullname, c_last_password_change, c_locked, c_password, c_comment, c_image, c_phone_no, c_sex, c_username, dtype, c_id) values
('2019-09-29 11:59:59', 'bomc', null, null, 0, true, '2019-12-31 11:59:59', true, 'boris brexit', null, false, 'My@123Password', 'no comment', null, '04212345678', 'MALE', 'boris@brexit.uk', 'UserEntity', 9);

-- Insert userPassword data.
insert into public.t_user_password (c_id, c_createdatetime, c_createuser, c_modifydatetime, c_modifyuser, c_version, c_password, c_password_changed, c_user_join) values 
(10, '2019-09-29 11:59:59', 'bomc', null, null, 0, 'MyNew@123Password', '2019-09-30 00:00:59', 9);

-- Create relationship between user and role.
insert into public.t_role_user_join values
(6, 9);

-- Create relationship between role and permission.
insert into public.t_role_permission_join values
(3, 7);