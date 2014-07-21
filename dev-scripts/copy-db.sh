#!/bin/bash
set -eu

repo_path="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

if [[ $# -ne 2 ]]; then
  echo "$0 <lähdekanta> <kohdekanta>" >&2
  exit 1;
fi

lahde=$1
kohde=$2

# postgres-käyttäjällä ei ole oikeutta hakemistoon /home/vagrant, joten
# vaihdetaan työhakemistoa ennen sudo-komentoa. Postgres ei anna ajaa
# CREATE/DROP DATABASE -komentoja "multi-command stringistä", joten kutsutaan
# psql:ää monta kertaa.
script=$(cat<<EOF
cd /tmp
sudo -u postgres psql -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '$kohde' AND pid <> pg_backend_pid();"
sudo -u postgres psql -c "DROP DATABASE IF EXISTS $kohde"
sudo -u postgres psql -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '$lahde' AND pid <> pg_backend_pid();"
sudo -u postgres psql -c "CREATE DATABASE $kohde WITH TEMPLATE $lahde;"
EOF)

cd $repo_path/vagrant
vagrant ssh db -c "$script"
