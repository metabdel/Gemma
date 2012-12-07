/**
 * This window asks users to provide phenotypes and threshold for the meta-analysis which they want to save as evidence. 
 * 
 * @author frances
 * @version $Id$
 */
Ext.namespace('Gemma');

Gemma.MetaAnalysisSaveAsEvidenceWindow = Ext.extend(Ext.Window, {
	metaAnalysisId: null,
	metaAnalysis: null,
	defaultQvalueThreshold: null,
	layout: 'fit',
	title: 'Save as Neurocarta evidence',
	modal: true,
	constrain: true,
	width: 700,
	height: 500,
	shadow: true,
	closeAction: 'close',
	initComponent: function() {
		var HORIZONTAL_ANCHOR = '-15';

		var currentThreshold = this.defaultQvalueThreshold;

		var hasErrorMessages = false;
		
		var phenotypeErrorMessages = [];
		var thresholdErrorMessage = '';		

		var updateErrorMessages = function() {
			var allErrorMessages = [].concat(phenotypeErrorMessages);
			
			if (thresholdErrorMessage != '') {
				allErrorMessages.push(thresholdErrorMessage);
			}

			// Reset it so that the OK button is disabled only when error occurs.	
			hasErrorMessages = allErrorMessages.length > 0;						
		
			if (hasErrorMessages) {					
				var formattedErrorMessages = '';
				for (var i = 0; i < allErrorMessages.length; i++) {
					formattedErrorMessages += allErrorMessages[i];
					if (i < allErrorMessages.length - 1) {
						formattedErrorMessages += '<br />';
					}
				}
				errorPanel.showError(formattedErrorMessages);
			} else {
				errorPanel.hide();
			}
		}

		var submitForm = function() {
		    if (formPanel.getForm().isValid()) {
				var selectedPhenotypes = phenotypesSearchPanel.getSelectedPhenotypes();

				if (selectedPhenotypes != null && selectedPhenotypes.length > 0) {
					showLoadMask();
						
					PhenotypeController.makeDifferentialExpressionEvidencesFromDiffExpressionMetaAnalysis(this.metaAnalysisId, selectedPhenotypes,
						thresholdTextField.getValue(), function(validateEvidenceValueObject) {

						hideLoadMask();						

						if (validateEvidenceValueObject == null) {	
			            	Ext.Msg.alert('Evidence saved', 'Evidence has been saved successfully.');
							this.fireEvent('evidenceSaved');			            	
					    	this.close();
			            } else {
							Ext.Msg.alert(Gemma.HelpText.WidgetDefaults.MetaAnalysisManagerGridPanel.ErrorTitle.saveMetaAnalysisAsEvidence,
								Gemma.convertToEvidenceError(validateEvidenceValueObject).errorMessage,
								function() {
									if (validateEvidenceValueObject.userNotLoggedIn) {
										Gemma.AjaxLogin.showLoginWindowFn();
									}
								}
							);
				        }
					}.createDelegate(this));
				}
		    }
		}.createDelegate(this);

		var showLoadMask = function(msg) {
			if (!this.loadMask) {
				this.loadMask = new Ext.LoadMask(this.getEl());
			}
			this.loadMask.msg = msg 
				? msg 
				: "Loading ...";
				
			this.loadMask.show();
		}.createDelegate(this);
		
		var hideLoadMask = function() {
			this.loadMask.hide();
		}.createDelegate(this);
		
		var errorPanel = new Gemma.PhenotypeAssociationForm.ErrorPanel({
		    region: 'north'	
		});
		
		var phenotypesSearchPanel = new Gemma.PhenotypeAssociationForm.PhenotypesSearchPanel({
			anchor: HORIZONTAL_ANCHOR,
			listeners: {
				validtyStatusChanged: function(isModifying, errorMessages) {
					phenotypeErrorMessages = errorMessages;
					updateErrorMessages();
				},
				scope: this
			}
		});
		
		var thresholdTextField = new Ext.form.NumberField({
			fieldLabel: 'q-value threshold',
			decimalPrecision: 10,
			value: this.defaultQvalueThreshold,
			minValue: 0,			
			maxValue: this.defaultQvalueThreshold,			
			minLength: 1,
			maxLength: 12,
			allowBlank: false,
			allowDecimals: true,
			allowNegative: false,
			width: 100,
			enableKeyEvents: true,
		    initComponent: function() {
				Ext.apply(this, {
					autoCreate: { tag: 'input', type: 'text', size: '20', autocomplete: 'off', maxlength: this.maxLength },
					listeners: {
						keyup: function(numberField, event) {
							var threshold = numberField.getValue();
							
							if 	(threshold != currentThreshold) {
								if (threshold > numberField.minValue && threshold <= numberField.maxValue) {
									thresholdErrorMessage = '';
									resultPanel.showResults(threshold);
								} else {
									thresholdErrorMessage = String.format(
										Gemma.HelpText.WidgetDefaults.MetaAnalysisSaveAsEvidenceWindow.ErrorMessage.qvalueThresholdOutOfRange,
											numberField.minValue, numberField.maxValue);
									resultPanel.clear();
								}
								
								updateErrorMessages();
							
								currentThreshold = threshold;
							}
						}
					}
				});

				this.superclass().initComponent.call(this);
	    	}
		});
		
		var resultPanel = new Gemma.MetaAnalysisResultPanel({
			anchor: HORIZONTAL_ANCHOR + ' -110',
			metaAnalysis: this.metaAnalysis,
			defaultQvalueThreshold: this.defaultQvalueThreshold,
			showLimitDisplayCombo: false
		});			
	
		var formPanel = new Ext.form.FormPanel({
			layout: 'border',    	
			monitorValid : true,
			items: [
				errorPanel,
				{
					xtype: 'panel',
				    region: 'center',
					layout: 'form',
				    border: false,
					autoScroll: true,
					defaults: {
						blankText: 'This field is required',
						labelWidth: 120
					},
					padding: '15px 0px 8px 15px',						
					items: [
						{
							// This component is always hidden and its main
							// purpose is to make the OK button 
							// enabled/disabled correctly.   
							xtype: 'textfield', 
							hidden: true,
							validator: function() {
								return !hasErrorMessages;
							}
						},						
						phenotypesSearchPanel,
						thresholdTextField,
						resultPanel
					]
				}],		
				buttons: [
					{
					    text: 'OK',
					    formBind: true,
					    handler: function() {
							submitForm();							
					    },
						scope: this
					},
					{
					    text: 'Cancel',
					    handler: function() {
					    	this.close();
					    },
						scope: this
					}
				]
		});

    	Ext.apply(this, {
			items: [
				formPanel
			]
		});
		
		Gemma.MetaAnalysisSaveAsEvidenceWindow.superclass.initComponent.call(this);
	}
});
