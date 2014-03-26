#!/bin/bash
set -eu

repo_path="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

clj_license_file="$( cd $(dirname ${BASH_SOURCE[0]}) && pwd)/license.clj"
js_license_file="$( cd $(dirname ${BASH_SOURCE[0]}) && pwd)/license.js"

cd $repo_path

for filename in $(grep -Lr "Licensed under the EUPL" * | grep -v "/vendor/" | grep -v "/select2/" | grep -v "/target/" | grep -v "/node_modules/" | grep -v "/bin/") ;
do
  if [[ ${filename} =~ \.clj$ ]] ;
  then
    echo "Lisätään clj lisenssiä: ${filename}"
    cat ${clj_license_file} ${filename} > ${filename}.new
    mv ${filename}{.new,}
  elif [[ ${filename} =~ \.js$ ]] ;
  then
    echo "Lisätään js lisenssiä: ${filename}"
    cat ${js_license_file} ${filename} > ${filename}.new
    mv ${filename}{.new,}
  fi
done;