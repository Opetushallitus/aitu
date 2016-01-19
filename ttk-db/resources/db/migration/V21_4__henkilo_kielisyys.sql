ALTER TABLE henkilo ADD CONSTRAINT henkilo_aidinkieli_check CHECK (aidinkieli IN ('fi', 'sv', '2k'));
