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
package org.orcid.core.adapter;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 *
 */
public interface JpaJaxbEmploymentAdapter {

    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Employment employment);

    Employment toEmployment(OrgAffiliationRelationEntity entity);
    
    EmploymentSummary toEmploymentSummary(OrgAffiliationRelationEntity entity);

    List<Employment> toEmployment(Collection<OrgAffiliationRelationEntity> entities);
    
    List<EmploymentSummary> toEmploymentSummary(Collection<OrgAffiliationRelationEntity> entities);
    
    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Employment employment, OrgAffiliationRelationEntity existing);

}