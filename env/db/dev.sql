create user ttk_adm with password 'ttk-adm';
CREATE DATABASE ttk;
GRANT ALL PRIVILEGES ON DATABASE ttk to ttk_adm;

create user ttk_user with password 'ttk';
GRANT CONNECT ON DATABASE ttk TO ttk_user;

create user aituhaku_user with password 'aituhaku';

\connect ttk

create schema aituhaku authorization ttk_adm;
