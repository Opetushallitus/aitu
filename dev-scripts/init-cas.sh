#!/bin/bash
set -eu

repo_path="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

set -x

cd $repo_path/vagrant

vagrant destroy -f auth
vagrant up auth

