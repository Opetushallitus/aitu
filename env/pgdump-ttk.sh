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

pg_dump -Fc -U $user -h $host -p $port $db > $dumpfile
