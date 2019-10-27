

-- 	Insert some test data.
INSERT INTO bomcdb.t_customer (c_id, c_createdatetime, c_createuser, c_modifydatetime, c_modifyuser, c_version, c_city, c_country, c_date_of_birth, c_email_address, c_first_name, c_house_number, c_last_name, c_phone_number, c_postal_code, c_street) VALUES 
(3, '2019-05-07 11:00:00', 'bomc_1', null, null, 0, 'myCity', 'CH', '2011-11-04', 'bomc_script_1@bomc.org', 'myFirstName1', '42', 'myLastName1', '042-424242', '4242', 'myStreet1'),
(4, '2019-05-07 11:00:00', 'bomc_2', null, null, 0, 'myCity', 'CH', '2011-11-13', 'bomc_script_4@bomc.org', 'myFirstName2', '42', 'myLastName2', '042-424242', '4242', 'myStreet2');

--insert into T_SECURITY_OBJECT (C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_DESCRIPTION, C_NAME, TYPE, C_ID) 
--values ('2019-09-29 11:59:59', 'bomc', null, null, 0, 'a user with extended permissions', 'admin_user', 'ROLE', 3),
--values ('2019-09-29 11:59:59', 'bomc', null, null, 0, 'a user with extended permissions', 'system_user', 'ROLE', 4),
--values ('2019-09-29 11:59:59', 'bomc', null, null, 0, 'a user with extended permissions', 'system_user', 'ROLE', 5);
--values ('2019-09-29 11:59:59', 'bomc', null, null, 0, 'a permissions that allows reading', 'read', 'PERMISSION', 6),
--values ('2019-09-29 11:59:59', 'bomc', null, null, 0, 'a permissions That allows editing', 'edit', 'PERMISSION', 7);
