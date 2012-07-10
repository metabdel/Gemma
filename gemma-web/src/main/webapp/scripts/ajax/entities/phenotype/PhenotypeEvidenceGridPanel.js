/**
 * It displays evidence linked to the current phenotypes and gene. 
 * 
 * @author frances
 * @version $Id$
 */
Ext.namespace('Gemma');

// evidenceStoreProxy should be overridden if used outside of Gemma. 
Gemma.PhenotypeEvidenceGridPanel = Ext.extend(Ext.grid.GridPanel, {
	evidenceStoreProxy: null,
	allowCreateOnlyWhenGeneSpecified: true,
	title: 'Evidence',
    autoScroll: true,
    stripeRows: true,
	loadMask: true,
	disableSelection: true,
    viewConfig: {
        forceFit: true
    },
	hasRelevanceColumn: true,
    currentPhenotypes: null,
    currentGene: null,
	deferLoadToRender: false,
	showDialogViewBibliographicReferenceOutsideOfGemma: function() {
		Ext.Msg.alert(Gemma.HelpText.WidgetDefaults.PhenotypePanel.viewBibliographicReferenceOutsideOfGemmaTitle,
			Gemma.HelpText.WidgetDefaults.PhenotypePanel.viewBibliographicReferenceOutsideOfGemmaText);
	},
    initComponent: function() {
   		var DEFAULT_TITLE = this.title; // A constant title that will be used when we don't have current gene.

		if (!Gemma.isRunningOutsideOfGemma()) {   		
	   		// Show Admin column after user logs in. 
			Gemma.Application.currentUser.on("logIn", 
				function(userName, isAdmin) {	
					var columnModel = this.getColumnModel();
					// Show Admin column for all logged-in users.
					columnModel.setHidden(columnModel.getIndexById('admin'), false);
				},
				this);
			   		
	   		// Hide Admin column after user logs out. 
			Gemma.Application.currentUser.on("logOut", 
				function() {	
					var columnModel = this.getColumnModel();
					// Hide Admin column when users log out.
					columnModel.setHidden(columnModel.getIndexById('admin'), true);
				},
				this);
		}
		
		var generateLink = function(methodWithArguments, imageSrc, description, width, height) {
			return '<span class="link" onClick="return Ext.getCmp(\'' + this.getId() + '\').' + methodWithArguments +
						'"><img src="' + imageSrc + '" alt="' + description + '" ext:qtip="' + description + '" ' +
						((width && height) ?
							'width="' + width + '" height="' + height + '" ' :
							'') +
						'/></span>';
			
		}.createDelegate(this);
   		
		var generatePublicationLinks = function(pudmedId, pubmedUrl) {
			var anchor = '';
			if (pudmedId != null) {
				var imageSrc = '/Gemma/images/icons/magnifier.png';
				var size = 12;
				
				if (Gemma.isRunningOutsideOfGemma()) {
					anchor += generateLink('showDialogViewBibliographicReferenceOutsideOfGemma();', imageSrc, 'View Bibliographic Reference', size, size);
				} else {
					var description = 'Go to Bibliographic Reference (in new window)';
					anchor += '<a target="_blank" href="/Gemma/bibRef/searchBibRefs.html?pubmedID=' +
			        	pudmedId +
			        	'"><img src="' + imageSrc + '" alt="' + description + '" ext:qtip="' + description + '" width="' + size + '" height="' + size + '" /></a>';
				}		        	
			}
		    return anchor + (new Ext.Template( Gemma.Common.tpl.pubmedLink.simple )).apply({
		    	pubmedURL: pubmedUrl
		    });
		}.createDelegate(this);
		
		var convertToExternalDatabaseAnchor = function(databaseName, url, useDatabaseIcon) {
			var html = '<a target="_blank" href="' + url + '">';
			if (useDatabaseIcon) {
				html += '<img ext:qtip="Go to ' + databaseName + ' (in new window)" ' + 
							'src="/Gemma/images/logo/' + databaseName + '.gif" alt="' + databaseName + '" />';		
			} else {
				html += url;
			}
			
			html += '</a>';
			
			return html;
		};

    	var createPhenotypeAssociationButton = new Ext.Button({
			disabled: this.allowCreateOnlyWhenGeneSpecified ?
				(this.currentGene == null) :
				false,
			handler: Gemma.isRunningOutsideOfGemma() ?
				function() {
					Ext.Msg.alert(Gemma.HelpText.WidgetDefaults.PhenotypePanel.modifyPhenotypeAssociationOutsideOfGemmaTitle,
						Gemma.HelpText.WidgetDefaults.PhenotypePanel.modifyPhenotypeAssociationOutsideOfGemmaText);
				} :
				function() {
					var createPhenotypeAssociationFormWindow = new Gemma.PhenotypeAssociationForm.Window();
					
					this.relayEvents(createPhenotypeAssociationFormWindow, ['phenotypeAssociationChanged']);	
					createPhenotypeAssociationFormWindow.showWindow(Gemma.PhenotypeAssociationForm.ACTION_CREATE,
						{
							gene: this.currentGene,
							phenotypes: this.currentPhenotypes
						});
				},
			scope: this,
			icon: "/Gemma/images/icons/add.png",
			tooltip: "Add new phenotype association"
    	});
		   
    	
		MyRowExpander = Ext.extend(Ext.grid.RowExpander, {
			getRowClass: function(record, index, rowParams, store) {
				if (record.data.homologueEvidence) {	
					rowParams.tstyle += 'color: gray;';
				} else {
					rowParams.tstyle += 'color: black;';
				}
				
				return this.superclass().getRowClass.call(this, record, index, rowParams, store);
			},
			enableCaching: false, // It needs to be false. Otherwise, its content will not be updated after grid's data is changed.
			lazyRender: false, // It needs to be false. Otherwise, after grid's data is changed, all rows will be collapsed even though the expand button ("+" button) shows the correct state.
			// Use class="x-grid3-cell-inner" so that we have padding around the description.
		    tpl: new Ext.Template(
		        '<div class="x-grid3-cell-inner" style="white-space: normal;">{rowExpanderText}</div>'
		    )
		});
		var rowExpander = new MyRowExpander();    	
    	
		if (this.evidencePhenotypeColumnRenderer == null) {
			this.evidencePhenotypeColumnRenderer = function(value, metadata, record, rowIndex, colIndex, store) {
				var phenotypesHtml = '';
				for (var i = 0; i < value.length; i++) {
					if (value[i].child || value[i].root) {					
						phenotypesHtml += '<span style="font-weight: bold; color: red;">' +
												value[i].value +
										  '</span>';
					} else {
						phenotypesHtml += value[i].value;
					}
					phenotypesHtml += '<br />';
				}					
				return phenotypesHtml;
		    }
		}

		var getGeneLink = this.getGeneLink;
		
		var getFormWindowData = function(id) {
			var record = this.getStore().getById(id);
			var evidenceClassName = record.data.className; 
		
			var data = null;
			
			if (evidenceClassName === 'LiteratureEvidenceValueObject') {
				data = {
					pubMedId: record.data.citationValueObject.pubmedAccession
				};
			} else if (evidenceClassName === 'ExperimentalEvidenceValueObject') {
				data = {
					primaryPubMedId: record.data.primaryPublicationCitationValueObject != null ?
										record.data.primaryPublicationCitationValueObject.pubmedAccession :
										null,
					// Assume we have at most one other PubMed Id.
					secondaryPubMedId: record.data.relevantPublicationsCitationValueObjects != null &&
								 	   record.data.relevantPublicationsCitationValueObjects.length > 0 ?
											record.data.relevantPublicationsCitationValueObjects[0].pubmedAccession :
											null,
					experimentCharacteristics: record.data.experimentCharacteristics
				}
			}
			
			if (data != null) {
				data.evidenceId = record.data.id;
				data.gene = {
					id: record.data.geneId,
					ncbiId: record.data.geneNCBI,
					officialSymbol: record.data.geneOfficialSymbol,
					officialName: record.data.geneOfficialName,
					taxonCommonName: record.data.taxonCommonName,
					// As of 2012-06-27, experiment tag's value combo box depends on taxon id.
					// Elodie and Nicolas said we did not have to set it. So, it is set to null.
					taxonId: null  
				};
				data.phenotypes = record.data.phenotypes;
				data.evidenceClassName = evidenceClassName;
				data.isNegativeEvidence = record.data.isNegativeEvidence;
				data.description = record.data.description;
				data.evidenceCode = record.data.evidenceCode;
				data.lastUpdated = record.data.lastUpdated;
			}
			
			return data;
		}.createDelegate(this);	
	
		var evidenceStore = new Ext.data.Store({
			proxy: this.evidenceStoreProxy == null ?
						new Ext.data.DWRProxy({
					        apiActionToHandlerMap: {
				    	        read: {
				        	        dwrFunction: GeneController.loadGeneEvidence,
				            	    getDwrArgsFunction: function(request){
				            	    	return [
											request.params['taxonCommonName'],
				            	    		request.params['showOnlyEditable'],
				            	    		request.params["geneId"],
					            	    	request.params["phenotypeValueUris"]
				            	    	];
					                }
				    	        }
					        }
				    	}) :
				    	this.evidenceStoreProxy,
		    reader: new Ext.data.JsonReader({
				idProperty: 'id',		    	
		        fields: [
			        // for EvidenceValueObject
					'id', 'className', 'description', 'evidenceCode', 'evidenceSecurityValueObject',
		        	'evidenceSource', 'isNegativeEvidence', 'lastUpdated', 'phenotypes', 'containQueryPhenotype',
		        	// for GroupEvidenceValueObject
		        	'literatureEvidences',
		        	// for ExperimentalEvidenceValueObject
		 			'experimentCharacteristics', 'primaryPublicationCitationValueObject',
		 			'relevantPublicationsCitationValueObjects',
		 			// for LiteratureEvidenceValueObject
		 			'citationValueObject',
					// for showing homologues' evidence
					'geneId', 'geneNCBI', 'geneOfficialSymbol', 'geneOfficialName', 'taxonCommonName', 'homologueEvidence',
		            {
						name: 'rowExpanderText',
						convert: function(value, record) {
							var descriptionHtml = '';
							
							switch (record.className) {
								case 'DiffExpressionEvidenceValueObject' :
									break;
								
								case 'ExperimentalEvidenceValueObject' :
									var descriptionHtml = '';
						
						        	if (record.primaryPublicationCitationValueObject != null) {
						        		descriptionHtml += '<p><b>Primary Publication</b>: ' +
						        			record.primaryPublicationCitationValueObject.citation + ' ' +
						        			generatePublicationLinks(record.primaryPublicationCitationValueObject.pubmedAccession,
						        				record.primaryPublicationCitationValueObject.pubmedURL) + '</p>';
						        	}
						
									var relPub = record.relevantPublicationsCitationValueObjects;
						        	if (relPub != null && relPub.length > 0) {
						        		descriptionHtml += '<p><b>Relevant Publication</b>: ';
						        		
										for (var i = 0; i < relPub.length; i++) {
											descriptionHtml += relPub[i].citation + ' ' +
												generatePublicationLinks(relPub[i].pubmedAccession, relPub[i].pubmedURL);
											
											if (i < relPub.length - 1) {
												descriptionHtml += " | ";
											}
										}
										descriptionHtml += '</p>';
						        	}
						        	
									var expChar = record.experimentCharacteristics;
						        	if (expChar != null && expChar.length > 0) {
										var expCharMap = new Object();
										for (var i = 0; i < expChar.length; i++) {
											if (expCharMap[expChar[i].category] == null) {
												expCharMap[expChar[i].category] = expChar[i].value;	
											} else {
												expCharMap[expChar[i].category] += " | " + expChar[i].value;
											}
											  
										}
						
										descriptionHtml += '<p>';
										Ext.iterate(expCharMap, function(key, value) {
										  descriptionHtml += '<b>' + key + "</b>: " + value + '<br />';
										});
										descriptionHtml += '</p>';
						        	}
									
									break;
						
								case 'GenericEvidenceValueObject' : 
									break;
						
								case 'GroupEvidenceValueObject' : 
									descriptionHtml += '<p>';
									
									for (var i = 0; i < record.literatureEvidences.length; i++) {
										descriptionHtml += '<b>Publication ' + (i + 1) + '</b>: ' +
											record.literatureEvidences[i].citationValueObject.citation + ' ' +
											generatePublicationLinks(
												record.literatureEvidences[i].citationValueObject.pubmedAccession,
												record.literatureEvidences[i].citationValueObject.pubmedURL) + '<br />';
									}
									
									descriptionHtml += '</p>';
									break;
						
								case 'LiteratureEvidenceValueObject' : 
						        	if (record.citationValueObject != null) {
						        		descriptionHtml += '<p><b>Publication</b>: ' +
						        			record.citationValueObject.citation + ' ' +
						        			generatePublicationLinks(
						        				record.citationValueObject.pubmedAccession,
						        				record.citationValueObject.pubmedURL) + '</p>';
						        	}
									break;
						
								case 'UrlEvidenceValueObject' : 
									break;
							}
							
							if (record.evidenceSource != null && record.evidenceSource.externalDatabase.name != null && record.evidenceSource.externalUrl != null) {
								var databaseName = record.evidenceSource.externalDatabase.name;
								descriptionHtml += '<p><b>Evidence Source</b>: ' + databaseName + ' ' + convertToExternalDatabaseAnchor(databaseName, record.evidenceSource.externalUrl, false) + '</p>'; 
							}
						
							if (record.description != null && record.description !== '') {
								descriptionHtml += '<p><b>Note</b>: ' + record.description + '</p>';
							}

							if (record.homologueEvidence) {
								var geneLink = getGeneLink ?
									getGeneLink(record.geneId) :
									'/Gemma/gene/showGene.html?id=' + record.geneId;
								
								descriptionHtml += String.format("<p><b>*</b> Inferred from homology with the {0} gene {1} " +
									"<a target='_blank' href='" + geneLink + "' ext:qtip='Go to {1} Details (in new window)'>" +
										"<img src='/Gemma/images/icons/magnifier.png' height='10' width='10'/>" + 
									"</a></p>",
								record.taxonCommonName, record.geneOfficialSymbol);
							}
							
							return descriptionHtml;
						}
		            }
		        ]
		    })
//			sortInfo: {	field: 'relevance', direction: 'DESC' }
		});

		Ext.apply(this, {
			store: evidenceStore,
			plugins: rowExpander,
			columns:[
				rowExpander,
				{
					header: '<img style="vertical-align: bottom;" ext:qwidth="198" ext:qtip="'+
							Gemma.HelpText.WidgetDefaults.PhenotypeEvidenceGridPanel.specificallyRelatedTT+
							'" width="16" height="16" src="/Gemma/images/icons/bullet_red.png">',
					dataIndex: 'containQueryPhenotype',
					width: 0.12,
		            renderer: function(value, metadata, record, rowIndex, colIndex, store) {
						if (value == true) {
							return this.header;
						} else {
							return '';
						}
		            },
		            hidden: !this.hasRelevanceColumn,
					sortable: true
				},
				{
					header: "Phenotypes",
					dataIndex: 'phenotypes',
					width: 0.35,
					renderer: this.evidencePhenotypeColumnRenderer,					
					sortable: false
				},
				{
					header: "Type",
					dataIndex: 'className',
					width: 1,
					renderer: function(value, metadata, record, rowIndex, colIndex, store) {
						var externalSource = '';
					
						if (record.data.evidenceSource != null && record.data.evidenceSource.externalDatabase != null) {
							externalSource = 'External Source [' + record.data.evidenceSource.externalDatabase.name + ']';
						}
						
						var typeColumnHtml = '';
						
						switch (value) {
							case 'DiffExpressionEvidenceValueObject' :
								typeColumnHtml = 'Differential Expression';
								break;
							
							case 'ExperimentalEvidenceValueObject' :
								var experimentValues = '';
								var experimentCharacteristics = record.data.experimentCharacteristics;
								
					        	if (experimentCharacteristics != null) {
									for (var i = 0; i < experimentCharacteristics.length; i++) {
										if (experimentCharacteristics[i].category == 'Experiment') {
											experimentValues += experimentCharacteristics[i].value + " | "; 
										}
									}
									if (experimentValues.length > 0) {
										experimentValues = ' [ ' + experimentValues.substr(0, experimentValues.length - 3) + ' ]'; // remove ' | ' at the end
									}
					        	}
							
					        	typeColumnHtml = 'Experimental' + experimentValues;
								break;
					
							case 'GenericEvidenceValueObject' :
								if (record.data.evidenceSource == null) {
									typeColumnHtml = 'Note';
								} else {
									typeColumnHtml = externalSource;
								}
								break;
					
							case 'GroupEvidenceValueObject' : 
								typeColumnHtml = '<b>' + record.data.literatureEvidences.length + 'x</b> Literature';
								break;

							case 'LiteratureEvidenceValueObject' : 
								typeColumnHtml = 'Literature';
								break;
					
							case 'UrlEvidenceValueObject' : 
								typeColumnHtml = 'Url';
								break;
						}
					
						if (value !== 'GenericEvidenceValueObject' && externalSource !== '') {
							typeColumnHtml += ' from ' + externalSource;
						}
					
						typeColumnHtml = 
							(record.data.isNegativeEvidence ? 
								"<img ext:qwidth='200' ext:qtip='" +
									Gemma.HelpText.WidgetDefaults.PhenotypeEvidenceGridPanel.negativeEvidenceTT+
									"' src='/Gemma/images/icons/thumbsdown.png' height='12'/> " :
								"") +
							typeColumnHtml;
						
						return '<span style="white-space: normal;">' + typeColumnHtml +'</span>'					
					},
					sortable: true
				},
				{
					header: "Evidence Code",
					dataIndex: 'evidenceCode',
					width: 0.33,
		            renderer: function(value, metadata, record, rowIndex, colIndex, store) {
						var columnRenderer;
						if (record.data.homologueEvidence) {
							columnRenderer = '<span ext:qwidth="200" ext:qtip="' + 
								'Inferred from homology with the ' + record.data.taxonCommonName + ' gene ' + record.data.geneOfficialSymbol + '.' + 
								'">' +
								'Inferred from ' + record.data.geneOfficialSymbol + ' [' + record.data.taxonCommonName + ']' + '</span>';
						} else {
							var evidenceCode = Gemma.decodeEvidenceCode(value);						
			            	
							columnRenderer = '<span ext:qwidth="200" ext:qtip="' + evidenceCode.tooltipText + '">' + evidenceCode.displayText + '</span>';
						}
						return columnRenderer; 
		            },
					sortable: true
				},
				{
					header: "Link Out",
					dataIndex: 'evidenceSource',
					width: 0.2,
					renderer: function(value, metadata, record, rowIndex, colIndex, store) {
						var linkOutHtml = '';
					
						switch (record.data.className) {
							case 'DiffExpressionEvidenceValueObject' :
								break;
							
							case 'ExperimentalEvidenceValueObject' :
					        	if (record.data.primaryPublicationCitationValueObject != null) {
									linkOutHtml += generatePublicationLinks(null,
										record.data.primaryPublicationCitationValueObject.pubmedURL);
								}
								break;
					
							case 'GenericEvidenceValueObject' : 
								break;
					
							case 'GroupEvidenceValueObject' :
								var pubmedURL = record.data.literatureEvidences[0].citationValueObject.pubmedURL;
								for (var i = 1; i < record.data.literatureEvidences.length; i++) {
									pubmedURL += ',' + record.data.literatureEvidences[i].citationValueObject.pubmedAccession;
								}
								
								linkOutHtml += generatePublicationLinks(null, pubmedURL);
								break;
					
							case 'LiteratureEvidenceValueObject' : 
					        	if (record.data.citationValueObject != null) {
					        		linkOutHtml += generatePublicationLinks(null,
					        			record.data.citationValueObject.pubmedURL);
					        	}
								break;
					
							case 'UrlEvidenceValueObject' : 
								break;
						}
					
						if (value != null && value.externalDatabase.name != null && value.externalUrl != null) {
							if (linkOutHtml !== '') {
								linkOutHtml += ' ';
							}
							linkOutHtml += convertToExternalDatabaseAnchor(value.externalDatabase.name, value.externalUrl, true);
						}
						
						return '<span style="white-space: normal;">' + linkOutHtml +'</span>'					
					},	
					sortable: false
				},
				{
					header: 'Admin',
					id: 'admin',
					width: 0.3,
		            renderer: function(value, metadata, record, rowIndex, colIndex, store) {
		            	var adminLinks = '';
		            	
						if (!this.hidden) {		            	
							adminLinks += Gemma.SecurityManager.getSecurityLink(
								'ubic.gemma.model.association.phenotype.PhenotypeAssociationImpl',
								record.data.id,
								record.data.evidenceSecurityValueObject.public,
								record.data.evidenceSecurityValueObject.shared,
								record.data.evidenceSecurityValueObject.currentUserIsOwner,
								null,
								null,
								'Phenotype Association'); // Evidence name for the title in Security dialog.

							if ((record.data.className === 'LiteratureEvidenceValueObject' ||						
							 	 record.data.className === 'ExperimentalEvidenceValueObject') &&
		            		    record.data.evidenceSecurityValueObject.currentUserHasWritePermission &&
								record.data.evidenceSource == null) {
			            		adminLinks += ' ' +
									generateLink('showCreateWindow(' + record.data.id + ');', '/Gemma/images/icons/add.png', 'Clone evidence') + ' ' +			            		
	            					generateLink('showEditWindow(' + record.data.id + ');', '/Gemma/images/icons/pencil.png', 'Edit evidence') + ' ' +
									generateLink('removeEvidence(' + record.data.id + ');', '/Gemma/images/icons/cross.png', 'Remove evidence');
		            		}
		            	}
		            	
						return adminLinks;
		            },
					hidden: Ext.get("hasUser") == null || (!Ext.get("hasUser").getValue()),
					sortable: true
				}
			],
			tbar: [
				createPhenotypeAssociationButton
			],
		    setCurrentData: function(currentFilters, currentPhenotypes, currentGene) {
		    	this.currentPhenotypes = currentPhenotypes;

				createPhenotypeAssociationButton.setDisabled(currentGene == null);
		    	
				if (currentGene != null) {
					this.setTitle("Evidence for " + currentGene.officialSymbol);
					
					this.currentGene = currentGene;
			    	
					var phenotypeValueUris = [];
					for (var i = 0; i < currentPhenotypes.length; i++) {
						phenotypeValueUris.push(currentPhenotypes[i].valueUri);
					}
								    	
					evidenceStore.reload({
						params: {
							taxonCommonName: currentFilters.taxonCommonName,
							showOnlyEditable: currentFilters.showOnlyEditable,
							geneId: currentGene.id,
							phenotypeValueUris: phenotypeValueUris		    			
						}
					});
				} else {
					this.currentGene = null;

					this.setTitle(DEFAULT_TITLE);					
					evidenceStore.removeAll();
				}                	
		    },
			loadGene: function(geneId) {
		    	evidenceStore.reload({
		    		params: {
						taxonCommonName: '',
						showOnlyEditable: false,
		    			geneId: geneId
		    		}
		    	});
			},
			showCreateWindow: function(id) {			
				var data = getFormWindowData(id);
			
				if (data != null) {
					// Because we are creating new evidence, they should be null.
					data.evidenceId = null;
					data.lastUpdated = null;
					
					var createPhenotypeAssociationFormWindow = new Gemma.PhenotypeAssociationForm.Window();
					this.relayEvents(createPhenotypeAssociationFormWindow, ['phenotypeAssociationChanged']);	
			
					createPhenotypeAssociationFormWindow.showWindow(Gemma.PhenotypeAssociationForm.ACTION_CREATE, data);
				}
			},			
			showEditWindow: function(id) {			
				var data = getFormWindowData(id);
			
				if (data != null) {
					var editPhenotypeAssociationFormWindow = new Gemma.PhenotypeAssociationForm.Window();
					this.relayEvents(editPhenotypeAssociationFormWindow, ['phenotypeAssociationChanged']);	
			
					editPhenotypeAssociationFormWindow.showWindow(Gemma.PhenotypeAssociationForm.ACTION_EDIT, data);
				}
			},			
			removeEvidence: function(id) {
				Ext.MessageBox.confirm('Confirm',
					'Are you sure you want to remove this evidence?',
					function(button) {
						if (button === 'yes') {
							PhenotypeController.removePhenotypeAssociation(id, function(validateEvidenceValueObject) {
								if (validateEvidenceValueObject == null) {
									this.fireEvent('phenotypeAssociationChanged');
								} else {
									if (validateEvidenceValueObject.evidenceNotFound) {
										// We still need to fire event to let listeners know that it has been removed.
										this.fireEvent('phenotypeAssociationChanged');
										Ext.Msg.alert('Evidence already removed', 'This evidence has already been removed by someone else.');
									} else {
										Ext.Msg.alert('Cannot remove evidence', Gemma.convertToEvidenceError(validateEvidenceValueObject).errorMessage,
											function() {
												if (validateEvidenceValueObject.userNotLoggedIn) {
													Gemma.AjaxLogin.showLoginWindowFn();
												}
											}
										);
									}
								}
							}.createDelegate(this));
						}
					},
					this);
			}			
		});

		Gemma.PhenotypeEvidenceGridPanel.superclass.initComponent.call(this);		
		
		if (!this.deferLoadToRender) {
			if (this.currentGene != null && this.currentGene.id != '') {
				this.loadGene(this.currentGene.id);
			}
			
		} else {
			this.on('render', function(){
				if (this.currentGene != null && this.currentGene.id != '') {
					this.loadGene(this.currentGene.id);
				}
			});
		}
    }
});