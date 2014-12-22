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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class NotificationDaoImpl extends GenericDaoImpl<NotificationEntity, Long> implements NotificationDao {

    public NotificationDaoImpl() {
        super(NotificationEntity.class);
    }

    @Override
    public List<NotificationEntity> findByOrcid(String orcid, int firstResult, int maxResults) {
        TypedQuery<NotificationEntity> query = readOnlyEntityManager.createQuery(
                "from NotificationEntity where orcid = :orcid and archivedDate is null order by dateCreated desc", NotificationEntity.class);
        query.setParameter("orcid", orcid);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @Override
    public NotificationEntity findLatestByOrcid(String orcid) {
        List<NotificationEntity> results = findByOrcid(orcid, 0, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<NotificationEntity> findUnsentByOrcid(String orcid) {
        TypedQuery<NotificationEntity> query = readOnlyEntityManager.createQuery("from NotificationEntity where sentDate is null and orcid = :orcid", NotificationEntity.class);
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

    @Override
    public int getUnreadCount(String orcid) {
        TypedQuery<Long> query = readOnlyEntityManager.createQuery("select count(*) from NotificationEntity where readDate is null and archivedDate is null and orcid = :orcid", Long.class);
        query.setParameter("orcid", orcid);
        return query.getSingleResult().intValue();
    }

    @Override
    public List<String> findOrcidsWithNotificationsToSend() {
        return findOrcidsWithNotificationsToSend(new Date());
    }

    @Override
    public List<String> findOrcidsWithNotificationsToSend(Date effectiveNow) {
        TypedQuery<String> query = readOnlyEntityManager.createNamedQuery(NotificationEntity.FIND_ORCIDS_WITH_NOTIFICATIONS_TO_SEND, String.class);
        query.setParameter("effectiveNow", effectiveNow);
        return query.getResultList();
    }

    @Override
    public NotificationEntity findByOricdAndId(String orcid, Long id) {
        TypedQuery<NotificationEntity> query = readOnlyEntityManager.createQuery("from NotificationEntity where orcid = :orcid and id = :id", NotificationEntity.class);
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void flagAsSent(Collection<Long> ids) {
        Query query = entityManager.createQuery("update NotificationEntity set sentDate = now() where id in :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void flagAsRead(String orcid, Long id) {
        Query query = entityManager.createQuery("update NotificationEntity set readDate = now() where orcid = :orcid and id = :id and readDate is null");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void flagAsArchived(String orcid, Long id) {
        Query query = entityManager.createQuery("update NotificationEntity set archivedDate = now() where orcid = :orcid and id = :id and archivedDate is null");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        query.executeUpdate();
    }

}
