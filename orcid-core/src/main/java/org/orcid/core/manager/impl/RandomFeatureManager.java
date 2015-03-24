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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public class RandomFeatureManager {

    @Value("${org.orcid.api.common.jaxb.OrcidValidationJaxbContextResolver.ORCID_MESSAGE_STRONG_VALIDATION:false}")
    private boolean ORCID_MESSAGE_STRONG_VALIDATION = false;

    public boolean isORCID_MESSAGE_STRONG_VALIDATION() {
        return ORCID_MESSAGE_STRONG_VALIDATION;
    }

    public void setORCID_MESSAGE_STRONG_VALIDATION(boolean oRCID_MESSAGE_STRONG_VALIDATION) {
        ORCID_MESSAGE_STRONG_VALIDATION = oRCID_MESSAGE_STRONG_VALIDATION;
    }

}
