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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.common.FuzzyDate;
import org.orcid.jaxb.model.common.Subtitle;
import org.orcid.jaxb.model.common.Title;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.Organization;
import org.orcid.jaxb.model.common.OrganizationAddress;
import org.orcid.jaxb.model.common.Url;
import org.orcid.jaxb.model.record.PeerReview;
import org.orcid.jaxb.model.record.PeerReviewType;
import org.orcid.jaxb.model.record.Role;
import org.orcid.jaxb.model.record.Subject;
import org.orcid.jaxb.model.record.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record.WorkTitle;
import org.orcid.jaxb.model.record.WorkType;

public class PeerReviewForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = -6291184427922193706L;

    private List<String> errors = new ArrayList<String>();

    private Visibility visibility;

    private Text putCode;

    private List<WorkExternalIdentifier> externalIdentifiers;

    private Text url;

    private Text role;

    private Text type;

    private Text orgName;
    
    private Text city;

    private Text region;

    private Text country;

    private String countryForDisplay;

    private Text disambiguatedOrganizationSourceId;

    private Text disambiguationSource;

    private Date completionDate;

    private PeerReviewSubjectForm subjectForm;

    private String source;

    private String sourceName;

    private Date createdDate;

    private Date lastModified;

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Text getPutCode() {
        return putCode;
    }

    public void setPutCode(Text putCode) {
        this.putCode = putCode;
    }

    public List<WorkExternalIdentifier> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<WorkExternalIdentifier> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public Text getUrl() {
        return url;
    }

    public void setUrl(Text url) {
        this.url = url;
    }

    public Text getRole() {
        return role;
    }

    public void setRole(Text role) {
        this.role = role;
    }

    public Text getType() {
        return type;
    }

    public void setType(Text type) {
        this.type = type;
    }

    public Text getCity() {
        return city;
    }

    public void setCity(Text city) {
        this.city = city;
    }

    public Text getRegion() {
        return region;
    }

    public void setRegion(Text region) {
        this.region = region;
    }

    public Text getCountry() {
        return country;
    }

    public void setCountry(Text country) {
        this.country = country;
    }

    public String getCountryForDisplay() {
        return countryForDisplay;
    }

    public void setCountryForDisplay(String countryForDisplay) {
        this.countryForDisplay = countryForDisplay;
    }

    public Text getDisambiguatedOrganizationSourceId() {
        return disambiguatedOrganizationSourceId;
    }

    public void setDisambiguatedOrganizationSourceId(Text disambiguatedOrganizationSourceId) {
        this.disambiguatedOrganizationSourceId = disambiguatedOrganizationSourceId;
    }

    public Text getDisambiguationSource() {
        return disambiguationSource;
    }

    public void setDisambiguationSource(Text disambiguationSource) {
        this.disambiguationSource = disambiguationSource;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public PeerReviewSubjectForm getSubjectForm() {
        return subjectForm;
    }

    public void setSubjectForm(PeerReviewSubjectForm subject) {
        this.subjectForm = subject;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }    
    
    public Text getOrgName() {
        return orgName;
    }

    public void setOrgName(Text orgName) {
        this.orgName = orgName;
    }

    public PeerReview toPeerReview() {
        PeerReview peerReview = new PeerReview();
        // Put Code
        if (!PojoUtil.isEmpty(putCode)) {
            peerReview.setPutCode(null);
        }
        // Visibility
        if (visibility != null) {
            peerReview.setVisibility(visibility);
        }

        // Completion date
        if (completionDate != null) {
            peerReview.setCompletionDate(new FuzzyDate(completionDate.toFuzzyDate()));
        }

        // External identifiers
        if (externalIdentifiers != null && !externalIdentifiers.isEmpty()) {
            peerReview.setExternalIdentifiers(new WorkExternalIdentifiers());
            for (WorkExternalIdentifier extId : externalIdentifiers) {
                peerReview.getExternalIdentifiers().getExternalIdentifier().add(extId.toRecordWorkExternalIdentifier());
            }
        }

        // Set Organization
        Organization organization = new Organization();
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization.setAddress(organizationAddress);
        if(!PojoUtil.isEmpty(orgName)) {
            organization.setName(orgName.getValue());
        }
        if (!PojoUtil.isEmpty(city)) {
            organizationAddress.setCity(city.getValue());
        }
        if (!PojoUtil.isEmpty(region)) {
            organizationAddress.setRegion(region.getValue());
        }
        if (!PojoUtil.isEmpty(country)) {
            organizationAddress.setCountry(Iso3166Country.fromValue(country.getValue()));
        }
        if (!PojoUtil.isEmpty(disambiguatedOrganizationSourceId)) {
            organization.setDisambiguatedOrganization(new DisambiguatedOrganization());
            organization.getDisambiguatedOrganization().setDisambiguatedOrganizationIdentifier(disambiguatedOrganizationSourceId.getValue());
            organization.getDisambiguatedOrganization().setDisambiguationSource(disambiguationSource.getValue());
        }
        peerReview.setOrganization(organization);

        // Role
        if (!PojoUtil.isEmpty(role)) {
            peerReview.setRole(Role.fromValue(role.getValue()));
        }

        // Type
        if (!PojoUtil.isEmpty(type)) {
            peerReview.setType(PeerReviewType.fromValue(type.getValue()));
        }

        // Url
        if (!PojoUtil.isEmpty(url)) {
            peerReview.setUrl(new Url(url.getValue()));
        }

        // Subject form
        if (subjectForm != null) {
            Subject subject = new Subject();
            subject.setExternalIdentifiers(new WorkExternalIdentifiers());
            // External identifiers
            if (subjectForm.getWorkExternalIdentifiers() != null && !subjectForm.getWorkExternalIdentifiers().isEmpty()) {
                subject.setExternalIdentifiers(new WorkExternalIdentifiers());
                for (WorkExternalIdentifier extId : subjectForm.getWorkExternalIdentifiers()) {
                    subject.getExternalIdentifiers().getExternalIdentifier().add(extId.toRecordWorkExternalIdentifier());
                }
            }

            // Journal title
            if (!PojoUtil.isEmpty(subjectForm.getJournalTitle())) {
                subject.setJournalTitle(new Title(subjectForm.getJournalTitle().getValue()));
            }

            // Title
            WorkTitle title = new WorkTitle();
            if (!PojoUtil.isEmpty(subjectForm.getTitle())) {
                title.setTitle(new Title(subjectForm.getTitle().getValue()));
            }
            if (!PojoUtil.isEmpty(subjectForm.getSubtitle())) {
                title.setSubtitle(new Subtitle(subjectForm.getSubtitle().getValue()));
            }
            if (subjectForm.getTranslatedTitle() != null && !PojoUtil.isEmpty(subjectForm.getTranslatedTitle().getContent())) {
                org.orcid.jaxb.model.common.TranslatedTitle translatedTitle = new org.orcid.jaxb.model.common.TranslatedTitle();
                translatedTitle.setContent(subjectForm.getTranslatedTitle().getContent());
                translatedTitle.setLanguageCode(subjectForm.getTranslatedTitle().getLanguageCode());
                title.setTranslatedTitle(translatedTitle);
            }
            subject.setTitle(title);

            // Type
            if (!PojoUtil.isEmpty(subjectForm.getWorkType())) {
                subject.setType(WorkType.fromValue(subjectForm.getWorkType().getValue()));
            }

            // Url
            if (!PojoUtil.isEmpty(subjectForm.getUrl())) {
                subject.setUrl(new Url(subjectForm.getUrl().getValue()));
            }

            if(!PojoUtil.isEmpty(subjectForm.getPutCode())) {
                subject.setPutCode(subjectForm.getPutCode().getValue());
            }
            
            peerReview.setSubject(subject);
        }

        return peerReview;
    }

    public static PeerReviewForm valueOf(PeerReview peerReview) {
        PeerReviewForm form = new PeerReviewForm();

        // Put code
        if (!PojoUtil.isEmpty(peerReview.getPutCode())) {
            form.setPutCode(Text.valueOf(peerReview.getPutCode()));
        }

        // Visibility
        if (peerReview.getVisibility() != null) {
            form.setVisibility(peerReview.getVisibility());
        }

        // Completion date
        if (!PojoUtil.isEmpty(peerReview.getCompletionDate())) {
            form.setCompletionDate(Date.valueOf(peerReview.getCompletionDate()));
        }

        // Role
        if (peerReview.getRole() != null) {
            form.setRole(Text.valueOf(peerReview.getRole().value()));
        }

        // Type
        if (peerReview.getType() != null) {
            form.setType(Text.valueOf(peerReview.getType().value()));
        }

        // Url
        if (!PojoUtil.isEmpty(peerReview.getUrl())) {
            form.setUrl(Text.valueOf(peerReview.getUrl().getValue()));
        }

        // Org info
        if (peerReview.getOrganization() != null) {
            if(!PojoUtil.isEmpty(peerReview.getOrganization().getName())) {
                form.setOrgName(Text.valueOf(peerReview.getOrganization().getName()));
            }
            if (peerReview.getOrganization().getAddress() != null) {
                if (!PojoUtil.isEmpty(peerReview.getOrganization().getAddress().getCity())) {
                    form.setCity(Text.valueOf(peerReview.getOrganization().getAddress().getCity()));
                }
                if (peerReview.getOrganization().getAddress().getCountry() != null) {
                    form.setCountry(Text.valueOf(peerReview.getOrganization().getAddress().getCountry().value()));
                }
                if (!PojoUtil.isEmpty(peerReview.getOrganization().getAddress().getRegion())) {
                    form.setRegion(Text.valueOf(peerReview.getOrganization().getAddress().getRegion()));
                }
            }

            if (peerReview.getOrganization().getDisambiguatedOrganization() != null) {
                if (!PojoUtil.isEmpty(peerReview.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier())) {
                    form.setDisambiguatedOrganizationSourceId(Text
                            .valueOf(peerReview.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier()));
                }
                if (!PojoUtil.isEmpty(peerReview.getOrganization().getDisambiguatedOrganization().getDisambiguationSource())) {
                    form.setDisambiguationSource(Text.valueOf(peerReview.getOrganization().getDisambiguatedOrganization().getDisambiguationSource()));
                }
            }
        }

        // External ids
        if(peerReview.getExternalIdentifiers() != null) {
            List<org.orcid.jaxb.model.record.WorkExternalIdentifier> externalIdentifiers = peerReview.getExternalIdentifiers().getExternalIdentifier();
            form.setExternalIdentifiers(new ArrayList<WorkExternalIdentifier>());
            for(org.orcid.jaxb.model.record.WorkExternalIdentifier extId : externalIdentifiers) {                
                form.getExternalIdentifiers().add(WorkExternalIdentifier.valueOf(extId));
            }                                    
        }        

        // Subject
        if(peerReview.getSubject() != null) {
            PeerReviewSubjectForm subjectForm = new PeerReviewSubjectForm();
            
            if(!PojoUtil.isEmpty(peerReview.getSubject().getPutCode())) {
                subjectForm.setPutCode(Text.valueOf(peerReview.getSubject().getPutCode()));
            }            
            
            if(peerReview.getSubject().getJournalTitle() != null && !PojoUtil.isEmpty(peerReview.getSubject().getJournalTitle().getContent())) {
                subjectForm.setJournalTitle(Text.valueOf(peerReview.getSubject().getJournalTitle().getContent()));
            }
                        
            if(!PojoUtil.isEmpty(peerReview.getSubject().getUrl())) {
                subjectForm.setUrl(Text.valueOf(peerReview.getSubject().getUrl().getValue()));
            }
            
            if(peerReview.getSubject().getType() != null) {
                subjectForm.setWorkType(Text.valueOf(peerReview.getSubject().getType().value()));
            }
                        
            if(peerReview.getSubject().getTitle() != null) {
                if(peerReview.getSubject().getTitle().getTitle() != null && !PojoUtil.isEmpty(peerReview.getSubject().getTitle().getTitle().getContent())) {
                    subjectForm.setTitle(Text.valueOf(peerReview.getSubject().getTitle().getTitle().getContent()));
                }
                
                if(peerReview.getSubject().getTitle().getTranslatedTitle() != null && !PojoUtil.isEmpty(peerReview.getSubject().getTitle().getTranslatedTitle().getContent())) {
                    TranslatedTitle translatedTitle = new TranslatedTitle();
                    translatedTitle.setContent(peerReview.getSubject().getTitle().getTranslatedTitle().getContent());
                    translatedTitle.setLanguageCode(peerReview.getSubject().getTitle().getTranslatedTitle().getLanguageCode());
                    subjectForm.setTranslatedTitle(translatedTitle);
                }
                
                if(peerReview.getSubject().getTitle().getSubtitle() != null && !PojoUtil.isEmpty(peerReview.getSubject().getTitle().getSubtitle().getContent())) {
                    subjectForm.setSubtitle(Text.valueOf(peerReview.getSubject().getTitle().getSubtitle().getContent()));
                }                
            }
            
            if(peerReview.getSubject().getExternalIdentifiers() != null) {
                List<org.orcid.jaxb.model.record.WorkExternalIdentifier> extIds = peerReview.getSubject().getExternalIdentifiers().getExternalIdentifier();
                subjectForm.setWorkExternalIdentifiers(new ArrayList<WorkExternalIdentifier> ());
                if(extIds != null) {
                    for(org.orcid.jaxb.model.record.WorkExternalIdentifier extId : extIds) {
                        subjectForm.getWorkExternalIdentifiers().add(WorkExternalIdentifier.valueOf(extId));
                    }
                }                
            }                                   
            
            
            
            form.setSubjectForm(subjectForm);
        }        

        // Source
        if(peerReview.getSource() != null) {
            form.setSource(peerReview.getSource().retrieveSourcePath());
            if(peerReview.getSource().getSourceName() != null)
                form.setSourceName(peerReview.getSource().getSourceName().getContent());
        }        

        // Created Date
        if(peerReview.getCreatedDate() != null) {
            form.setCreatedDate(Date.valueOf(peerReview.getCreatedDate()));
        }        

        // Last modified
        if(peerReview.getLastModifiedDate() != null) {
            form.setLastModified(Date.valueOf(peerReview.getLastModifiedDate()));
        }        

        return form;
    }

}