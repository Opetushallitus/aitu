/**
 * Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package oph.korma.auth;

import java.sql.Connection;

import com.mchange.v2.c3p0.AbstractConnectionCustomizer;
import com.mchange.v2.c3p0.ConnectionCustomizer;

public class C3P0NamedCustomizer extends AbstractConnectionCustomizer {

    private static ConnectionCustomizer customizerImpl;

    public static ConnectionCustomizer setImpl(ConnectionCustomizer impl) {
        customizerImpl = impl;
        return impl;
    }

    @Override
    public void onCheckOut(Connection c, String s) throws Exception {
        customizerImpl.onCheckOut(c, s);
    }

    @Override
    public void onCheckIn(Connection c, String s) throws Exception {
        customizerImpl.onCheckIn(c,s);
    }
}
