package org.orcid.core.manager.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class ClientDetailsManagerImpl implements ClientDetailsManager {

    @Resource
    ClientDetailsDao clientDetailsDao;

    @Override    
    @Cacheable(value = "client-details", key = "#orcid.concat('-').concat(#lastModified)")
    public ClientDetailsEntity findByClientId(String orcid, Date lastModified) {
        return clientDetailsDao.findByClientId(orcid);
    }

    @Override
    @Transactional
    public void removeByClientId(String clientId) {
        clientDetailsDao.removeByClientId(clientId);
    }

    @Override
    @Transactional
    public void persist(ClientDetailsEntity clientDetails) {
        clientDetailsDao.persist(clientDetails);
    }

    @Override
    @Transactional
    public ClientDetailsEntity merge(ClientDetailsEntity clientDetails) {
        return clientDetailsDao.merge(clientDetails);
    }
    
    @Override
    @Transactional
    public void remove(String clientId){
        clientDetailsDao.remove(clientId);        
    }
    
    @Override    
    public ClientDetailsEntity find(String clientId) {
        return clientDetailsDao.find(clientId);
    }
    
    @Override
    public List<ClientDetailsEntity> getAll() {
        return clientDetailsDao.getAll();
    }
}
