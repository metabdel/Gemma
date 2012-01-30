<%@ include file="/common/taglibs.jsp"%>
<head>
<jwr:script src='/scripts/ajax/ext/data/DwrProxy.js' />

<script type="text/javascript">
Ext.namespace('Gemma');
Ext.onReady(function() {
	Ext.QuickTips.init();
	// need wrapper panel because tabPanels can't have title headers (it's used for tabs)
	new Gemma.GemmaViewPort({
		 	centerPanelConfig: new Ext.Panel({
		 		items:[
		 			new Gemma.GenePage({
		 				geneId:  Ext.get("geneId").getValue(),
		 				geneSymbol: Ext.get("geneSymbol").getValue(), 
		 				geneName: Ext.get("geneName").getValue(),
		 				geneTaxonName: Ext.get("geneTaxonName").getValue() 
			 	})],
			 	layout:'fit', 
			 	title:Ext.get("geneSymbol").getValue()
			 })
		 });
});
</script>

	<title><c:if test="${not empty geneOfficialSymbol}">
			${geneOfficialSymbol}
		</c:if> <fmt:message key="gene.details" />
	</title>
</head>

<body>

	<input type="hidden" name="geneId" id="geneId" value="${geneId}" />
	<input type="hidden" name="geneSymbol" id="geneSymbol" value="${geneOfficialSymbol}" />
	<input type="hidden" name="geneName" id="geneName" value="${geneOfficialName}" />
	<input type="hidden" name="geneTaxonName" id="geneTaxonName" value="${geneTaxonCommonName}" />
	
	<div id="newGenePageWidget"></div>