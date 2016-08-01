-- tässä muodossa periaatteessa voidaan ajaa koska tahansa. Voimassaolevia toimikausia pitäisi olla aina täsmälleen yksi kappale
update toimikausi set voimassa = false where loppupvm < now();
update toimikausi set voimassa = true where loppupvm > now();
