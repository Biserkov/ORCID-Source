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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.13 at 04:58:08 PM BST 
//

package org.orcid.jaxb.model.record;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common.Url;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "type", "value", "url" })
@XmlRootElement(name = "externalIdentifier", namespace = "http://www.orcid.org/ns/funding")
public class FundingExternalIdentifier implements ExternalIdentifier, Serializable {
	private static final long serialVersionUID = 1L;	
	@XmlElement(name="external-identifier-type", namespace = "http://www.orcid.org/ns/funding")
	protected FundingExternalIdentifierType type;
	@XmlElement(name="external-identifier-value", namespace = "http://www.orcid.org/ns/funding")
	protected String value;
	@XmlElement(name="external-identifier-url", namespace = "http://www.orcid.org/ns/funding")
	protected Url url;	
	
	public FundingExternalIdentifierType getType() {
		return type;
	}
	public void setType(FundingExternalIdentifierType type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Url getUrl() {
		return url;
	}
	public void setUrl(Url url) {
		this.url = url;
	}	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FundingExternalIdentifier other = (FundingExternalIdentifier) obj;		
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;		
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		//url is ignored, since, for groupings it is not needed
		return true;
	}
	
	@Override
	public boolean passGroupingValidation() {
	    return true;
	}
}
