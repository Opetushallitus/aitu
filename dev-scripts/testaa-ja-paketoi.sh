#!/bin/bash
set -eu

repo_path="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

vaadi_ohjelma() {
    type -P $1 2>&1 > /dev/null || {
        echo >&2 "Puuttuva riippuvuus: $2"
        echo >&2 $3
        exit 1
    }
}

vaadi_versio() {
    local versio=`/bin/bash -c "$1"`
    if [[ $versio != $2 ]]; then
        echo >&2 "Väärä versio: $versio (vaaditaan $2)"
        echo >&2 $3
        exit 1
    fi
}

echo ===============================================================================
echo Tarkistetaan riippuvuudet
echo ===============================================================================
vaadi_ohjelma vagrant Vagrant 'Ks. http://downloads.vagrantup.com/'
vaadi_ohjelma lein Leiningen 'Ks. http://leiningen.org/'
vaadi_versio "lein version | cut -d ' ' -f 1,2" 'Leiningen 2.3.4' 'Aja komento: lein upgrade 2.3.4'

echo ===============================================================================
echo Tarkistetaan, ettei Git-repossa ole muutoksia
echo ===============================================================================
cd $repo_path
git_status=`git status --porcelain`
if [[ -n "$git_status" ]]; then
  echo >&2 "Reposta löytyi muutoksia:"
  echo >&2 $git_status
  exit 1
fi

echo ===============================================================================
echo Ajetaan yksikkötestit
echo ===============================================================================
cd $repo_path/ttk
lein do clean, test

echo ===============================================================================
echo Alustetaan tietokanta
echo ===============================================================================
$repo_path/dev-scripts/init-db.sh

echo ===============================================================================
echo Ajetaan integraatiotestit
echo ===============================================================================
cd $repo_path/ttk
lein test :integraatio

echo ===============================================================================
echo Alustetaan sovelluspalvelin
echo ===============================================================================
cd $repo_path/ttk
echo "`git rev-parse HEAD` | `date '+%Y-%m-%d %H:%M:%S'`" > resources/build-id.txt
$repo_path/dev-scripts/init-aitu.sh

echo ===============================================================================
echo Ajetaan e2e-testit
echo ===============================================================================
export AITU_URL='http://192.168.50.52/aitu'
cd $repo_path/amtu-e2e
lein test

echo ===============================================================================
echo Asennuspaketti valmis tiedostossa $repo_path/ttk/target/ttk-standalone.jar
echo ===============================================================================
