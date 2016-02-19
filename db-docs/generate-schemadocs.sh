#!/bin/bash

set -eu

echo 'Generoidaan dokumentaatio skeemasta'

java -jar schemaSpy_5.0.0.jar -dp postgresql-9.3-1101-jdbc41.jar -t pgsql -host localhost -db ttk -u ttk_adm -p ttk-adm -s public -o schemadocs 

echo 'siivotaan mainosbanneri häiritsemästä ja korjataan merkistöt utf-8 muotoon.'
cd schemadocs
touch null.js


# rekursiivisesti käydään läpi kaikki html tiedostot
find . -name "*.html" -type f |
  (while read file; do
    echo "found  $file"
    LC_CTYPE=C && cat $file |sed 's/http\:\/\/pagead2.googlesyndication.com\/pagead\/show_ads.js/null.js/g' > siivottu.html
    LC_CTYPE=C && cat siivottu.html | sed 's/<head>/<head><meta charset\=\"ISO-8859-1\" \/>/g' |
      sed 's/ISO-8859-1/UTF-8/g' > merkistokorjattu.html
    iconv -f ISO-8859-1 -t UTF-8 merkistokorjattu.html > $file
  done);



