#!/bin/bash
set -eu
cd {{ asennushakemisto }}
java -jar {{ palvelu }}-db.jar {{ migraation_argumentit }} 1> logs/{{ palvelu }}-migraatio.stdout.`date +%Y-%m-%d-%H-%M-%S` 2> logs/{{ palvelu }}-migraatio.stderr.`date +%Y-%m-%d-%H-%M-%S`
