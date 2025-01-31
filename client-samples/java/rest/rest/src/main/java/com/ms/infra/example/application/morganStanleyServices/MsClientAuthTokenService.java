package com.ms.infra.example.application.morganStanleyServices;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCertificate;
import com.ms.infra.example.application.config.MicroprofileConfigService;

import java.net.MalformedURLException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * This class is responsible for generating bearer tokens using the MSAL library.
 */
public class MsClientAuthTokenService {
    /**
     * Morgan Stanley OAuth2 Token Uri
     */
    private final String tokenUri;

    /**
     * Client app ID
     */
    private final String clientId;

    /**
     * Client app scope
     */
    private final String scope;

    /**
     * Client's private key
     */
    private final PrivateKey privateKey;

    /**
     * Client public certificate
     */
    private final X509Certificate publicCertificate;

    /**
     * Constructor method
     *
     * @param microprofileConfigService class which retrieves config values
     * @throws Exception Throws I/O exception if error with key files
     */
    public MsClientAuthTokenService(MicroprofileConfigService microprofileConfigService) throws Exception {
        // get properties from configService class
        this.tokenUri = microprofileConfigService.getMsOAuth2TokenUri();
        this.clientId = microprofileConfigService.getClientAppId();
        this.scope = microprofileConfigService.getClientAppScope();
        this.privateKey = microprofileConfigService.getPrivateKey();
        this.publicCertificate = microprofileConfigService.getPublicCertificate();
    }

    /**
     * This method uses the MSAL library to generate bearer tokens.
     * @return bearer token
     * @throws MalformedURLException throws MalformedURLException if error whilst adding authority to builder
     */
    public String getAccessToken() throws MalformedURLException {
        final ClientCredentialParameters clientCredentialParameters = ClientCredentialParameters.builder(Collections.singleton(scope)).build();
        final IClientCertificate fromCertificate = ClientCredentialFactory.createFromCertificate(privateKey, publicCertificate);
        final ConfidentialClientApplication confidentialClientApplication = ConfidentialClientApplication.builder(clientId, fromCertificate)
            .authority(tokenUri)
            .build();

        final CompletableFuture<IAuthenticationResult> resultFuture = confidentialClientApplication.acquireToken(clientCredentialParameters);
        return resultFuture.join()
            .accessToken();
    }
}
