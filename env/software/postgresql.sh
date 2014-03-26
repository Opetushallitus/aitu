#!/bin/bash
set -eu

# voi olla raskasta ottaa kaikki uusimmat paketit..
# yum -y update
# yum -y upgrade

# konfiguroidaan postgre repo
rpm -i http://yum.postgresql.org/9.2/redhat/rhel-6-x86_64/pgdg-centos92-9.2-6.noarch.rpm

# nyt yum osaa installoida postgren
yum -y install postgresql92-server postgresql92-contrib

# alustetaan postgre
service postgresql-9.2 initdb

# postgre kuuntelemaan ja ottamaan vastaan yhteyksia muualta kuin localhostista
PG_HBA=/var/lib/pgsql/9.2/data/pg_hba.conf

echo 'local   all             all                  peer' > "$PG_HBA"
echo 'host    all             all             all  md5' >> "$PG_HBA"

chown postgres:postgres "$PG_HBA"

echo "listen_addresses = '*'" >> /var/lib/pgsql/9.2/data/postgresql.conf
echo "aitu.kayttaja = '-'" >> /var/lib/pgsql/9.2/data/postgresql.conf

# kayntiin
service postgresql-9.2 start
chkconfig postgresql-9.2 on
