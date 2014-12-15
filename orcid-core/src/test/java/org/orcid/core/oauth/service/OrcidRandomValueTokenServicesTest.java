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
package org.orcid.core.oauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.oauth.OrcidOauth2ClientAuthentication;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.test.DBUnitTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidRandomValueTokenServicesTest extends DBUnitTest {

    @Resource(name = "tokenServices")
    private OrcidRandomValueTokenServices tokenServices;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrcidOauth2AuthorisationDetailsData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/OrcidOauth2AuthorisationDetailsData.xml", "/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }

    @Before
    public void setPersistentTokensOn() {
        OrcidRandomValueTokenServices orcidTokenServices = (OrcidRandomValueTokenServices) tokenServices;
        orcidTokenServices.setSupportRefreshToken(true);
    }        
    
    @Test
    @Transactional
    @Rollback
    public void testCreateReadLimitedAccessToken() {
        Date earliestExpiry = oneHoursTime();
        Date earliestRefreshExpiry = earliestExpiry;

        Map<String, String> authorizationParameters = new HashMap<>();
        String clientId = "4444-4444-4444-4441";
        authorizationParameters.put(AuthorizationRequest.CLIENT_ID, clientId);
        authorizationParameters.put(AuthorizationRequest.SCOPE, "/orcid-profile/read-limited");
        AuthorizationRequest request = new DefaultAuthorizationRequest(authorizationParameters);
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        Authentication userAuthentication = new OrcidOauth2ClientAuthentication(clientDetails);
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        OAuth2AccessToken oauth2AccessToken = tokenServices.createAccessToken(authentication);

        Date latestExpiry = oneHoursTime();
        Date latestRefreshExpiry = latestExpiry;

        assertNotNull(oauth2AccessToken);
        assertFalse(oauth2AccessToken.getExpiration().before(earliestExpiry));
        assertFalse(oauth2AccessToken.getExpiration().after(latestExpiry));
        assertNotNull(oauth2AccessToken.getRefreshToken());
        
        OrcidOauth2TokenDetail tokenDetail = orcidOauthTokenDetailService.findByRefreshTokenValue(oauth2AccessToken.getRefreshToken().getValue());
        assertNotNull(tokenDetail);
        Date refreshTokenExpiration = tokenDetail.getRefreshTokenExpiration();
        assertFalse(refreshTokenExpiration.before(earliestRefreshExpiry));
        assertFalse(refreshTokenExpiration.after(latestRefreshExpiry));                
    }

    @Test
    @Transactional
    @Rollback
    public void testCreateAddWorkAccessToken() {
        Date earliestExpiry = oneHoursTime();
        Date earliestRefreshExpiry = earliestExpiry;

        Map<String, String> authorizationParameters = new HashMap<>();
        String clientId = "4444-4444-4444-4441";
        authorizationParameters.put(AuthorizationRequest.CLIENT_ID, clientId);
        authorizationParameters.put(AuthorizationRequest.SCOPE, "/orcid-works/create");
        AuthorizationRequest request = new DefaultAuthorizationRequest(authorizationParameters);
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        Authentication userAuthentication = new OrcidOauth2ClientAuthentication(clientDetails);
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        OAuth2AccessToken oauth2AccessToken = tokenServices.createAccessToken(authentication);

        Date latestExpiry = oneHoursTime();
        Date latestRefreshExpiry = latestExpiry;

        assertNotNull(oauth2AccessToken);
        assertFalse(oauth2AccessToken.getExpiration().before(earliestExpiry));
        assertFalse(oauth2AccessToken.getExpiration().after(latestExpiry));
        assertNotNull(oauth2AccessToken.getRefreshToken());

        OrcidOauth2TokenDetail tokenDetail = orcidOauthTokenDetailService.findByRefreshTokenValue(oauth2AccessToken.getRefreshToken().getValue());
        assertNotNull(tokenDetail);
        Date refreshTokenExpiration = tokenDetail.getRefreshTokenExpiration();
        assertFalse(refreshTokenExpiration.before(earliestRefreshExpiry));
        assertFalse(refreshTokenExpiration.after(latestRefreshExpiry));
    }

    @Test
    @Transactional
    @Rollback
    public void testReissuedAccessTokenHasUpdatedExpiration() throws InterruptedException {
        Date earliestExpiry = oneHoursTime();
        Date earliestRefreshExpiry = earliestExpiry;

        Map<String, String> authorizationParameters = new HashMap<>();
        String clientId = "4444-4444-4444-4441";
        authorizationParameters.put(AuthorizationRequest.CLIENT_ID, clientId);
        authorizationParameters.put(AuthorizationRequest.SCOPE, "/orcid-works/create");
        AuthorizationRequest request = new DefaultAuthorizationRequest(authorizationParameters);
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        Authentication userAuthentication = new OrcidOauth2ClientAuthentication(clientDetails);
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        OAuth2AccessToken oauth2AccessToken = tokenServices.createAccessToken(authentication);

        Date latestExpiry = oneHoursTime();
        Date latestRefreshExpiry = latestExpiry;

        assertNotNull(oauth2AccessToken);
        assertFalse(oauth2AccessToken.getExpiration().before(earliestExpiry));
        assertFalse(oauth2AccessToken.getExpiration().after(latestExpiry));
        assertNotNull(oauth2AccessToken.getRefreshToken());

        OrcidOauth2TokenDetail tokenDetail = orcidOauthTokenDetailService.findByRefreshTokenValue(oauth2AccessToken.getRefreshToken().getValue());
        assertNotNull(tokenDetail);
        Date refreshTokenExpiration = tokenDetail.getRefreshTokenExpiration();
        assertFalse(refreshTokenExpiration.before(earliestRefreshExpiry));
        assertFalse(refreshTokenExpiration.after(latestRefreshExpiry));

        Thread.sleep(1000);
        earliestExpiry = oneHoursTime();
        earliestRefreshExpiry = earliestExpiry;

        OAuth2AccessToken reissuedOauth2AccessToken = tokenServices.createAccessToken(authentication);

        latestExpiry = oneHoursTime();
        latestRefreshExpiry = latestExpiry;

        assertNotNull(reissuedOauth2AccessToken);
        // Check is the same token - should be as token has not be used, and has
        // not expired.
        assertEquals(oauth2AccessToken.getValue(), reissuedOauth2AccessToken.getValue());
        assertFalse(reissuedOauth2AccessToken.getExpiration().before(earliestExpiry));
        assertFalse(reissuedOauth2AccessToken.getExpiration().after(latestExpiry));

        OrcidOauth2TokenDetail reissuedTokenDetail = orcidOauthTokenDetailService.findByRefreshTokenValue(reissuedOauth2AccessToken.getRefreshToken().getValue());
        assertNotNull(reissuedTokenDetail);
        Date reissuedRefreshTokenExpiration = reissuedTokenDetail.getRefreshTokenExpiration();
        assertFalse(reissuedRefreshTokenExpiration.before(earliestRefreshExpiry));
        assertFalse(reissuedRefreshTokenExpiration.after(latestRefreshExpiry));

    }

    private Date twentyYearsTime() {
        Calendar earliestExpiry = new GregorianCalendar();
        // This is roughly 2 years in seconds - used in the implementation, but
        // not sure how was calculated now.
        earliestExpiry.add(Calendar.SECOND, 631138519);
        return earliestExpiry.getTime();
    }

    private Date oneHoursTime() {
        Calendar earliestExpiry = new GregorianCalendar();
        earliestExpiry.add(Calendar.HOUR, 1);
        return earliestExpiry.getTime();
    }

    @Test
    @Transactional
    @Rollback
    public void testRefreshAccessToken() {
        String refreshTokenValue = "some-long-oauth2-refresh-value-1";
        Map<String, String> authorizationParameters = new HashMap<>();
        authorizationParameters.put(AuthorizationRequest.CLIENT_ID, "4444-4444-4444-4441");
        AuthorizationRequest request = new DefaultAuthorizationRequest(authorizationParameters);

        OAuth2AccessToken oauth2AccessToken = tokenServices.refreshAccessToken(refreshTokenValue, request);
        assertNotNull(oauth2AccessToken);
        String tokenValue = oauth2AccessToken.getValue();
        assertFalse("Token value should be different", tokenValue.equals("some-long-oauth2-token-value-1"));
    }
    
    /**
     * Check that the token created with a non persistent code will expire within an hour 
     * */
    @Test
    @Transactional
    @Rollback
    public void tokenExpireInAnHourTest() throws InterruptedException {
        Map<String, String> authorizationParameters = new HashMap<>();
        String clientId = "4444-4444-4444-4441";
        authorizationParameters.put(AuthorizationRequest.CLIENT_ID, clientId);
        authorizationParameters.put(AuthorizationRequest.SCOPE, "/orcid-works/create");
        authorizationParameters.put("code", "code2");
        
        AuthorizationRequest request = new DefaultAuthorizationRequest(authorizationParameters);
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        Authentication userAuthentication = new OrcidOauth2ClientAuthentication(clientDetails);
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        OAuth2AccessToken oauth2AccessToken = tokenServices.createAccessToken(authentication);
                
        Date tokenExpiration = oauth2AccessToken.getExpiration();
        Thread.sleep(2000);
        
        //The token expires in less than one hour
        assertFalse(tokenExpiration.after(oneHoursTime()));
        
    }

    /**
     * Check that the token created with a persistent code will expire within 20 years
     * */
    @Test
    @Transactional
    @Rollback
    public void tokenExpireIn20YearsTest() throws InterruptedException {
        Date in20years = twentyYearsTime();
        
        Thread.sleep(2000);
        
        Map<String, String> authorizationParameters = new HashMap<>();
        String clientId = "4444-4444-4444-4441";
        authorizationParameters.put(AuthorizationRequest.CLIENT_ID, clientId);
        authorizationParameters.put(AuthorizationRequest.SCOPE, "/orcid-works/create");
        authorizationParameters.put("code", "code1");
        
        AuthorizationRequest request = new DefaultAuthorizationRequest(authorizationParameters);
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        Authentication userAuthentication = new OrcidOauth2ClientAuthentication(clientDetails);
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        OAuth2AccessToken oauth2AccessToken = tokenServices.createAccessToken(authentication);
        
        
        Date tokenExpiration = oauth2AccessToken.getExpiration();
        
        //The token expires in 20 years
        assertFalse(in20years.after(tokenExpiration));
        
        in20years = twentyYearsTime();
        
        //Confirm the token expires in 20 years
        assertFalse(tokenExpiration.after(in20years));
    }                            
    
    /**
     * Load authentication using a persistent token
     * */
    @Test
    @Transactional
    @Rollback
    public void loadAuthenticationWithPersistentTokenTest() {
        try {
            OAuth2Authentication result = tokenServices.loadAuthentication("persistent-token-2");
            assertNotNull(result);
        } catch(Exception e) {
            fail();
        }               
    }            
}
