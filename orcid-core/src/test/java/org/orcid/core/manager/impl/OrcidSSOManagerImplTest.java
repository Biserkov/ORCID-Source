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
package org.orcid.core.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.OrcidSSOManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientGrantedAuthorityEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidSSOManagerImplTest extends BaseTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ProfileWorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml");

    private String orcid1 = "4444-4444-4444-444X";

    @Resource
    OrcidSSOManager orcidSSOManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }

    @Test
    @Rollback(true)
    public void testGrantSSOAccessToUser() {
        HashSet<String> uris = new HashSet<String>();
        uris.add("http://1.com");
        uris.add("http://2.com");
        orcidSSOManager.grantSSOAccess(orcid1, "My App", "My Description", "MyWebsite", uris);
        ClientDetailsEntity clientDetails = orcidSSOManager.getUserCredentials(orcid1);
        assertNotNull(clientDetails);
        assertNotNull(clientDetails.getAuthorizedGrantTypes());
        assertTrue(clientDetails.getAuthorizedGrantTypes().contains("authorization_code"));
        assertNotNull(clientDetails.getClientRegisteredRedirectUris());
        assertEquals(clientDetails.getClientRegisteredRedirectUris().size(), 2);
        for (ClientRedirectUriEntity redirectUri : clientDetails.getClientRegisteredRedirectUris()) {
            assertTrue(redirectUri.getRedirectUri().equals("http://1.com") || redirectUri.getRedirectUri().equals("http://2.com"));
            assertTrue(redirectUri.getRedirectUriType().equals(RedirectUriType.SSO_AUTHENTICATION.value()));
        }

        Set<ClientAuthorisedGrantTypeEntity> grantTypeList = clientDetails.getClientAuthorizedGrantTypes();
        assertEquals(2, grantTypeList.size());
        
        Set<String> grantTypes = clientDetails.getAuthorizedGrantTypes();
        assertTrue(grantTypes.contains("authorization_code"));
        assertTrue(grantTypes.contains("client_credentials"));
        

        List<ClientGrantedAuthorityEntity> grantedAuthList = clientDetails.getClientGrantedAuthorities();
        assertEquals(1, grantedAuthList.size());
        for (ClientGrantedAuthorityEntity grantedAuth : grantedAuthList) {
            assertEquals(grantedAuth.getAuthority(), "ROLE_PUBLIC");
        }
    }

    @Test
    @Rollback(true)
    public void testRevokeSSOAccessToUser() {
        HashSet<String> uris = new HashSet<String>();
        uris.add("http://1.com");
        uris.add("http://2.com");
        // Grant SSO
        orcidSSOManager.grantSSOAccess(orcid1, "My App", "My Description", "MyWebsite", uris);
        ClientDetailsEntity clientDetails = orcidSSOManager.getUserCredentials(orcid1);
        // Check the client details have been granted
        assertNotNull(clientDetails);
        // Revoke SSO
        orcidSSOManager.revokeSSOAccess(orcid1);
        // Fetch the profile and check
        ClientDetailsEntity clientDetails2 = orcidSSOManager.getUserCredentials(orcid1);
        // Check the profile doesnt have client details entity
        assertNull(clientDetails2);
    }
}