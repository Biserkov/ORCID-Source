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

import java.util.Date;

import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileEntityCacheManagerImpl implements ProfileEntityCacheManager {

    @Resource
    ProfileEntityManager profileEntityManager;

    LockerObjectsManager pubLocks = new LockerObjectsManager();

    @Resource(name = "profileEntityCache")
    private Cache profileCache;

    LockerObjectsManager lockers = new LockerObjectsManager();

    private String releaseName = ReleaseNameUtils.getReleaseName();

    private static final Logger LOG = LoggerFactory.getLogger(ProfileEntityCacheManagerImpl.class);

    @Override
    public ProfileEntity retrieve(String orcid) throws IllegalArgumentException {
        Object key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = retrieveLastModifiedDate(orcid);
        ProfileEntity profile = toProfileEntity(profileCache.get(key));
        if (needsFresh(dbDate, profile))
            try {
                synchronized (lockers.obtainLock(orcid)) {
                    profile = toProfileEntity(profileCache.get(orcid));
                    if (needsFresh(dbDate, profile)) {
                        profile = profileEntityManager.findByOrcid(orcid);
                        if(profile == null)
                            throw new IllegalArgumentException("Invalid orcid " + orcid);
                        profileCache.put(new Element(key, profile));
                    }
                }
            } finally {
                lockers.releaseLock(orcid);
            }
        return profile;
    }

    @Override
    public void put(ProfileEntity profileEntity) {
        put(profileEntity.getId(), profileEntity);
    }
    public void put(String orcid, ProfileEntity profile) {
        try {
            synchronized (lockers.obtainLock(orcid)) {
                profileCache.put(new Element(new OrcidCacheKey(orcid, releaseName), profile));
            }
        } finally {
            lockers.releaseLock(orcid);
        }
    }

    
    @Override
    public void removeAll() {
        profileCache.removeAll();
    }

    @Override
    public void remove(String orcid) {
        profileCache.remove(new OrcidCacheKey(orcid, releaseName));
    }
    
    private Date retrieveLastModifiedDate(String orcid) {
        Date date = null;
        try {
            date = profileEntityManager.getLastModified(orcid);
        } catch (javax.persistence.NoResultException e) {
             LOG.debug("Missing retrieveLastModifiedDate orcid:" + orcid);   
        }
        return date;
    }
    
    static public ProfileEntity toProfileEntity(Element element) {
        return (ProfileEntity) (element != null ? element.getObjectValue() : null);
    }

    static public boolean needsFresh(Date dbDate, ProfileEntity profileEntity) {
        if (profileEntity == null)
            return true;
        if (!profileEntity.getLastModified().equals(dbDate))
            return true;
        if (dbDate == null) // not sure when this happens?
            return true;
        return false;
    }
}
