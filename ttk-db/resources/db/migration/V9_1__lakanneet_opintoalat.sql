-- OPH-363
-- poistetaan opintoalat, joille ei ole tutkintoja. Näitä ei olisi pitänyt tuoda konversiossa.
delete from opintoala oa
where not exists (select * from nayttotutkinto nt where nt.opintoala = oa.opintoala_tkkoodi);

-- poistetaan koulutusalat, joihin ei liity opintoaloja. Em. opintoalojen lisäksi mukana on yksi lakannut koulutusala.
delete from koulutusala ka
where not exists (select * from opintoala oa where oa.koulutusala_tkkoodi = ka.koulutusala_tkkoodi);
