--
-- Sample dataset containing a number of customers and orders. The reviews are entirely fictional :)
--
ALTER TABLE T_ITEM ALTER COLUMN C_CREATEDATETIME SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE T_ITEM ALTER COLUMN C_MODIFYDATETIME SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE T_CUSTOMER ALTER COLUMN C_CREATEDATETIME SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE T_CUSTOMER ALTER COLUMN C_MODIFYDATETIME SET DEFAULT CURRENT_TIMESTAMP;  

-- =================================================================================================

-- Items 
INSERT INTO T_ITEM (C_ID, C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_NAME, C_PRICE) VALUES (100, CURRENT_TIMESTAMP(), 'admin', CURRENT_TIMESTAMP(), 'null', 0, 'Apple Watch', 135.5);
INSERT INTO T_ITEM (C_ID, C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_NAME, C_PRICE) VALUES (101, CURRENT_TIMESTAMP(), 'admin', CURRENT_TIMESTAMP(), 'null', 0, 'MacBook Pro', 2000.99);
INSERT INTO T_ITEM (C_ID, C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_NAME, C_PRICE) VALUES (102, CURRENT_TIMESTAMP(), 'admin', CURRENT_TIMESTAMP(), 'null', 0, 'iPhone', 1299);
INSERT INTO T_ITEM (C_ID, C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_NAME, C_PRICE) VALUES (103, CURRENT_TIMESTAMP(), 'admin', CURRENT_TIMESTAMP(), 'null', 0, 'iPad', 799.98);
INSERT INTO T_ITEM (C_ID, C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_NAME, C_PRICE) VALUES (104, CURRENT_TIMESTAMP(), 'admin', CURRENT_TIMESTAMP(), 'null', 0, 'MacBook Air', 1999);
INSERT INTO T_ITEM (C_ID, C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_NAME, C_PRICE) VALUES (105, CURRENT_TIMESTAMP(), 'admin', CURRENT_TIMESTAMP(), 'null', 0, 'iPod', 179);
INSERT INTO T_ITEM (C_ID, C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_NAME, C_PRICE) VALUES (106, CURRENT_TIMESTAMP(), 'admin', CURRENT_TIMESTAMP(), 'null', 0, 'Lady in red', 1000);
INSERT INTO T_ITEM (C_ID, C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_NAME, C_PRICE) VALUES (107, CURRENT_TIMESTAMP(), 'admin', CURRENT_TIMESTAMP(), 'null', 0, 'Apple Watch Nike+', 599.99);
INSERT INTO T_ITEM (C_ID, C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_NAME, C_PRICE) VALUES (108, CURRENT_TIMESTAMP(), 'admin', CURRENT_TIMESTAMP(), 'null', 0, 'Apple TV', 199.99);

-- Customers
INSERT INTO T_CUSTOMER (C_ID, C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_NAME, C_FIRSTNAME, C_USERNAME) VALUES (100, CURRENT_TIMESTAMP(), 'admin', CURRENT_TIMESTAMP(), null, 0, 'admin', 'admin', 'admin@admin.org');
INSERT INTO T_CUSTOMER (C_ID, C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_NAME, C_FIRSTNAME, C_USERNAME) VALUES (101, CURRENT_TIMESTAMP(), 'bomc1', CURRENT_TIMESTAMP(), null, 0, 'bomc', 'bomc', 'bomc@bomc.org');
INSERT INTO T_CUSTOMER (C_ID, C_CREATEDATETIME, C_CREATEUSER, C_MODIFYDATETIME, C_MODIFYUSER, C_VERSION, C_NAME, C_FIRSTNAME, C_USERNAME) VALUES (102, CURRENT_TIMESTAMP(), 'bomc2', CURRENT_TIMESTAMP(), null, 0, 'bomc1', 'bomc1', 'bomc1@bomc.org');
