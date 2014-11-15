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
package org.orcid.core.manager;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Visibility;

/**
 * @author Will Simpson
 */
public interface OrcidProfileManager {

    /**
     * Creates a new profile, assigning it a new ORCID
     * 
     * @param orcidProfile
     *            the new profile valiues
     * @return the profile as it is represented in the data store after its
     *         creation
     */
    OrcidProfile createOrcidProfile(OrcidProfile orcidProfile);

    /**
     * Creates a new profile, assigning it a new ORCID. Also, sends a
     * notification email to the email in the created profile.
     * 
     * @param orcidProfile
     *            the new profile valiues
     * @return the profile as it is represented in the data store after its
     *         creation
     */

    OrcidProfile createOrcidProfileAndNotify(OrcidProfile orcidProfile);

    /**
     * Updates an existing profile
     * 
     * @param orcidProfile
     *            the new properties of the profile
     * @return the updated profile as it new state dictates
     */
    OrcidProfile updateOrcidProfile(OrcidProfile orcidProfile);

    /**
     * Retrieves the orcid external identifiers given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the bio populated
     */
    OrcidProfile retrieveClaimedExternalIdentifiers(String orcid);

    /**
     * Retrieves the orcid bio given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the bio populated
     */
    OrcidProfile retrieveClaimedOrcidBio(String orcid);

    /**
     * Retrieves the orcid affiliations given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the affiliations populated
     */
    OrcidProfile retrieveClaimedAffiliations(String orcid);

    /**
     * Retrieves the orcid fundings given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the funding list populated
     */
    OrcidProfile retrieveClaimedFundings(String orcid);

    /**
     * Returns true if ORCID exist.
     * 
     * @param orcid
     * @return
     */
    public boolean exists(String orcid);

    /**
     * Retrieves the orcid works given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the works populated
     */
    OrcidProfile retrieveClaimedOrcidWorks(String orcid);

    /**
     * Retrieves the orcid profile given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the full orcid profile
     */
    OrcidProfile retrieveOrcidProfile(String orcid);

    OrcidProfile retrieveOrcidProfile(String orcid, LoadOptions loadOptions);

    OrcidProfile retrievePublicOrcidProfile(String orcid);

    OrcidProfile retrievePublicOrcidProfileFromCache(String orcid, long lastModifiedDate);

    /**
     * Retrieves the orcid profile given an identifier, without any personal
     * internal data.
     * <p/>
     * Specifically, this is for use by tier 1
     * 
     * @param orcid
     *            the identifier
     * @return the full orcid profile
     */
    OrcidProfile retrieveOrcidProfileWithNoInternal(String orcid);

    /**
     * Persist the country and country visibility
     * 
     * * @param orcidProfile Profile containing the personal details, contact
     * details and identifiers to replace those in the DB.
     */
    void updateCountry(OrcidProfile orcidProfile);

    /**
     * Persist the biography
     * 
     * * @param orcidProfile Profile containing the personal details, contact
     * details and identifiers to replace those in the DB.
     */
    public void updateBiography(OrcidProfile orcidProfile);

    /**
     * Like {@link #updatePersonalInformation(OrcidProfile)}, but for primary
     * institution and joint affiliation (not past institutions).
     * 
     * @see #updatePersonalInformation(OrcidProfile)
     */
    OrcidProfile updateAffiliations(OrcidProfile orcidProfile);

    /**
     * Like {@link #updatePersonalInformation(OrcidProfile)}, but for primary
     * fundings and joint affiliation (not past institutions).
     * 
     * @see #updatePersonalInformation(OrcidProfile)
     */
    OrcidProfile updateFundings(OrcidProfile orcidProfile);

    /**
     * Overwrites the password and security details in the DB with the values in
     * the OrcidProfile object passed into the method.
     * 
     * @param updatedOrcidProfile
     */
    void updatePasswordInformation(OrcidProfile updatedOrcidProfile);

    /**
     * Overwrites security details ONLY in the DB with the values in the
     * OrcidProfile object passed into the method.
     * 
     * @param updatedOrcidProfile
     * @return
     */
    void updateSecurityQuestionInformation(OrcidProfile updatedOrcidProfile);

    void updatePreferences(String orcid, Preferences preferences);

    /**
     * Overwrites preferences in the DB with the values in updatedProfile.
     */
    OrcidProfile updateOrcidPreferences(OrcidProfile updatedOrcidProfile);

    /**
     * Adds the works from orcidProfile to the existing profile in the DB
     * (without removing existing works, and without any attempt at
     * de-duplication).
     * 
     * @param orcidProfile
     *            The works to add to the profile.
     */
    void addOrcidWorks(OrcidProfile orcidProfile);

    /**
     * Deletes an ORCID works for the ORCID supplied as a parameter
     * 
     * @param orcid
     *            the owning ORCID
     * @param positionsToDelete
     *            the indexes of the sections (portions) to delete
     */

    OrcidProfile deleteOrcidWorks(String orcid, int[] positionsToDelete);

    /**
     * Updates the visibility of the ORCID works for the ORCID supplied as a
     * parameter
     * 
     * @param orcid
     *            the owning ORCID
     * @param visibility
     *            the visibility to update the works to
     */
    OrcidProfile updateOrcidWorkVisibility(String orcid, int[] positionsToUpdate, Visibility visibility);

    /**
     * Deletes an approved client application's token from a profile.
     * 
     * @param userOrcid
     *            The ORCID of the user from which to delete the approved
     *            application's token.
     * @param applicationOrcid
     *            The ORCID of the client application for which to remove a
     *            token from the user's profile.
     * @param scopes
     *            The scopes of the token.
     * @return
     */
    OrcidProfile revokeApplication(String userOrcid, String applicationOrcid, Collection<ScopePathType> scopes);

    /**
     * Deletes an entire ORCID profile - use with care...
     * 
     * @param orcid
     *            the ORCID
     * 
     */
    OrcidProfile deleteProfile(String orcid);

    /**
     * Checks that the email is not already being used
     * 
     * @param email
     *            the value to be used to check for an existing record
     */
    boolean emailExists(String email);

    /**
     * Adds a new {@link org.orcid.jaxb.model.message.Affiliation} to the
     * {@link} OrcidProfile} and returns the updated values
     * 
     * @param orcidProfile
     * @return
     */
    void addAffiliations(OrcidProfile orcidProfile);

    /**
     * Adds a new {@link org.orcid.jaxb.model.message.FundingList} to the
     * {@link} OrcidProfile} and returns the updated values
     * 
     * @param orcidProfile
     * @return
     */
    void addFundings(OrcidProfile orcidProfile);

    /**
     * Attempt to locate a profile with the email address. This is for internal
     * use only, and should not be exposed to any external clients.
     * 
     * @param email
     * @return
     */
    OrcidProfile retrieveOrcidProfileByEmail(String email);

    OrcidProfile retrieveOrcidProfileByEmail(String email, LoadOptions loadOptions);

    /**
     * Updates the ORCID bio data
     * 
     * @param orcidProfile
     * @return
     */
    OrcidProfile updateOrcidBio(OrcidProfile orcidProfile);

    /**
     * Updates the ORCID works only
     * 
     * @param orcidProfile
     * @return
     */
    OrcidProfile updateOrcidWorks(OrcidProfile orcidProfile);

    /**
     * Add new external identifiers to an existing profile
     * 
     * @param orcidProfile
     * @return
     */
    OrcidProfile addExternalIdentifiers(OrcidProfile orcidProfile);

    /**
     * Add new delegates to the profile. The orcidProfile should contain the
     * Orcid for the profile giving permission. The delegation section should
     * contain a GivenPermissionTo for each new delegate. If the delegate has
     * already been permission for this profile, then it is ignored.
     * 
     * @param orcidProfile
     * @return
     */
    OrcidProfile addDelegates(OrcidProfile orcidProfile);

    OrcidProfile revokeDelegate(String giverOrcid, String receiverOrcid);

    void processProfilesPendingIndexing();
    
    void processProfilesThatMissedIndexing();

    void processUnclaimedProfilesToFlagForIndexing();
    
    void processUnclaimedProfilesForReminder();

    /**
     * Deactivate an Orcid user -this involves blanking out most fields and
     * setting a flag
     * 
     * @param orcidProfile
     * @return
     */

    public void processUnverifiedEmails7Days();

    OrcidProfile deactivateOrcidProfile(OrcidProfile orcidProfile);

    /**
     * Reactivate a deactivated profile
     * 
     * @param orcidProfile
     * @return
     * */
    OrcidProfile reactivateOrcidProfile(OrcidProfile orcidProfile);

    OrcidWorks dedupeWorks(OrcidWorks orcidWorks);

    OrcidProfile retrieveClaimedOrcidProfile(String orcid);

    public void updateNames(OrcidProfile orcidProfile);

    Date retrieveLastModifiedDate(String orcid);

    Date updateLastModifiedDate(String orcid);

    void clearOrcidProfileCache();

    public void addLocale(OrcidProfile orcidProfile, Locale locale);

    public void processProfilePendingIndexingInTransaction(final String orcid);
    
    public void checkWorkExternalIdentifiersAreNotDuplicated(List<OrcidWork> newOrcidWorksList, List<OrcidWork> existingWorkList);
    
    public void setCompareWorksUsingScopusWay(boolean compareWorksUsingScopusWay);

}
