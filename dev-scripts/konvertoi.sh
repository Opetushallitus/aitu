#!/bin/bash
set -eu

repo_path="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

dump=true
if [[ -n $1 ]]; then
    if [[ $1 == '--ei-dumppia' ]]; then
        dump=false
        echo 'ei dumppia'
    else
        echo 'Virheellinen valitsin:' $1
        echo 'Ainoa sallittu valitsin on --ei-dumppia'
        exit 1
    fi
fi

set -x

cd $repo_path/ttkr-konversio
lein run
PGPASSWORD=ttk-adm
psql -Uttk_adm -h127.0.0.1 -p2345 ttk < $repo_path/ttk-db/resources/sql/tutkinnonosat.sql
psql -Uttk_adm -h127.0.0.1 -p2345 ttk < $repo_path/ttk-db/resources/sql/osaamisalat.sql
psql -Uttk_adm -h127.0.0.1 -p2345 ttk < $repo_path/ttk-db/resources/sql/oppilaitokset.sql

if $dump; then
    cd $repo_path/vagrant
    vagrant ssh db -c 'cd /env && ./pgdump-ttk.sh /dumps/amtu-dump.db dev-db.pgpass ttk ttk_adm 127.0.0.1'
fi
