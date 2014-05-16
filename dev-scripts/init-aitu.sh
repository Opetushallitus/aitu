#!/bin/bash
set -eu

repo_path="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

set -x

read -p "Alustetaanko CAS-palvelin [K/e]? " cas
read -p "Alustetaanko tietokantapalvelin [K/e]? " db

cd $repo_path/vagrant

if [[ $cas == [Kk]* ]] || [[ $cas == "" ]]; then
  vagrant destroy -f auth
  vagrant up auth
fi

if [[ $db == [Kk]* ]] || [[ $db == "" ]]; then
  $repo_path/dev-scripts/init-db.sh
fi

vagrant destroy -f aitu
vagrant up aitu

$repo_path/dev-scripts/deploy.sh
