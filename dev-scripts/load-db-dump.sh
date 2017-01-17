#!/bin/bash
set -eu

REPO_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

set -x

cd $REPO_PATH/vagrant
vagrant ssh db -c 'cd /env && ./pgload-ttk.sh /dumps/amtu-dump.db dev-db.pgpass ttk ttk_adm 127.0.0.1'

cd $REPO_PATH/ttk-db
lein run 'postgresql://ttk_adm:ttk-adm@127.0.0.1:2345/ttk' -u ttk_user -t --anonymisointi  --aituhaku-username aituhaku_user
