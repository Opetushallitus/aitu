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
package oph.cas.util;

import java.security.GeneralSecurityException;


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

/**
 * Vain kehityskäyttöön. Mahdollistaa https://localhost yhteydet ohittamalla Javan SSL turvamekanismit.
 * <p>
 * <ul>
 *   <li>http://stackoverflow.com/questions/2893819/telling-java-to-accept-self-signed-ssl-certificate</li>
 *   <li>http://stackoverflow.com/questions/859111/how-do-i-accept-a-self-signed-certificate-with-a-java-httpsurlconnection</li>
 *   <li>http://stackoverflow.com/questions/2290570/pkix-path-building-failed-while-making-ssl-connection</li>
 * </ul>
 * @author anttivi
 */
public class DevelopmentSSLAuthUtil {

    public static final class SSLAuthConfig {
        private final HostnameVerifier hostNameVerifier;
        private final SSLSocketFactory socketFactory;

        public SSLAuthConfig(HostnameVerifier hostNameVerifier, SSLSocketFactory socketFactory) {
            this.hostNameVerifier = hostNameVerifier;
            this.socketFactory = socketFactory;
        }

        public HostnameVerifier getHostNameVerifier() {
            return hostNameVerifier;
        }

        public SSLSocketFactory getSocketFactory() {
            return socketFactory;
        }
     }

    private static SSLAuthConfig getCurrentSSLConfig() {
        return new SSLAuthConfig(HttpsURLConnection.getDefaultHostnameVerifier(),
                HttpsURLConnection.getDefaultSSLSocketFactory());
    }

    private static SSLAuthConfig createUntrustedSSLConfiguration(HostnameVerifier verifier) throws GeneralSecurityException {
        return new SSLAuthConfig(verifier, createUntrustedSocketFactory());
    }

    private static SSLAuthConfig swapSSLConfig(SSLAuthConfig newConfiguration) {
        SSLAuthConfig oldConfig = getCurrentSSLConfig();
        HttpsURLConnection.setDefaultHostnameVerifier(newConfiguration.getHostNameVerifier());
        HttpsURLConnection.setDefaultSSLSocketFactory(newConfiguration.getSocketFactory());
        return oldConfig;
    }

    public static SSLAuthConfig enableUntrustedSSLForLocalhostOnly() throws GeneralSecurityException {
        return swapSSLConfig(createUntrustedSSLConfiguration(LOCALHOST_VERIFIER));
    }

    public static SSLAuthConfig enableUntrustedSSL() throws GeneralSecurityException {
        return swapSSLConfig(createUntrustedSSLConfiguration(GLOBAL_VERIFIER));
    }

    //for localhost testing only
    private static final HostnameVerifier LOCALHOST_VERIFIER = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession sslSession) {
            if (hostname.equals("localhost")) {
                return true;
            }
            return false;
        }
    };

    private static final HostnameVerifier GLOBAL_VERIFIER = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession sslSession) {
            return true;
        }
    };

    // Create a trust manager that does not validate certificate chains
    private static final TrustManager UNTRUST_MANAGER = new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
        }
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
        }
    };

    private static SSLSocketFactory createUntrustedSocketFactory() throws GeneralSecurityException {
        TrustManager[] trustAllCerts = new TrustManager[] { UNTRUST_MANAGER };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        return sc.getSocketFactory();
    }
}
