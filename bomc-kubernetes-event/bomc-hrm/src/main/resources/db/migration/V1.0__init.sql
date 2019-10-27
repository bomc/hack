
    alter table if exists bomcdb.t_role_permission_join 
       drop constraint if exists FKlhnhd7o7ryc0eugatw8cnvxq4;

    alter table if exists bomcdb.t_role_permission_join 
       drop constraint if exists FK2uv274out3q80hyw2vrb00fic;

    alter table if exists bomcdb.t_role_user_join 
       drop constraint if exists FKt1k39e0y68vfko3ancuam40wq;

    alter table if exists bomcdb.t_role_user_join 
       drop constraint if exists FK2oooo5jjf95ll5psx4f8eyhku;

    alter table if exists bomcdb.t_user_password 
       drop constraint if exists FKrvx5dp2ockv3erm09bgdmxukj;

    drop table if exists bomcdb.t_customer cascade;

    drop table if exists bomcdb.t_role_permission_join cascade;

    drop table if exists bomcdb.t_role_user_join cascade;

    drop table if exists bomcdb.t_security_object cascade;

    drop table if exists bomcdb.t_user cascade;

    drop table if exists bomcdb.t_user_password cascade;

    drop sequence if exists bomcdb.hibernate_sequence;
    
    -- Start with 1001 to let the lower ones reserved for flyway init scripts.
    create sequence bomcdb.hibernate_sequence
      start with 1001
	--  maxvalue 9999999999999999999
	--  minvalue 0
	--  nocycle
	--  nocache
	--  noorder
      ;
      
    create table bomcdb.t_customer (
       c_id int8 not null,
        c_createdatetime timestamp not null,
        c_createuser varchar(255),
        c_modifydatetime timestamp,
        c_modifyuser varchar(255),
        c_version int8,
        c_city varchar(40),
        c_country varchar(3),
        c_date_of_birth date not null,
        c_email_address varchar(255),
        c_first_name varchar(30),
        c_house_number varchar(5),
        c_last_name varchar(30),
        c_phone_number varchar(15),
        c_postal_code varchar(10),
        c_street varchar(60),
        primary key (c_id)
    );

    create table bomcdb.t_role_permission_join (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table bomcdb.t_role_user_join (
       role_id int8 not null,
        user_id int8 not null,
        primary key (role_id, user_id)
    );

    create table bomcdb.t_security_object (
       type varchar(20) not null,
        c_id int8 not null,
        c_createdatetime timestamp not null,
        c_createuser varchar(255),
        c_modifydatetime timestamp,
        c_modifyuser varchar(255),
        c_version int8,
        c_description varchar(1024),
        c_name varchar(255) not null,
        c_immutable boolean,
        primary key (c_id)
    );

    create table bomcdb.t_user (
       DTYPE varchar(31) not null,
        c_id int8 not null,
        c_createdatetime timestamp not null,
        c_createuser varchar(255),
        c_modifydatetime timestamp,
        c_modifyuser varchar(255),
        c_version int8,
        c_enabled boolean,
        c_expiration_date timestamp,
        c_extern boolean,
        c_fullname varchar(255),
        c_last_password_change timestamp,
        c_locked boolean,
        c_password varchar(255),
        c_comment varchar(255),
        c_image bytea,
        c_phone_no varchar(255),
        c_sex varchar(255),
        c_username varchar(255) not null,
        primary key (c_id)
    );

    create table bomcdb.t_user_password (
       c_id int8 not null,
        c_createdatetime timestamp not null,
        c_createuser varchar(255),
        c_modifydatetime timestamp,
        c_modifyuser varchar(255),
        c_version int8,
        c_password varchar(255),
        c_password_changed timestamp,
        c_user_join int8,
        primary key (c_id)
    );

    alter table bomcdb.t_customer 
       add constraint UK_4lgnk4im8h61kkr0sv1pqyf1f unique (c_email_address);

    alter table bomcdb.t_security_object 
       add constraint UK_7nthv2aiewy78qk03b4ine6nw unique (c_name);

    alter table bomcdb.t_user 
       add constraint UK_5cje2jmi6q92umq0thq2idvyj unique (c_username);

    alter table bomcdb.t_role_permission_join 
       add constraint FKlhnhd7o7ryc0eugatw8cnvxq4 
       foreign key (permission_id) 
       references bomcdb.t_security_object;

    alter table bomcdb.t_role_permission_join 
       add constraint FK2uv274out3q80hyw2vrb00fic 
       foreign key (role_id) 
       references bomcdb.t_security_object;

    alter table bomcdb.t_role_user_join 
       add constraint FKt1k39e0y68vfko3ancuam40wq 
       foreign key (user_id) 
       references bomcdb.t_user;

    alter table bomcdb.t_role_user_join 
       add constraint FK2oooo5jjf95ll5psx4f8eyhku 
       foreign key (role_id) 
       references bomcdb.t_security_object;

    alter table bomcdb.t_user_password 
       add constraint FKrvx5dp2ockv3erm09bgdmxukj 
       foreign key (c_user_join) 
       references bomcdb.t_user;


    