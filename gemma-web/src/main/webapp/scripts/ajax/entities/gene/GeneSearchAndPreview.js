/**
 * 
 * TODO refactor combo and preview parts into separate components (combo was originally displayed in the preview, which is why they are part of the same component)
 * 
 * @author thea
 * @version $Id$
 */
Ext.namespace('Gemma');

Gemma.GeneSearchAndPreview = Ext.extend(Ext.Panel, {
	listModified : false,
	getSelectedGeneOrGeneSetValueObject:function(){
		return (this.selectedGeneOrGroup)?this.selectedGeneOrGroup.resultValueObject:null;
	},
	setSelectedGeneSetValueObject: function(gsvo){
		this.selectedGeneSetValueObject = gsvo;
		this.isGeneSet = true;
		this.isGene = false;
	},
	getSelectedGeneSetValueObject: function(){
		return this.selectedGeneSetValueObject;
	},
	resetGenePreview : function() {
		this.preview.resetPreview();
	},
	showGenePreview : function() {
		this.preview.showPreview();
	},
	collapsePreview : function(){
		this.preview.collapsePreview();
	},
	maskGenePreview : function() {
		if (!this.loadMask && this.getEl()) {
			this.loadMask = new Ext.LoadMask(this.getEl(), {
						msg : "Loading Genes ..."
					});
		}
		if (this.loadMask) {
			this.loadMask.show();
		}
	},

	/**
	 * called when a record is selected from geneAndGeneGroupCombo
	 * @param {Object} record
	 * @param {Object} query
	 */
	loadGeneOrGroup : function(record, query) {

		this.selectedGeneOrGroup = record.data;
		if(this.selectedGeneOrGroup.resultValueObject instanceof GeneSetValueObject){
			this.setSelectedGeneSetValueObject( this.selectedGeneOrGroup.resultValueObject );
			this.isGeneSet = true;
			this.isGene = false;
		}else if (this.selectedGeneOrGroup.resultValueObject instanceof GeneValueObject){
			this.isGene = true;
			this.isGeneSet = false;
		}

		var id = record.get("resultValueObject").id;
		var size = record.get("size");
		var name = record.get("name");
		var taxonId = this.searchForm.getTaxonId();
		
		// for bookmarking diff ex viz
		if (id === null) {
			var queryToGetSelected = "";
			if (this.isGeneSet && this.selectedGeneSetValueObject instanceof GOGroupValueObject && this.name.match(/^GO_\d+/)) {
				queryToGetSelected = "taxon:"+taxonId+";GO:"+name;
			}else if(this.isGeneSet && resultValueObject instanceof FreeTextGeneResultsValueObject && name.indexOf(query)!=-1){
				queryToGetSelected = "taxon:"+taxonId+";query:"+query;
			}
			this.queryUsedToGetSessionGroup = queryToGetSelected;
		}

		var geneIds = [];

		// load preview of group if group was selected
		if (this.isGeneSet) {
			
			this.preview.loadGenePreviewFromGeneSet(this.getSelectedGeneSetValueObject());

		}
		// load single gene if gene was selected
		else {
			
			this.selectedGeneOrGroup.memberIds = [id];
			this.geneIds = [id];

			this.searchForm.geneGroupId = null;

			// reset the gene preview panel content
			this.resetGenePreview();

			this.preview.loadGenePreviewFromGenes([this.selectedGeneOrGroup.resultValueObject]);
		}
	},

	/**
	 * update the contents of the gene preview box and the this.geneIds value
	 * using a list of gene Ids
	 * 
	 * @param geneIds
	 *            an array of geneIds to use
	 */
	loadGenes : function(ids) {

		this.preview.mask();

		// store the ids
		this.geneIds = ids;
		this.searchForm.geneIds = ids;
		// load the preview
		this.preview.loadGenePreviewFromIds(ids);

	},

	/**
	 * Given text, search Gemma for matching genes. Used to 'bulk load' genes
	 * from the GUI.
	 * 
	 * @param {}
	 *            e
	 */
	getGenesFromList : function(e, taxonId) {
		var taxonName;
		if (!taxonId && this.searchForm.getTaxonId()) {
			taxonId = this.searchForm.getTaxonId();
			taxonName = this.searchForm.getTaxonName();
		}else{
			taxonId = this.symbolList._taxonCombo.getTaxon().id;
			taxonName = this.symbolList._taxonCombo.getTaxon().data.commonName;
		}

		if (isNaN(taxonId)) {
			Ext.Msg.alert("Missing information", "Please select a taxon.");
			return;
		}

		this.searchForm.taxonChanged(taxonId, taxonName);
		this.geneCombo.disable().hide();
		this.helpBtn.hide();
		this.symbolListButton.hide();
		this.fireEvent('madeFirstSelection');
		this.doLayout();
								
		var loadMask = new Ext.LoadMask(this.getEl(), {
					msg : "Loading genes..."
				});
		loadMask.show();
		var text = e.geneNames;
		GenePickerController.searchMultipleGenesGetMap(text, taxonId, {

			callback : function(queryToGenes) {
				var i;
				var geneData = [];
				var warned = false;
				this.maskGenePreview();

				var geneIds = [];
				var queriesWithMoreThanOneResult = [];
				var queriesWithNoResults = [];
				var query;

				// for each query
				for (query in queryToGenes) {
					var genes = queryToGenes[query];
					// for each result of that query

					// if a query matched more than one result, store for
					// notifying user
					if (genes.length > 1) {
						queriesWithMoreThanOneResult.push(query);
					}

					// if a query matched no results, store for notifying user
					if (genes.length === 0) {
						queriesWithNoResults.push(query);
					}

					for (i = 0; i < genes.length; i++) {
						// store all ids
						geneIds.push(genes[i].id);
					}
				}

				this.searchForm.geneIds = geneIds;
				this.geneIds = geneIds;
				var newGeneSet = new SessionBoundGeneSetValueObject();
				newGeneSet.modified = false;
				newGeneSet.geneIds = geneIds;
				newGeneSet.taxonId = taxonId;
				newGeneSet.name = 'From Symbol List';
				newGeneSet.description = 'Group made from gene symbols entered.';
				newGeneSet.size = geneIds.length;
				newGeneSet.id = null;
				this.selectedGeneOrGroup = newGeneSet;
				this.selectedGeneOrGroup.memberIds = geneIds;
				this.selectedGeneOrGroup.resultValueObject = newGeneSet;

				// if some genes weren't found or some gene matches were
				// inexact,
				// prepare a msg for the user

				var msgMany = "";
				var msgNone = "";
				if (queriesWithMoreThanOneResult.length > 0) {
					msgMany = queriesWithMoreThanOneResult.length +
							((queriesWithMoreThanOneResult.length === 1) ? " query" : " queries") +
							"  returned more than one gene, all were added to the results: <br>";
					// for each query
					query = '';
					for (i = 0; i < queriesWithMoreThanOneResult.length; i++) {
						query = queriesWithMoreThanOneResult[i];
						msgMany += "<br> - <b>" + query + "</b> matched: ";
						genes = queryToGenes[query];
						// for each result of that query
						for (var j = 0; j < genes.length && j < 10; j++) {
							msgMany += genes[j].officialSymbol;
							msgMany += (j + 1 < genes.length) ? ", " : ".";
						}
						if (genes.length > 10) {
							msgMany += "...(" + genes.length - 20 + " more)";
						}
					}
					msgMany += '<br><br><br>';
				}
				if (queriesWithNoResults.length > 0) {
					msgNone = queriesWithNoResults.length +
							((queriesWithNoResults.length === 1) ? " query" : " queries") +
							" did not match any genes in Gemma:<br><br>";
					// for each query
					query = '';
					for (i = 0; i < queriesWithNoResults.length; i++) {
						query = queriesWithNoResults[i];
						msgNone += " - " + query + "<br>";
					}
				}

				// reset the gene preview panel content
				this.resetGenePreview();


				this.preview.loadGenePreviewFromGenes(genes);
				this.preview.setTaxonId(taxonId);
				
				
				if (queriesWithMoreThanOneResult.length > 0 || queriesWithNoResults.length > 0) {

					this.preview.insertMessage('<div style="padding-bottom:7px;color:red;">Not all symbols had exact matches ('+
										 '<a onmouseover="this.style.cursor=\'pointer\'" '+
										 'onclick="Ext.Msg.alert(\'Query Result Details\',\'<br>'+
										 msgMany+
										 msgNone+
										 '\');" style="color: red; text-decoration: underline;">details</a>)</div>'
							);
				}
				
				this.preview.show();

				loadMask.hide();

			}.createDelegate(this),

			errorHandler : function(e) {
				this.getEl().unmask();
				Ext.Msg.alert('There was an error', e);
			}
		});
		this.fireEvent('select');
	},
	/**
	 * Allows updates to the query genes in the form based on existing GeneValueObjects already returned from the server.
	 * 
	 * A stripped down version of getGenesFromList because we already have the GeneValueObjects
	 * from the search results and there is no need for a call to the back end
	 * 
	 * @param {}
	 *            e
	 */
	getGenesFromGeneValueObjects : function(genesToPreview, geneIds, taxonId, taxonName) {		
		
		this.searchForm.geneIds = geneIds;
		this.geneIds = geneIds;
		var newGeneSet = new SessionBoundGeneSetValueObject();
		newGeneSet.modified = false;
		newGeneSet.geneIds = geneIds;
		newGeneSet.taxonId = taxonId;
		newGeneSet.name = 'From Symbol List';
		newGeneSet.description = 'Group made from gene symbols entered.';
		newGeneSet.size = geneIds.length;
		newGeneSet.id = null;
		this.selectedGeneOrGroup = newGeneSet;
		this.selectedGeneOrGroup.memberIds = geneIds;
		this.selectedGeneOrGroup.resultValueObject = newGeneSet;

		this.searchForm.taxonChanged(taxonId, taxonName);
		this.geneCombo.disable().hide();
		this.helpBtn.hide();
		this.symbolListButton.hide();
		this.fireEvent('madeFirstSelection');
		this.doLayout();
		
		
		this.preview.loadGenePreviewFromGenes(genesToPreview);
		this.preview.show();
		
		this.fireEvent('select');
	},
	initComponent : function() {

		this.newBoxTriggered = false;
		this.geneCombo = new Gemma.GeneAndGeneGroupCombo({
			width: 282,
			hideTrigger: true,
			taxonId: this.taxonId
		});
		
		this.geneCombo.on('select', function(combo, record, index) {
								
								this.searchForm.taxonChanged(record.get("taxonId"), record.get("taxonName"));
					
								var query = combo.store.baseParams.query;
								this.loadGeneOrGroup(record, query);
								this.preview.showPreview();
								this.preview.show();
								
								this.preview.setTaxonId(record.get("taxonId"));

								// if this was the first time a selection was
								// made using this box
								if (combo.startValue === '' && this.newBoxTriggered === false) {
									this.fireEvent('madeFirstSelection');
									this.newBoxTriggered = true;
									this.helpBtn.hide();
									this.symbolListButton.hide();
								}
								combo.disable().hide();
								this.helpBtn.hide();
								this.symbolListButton.hide();
								this.doLayout();
								
							},
							this
						);
					

	
		this.relayEvents(this.geneCombo, ['select']);

	
		this.symbolList = new Gemma.GeneImportPanel({
			height:300,
			showTaxonCombo: true,
			listeners: {
				'commit': {
					fn: this.getGenesFromList.createDelegate(this),
					scope: this
				},
				'show': {
					fn: function(){
						if (this.searchForm.getTaxonId() !== null && this.searchForm.getTaxonId() && typeof this.searchForm.getTaxonId() !== 'undefined') {
							this.symbolList._taxonCombo.setTaxonById(this.searchForm.getTaxonId());
							this.symbolList._taxonCombo.disable();
						}
					},
					scope:this
				}
			}
		});
		

		this.symbolListButton = new Ext.Button({
					icon : "/Gemma/images/icons/page_upload.png",
					cls : "x-btn-icon",
					tooltip : "Select multiple genes with a list of symbols or NCBI IDs",
					disabled : false,
					style:'padding-right:5px',
					handler : function() {
						this.geneCombo.reset();
						this.symbolList.show();
					}.createDelegate(this, [], true)
				});

		this.preview = new Gemma.GeneSetPreview();
			
		this.preview.on('geneListModified', function(newSets) {
			var i;
			for (i = 0; i < newSets.length; i++) { // should only be one
				if (typeof newSets[i].geneIds !== 'undefined' && typeof newSets[i].name !== 'undefined') {
					// update record
					this.selectedGeneOrGroup.resultValueObject = newSets[i];
					this.setSelectedGeneSetValueObject(newSets[i]);
				}
			}
		}, this);

		this.preview.on('doneModification', function() {
			this.searchForm.getEl().unmask();
		}, this);
		
		
		this.preview.on('removeMe', function() {
			this.fireEvent('removeGene');
		}, this);
	
		this.helpBtn = new Gemma.InlineHelpIcon({
			tooltipText:'Select a general group of genes or try searching for genes by symbol, '+
					'GO terms or keywords such as: schizophrenia, hippocampus etc.<br><br>'+
					'<b>Example: search for "map kinase" and select a GO group</b>'
		});
		Ext.apply(this, {
			width: 335,
			frame: false,
			border: false,
			hideBorders: true,
			items: [{
				layout: 'hbox',
				hideBorders: true,
				items: [this.symbolListButton, this.geneCombo, this.helpBtn]
			}, this.preview]
		});
				
		Gemma.GeneSearchAndPreview.superclass.initComponent.call(this);

	}

});

Ext.reg('geneSearchAndPreview', Gemma.GeneSearchAndPreview);