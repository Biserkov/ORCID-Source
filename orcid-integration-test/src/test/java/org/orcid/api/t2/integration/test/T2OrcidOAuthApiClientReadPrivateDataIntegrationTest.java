/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.t2.integration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.api.t2.T2OAuthAPIService;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;
import org.orcid.test.DBUnitTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
public class T2OrcidOAuthApiClientReadPrivateDataIntegrationTest extends DBUnitTest {
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    private static final Pattern AUTHORIZATION_CODE_PATTERN = Pattern.compile("code=(.+)");

    private static final String READ_PRIVATE_WORKS_CLIENT_ID = "9999-9999-9999-9991";
    private static final String READ_PRIVATE_FUNDING_CLIENT_ID = "9999-9999-9999-9992";
    private static final String READ_PRIVATE_AFFILIATIONS_CLIENT_ID = "9999-9999-9999-9993";
    private static final String READ_ONLY_LIMITED_INFO_CLIENT_ID = "9999-9999-9999-9994";
    private static final String READ_PRIVATE_WORKS_CLIENT_ID_2 = "9999-9999-9999-9995";
    private static final String READ_PRIVATE_FUNDING_CLIENT_ID_2 = "9999-9999-9999-9996";
    private static final String READ_PRIVATE_AFFILIATIONS_CLIENT_ID_2 = "9999-9999-9999-9997";

    private static final List<String> DATA_FILES = Arrays.asList("/group_client_data/EmptyEntityData.xml", "/group_client_data/SecurityQuestionEntityData.xml",
            "/group_client_data/ProfileEntityData.xml", "/group_client_data/WorksEntityData.xml", "/group_client_data/OrgsEntityData.xml",
            "/group_client_data/ClientDetailsEntityData.xml", "/group_client_data/ProfileWorksEntityData.xml", "/group_client_data/OrgAffiliationEntityData.xml",
            "/group_client_data/ProfileFundingEntityData.xml");

    private WebDriver webDriver;

    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;

    private String redirectUri;

    @Resource
    private ClientRedirectDao clientRedirectDao;
    
    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Resource
    private ProfileDao profileDao;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> oauthT2Client;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES, null);
    }
    
    @Before
    @Transactional
    public void before() {
        webDriver = new FirefoxDriver();
        redirectUri = webBaseUrl + "/oauth/playground";

        // Set redirect uris if needed
        ClientRedirectUriPk clientRedirectUriPk = new ClientRedirectUriPk(READ_PRIVATE_WORKS_CLIENT_ID, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(READ_PRIVATE_WORKS_CLIENT_ID, redirectUri);
        }

        clientRedirectUriPk = new ClientRedirectUriPk(READ_PRIVATE_WORKS_CLIENT_ID_2, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(READ_PRIVATE_WORKS_CLIENT_ID_2, redirectUri);
        }

        clientRedirectUriPk = new ClientRedirectUriPk(READ_PRIVATE_AFFILIATIONS_CLIENT_ID, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(READ_PRIVATE_AFFILIATIONS_CLIENT_ID, redirectUri);
        }

        clientRedirectUriPk = new ClientRedirectUriPk(READ_PRIVATE_AFFILIATIONS_CLIENT_ID_2, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(READ_PRIVATE_AFFILIATIONS_CLIENT_ID_2, redirectUri);
        }

        clientRedirectUriPk = new ClientRedirectUriPk(READ_PRIVATE_FUNDING_CLIENT_ID, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(READ_PRIVATE_FUNDING_CLIENT_ID, redirectUri);
        }

        clientRedirectUriPk = new ClientRedirectUriPk(READ_PRIVATE_FUNDING_CLIENT_ID_2, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(READ_PRIVATE_FUNDING_CLIENT_ID_2, redirectUri);
        }

        clientRedirectUriPk = new ClientRedirectUriPk(READ_ONLY_LIMITED_INFO_CLIENT_ID, redirectUri);
        if (clientRedirectDao.find(clientRedirectUriPk) == null) {
            clientDetailsManager.addClientRedirectUri(READ_ONLY_LIMITED_INFO_CLIENT_ID, redirectUri);
        }

        webDriver.get(webBaseUrl + "/signout");

        // Update last modified to force cache eviction (because DB unit deletes
        // a load of stuff from the DB, but reinserts profiles with older last
        // modified date)
        for (ProfileEntity profile : profileDao.getAll()) {
            profileDao.updateLastModifiedDateWithoutResult(profile.getId());
        }
    }

    @After
    public void after() {
        webDriver.quit();
    }

    /**
     * Check fetching information from a client that only have read-limited
     * access
     * */
    @Test
    public void testGetProfileWithOnlyReadLimitedScope() throws JSONException, InterruptedException {
        String scopes = "/orcid-profile/read-limited";
        String authorizationCode = obtainAuthorizationCode(scopes, READ_ONLY_LIMITED_INFO_CLIENT_ID);
        String accessToken = obtainAccessToken(READ_ONLY_LIMITED_INFO_CLIENT_ID, authorizationCode, redirectUri, scopes);

        ClientResponse fullResponse1 = oauthT2Client.viewFullDetailsXml("9999-9999-9999-9989", accessToken, "v1.2_rc5");
        assertEquals(200, fullResponse1.getStatus());
        OrcidMessage orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check works
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        List<OrcidWork> works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        for (OrcidWork work : works) {
            String putCode = work.getPutCode();
            Visibility v = work.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to work: " + putCode);
            }
        }

        // Check funding
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings().size());

        List<Funding> fundings = orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings();
        for (Funding funding : fundings) {
            String putCode = funding.getPutCode();
            Visibility v = funding.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to funding: " + putCode);
            }
        }

        // Check affiliations
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().size());

        List<Affiliation> affiliations = orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation();
        for (Affiliation affiliation : affiliations) {
            String putCode = affiliation.getPutCode();
            Visibility v = affiliation.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to affiliation: " + putCode);
            }
        }
    }

    /**
     * ------------------ CHECKING WORKS ------------------
     * */
    /**
     * Check fetching information from a client that should have access to his
     * private works
     * */
    @Test
    public void testGetProfileWithOwnPrivateWorks() throws JSONException, InterruptedException {
        String scopes = "/orcid-profile/read-limited /orcid-works/update";
        String authorizationCode = obtainAuthorizationCode(scopes, READ_PRIVATE_WORKS_CLIENT_ID);
        String accessToken = obtainAccessToken(READ_PRIVATE_WORKS_CLIENT_ID, authorizationCode, redirectUri, scopes);

        ClientResponse fullResponse1 = oauthT2Client.viewFullDetailsXml("9999-9999-9999-9989", accessToken, "v1.2_rc5");
        assertEquals(200, fullResponse1.getStatus());
        OrcidMessage orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check works
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(3, orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        List<OrcidWork> works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        boolean containMyPrivateWork = false;
        for (OrcidWork work : works) {
            String putCode = work.getPutCode();
            if (putCode.equals("1") || putCode.equals("5")) {
                fail("This client should not have access to work: " + putCode);
            } else {
                if (putCode.equals("4")) {
                    containMyPrivateWork = true;
                }
            }
        }

        if (!containMyPrivateWork)
            fail("Client doesnt have his private work with put code: 4");

        // Check funding
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings().size());

        List<Funding> fundings = orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings();
        for (Funding funding : fundings) {
            String putCode = funding.getPutCode();
            Visibility v = funding.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to funding: " + putCode);
            }
        }

        // Check affiliations
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().size());

        List<Affiliation> affiliations = orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation();
        for (Affiliation affiliation : affiliations) {
            String putCode = affiliation.getPutCode();
            Visibility v = affiliation.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to affiliation: " + putCode);
            }
        }
    }

    /**
     * Check fetching information from a client that should have access to his
     * private works
     * */
    @Test
    public void testGetProfileWithOwnPrivateWorks2() throws JSONException, InterruptedException {
        String scopes = "/orcid-profile/read-limited /orcid-works/update";
        String authorizationCode = obtainAuthorizationCode(scopes, READ_PRIVATE_WORKS_CLIENT_ID_2);
        String accessToken = obtainAccessToken(READ_PRIVATE_WORKS_CLIENT_ID_2, authorizationCode, redirectUri, scopes);

        ClientResponse fullResponse1 = oauthT2Client.viewFullDetailsXml("9999-9999-9999-9989", accessToken, "v1.2_rc5");
        assertEquals(200, fullResponse1.getStatus());
        OrcidMessage orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check works
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(3, orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        List<OrcidWork> works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        boolean containMyPrivateWork = false;
        for (OrcidWork work : works) {
            String putCode = work.getPutCode();
            if (putCode.equals("1") || putCode.equals("4")) {
                fail("This client should not have access to work: " + putCode);
            } else {
                if (putCode.equals("5")) {
                    containMyPrivateWork = true;
                }
            }
        }

        if (!containMyPrivateWork)
            fail("Client doesnt have his private work with put code: 5");

        // Check funding
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings().size());

        List<Funding> fundings = orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings();
        for (Funding funding : fundings) {
            String putCode = funding.getPutCode();
            Visibility v = funding.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to funding: " + putCode);
            }
        }

        // Check affiliations
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().size());

        List<Affiliation> affiliations = orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation();
        for (Affiliation affiliation : affiliations) {
            String putCode = affiliation.getPutCode();
            Visibility v = affiliation.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to affiliation: " + putCode);
            }
        }
    }

    /**
     * ------------------ CHECKING FUNDING ------------------
     * */
    /**
     * Check fetching information from a client that should have access to his
     * private funding
     * */
    @Test
    public void testGetProfileWithOwnPrivateFunding() throws JSONException, InterruptedException {
        String scopes = "/orcid-profile/read-limited /funding/update";
        String authorizationCode = obtainAuthorizationCode(scopes, READ_PRIVATE_FUNDING_CLIENT_ID);
        String accessToken = obtainAccessToken(READ_PRIVATE_FUNDING_CLIENT_ID, authorizationCode, redirectUri, scopes);

        ClientResponse fullResponse1 = oauthT2Client.viewFullDetailsXml("9999-9999-9999-9989", accessToken, "v1.2_rc5");
        assertEquals(200, fullResponse1.getStatus());
        OrcidMessage orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check works
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        List<OrcidWork> works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        for (OrcidWork work : works) {
            String putCode = work.getPutCode();
            Visibility v = work.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to work: " + putCode);
            }
        }

        // Check funding
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertEquals(3, orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings().size());

        List<Funding> fundings = orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings();
        boolean haveMyOwnPrivateFunding = false;
        for (Funding funding : fundings) {
            String putCode = funding.getPutCode();
            if (putCode.equals("1") || putCode.equals("5")) {
                fail("This client should not have access to funding: " + putCode);
            } else if (putCode.equals("4")) {
                haveMyOwnPrivateFunding = true;
            }
        }

        if (!haveMyOwnPrivateFunding) {
            fail("This client must have his own private funding with put code 4");
        }

        // Check affiliations
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().size());

        List<Affiliation> affiliations = orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation();
        for (Affiliation affiliation : affiliations) {
            String putCode = affiliation.getPutCode();
            Visibility v = affiliation.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to affiliation: " + putCode);
            }
        }
    }

    /**
     * Check fetching information from a client that should have access to his
     * private funding
     * */
    @Test
    public void testGetProfileWithOwnPrivateFunding2() throws JSONException, InterruptedException {
        String scopes = "/orcid-profile/read-limited /funding/update";
        String authorizationCode = obtainAuthorizationCode(scopes, READ_PRIVATE_FUNDING_CLIENT_ID_2);
        String accessToken = obtainAccessToken(READ_PRIVATE_FUNDING_CLIENT_ID_2, authorizationCode, redirectUri, scopes);

        ClientResponse fullResponse1 = oauthT2Client.viewFullDetailsXml("9999-9999-9999-9989", accessToken, "v1.2_rc5");
        assertEquals(200, fullResponse1.getStatus());
        OrcidMessage orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check works
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        List<OrcidWork> works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        for (OrcidWork work : works) {
            String putCode = work.getPutCode();
            Visibility v = work.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to work: " + putCode);
            }
        }

        // Check funding
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertEquals(3, orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings().size());

        List<Funding> fundings = orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings();
        boolean haveMyOwnPrivateFunding = false;
        for (Funding funding : fundings) {
            String putCode = funding.getPutCode();
            if (putCode.equals("1") || putCode.equals("4")) {
                fail("This client should not have access to funding: " + putCode);
            } else if (putCode.equals("5")) {
                haveMyOwnPrivateFunding = true;
            }
        }

        if (!haveMyOwnPrivateFunding) {
            fail("This client must have his own private funding with put code 5");
        }

        // Check affiliations
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().size());

        List<Affiliation> affiliations = orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation();
        for (Affiliation affiliation : affiliations) {
            String putCode = affiliation.getPutCode();
            if (putCode.equals("1") || putCode.equals("4") || putCode.equals("5")) {
                fail("This client should not have access to affiliation: " + putCode);
            }
        }
    }

    /**
     * ------------------ CHECKING AFFILIATIONS ------------------
     * */
    /**
     * Check fetching information from a client that should have access to his
     * private funding
     * */
    @Test
    public void testGetProfileWithOwnPrivateAffiliations() throws JSONException, InterruptedException {
        String scopes = "/orcid-profile/read-limited /affiliations/update";
        String authorizationCode = obtainAuthorizationCode(scopes, READ_PRIVATE_AFFILIATIONS_CLIENT_ID);
        String accessToken = obtainAccessToken(READ_PRIVATE_AFFILIATIONS_CLIENT_ID, authorizationCode, redirectUri, scopes);

        ClientResponse fullResponse1 = oauthT2Client.viewFullDetailsXml("9999-9999-9999-9989", accessToken, "v1.2_rc5");
        assertEquals(200, fullResponse1.getStatus());
        OrcidMessage orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check works
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        List<OrcidWork> works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        for (OrcidWork work : works) {
            String putCode = work.getPutCode();
            Visibility v = work.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to work: " + putCode);
            }
        }

        // Check funding
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings().size());

        List<Funding> fundings = orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings();
        for (Funding funding : fundings) {
            String putCode = funding.getPutCode();
            Visibility v = funding.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to funding: " + putCode);
            }
        }

        // Check affiliations
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        assertEquals(3, orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().size());

        List<Affiliation> affiliations = orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation();
        boolean haveMyOwnPrivateAffiliation = false;
        for (Affiliation affiliation : affiliations) {
            String putCode = affiliation.getPutCode();
            if (putCode.equals("1") || putCode.equals("5")) {
                fail("This client should not have access to affiliation: " + putCode);
            } else if (putCode.equals("4")) {
                haveMyOwnPrivateAffiliation = true;
            }
        }

        if (!haveMyOwnPrivateAffiliation) {
            fail("This client must have his own private funding with put code 5");
        }
    }

    /**
     * Check fetching information from a client that should have access to his
     * private funding
     * */
    @Test
    public void testGetProfileWithOwnPrivateAffiliations2() throws JSONException, InterruptedException {
        String scopes = "/orcid-profile/read-limited /affiliations/update";
        String authorizationCode = obtainAuthorizationCode(scopes, READ_PRIVATE_AFFILIATIONS_CLIENT_ID_2);
        String accessToken = obtainAccessToken(READ_PRIVATE_AFFILIATIONS_CLIENT_ID_2, authorizationCode, redirectUri, scopes);

        ClientResponse fullResponse1 = oauthT2Client.viewFullDetailsXml("9999-9999-9999-9989", accessToken, "v1.2_rc5");
        assertEquals(200, fullResponse1.getStatus());
        OrcidMessage orcidMessage = fullResponse1.getEntity(OrcidMessage.class);
        // Check returning message
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());

        // Check works
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        List<OrcidWork> works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        for (OrcidWork work : works) {
            String putCode = work.getPutCode();
            Visibility v = work.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to work: " + putCode);
            }
        }

        // Check funding
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertEquals(2, orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings().size());

        List<Funding> fundings = orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings();
        for (Funding funding : fundings) {
            String putCode = funding.getPutCode();
            Visibility v = funding.getVisibility();
            if (Visibility.PRIVATE.equals(v)) {
                fail("This client should not have access to funding: " + putCode);
            }
        }

        // Check affiliations
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        assertEquals(3, orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().size());

        List<Affiliation> affiliations = orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation();
        boolean haveMyOwnPrivateAffiliation = false;
        for (Affiliation affiliation : affiliations) {
            String putCode = affiliation.getPutCode();
            if (putCode.equals("1") || putCode.equals("4")) {
                fail("This client should not have access to affiliation: " + putCode);
            } else if (putCode.equals("5")) {
                haveMyOwnPrivateAffiliation = true;
            }
        }

        if (!haveMyOwnPrivateAffiliation) {
            fail("This client must have his own private funding with put code 5");
        }
    }

    private String obtainAuthorizationCode(String scopes, String orcid) throws InterruptedException {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, orcid, scopes, redirectUri));
        return obtainAuthorizationCode(orcid, scopes, redirectUri);
    }

    private String obtainAuthorizationCode(String orcid, String scopes, String redirectUri) throws InterruptedException {
        webDriver.get(String.format("%s/oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s", webBaseUrl, orcid, scopes, redirectUri));
        webDriver.get(String.format("%s?oneStep",webDriver.getCurrentUrl()));
        //Switch to the login form
        WebElement switchFromLink = webDriver.findElement(By.id("in-register-switch-form"));
        switchFromLink.click();
        Thread.sleep(500);
        //Fill the form
        WebElement userId = webDriver.findElement(By.id("userId"));
        userId.sendKeys("user_to_test@user.com");
        WebElement password = webDriver.findElement(By.id("password"));
        password.sendKeys("password");
        WebElement submitButton = webDriver.findElement(By.id("authorize-button")); 
        submitButton.click();
        
        (new WebDriverWait(webDriver, DEFAULT_TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().equals("ORCID Playground");
            }
        });
        String currentUrl = webDriver.getCurrentUrl();
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        String authorizationCode = matcher.group(1);
        assertNotNull(authorizationCode);
        return authorizationCode;
    }

    private String obtainAccessToken(String clientId, String authorizationCode, String redirectUri, String scopes) throws JSONException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", "client-secret");
        params.add("grant_type", "authorization_code");
        params.add("scope", scopes);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);
        ClientResponse tokenResponse = oauthT2Client.obtainOauth2TokenPost("client_credentials", params);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        return accessToken;
    }

}
