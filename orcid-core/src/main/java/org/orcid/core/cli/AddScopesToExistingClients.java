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
package org.orcid.core.cli;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Angel Montenegro
 * 
 * */
public class AddScopesToExistingClients {

    
    @Option(name = "-s", usage = "CSV new scopes to add, they must be valid values in the ScopePathType enum")
    private String newScopes;
    
    @Option(name = "-t", usage = "CSV client types, must be one in ClientType, if null, assume the change applies to all client types")
    private String clientTypes;
    
    private Set<ScopePathType> scopes = new HashSet<ScopePathType>();
    
    private Set<ClientType> allowedClientTypes = new HashSet<ClientType>();
    
    private ClientDetailsManager clientDetailsManager;
    private TransactionTemplate transactionTemplate;

    private int clientsUpdated = 0;    
    
    public static void main(String [] args) {
        AddScopesToExistingClients addScopesToExistingClients = new AddScopesToExistingClients();
        CmdLineParser parser = new CmdLineParser(addScopesToExistingClients);
        try {           
            parser.parseArgument(args);
            addScopesToExistingClients.validateParameters(parser);
            addScopesToExistingClients.init();
            addScopesToExistingClients.process();
            System.out.println();
            System.out.println();
            System.out.println(addScopesToExistingClients.getClientsUpdated() + " clients were updated");
            System.out.println();
            System.out.println();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }        
        System.exit(0);
    }
    
    @SuppressWarnings("resource")
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        clientDetailsManager = (ClientDetailsManager) context.getBean("clientDetailsManager");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
    
    public void process() {         
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                List<ClientDetailsEntity> clients = clientDetailsManager.getAll();
                for (ClientDetailsEntity client : clients) {
                    // Only updater clients should be updated
                    if (isInAllowedClientTypes(client)) {
                        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(client.getId());
                        updateScopes(clientDetails);
                    }
                }
            }
        });
    }
    
    private boolean isInAllowedClientTypes(ClientDetailsEntity client) {
        //Ignore the public client
        if(client.getClientType() == null || ClientType.PUBLIC_CLIENT.equals(client.getClientType())) {
            return false;
        }
        
        if(allowedClientTypes == null || allowedClientTypes.isEmpty())
            return true;
        
        for(ClientType clientType : allowedClientTypes) {
            if(clientType.equals(client.getClientType()))
                return true;
        }
        
        return false;
    }
    
    private void updateScopes(ClientDetailsEntity clientDetails) {        
        for(ScopePathType scope : scopes) {            
            boolean alreadyHaveReadPublicScope = false;
            for (ClientScopeEntity existingScope : clientDetails.getClientScopes()) {
                if (scope.value().equals(existingScope.getScopeType())) {
                    alreadyHaveReadPublicScope = true;
                    break;
                }
            }

            if (!alreadyHaveReadPublicScope) {
                ClientScopeEntity clientScope = new ClientScopeEntity();
                clientScope.setClientDetailsEntity(clientDetails);
                clientScope.setScopeType(scope.value());
                clientScope.setDateCreated(new Date());
                clientScope.setLastModified(new Date());
                clientDetails.getClientScopes().add(clientScope);
                clientDetailsManager.merge(clientDetails);
                clientsUpdated += 1;
                System.out.println("Client " + clientDetails.getId() + " has been updated");                
            } else {
                System.out.println("Client " + clientDetails.getId() + " already have the " + scope.value() + " scope");
            }
        }                
    }        
    
    public void validateParameters(CmdLineParser parser) throws CmdLineException {
        if(PojoUtil.isEmpty(newScopes)) {
            throw new CmdLineException(parser, "-s parameter must not be null");
        } else {
            String [] scopesArray = newScopes.split(",");
            for(String scope : scopesArray) {
                try {
                    scopes.add(ScopePathType.fromValue(scope));
                } catch(IllegalArgumentException ie) {
                    throw new CmdLineException(parser, "Invalid scope: " + scope);
                }
            }
        }
        
        if(!PojoUtil.isEmpty(clientTypes)) {
            String [] clientTypesArray = clientTypes.split(",");
            for(String clientType : clientTypesArray) {
                try {
                    allowedClientTypes.add(ClientType.fromValue(clientType));
                } catch(IllegalArgumentException ie) {
                    throw new CmdLineException(parser, "Invalid client type: " + clientType);
                }
            }            
        }
    }

    public int getClientsUpdated() {
        return clientsUpdated;
    }            
}
