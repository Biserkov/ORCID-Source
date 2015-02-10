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
package org.orcid.integration.blackbox.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.blackbox.helper.InitializeDataHelper;
import org.orcid.integration.blackbox.helper.OauthHelper;
import org.orcid.jaxb.model.clientgroup.GroupType;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-api-context.xml" })
public class OauthAuthorizationCodeTest {    
    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.core.oauth.auth_code.expiration_minutes:1440}")
    private int authorizationCodeExpiration;
    @Value("${org.orcid.web.testMember1.memberId}")
    public String groupId;    
    @Value("${org.orcid.web.testMember1.type}")
    public String groupType;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;    
    @Value("${org.orcid.web.testClient1.redirectUri}")
    public String redirectUri;    
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String testUser1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    private static String email;
    @Value("${org.orcid.web.testUser1.password}")
    private static String password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    private static String orcidId;
            
    @Resource(name = "orcidOauth2AuthoriziationCodeDetailDao")
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;    
    @Resource
    private InitializeDataHelper initializeDataHelper;    
    @Resource
    private OauthHelper oauthHelper;    
    
    @Before
    public void before() throws Exception {
        //If the member doesnt exists yet, create it
        if(!initializeDataHelper.userExists(groupId)) {
            initializeDataHelper.createMember(GroupType.fromValue(groupType), groupId);
        }
        //If the client doesnt exists yet, create it
        if(!initializeDataHelper.clientExists(client1ClientId)) {
            initializeDataHelper.createClient(groupId, redirectUri, client1ClientId, client1ClientSecret);
        }
        //If the user doesnt exists yet, creat it
        if(!initializeDataHelper.userExists(orcidId)) {
            initializeDataHelper.createProfile(email, password, orcidId);
        }
    }
    
    @After
    public void after() {
        oauthHelper.closeWebDriver();
    }    
    
    @Test
    public void useAuthorizationCodeWithValidScopesTest() throws InterruptedException, JSONException {        
        String accessToken = oauthHelper.obtainAccessToken(client1ClientId, client1ClientSecret, "/orcid-works/create", email, password, redirectUri, true);        
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));
    }
    
    @Test
    public void useAuthorizationCodeWithInalidScopesTest() throws InterruptedException, JSONException {
        String authorizationCode = oauthHelper.getAuthorizationCode(client1ClientId, "/orcid-works/create", email, password, true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));        
        ClientResponse tokenResponse = oauthHelper.getClientResponse(client1ClientId, client1ClientSecret, "/orcid-works/update", redirectUri, authorizationCode);
        assertEquals(401, tokenResponse.getStatus());  
        OrcidMessage result = tokenResponse.getEntity(OrcidMessage.class);
        assertNotNull(result);
        assertNotNull(result.getErrorDesc());
        assertEquals("OAuth2 problem : Invalid scopes: /orcid-works/update available scopes for this code are: [/orcid-works/create]", result.getErrorDesc().getContent());
    }
    
    @Test
    public void useAuthorizationCodeWithoutScopesTest() throws InterruptedException, JSONException {
        String authorizationCode = oauthHelper.getAuthorizationCode(client1ClientId, "/orcid-works/create", email, password, true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = oauthHelper.getClientResponse(client1ClientId, client1ClientSecret, null, redirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));
    }
    
    @Test
    public void useClientCredentialsGrantTypeScope() throws InterruptedException, JSONException {
        String authorizationCode = oauthHelper.getAuthorizationCode(client1ClientId, "/orcid-works/create", email, password, true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        ClientResponse tokenResponse = oauthHelper.getClientResponse(client1ClientId, client1ClientSecret, "/orcid-works/create /webhook", redirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String scope = (String) jsonObject.get("scope");
        assertNotNull(scope);
        assertEquals("/orcid-works/create", scope);
    }
    
    @Test
    public void authorizationCodeExpiresAfterXMinutesTest() throws InterruptedException, JSONException {
        String authorizationCode = oauthHelper.getAuthorizationCode(client1ClientId, "/orcid-works/create", email, password, true);
        assertFalse(PojoUtil.isEmpty(authorizationCode));
        
        OrcidOauth2AuthoriziationCodeDetail authorizationCodeEntity = orcidOauth2AuthoriziationCodeDetailDao.find(authorizationCode);
        Date dateCreated = authorizationCodeEntity.getDateCreated();
        Calendar c = Calendar.getInstance();
        c.setTime(dateCreated);
        c.add(Calendar.MINUTE, (-authorizationCodeExpiration - 1) );
        dateCreated = c.getTime();
        authorizationCodeEntity.setDateCreated(dateCreated);
        orcidOauth2AuthoriziationCodeDetailDao.merge(authorizationCodeEntity);
        
        ClientResponse tokenResponse = oauthHelper.getClientResponse(client1ClientId, client1ClientSecret, "/orcid-works/create /webhook", redirectUri, authorizationCode);
        assertEquals(400, tokenResponse.getStatus());
        OrcidMessage result = tokenResponse.getEntity(OrcidMessage.class);
        assertNotNull(result);
        assertNotNull(result.getErrorDesc());
        assertEquals("Bad Request : Authorization code has expired", result.getErrorDesc().getContent());
        
    }    
}