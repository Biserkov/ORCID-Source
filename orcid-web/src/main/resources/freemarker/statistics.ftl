<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#-- @ftlvariable name="statistics" type="java.util.Map" -->
<@public >
<#escape x as x?html>
<div id="statistics">
	<div class="row">	    
	    <div class="col-md-9 col-md-offset-3">
	    	<span class="page-header"><@orcid.msg 'statistics.header'/></span>
	    </div>
    </div>
    <div class="row">    
	    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
	        	<span class="stat-name"><@orcid.msg 'statistics.live_ids'/></span>
	    </div>
	    <div class="col-md-3 col-sm-4 col-xs-5 right">
	    	<span class="stat-data"><#if statistics['liveIds']??>${statistics['liveIds']}<#else><@orcid.msg 'statistics.calculating'/></#if></span>
	    </div>	  
    </div>
    <div class="row">        
	    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
	    	<span class="stat-name"><@orcid.msg 'statistics.ids_with_works'/></span>
	    </div>
	    <div class="col-md-3 col-sm-4 col-xs-5 right">
	    	<span class="stat-data"><#if statistics['idsWithWorks']??>${statistics['idsWithWorks']}<#else><@orcid.msg 'statistics.calculating'/></#if></span>
		</div>	  
    </div>
    <div class="row">        
	    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
	    	<span class="stat-name"><@orcid.msg 'statistics.number_of_works'/></span>
	    </div>
	    <div class="col-md-3 col-sm-4 col-xs-5 right">
	    	<span class="stat-data"><#if statistics['works']??>${statistics['works']}<#else><@orcid.msg 'statistics.calculating'/></#if></span>	
		</div>	  
    </div>
    <div class="row">        
	    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
			<span class="stat-name"><@orcid.msg 'statistics.number_of_works_with_dois'/></span>	    
	    </div>
	    <div class="col-md-3 col-sm-4 col-xs-5 right">
	    	<span class="stat-data"><#if statistics['worksWithDois']??>${statistics['worksWithDois']}<#else><@orcid.msg 'statistics.calculating'/></#if></span>
		</div>
    </div>  
    
    
    <div class="row">        
	    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
			<span class="stat-name"><@orcid.msg 'statistics.number_of_locked_records'/></span>	    
	    </div>
	    <div class="col-md-3 col-sm-4 col-xs-5 right">
	    	<span class="stat-data"><#if statistics['lockedRecords']??>${statistics['lockedRecords']}<#else><@orcid.msg 'statistics.calculating'/></#if></span>
		</div>
    </div>  
    
    
    	
    <div class="row">        
	    <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
			<span class="stat-date"><@orcid.msg 'statistics.statistics_generation_date_label'/>&nbsp;<#if (statistics_date)??>${statistics_date}<#else>NA</#if></span>	    
	    </div>  		
    </div>
</div>
</#escape>
</@public>