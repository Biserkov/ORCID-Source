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
package org.orcid.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import javax.annotation.Resource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.jaxb.model.common.Organization;
import org.orcid.jaxb.model.common.OrganizationAddress;
import org.orcid.jaxb.model.common.Title;
import org.orcid.jaxb.model.message.ActivitiesVisibilityDefault;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.FundingTitle;
import org.orcid.jaxb.model.record.PeerReview;
import org.orcid.jaxb.model.record.PeerReviewType;
import org.orcid.jaxb.model.record.Role;
import org.orcid.jaxb.model.record.Subject;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.WorkExternalIdentifier;
import org.orcid.jaxb.model.record.WorkExternalIdentifierId;
import org.orcid.jaxb.model.record.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record.WorkTitle;
import org.orcid.jaxb.model.record.WorkType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class SourceInActivitiesTest extends BaseTest {

    private static final String CLIENT_1_ID = "APP-5555555555555555";
    private static final String CLIENT_2_ID = "APP-5555555555555556";

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private WorkManager workManager;

    @Resource
    private ProfileWorkManager profileWorkManager;

    @Resource
    ProfileFundingManager profileFundingManager;

    @Resource
    AffiliationsManager affiliationsManager;
    
    @Resource
    private PeerReviewManager peerReviewManager;

    @Resource
    private OrgManager orgManager;

    @Mock
    private SourceManager sourceManager;

    static String userOrcid = null;
    static Organization organization = null;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }

    @Before
    public void before() {
        profileWorkManager.setSourceManager(sourceManager);
        profileFundingManager.setSourceManager(sourceManager);
        affiliationsManager.setSourceManager(sourceManager);
        peerReviewManager.setSourceManager(sourceManager);
        if (PojoUtil.isEmpty(userOrcid)) {
            OrcidProfile newUser = getMinimalOrcidProfile();
            userOrcid = newUser.getOrcidIdentifier().getPath();
        }
    }

    @AfterClass
    public static void after() throws Exception {
        removeDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }

    @Test
    public void sourceDoesntChange_Work_Test() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ProfileEntity(userOrcid)));
        ProfileWorkEntity work1 = getProfileWorkEntity(userOrcid);
        assertNotNull(work1);
        assertFalse(PojoUtil.isEmpty(work1.getWork().getTitle()));
        assertEquals(userOrcid, work1.getSource().getSourceId());

        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        ProfileWorkEntity work2 = getProfileWorkEntity(userOrcid);
        assertNotNull(work2);
        assertFalse(PojoUtil.isEmpty(work2.getWork().getTitle()));
        assertEquals(CLIENT_1_ID, work2.getSource().getSourceId());

        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_2_ID)));
        ProfileWorkEntity work3 = getProfileWorkEntity(userOrcid);
        assertNotNull(work3);
        assertFalse(PojoUtil.isEmpty(work3.getWork().getTitle()));
        assertEquals(CLIENT_2_ID, work3.getSource().getSourceId());

        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ProfileEntity(userOrcid)));
        ProfileWorkEntity work4 = getProfileWorkEntity(userOrcid);
        assertNotNull(work4);
        assertFalse(PojoUtil.isEmpty(work4.getWork().getTitle()));
        assertEquals(userOrcid, work4.getSource().getSourceId());

        ProfileWorkEntity fromDb1 = profileWorkManager.getProfileWork(userOrcid, String.valueOf(work1.getWork().getId()));
        assertNotNull(fromDb1);
        assertEquals(userOrcid, fromDb1.getSource().getSourceId());

        ProfileWorkEntity fromDb2 = profileWorkManager.getProfileWork(userOrcid, String.valueOf(work2.getWork().getId()));
        assertNotNull(fromDb2);
        assertEquals(CLIENT_1_ID, fromDb2.getSource().getSourceId());

        ProfileWorkEntity fromDb3 = profileWorkManager.getProfileWork(userOrcid, String.valueOf(work3.getWork().getId()));
        assertNotNull(fromDb3);
        assertEquals(CLIENT_2_ID, fromDb3.getSource().getSourceId());

        ProfileWorkEntity fromDb4 = profileWorkManager.getProfileWork(userOrcid, String.valueOf(work4.getWork().getId()));
        assertNotNull(fromDb4);
        assertEquals(userOrcid, fromDb4.getSource().getSourceId());
    }

    @Test
    public void sourceDoesntChange_Funding_Test() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ProfileEntity(userOrcid)));
        ProfileFundingEntity funding1 = getProfileFundingEntity(userOrcid);
        assertNotNull(funding1);
        assertFalse(PojoUtil.isEmpty(funding1.getTitle()));
        assertEquals(userOrcid, funding1.getSource().getSourceId());

        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        ProfileFundingEntity funding2 = getProfileFundingEntity(userOrcid);
        assertNotNull(funding2);
        assertFalse(PojoUtil.isEmpty(funding2.getTitle()));
        assertEquals(CLIENT_1_ID, funding2.getSource().getSourceId());

        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_2_ID)));
        ProfileFundingEntity funding3 = getProfileFundingEntity(userOrcid);
        assertNotNull(funding3);
        assertFalse(PojoUtil.isEmpty(funding3.getTitle()));
        assertEquals(CLIENT_2_ID, funding3.getSource().getSourceId());

        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ProfileEntity(userOrcid)));
        ProfileFundingEntity funding4 = getProfileFundingEntity(userOrcid);
        assertNotNull(funding4);
        assertFalse(PojoUtil.isEmpty(funding4.getTitle()));
        assertEquals(userOrcid, funding4.getSource().getSourceId());

        ProfileFundingEntity fromDb1 = profileFundingManager.getProfileFundingEntity(String.valueOf(funding1.getId()));
        assertNotNull(fromDb1);
        assertEquals(userOrcid, fromDb1.getSource().getSourceId());

        ProfileFundingEntity fromDb2 = profileFundingManager.getProfileFundingEntity(String.valueOf(funding2.getId()));
        assertNotNull(fromDb2);
        assertEquals(CLIENT_1_ID, fromDb2.getSource().getSourceId());

        ProfileFundingEntity fromDb3 = profileFundingManager.getProfileFundingEntity(String.valueOf(funding3.getId()));
        assertNotNull(fromDb3);
        assertEquals(CLIENT_2_ID, fromDb3.getSource().getSourceId());

        ProfileFundingEntity fromDb4 = profileFundingManager.getProfileFundingEntity(String.valueOf(funding4.getId()));
        assertNotNull(fromDb4);
        assertEquals(userOrcid, fromDb4.getSource().getSourceId());
    }

    @Test
    public void sourceDoesntChange_PeerReview_Test() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ProfileEntity(userOrcid)));
        PeerReview peerReview1 = getPeerReview(userOrcid);
        assertNotNull(peerReview1);
        assertNotNull(peerReview1.getSubject());
        assertNotNull(peerReview1.getSubject().getTitle());
        assertNotNull(peerReview1.getSubject().getTitle().getTitle());
        assertFalse(PojoUtil.isEmpty(peerReview1.getSubject().getTitle().getTitle().getContent()));
        assertEquals(userOrcid, peerReview1.retrieveSourcePath());
        
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PeerReview peerReview2 = getPeerReview(userOrcid);
        assertNotNull(peerReview2);
        assertNotNull(peerReview2.getSubject());
        assertNotNull(peerReview2.getSubject().getTitle());
        assertNotNull(peerReview2.getSubject().getTitle().getTitle());
        assertFalse(PojoUtil.isEmpty(peerReview2.getSubject().getTitle().getTitle().getContent()));
        assertEquals(CLIENT_1_ID, peerReview2.retrieveSourcePath());
        
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_2_ID)));
        PeerReview peerReview3 = getPeerReview(userOrcid);
        assertNotNull(peerReview3);
        assertNotNull(peerReview3.getSubject());
        assertNotNull(peerReview3.getSubject().getTitle());
        assertNotNull(peerReview3.getSubject().getTitle().getTitle());
        assertFalse(PojoUtil.isEmpty(peerReview3.getSubject().getTitle().getTitle().getContent()));
        assertEquals(CLIENT_2_ID, peerReview3.retrieveSourcePath());
        
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ProfileEntity(userOrcid)));
        PeerReview peerReview4 = getPeerReview(userOrcid);
        assertNotNull(peerReview4);
        assertNotNull(peerReview4.getSubject());
        assertNotNull(peerReview4.getSubject().getTitle());
        assertNotNull(peerReview4.getSubject().getTitle().getTitle());
        assertFalse(PojoUtil.isEmpty(peerReview4.getSubject().getTitle().getTitle().getContent()));
        assertEquals(userOrcid, peerReview4.retrieveSourcePath());
        
        PeerReview fromDb1 = peerReviewManager.getPeerReview(userOrcid, peerReview1.getPutCode());
        assertNotNull(fromDb1);
        assertEquals(userOrcid, fromDb1.retrieveSourcePath());
        
        PeerReview fromDb2 = peerReviewManager.getPeerReview(userOrcid, peerReview2.getPutCode());
        assertNotNull(fromDb2);
        assertEquals(CLIENT_1_ID, fromDb2.retrieveSourcePath());
        
        PeerReview fromDb3 = peerReviewManager.getPeerReview(userOrcid, peerReview3.getPutCode());
        assertNotNull(fromDb3);
        assertEquals(CLIENT_2_ID, fromDb3.retrieveSourcePath());
        
        PeerReview fromDb4 = peerReviewManager.getPeerReview(userOrcid, peerReview4.getPutCode());
        assertNotNull(fromDb4);
        assertEquals(userOrcid, fromDb4.retrieveSourcePath());
    }

    @Test
    public void sourceDoesntChange_Affiliation_Test() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ProfileEntity(userOrcid)));
        Education education1 = getEducation(userOrcid);
        assertNotNull(education1);        
        assertEquals(userOrcid, education1.retrieveSourcePath());
        
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        Education education2 = getEducation(userOrcid);
        assertNotNull(education2);        
        assertEquals(CLIENT_1_ID, education2.retrieveSourcePath());
        
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_2_ID)));
        Education education3 = getEducation(userOrcid);
        assertNotNull(education3);        
        assertEquals(CLIENT_2_ID, education3.retrieveSourcePath());
        
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ProfileEntity(userOrcid)));
        Education education4 = getEducation(userOrcid);
        assertNotNull(education4);        
        assertEquals(userOrcid, education4.retrieveSourcePath());
        
        Education fromDb1 = affiliationsManager.getEducationAffiliation(userOrcid, education1.getPutCode());
        assertNotNull(fromDb1);
        assertEquals(userOrcid, fromDb1.retrieveSourcePath());
        
        Education fromDb2 = affiliationsManager.getEducationAffiliation(userOrcid, education2.getPutCode());
        assertNotNull(fromDb2);
        assertEquals(CLIENT_1_ID, fromDb2.retrieveSourcePath());
        
        Education fromDb3 = affiliationsManager.getEducationAffiliation(userOrcid, education3.getPutCode());
        assertNotNull(fromDb3);
        assertEquals(CLIENT_2_ID, fromDb3.retrieveSourcePath());
        
        Education fromDb4 = affiliationsManager.getEducationAffiliation(userOrcid, education4.getPutCode());
        assertNotNull(fromDb4);
        assertEquals(userOrcid, fromDb4.retrieveSourcePath());
    }

    private OrcidProfile getMinimalOrcidProfile() {
        OrcidProfile profile = new OrcidProfile();
        OrcidBio bio = new OrcidBio();
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new org.orcid.jaxb.model.message.Email(System.currentTimeMillis() + "@user.com"));
        Preferences preferences = new Preferences();
        preferences.setSendChangeNotifications(new SendChangeNotifications(true));
        preferences.setSendOrcidNews(new SendOrcidNews(true));
        preferences.setSendMemberUpdateRequests(true);
        preferences.setSendEmailFrequencyDays("1");
        preferences.setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.fromValue("public")));
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setFamilyName(new FamilyName("First"));
        personalDetails.setGivenNames(new GivenNames("Last"));
        bio.setContactDetails(contactDetails);
        bio.setPersonalDetails(personalDetails);
        OrcidInternal internal = new OrcidInternal();
        internal.setPreferences(preferences);
        profile.setOrcidBio(bio);
        profile.setOrcidInternal(internal);
        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(true));
        orcidHistory.setCreationMethod(CreationMethod.fromValue("integration-test"));
        profile.setOrcidHistory(orcidHistory);
        orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        profile.setPassword("password1");
        return orcidProfileManager.createOrcidProfile(profile, false);
    }

    private ProfileWorkEntity getProfileWorkEntity(String userOrcid) {
        Work work = new Work();
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("Work " + System.currentTimeMillis()));
        work.setWorkTitle(title);
        work.setWorkType(org.orcid.jaxb.model.record.WorkType.BOOK);
        work = profileWorkManager.createWork(userOrcid, work);
        return profileWorkManager.getProfileWork(userOrcid, work.getPutCode());
    }

    private ProfileFundingEntity getProfileFundingEntity(String userOrcid) {
        Funding funding = new Funding();        
        funding.setOrganization(getOrganization());
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title("Title " + System.currentTimeMillis()));
        funding.setTitle(title);
        funding.setType(org.orcid.jaxb.model.record.FundingType.AWARD);
        funding = profileFundingManager.createFunding(userOrcid, funding);
        return profileFundingManager.getProfileFundingEntity(funding.getPutCode());
    }

    private Education getEducation(String userOrcid) {
        Education education = new Education();
        education.setOrganization(getOrganization());
        education = affiliationsManager.createEducationAffiliation(userOrcid, education);
        return affiliationsManager.getEducationAffiliation(userOrcid, education.getPutCode());
    }
    
    private PeerReview getPeerReview(String userOrcid) {
        PeerReview peerReview = new PeerReview();
        peerReview.setOrganization(getOrganization());
        peerReview.setType(PeerReviewType.EVALUATION);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Title " + System.currentTimeMillis()));
        WorkExternalIdentifiers workExtIds = new WorkExternalIdentifiers();
        WorkExternalIdentifier workExtId = new WorkExternalIdentifier();
        workExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId("ID"));
        workExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        workExtIds.getExternalIdentifier().add(workExtId);
        Subject subject = new Subject();        
        subject.setTitle(workTitle);
        subject.setExternalIdentifiers(workExtIds);
        subject.setType(WorkType.ARTISTIC_PERFORMANCE);
        peerReview.setSubject(subject);
        peerReview.setExternalIdentifiers(workExtIds);
        peerReview.setRole(Role.CHAIR);
        peerReview = peerReviewManager.createPeerReview(userOrcid, peerReview);
        return peerReviewManager.getPeerReview(userOrcid, peerReview.getPutCode());
    }

    private Organization getOrganization() {
        if (organization == null) {
            OrganizationAddress address = new OrganizationAddress();
            address.setCity("City");
            address.setRegion("Region");
            address.setCountry(org.orcid.jaxb.model.common.Iso3166Country.US);                        
            organization = new Organization();                                    
            organization.setName("Org name");
            organization.setAddress(address);
        }
        return organization;
    }        
}
