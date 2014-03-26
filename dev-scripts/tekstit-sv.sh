#!/bin/bash
set -eu

REPO_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

set -x

cd $REPO_PATH/ttk/resources/i18n
sed -E -e 's/(.+)/\1 (sv)/' tekstit.properties > tekstit_sv.properties
