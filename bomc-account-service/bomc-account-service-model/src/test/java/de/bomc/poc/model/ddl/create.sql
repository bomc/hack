create sequence hibernate_sequence start with 1 increment by 1
create table hibernate_sequences (sequence_name varchar(255) not null, next_val bigint, primary key (sequence_name))
create table T_ACCOUNT (C_ID bigint not null, C_CREATEDATE varbinary(255) not null, C_CREATEUSER varchar(255), C_MODIFYDATE varbinary(255), C_MODIFYUSER varchar(255), C_VERSION bigint, C_NAME varchar(255) not null, primary key (C_ID))
create table T_ACCOUNT_USER (C_FK_ACCOUNT_ID bigint not null, C_FK_User_ID bigint not null, primary key (C_FK_ACCOUNT_ID, C_FK_User_ID))
create table T_ADDRESS (C_ID bigint not null, C_CREATEDATE varbinary(255) not null, C_CREATEUSER varchar(255), C_MODIFYDATE varbinary(255), C_MODIFYUSER varchar(255), C_VERSION bigint, C_CITY varchar(32) not null, C_COUNTRY varchar(20) not null, C_STREET varchar(255) not null, C_ZIP_CODE varchar(10) not null, JOIN_COL_ADDRESS_USER bigint not null, primary key (C_ID))
create table T_COLLECT_ACCOUNT_USER (T_COLLECT_JOIN_ACCOUNT bigint not null, T_COLLECT_JOIN_USER bigint not null, C_USER_TYPE varchar(255) not null, primary key (T_COLLECT_JOIN_ACCOUNT, T_COLLECT_JOIN_USER, C_USER_TYPE))
create table T_LEGAL_USER (C_ID bigint not null, C_CREATEDATE varbinary(255) not null, C_CREATEUSER varchar(255), C_MODIFYDATE varbinary(255), C_MODIFYUSER varchar(255), C_VERSION bigint, C_USERNAME varchar(32) not null, C_COMPANY_ID varchar(255) not null, primary key (C_ID))
create table T_PRIVATE_USER (C_ID bigint not null, C_CREATEDATE varbinary(255) not null, C_CREATEUSER varchar(255), C_MODIFYDATE varbinary(255), C_MODIFYUSER varchar(255), C_VERSION bigint, C_USERNAME varchar(32) not null, C_BIRTH_DATE varbinary(255) not null, primary key (C_ID))
create table T_USER (C_ID bigint not null, C_CREATEDATE varbinary(255) not null, C_CREATEUSER varchar(255), C_MODIFYDATE varbinary(255), C_MODIFYUSER varchar(255), C_VERSION bigint, C_USERNAME varchar(32) not null, primary key (C_ID))
alter table T_LEGAL_USER add constraint UK_t68e1p55mhacjk1qlk917dwk1 unique (C_ID)
alter table T_LEGAL_USER add constraint UK_ol10v9lghbjxh8aisgmsrd0m1 unique (C_USERNAME)
alter table T_PRIVATE_USER add constraint UK_871l7pgbioi2oh5u0j8jdkuar unique (C_ID)
alter table T_PRIVATE_USER add constraint UK_kodcr6hfvtiy9mvm4dghiolnp unique (C_USERNAME)
alter table T_USER add constraint UK_ijnm8hyplavljmeb0cu7nkf9b unique (C_USERNAME)
alter table T_ACCOUNT_USER add constraint FKr14vlcmb2cusw2mepey8x7ww9 foreign key (C_FK_ACCOUNT_ID) references T_ACCOUNT
alter table T_COLLECT_ACCOUNT_USER add constraint FKt1n9n1rtulpt38eqx1ixkkcgq foreign key (T_COLLECT_JOIN_ACCOUNT, T_COLLECT_JOIN_USER) references T_ACCOUNT_USER
