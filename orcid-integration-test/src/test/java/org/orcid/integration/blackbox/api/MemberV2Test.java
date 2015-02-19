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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.Work;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class MemberV2Test {

    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String redirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String testUser1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> t2OAuthClient;

    @Resource
    private MemberV2ApiClientImpl memberV2ApiClient;

    private WebDriver webDriver;

    private WebDriverHelper webDriverHelper;

    @Resource
    private OauthHelper oauthHelper;

    @Before
    public void before() {
        webDriver = new FirefoxDriver();
        webDriverHelper = new WebDriverHelper(webDriver, webBaseUrl, redirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
    }

    @After
    public void after() {
        webDriver.quit();
    }

    @Test
    public void testGetNotificationToken() throws JSONException, InterruptedException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
    }

    @Test
    public void createWork() throws JSONException, InterruptedException {
        Work notification = unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml");
        notification.setPutCode(null);
        String accessToken = getAccessToken();
        ClientResponse response = memberV2ApiClient.createWorkXml(testUser1OrcidId, notification, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        String locationPath = response.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath,
                locationPath.matches(".*/v2.0_rc1/4444-4444-4444-4441/work/\\d+"));
    }

    private String getAccessToken() throws InterruptedException, JSONException {
        return oauthHelper.obtainAccessToken(client1ClientId, client1ClientSecret, ScopePathType.ACTIVITIES_UPDATE.value(), user1UserName, user1Password, redirectUri);
    }

    public Work unmarshallFromPath(String path) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Work work = unmarshall(reader);
            return work;
        } catch (IOException e) {
            throw new RuntimeException("Error reading notification from classpath", e);
        }
    }

    public Work unmarshall(Reader reader) {
        try {
            JAXBContext context = JAXBContext.newInstance(Work.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Work) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }

}