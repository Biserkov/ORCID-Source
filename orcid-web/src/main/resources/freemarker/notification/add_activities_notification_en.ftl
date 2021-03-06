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
<!DOCTYPE html>
<html>
<#assign verDateTime = startupDate?datetime>
<#assign ver="${verDateTime?iso_utc}">

<#assign aworks = 0>
<#assign tworks = "">
<#assign wbuttons = false>
<#assign wurl = "">
<#assign wputCode = "">

<#assign aeducation = 0>
<#assign teducation = "">
<#assign edubuttons = false>
<#assign eduUrl = "">
<#assign eduPutCode = "">

<#assign aemployment = 0>
<#assign temployment = "">
<#assign empButtons = false>
<#assign empUrl = "">
<#assign empPutCode = "">

<#assign apeerreview = 0>
<#assign tpeerreview = "">
<#assign pButtons = false>
<#assign pUrl = "">
<#assign pPutCode = "">

<#assign afunding = 0>
<#assign tfunding = "">
<#assign fButtons = false>
<#assign fUrl = "">
<#assign fPutCode = "">


<head>
    <meta charset="utf-8" />    
    <meta name="description" content="">
    <meta name="author" content="ORCID">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.28/angular.min.js"></script>
    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.1.0/css/bootstrap.min.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticCdn}/css/fonts.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticLoc}/css/glyphicons.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticCdn}/css/orcid.new.css?v=${ver}"/>
    <style> 
		body, html{			
			color: #494A4C;
			font-size: 15px;
			font-family: 'Gill Sans W02', 'Helvetica', sans-serif;
			font-style: normal;
			min-height: 100px; /* Do not change */
			height: auto; /* Do not change */
			padding-bottom: 30px; /* Do not change */
		}
		
		.workspace-accordion-header{
			color: #FFF;
			font-weight: bold;
			padding: 5px;
			margin: 10px 0;
			cursor: pointer;
		}
		
		.notifications-inner{
			padding: 5px 15px;			
		}
		
		.notifications-buttons{
			margin-top: 15px;
		}
		
		.glyphicons{
			top: -10px;
			padding-left: 21px;
			
		}
		
		.glyphicons:before{
			color: #FFF;
			font: 16px/1em 'Glyphicons Regular'
		}
		
		
	</style>
	<script type="text/javascript">
		var appInIframe = angular.module('appInFrame', []);
	
		appInIframe.factory('$parentScope', function($window) {
		  return $window.parent.angular.element($window.frameElement).scope();
		});
	
		appInIframe.controller('iframeController', function($scope, $parentScope) {
			
			
		  $scope.archivedDate = "${notification.archivedDate!}"
		  
		  $scope.archive = function(id) {			
			$parentScope.archive(id);
			$parentScope.$apply();
		  };		  
		});
	</script>
	<!--  Do not remove -->
	<script type="text/javascript" src="${staticCdn}/javascript/iframeResizer.contentWindow.min.js?v=${ver}"></script>
</head>
<body data-baseurl="<@spring.url '/'/>" ng-app="appInFrame" ng-controller="iframeController">
	
	<#list notification.activities.activities?sort_by("activityType") as activity>
		<#switch activity.activityType>
			 <#case "WORK">
			  	<#assign aworks = aworks + 1>
			  	<#assign tworks = tworks + activity.activityName>
			  	<#if activity.externalId??>
	           		<#assign tworks = tworks + "(" + activity.externalId.externalIdType + ":" + activity.externalId.externalIdValue + ")">
	       		</#if>
	       		<#assign tworks = tworks + "<br/>">
	       		<#if notification.authorizationUrl??>
	       			<#assign wbuttons = true>
	       			<#assign wurl = notification.authorizationUrl.uri>
	       			<#assign wputCode = notification.putCode>
	       		</#if>
			    <#break>
			  <#case "EMPLOYMENT">
			     <#assign aemployment = aemployment + 1>
			     <#assign temployment = temployment + activity.activityName + "<br/>">
			     <#break>
			  <#case "EDUCATION">
			     <#assign aeducation = aeducation + 1>
			     <#assign teducation = teducation + activity.activityName + "<br/>">
			     <#break>
			 <#case "FUNDING">
			     <#assign afunding = afunding + 1>
			     <#assign tfunding = tfunding + activity.activityName>
			     <#if activity.externalId??>
	           		<#assign tfunding = tfunding + "(" + activity.externalId.externalIdType + ":" + activity.externalId.externalIdValue + ")">
	       		 </#if>
	       		 <#assign tfunding = tfunding + "<br/>">
			     <#break>
			 <#case "PEER_REVIEW">
			     <#assign apeerreview = apeerreview + 1>
			     <#assign tpeerreview = tpeerreview + activity.activityName>
			     <#if activity.externalId??>
	           		<#assign tpeerreview = tpeerreview + "(" + activity.externalId.externalIdType + ":" + activity.externalId.externalIdValue + ")">
	       		 </#if>
	       		 <#assign tpeerreview = tpeerreview + "<br/>">
			     <#break>
			  <#default>
		</#switch>
		<#if activity.externalId??>
	           (${activity.externalId.externalIdType}: ${activity.externalId.externalIdValue})
	       </#if>
	</#list>


	<!-- Start rendering -->
	<div>
	    <strong>${notification.source.sourceName.content}</strong> would like to add the following items to your record:
	</div>
	<div class="notifications-inner">
		<#if aeducation gt 0>
			<!-- Education -->
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> Education (${aeducation})
			</div>
			<strong>${teducation}</strong>
		</#if>
		<#if aemployment gt 0>
			<!-- Employment -->
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> Employment (${aemployment})
			</div>
			<strong>${temployment}</strong>
		</#if>
		<#if afunding gt 0>
			<!-- Funding -->
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> Fundings (${afunding})
			</div>
			<strong>${tfunding}</strong>
			<#if fButtons>
				<div class="notifications-buttons">
					<a class="btn btn-primary" href="${fUrl}" target="_blank"><span class="glyphicons cloud-upload"></span> Add now</a>  <a class="btn btn-default" href="" ng-click="archive('${fPutCode?c}')" type="reset" ng-hide="archivedDate">Archive</a>
				</div>
			</#if>
		</#if>
		<#if apeerreview gt 0>
			<!-- Peer Review -->
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> Peer Review (${apeerreview})
			</div>
			<strong>${tpeerreview}</strong>
			<#if pButtons>
				<div class="notifications-buttons">
					<a class="btn btn-primary" href="${pUrl}" target="_blank"><span class="glyphicons cloud-upload"></span> Add now</a>  <a class="btn btn-default" href="" ng-click="archive('${pPutCode?c}')" type="reset" ng-hide="archivedDate">Archive</a>
				</div>
			</#if>
		</#if>
		<#if aworks gt 0>
			<!-- Works -->
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> Works (${aworks})
			</div>
			<strong>${tworks}</strong>
			<#if wbuttons>
				<div class="notifications-buttons">
					<a class="btn btn-primary" href="${wurl}" target="_blank"><span class="glyphicons cloud-upload"></span> Add now</a>  <a class="btn btn-default" href="" ng-click="archive('${wputCode?c}')" type="reset" ng-hide="archivedDate">Archive</a>
				</div>
			</#if>
		</#if>
	</div>	
	
</body>
</html>