#!/bin/bash
set -eu

dumpfile=${1:-'amtu-dump.db'}
pgpassfile=${2:-'demo.pgpass'}
db=${3:-'ttk_demo'}
user=${4:-'ttk_demo_adm'}
host=${5:-'185.20.138.89'}
port=${6:-'5432'}

set -x

chmod u-x,go-rwx $pgpassfile
export PGPASSFILE=$pgpassfile

sudo -u postgres psql -d $db -c "drop schema public cascade; create schema public; alter user $user with superuser; grant all on schema public to public; grant all on schema public to postgres; "
pg_restore --no-acl --no-owner -U $user -h $host -d $db $dumpfile
sudo -u postgres psql -d $db -c "alter user $user with nosuperuser; "
