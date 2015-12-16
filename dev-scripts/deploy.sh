#!/bin/bash
set -eu

repo_path="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

set -x

cd $repo_path/ttk
lein do clean, uberjar

cd $repo_path/ttk-db
lein do clean, uberjar

cd $repo_path/ansible
chmod 600 yhteiset/dev_id_rsa
ssh-add yhteiset/dev_id_rsa
ansible-playbook -v -i aitu_vagrant/hosts yhteiset/julkaise_paikallinen_versio.yml -e "sovellus_jar=\"$repo_path/ttk/target/ttk-standalone.jar\" migraatio_jar=\"$repo_path/ttk-db/target/ttk-db-standalone.jar\""
