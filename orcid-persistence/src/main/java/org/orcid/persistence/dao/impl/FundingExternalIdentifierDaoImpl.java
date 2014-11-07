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

import java.util.List;

import javax.persistence.Query;

import org.orcid.persistence.dao.FundingExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.FundingExternalIdentifierEntity;
import org.springframework.transaction.annotation.Transactional;

public class FundingExternalIdentifierDaoImpl extends GenericDaoImpl<FundingExternalIdentifierEntity, Long> implements FundingExternalIdentifierDao  {

	public FundingExternalIdentifierDaoImpl() {
		super(FundingExternalIdentifierEntity.class);
	}

	/**
	 * Removes a funding external identifier
	 * 
	 * @param id
	 *            The id of the external identifier
	 * @return true if the external identifier was deleted
	 * */
	@Override
	@Transactional
	public boolean removeFundingExternalIdentifier(String id) {
		Query query = entityManager.createQuery("delete from FundingExternalIdentifierEntity where id=:id");
		query.setParameter("id", Long.valueOf(id));
		return query.executeUpdate() > 0 ? true : false;
	}

	/**
	 * Updates an external identifier
	 * 
	 * @param id
	 *            The id of the external identifier
	 * 
	 * @param type
	 *            the new type
	 * 
	 * @param value
	 *            the new value
	 * 
	 * @param url
	 *            The new url
	 * 
	 * @return true if the external identifier was updated
	 * */
	@Override
	@Transactional
	public boolean updateFundingExternalIdentifier(String id, String type,
			String value, String url) {
		Query query = entityManager.createQuery("update FundingExternalIdentifierEntity set type=:type, value=:value, url=:url where id=:id");
		query.setParameter("id", Long.valueOf(id));
		query.setParameter("type", type);
		query.setParameter("value", value);
		query.setParameter("url", url);		
		return query.executeUpdate() > 0 ? true : false;
	}

	/**
	 * Creates a new funding external identifier
	 * 
	 * @param newFundingExternalIdentifierEntity
	 *            The object to be persisted
	 * @return the created FundingExternalIdentifierEntity with the id assigned
	 *         on database
	 * */
	@Override
	@Transactional
	public FundingExternalIdentifierEntity createFundingExternalIdentifier(
			FundingExternalIdentifierEntity newFundingExternalIdentifierEntity) {
		entityManager.persist(newFundingExternalIdentifierEntity);
		return newFundingExternalIdentifierEntity;	
	}

	/**
	 * Get the external identifier associated with the provided id
	 * 
	 * @param id
	 *            the funding external identifier id
	 * 
	 * @return the FundingExternalIdentifierEntity object associated with the
	 *         provided id
	 * */
	@Override
	public FundingExternalIdentifierEntity getFundingExternalIdentifier(String id) {
		Query query = entityManager.createQuery("from FundingExternalIdentifierEntity where id=:id");
		query.setParameter("id", Long.valueOf(id));
		return (FundingExternalIdentifierEntity)query.getSingleResult();		
	}

	/**
	 * Get the list of external identifiers associated with the given
	 * profileFundingEntity object
	 * 
	 * @param profileFundingId
	 *            The id of the orgFundingRelation object
	 * 
	 * @return the ProfileFundingEntity object
	 * */
	@Override
	public List<FundingExternalIdentifierEntity> getFundingExternalIdentifiers(
			String profileFundingId) {
		Query query = entityManager.createQuery("from FundingExternalIdentifierEntity where orgFunding.id=:id");
		query.setParameter("id", Long.valueOf(profileFundingId));
		return query.getResultList();
	}
}
