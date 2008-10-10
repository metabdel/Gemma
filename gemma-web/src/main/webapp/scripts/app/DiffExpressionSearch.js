/**
 * @author keshav
 * @version $Id$
 */
Ext.namespace('Gemma');
Ext.BLANK_IMAGE_URL = '/Gemma/images/default/s.gif';
Ext.onReady(function() {
	Ext.QuickTips.init();
	Ext.state.Manager.setProvider(new Ext.state.CookieProvider());

	var admin = dwr.util.getValue("hasAdmin");

	if (Ext.isIE && !Ext.isIE7) {
		Ext.DomHelper.append('diffExpression-form', {
			tag : 'p',
			cls : 'trouble',
			html : 'This page displays improperly in older versions of Internet Explorer.  Please upgrade to Internet Explorer 7.'
		});
	}

	var searchPanel = new Gemma.DiffExpressionSearchForm({});
	searchPanel.render("diffExpression-form");

	var diffExGrid = new Gemma.DiffExpressionGrid({
				renderTo : "diffExpression-results",
				title : "Differentially expressed genes",
				pageSize : 25
			});

	searchPanel.geneChooserPanel.toolbar.taxonCombo.on('ready', function() {
				Ext.get('loading').remove();
				Ext.get('loading-mask').fadeOut({
							duration : 0.5,
							remove : true
						});
			});

	searchPanel.on("aftersearch", function(panel, result) {

		var link = panel.getBookmarkableLink();
		diffExGrid
				.setTitle(String
						.format(
								"Differentially expressed genes <a href='{0}'>(bookmarkable link)</a> <a href='{0}&export'>(export as text)</a>",
								link));

		diffExGrid.loadData(result);

	});

});