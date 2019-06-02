alter table T_ACCOUNT_USER drop constraint FKr14vlcmb2cusw2mepey8x7ww9
alter table T_COLLECT_ACCOUNT_USER drop constraint FKt1n9n1rtulpt38eqx1ixkkcgq
drop table hibernate_sequences if exists
drop table T_ACCOUNT if exists
drop table T_ACCOUNT_USER if exists
drop table T_ADDRESS if exists
drop table T_COLLECT_ACCOUNT_USER if exists
drop table T_LEGAL_USER if exists
drop table T_PRIVATE_USER if exists
drop table T_USER if exists
drop sequence hibernate_sequence
