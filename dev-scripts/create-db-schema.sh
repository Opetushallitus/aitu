#!/bin/bash
set -eu

REPO_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

set -x

cd $REPO_PATH/ttk-db
lein run 'postgresql://ttk_adm:ttk-adm@127.0.0.1:2345/ttk' -u ttk_user --aituhaku-username aituhaku_user --clear -t $@
