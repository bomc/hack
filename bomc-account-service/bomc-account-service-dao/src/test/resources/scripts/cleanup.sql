SET FOREIGN_KEY_CHECKS=0;

TRUNCATE T_ADDRESS;
TRUNCATE T_USER;
TRUNCATE T_LEGAL_USER;
TRUNCATE T_PRIVATE_USER;
TRUNCATE T_GENERATOR_ACCOUNT;
TRUNCATE T_ACCOUNT;
TRUNCATE T_ACCOUNT_USER;
TRUNCATE T_COLLECT_ACCOUNT_USER;

SET FOREIGN_KEY_CHECKS=1;

<!-- drop table T_ACCOUNT if exists -->
<!-- drop table T_ACCOUNT_USER if exists -->
<!-- drop table T_ADDRESS if exists -->
<!-- drop table T_COLLECT_ACCOUNT_USER if exists -->
<!-- drop table T_GENERATOR_ACCOUNT if exists -->
<!-- drop table T_LEGAL_USER if exists -->
<!-- drop table T_PRIVATE_USER if exists -->
<!-- drop table T_USER if exists -->
<!-- drop sequence if exists hibernate_sequence -->
