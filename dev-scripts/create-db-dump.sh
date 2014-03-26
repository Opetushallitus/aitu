#!/bin/bash
set -eu

REPO_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

set -x

cd $REPO_PATH/vagrant
vagrant ssh db -c 'cd /env && ./pgdump-ttk.sh /dumps/amtu-dump.db dev-db.pgpass ttk ttk_adm 127.0.0.1'
