#!/bin/bash
set -eu

REPO_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

set -x

cd $REPO_PATH/ttk
lein do clean, uberjar
ttk_jar=$REPO_PATH/ttk/target/ttk-standalone.jar

cd $REPO_PATH/ttk-db
lein do clean, uberjar
ttk_db_jar=$REPO_PATH/ttk-db/target/ttk-db-standalone.jar

chmod go= $REPO_PATH/env/ssh/dev_id_rsa
cd $REPO_PATH/ttk

export AITU_DB_USER=ttk_user
export AITUHAKU_DB_USER=aituhaku_user
export AITU_SSH_KEY=$REPO_PATH/env/ssh/dev_id_rsa
./deploy.sh -c -t $ttk_jar $ttk_db_jar aituadmin@192.168.50.52
