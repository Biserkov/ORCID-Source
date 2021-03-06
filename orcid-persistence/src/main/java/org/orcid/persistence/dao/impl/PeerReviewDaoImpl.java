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
package org.orcid.persistence.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.springframework.transaction.annotation.Transactional;

public class PeerReviewDaoImpl extends GenericDaoImpl<PeerReviewEntity, Long> implements PeerReviewDao {

    public PeerReviewDaoImpl() {
        super(PeerReviewEntity.class);
    }

    @Override
    public PeerReviewEntity getPeerReview(String userOrcid, String peerReviewId) {
        Query query = entityManager.createQuery("from PeerReviewEntity where profile.id=:userOrcid and id=:peerReviewId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("peerReviewId", Long.valueOf(peerReviewId));
        return (PeerReviewEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean removePeerReview(String userOrcid, Long peerReviewId) {
        Query query = entityManager.createQuery("delete from PeerReviewEntity where profile.id=:userOrcid and id=:peerReviewId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("peerReviewId", peerReviewId);
        return query.executeUpdate() > 0 ? true : false;
    }    
    
    @Override
    public List<PeerReviewEntity> getByUser(String userOrcid) {
        TypedQuery<PeerReviewEntity> query = entityManager.createQuery("from PeerReviewEntity where profile.id=:userOrcid", PeerReviewEntity.class);
        query.setParameter("userOrcid", userOrcid);
        return query.getResultList();
    }
    
    @Override
    @Transactional
    public boolean updateToMaxDisplay(String orcid, String id) {
        Query query = entityManager.createNativeQuery("UPDATE peer_review SET display_index = (select coalesce(MAX(display_index) + 1, 0) from peer_review where orcid=:orcid and id != :id ) WHERE id = :id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", Long.valueOf(id));
        return query.executeUpdate() > 0 ? true : false;
    }
    
    @Override
    @Transactional
    public boolean updateVisibilities(String orcid, ArrayList<Long> peerReviewIds, Visibility visibility) {
        Query query = entityManager
                .createQuery("update PeerReviewEntity set visibility=:visibility, lastModified=now() where id in (:peerReviewIds) and  profile.id=:orcid");
        query.setParameter("peerReviewIds", peerReviewIds);
        query.setParameter("visibility", visibility);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0 ? true : false;
    }
}
