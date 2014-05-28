#!/bin/bash
set -eu

if [[ $# -lt 2 ]] ; then
  echo "$0 <dumppi> <inventory> [ansiblen argumentit]"
  exit 1
fi

script_path=$(cd $(dirname $0) && pwd)

dumppi="$(cd $(dirname $1) && pwd)/$(basename $1)"
inventory=$2
ansiblen_argumentit=${@:3}

read -s -p 'Tietokannan pääkäyttäjän salasana: ' tietokannan_paakayttajan_salasana
echo
if [[ -ne $tietokannan_paakayttajan_salasana ]] ; then
  ansiblen_argumentit="$ansiblen_argumentit -e \"tietokannan_paakayttajan_salasana='$tietokannan_paakayttajan_salasana'\""
fi

ansible-playbook $ansiblen_argumentit -i "$inventory" "$script_path/lataa_tietokantadumppi.yml" -e "dumppi='$dumppi'"
