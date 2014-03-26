#!/bin/bash
set -eu

REPO_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

set -x

read -p "Alustetaanko CAS [K/e]? " cas

cd $REPO_PATH/vagrant

if [[ $cas == [Kk]* ]] || [[ $cas == "" ]] ;
then
  vagrant destroy -f auth
  vagrant up auth
fi

vagrant destroy -f aitu
vagrant up aitu

$REPO_PATH/dev-scripts/deploy.sh
