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
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.12.05 at 03:27:53 PM GMT 
//

package org.orcid.jaxb.model.record;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for disambiguated-organization complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="disambiguated-organization">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}disambiguated-organization-identifier"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}disambiguation-source"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "disambiguatedOrganizationIdentifier", "disambiguationSource" })
@XmlRootElement(name = "disambiguatedOrganization", namespace = "http://www.orcid.org/ns/common")
public class DisambiguatedOrganization implements Serializable {

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, namespace = "http://www.orcid.org/ns/common")
    protected String disambiguatedOrganizationIdentifier;
    @XmlElement(required = true, namespace = "http://www.orcid.org/ns/common")
    protected String disambiguationSource;
    @XmlTransient
    protected Long id;

    /**
     * Gets the value of the disambiguatedOrganizationIdentifier property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDisambiguatedOrganizationIdentifier() {
        return disambiguatedOrganizationIdentifier;
    }

    /**
     * Sets the value of the disambiguatedOrganizationIdentifier property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setDisambiguatedOrganizationIdentifier(String value) {
        this.disambiguatedOrganizationIdentifier = value;
    }

    /**
     * Gets the value of the disambiguationSource property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDisambiguationSource() {
        return disambiguationSource;
    }

    /**
     * Sets the value of the disambiguationSource property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setDisambiguationSource(String value) {
        this.disambiguationSource = value;
    }    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((disambiguatedOrganizationIdentifier == null) ? 0 : disambiguatedOrganizationIdentifier.hashCode());
        result = prime * result + ((disambiguationSource == null) ? 0 : disambiguationSource.hashCode());
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
        DisambiguatedOrganization other = (DisambiguatedOrganization) obj;
        if (disambiguatedOrganizationIdentifier == null) {
            if (other.disambiguatedOrganizationIdentifier != null)
                return false;
        } else if (!disambiguatedOrganizationIdentifier.equals(other.disambiguatedOrganizationIdentifier))
            return false;
        if (disambiguationSource == null) {
            if (other.disambiguationSource != null)
                return false;
        } else if (!disambiguationSource.equals(other.disambiguationSource))
            return false;
        return true;
    }

}