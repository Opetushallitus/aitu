oph-logo.pdf on svg tiedostosta tehty pelkän logon sisältävä pdf.

Svg kuvan voi muuntaa pdf:ksi esimerkiksi Apache Batik:n avulla (http://xmlgraphics.apache.org/batik/) seuraavasti:
java -jar batik-rasterizer.jar -m application/pdf -d oph-logo.pdf opetushallitus.svg
