/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.integration.api.t2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
public abstract class BaseT2OrcidOAuthApiClientIntegrationTest {
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    protected String clientSecret;
    protected String clientId;
    protected String groupOrcid;
    protected String orcid;
    protected String accessToken;
    protected String blankScopeToken;
    protected String grantType = "client_credentials";

    @Resource
    protected OrcidClientDataHelper orcidClientDataHelper;

    @Resource(name="t2OAuthClient")
    protected T2OAuthAPIService<ClientResponse> oauthT2Client;
    
    @Resource(name="t2OAuthClient1_2_rc6")
    protected T2OAuthAPIService<ClientResponse> oauthT2Client1_2_rc6;

    @Resource
    protected Client jerseyClient;

    @Value("${org.orcid.t2.client.base_url}")
    protected URI t2BaseUrl;

    protected void createAccessTokenFromCredentials() throws Exception {
        this.accessToken = createAccessTokenFromCredentials(ScopePathType.ORCID_PROFILE_CREATE.value());
        assertNotNull(this.accessToken);
    }
    
    protected void createBlankTokenFromCredentials() throws Exception {
        this.blankScopeToken = createAccessTokenFromCredentials(ScopePathType.WEBHOOK.value());
        OrcidOauth2TokenDetail orcidOauth2TokenDetail = orcidOauthTokenDetailService.findNonDisabledByTokenValue(blankScopeToken);
        orcidOauth2TokenDetail.setScope("");
        orcidOauthTokenDetailService.saveOrUpdate(orcidOauth2TokenDetail);
        assertNotNull(this.blankScopeToken);
    }
        
    protected String createAccessTokenFromCredentials(String scopes) throws Exception {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", grantType);
        params.add("scope", scopes);
        ClientResponse clientResponse = oauthT2Client.obtainOauth2TokenPost("client_credentials", params);
        assertEquals(200, clientResponse.getStatus());
        String body = clientResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        return (String) jsonObject.get("access_token");
    }        
    
    @Before
    public void createClientCredentialsAndAccessToken() throws Exception {
        OrcidClientGroup orcidClientGroup = orcidClientDataHelper.createAndPersistClientGroupSingle();
        this.groupOrcid = orcidClientGroup.getGroupOrcid();
        List<OrcidClient> createdClients = orcidClientGroup.getOrcidClient();
        OrcidClient complexityClient = createdClients.get(0);
        this.clientId = complexityClient.getClientId();
        this.clientSecret = complexityClient.getClientSecret();
        createAccessTokenFromCredentials();
        createBlankTokenFromCredentials();
    }

    @After
    public void clearOrcid() throws Exception {
        // remove any client data if it exists -- plus each dependent profile
        orcidClientDataHelper.deleteOrcidProfile(orcid);
        //orcidClientDataHelper.deleteClientId(clientId);
        //orcidClientDataHelper.deleteOrcidProfile(groupOrcid);

        clientSecret = null;
        clientId = null;
        groupOrcid = null;
        orcid = null;
        accessToken = null;
    }

    protected ClientResponse createNewOrcidUsingAccessToken() throws Exception {
        OrcidMessage profile = orcidClientDataHelper.createFromXML(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        ClientResponse clientResponse = oauthT2Client1_2_rc6.createProfileXML(profile, accessToken);
        // assign orcid any time it's created for use in tear-down
        this.orcid = orcidClientDataHelper.extractOrcidFromResponseCreated(clientResponse);
        return clientResponse;
    }

    protected void assertClientResponse401Details(ClientResponse clientResponse) throws Exception {
        // we've created client details but not tied them to an access token
        assertEquals(401, clientResponse.getStatus());
        assertTrue(clientResponse.getHeaders().containsKey("WWW-Authenticate"));
        List<String> authHeaders = clientResponse.getHeaders().get("WWW-Authenticate");
        assertTrue(authHeaders.contains("Bearer realm=\"ORCID T2 API\", error=\"invalid_token\", error_description=\"Invalid access token: null\""));
    }

    protected void assertClientResponse403SecurityProblem(ClientResponse clientResponse) throws Exception {
        // we've created client details but not tied them to an access token
        assertEquals(403, clientResponse.getStatus());
        String textEntity = clientResponse.getEntity(String.class);
        assertTrue(textEntity.contains("Security problem"));
    }


}
