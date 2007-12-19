Ext.namespace('Ext.Gemma');

/* Ext.Gemma.AnnotationGrid constructor...
 * 	div is the name of the div in which to render the grid.
 * 	config is a hash with the following options:
 * 		readMethod : the DWR method that returns the list of AnnotationValueObjects
 * 			( e.g.: ExpressionExperimentController.getAnnotation )
 * 		readParams : an array of parameters that will be passed to the readMethod
 * 			( e.e.: [ { id:x, classDelegatingFor:"ExpressionExperimentImpl" } ] )
 * 		             or a pointer to a function that will return the array of parameters
 * 		editable : if true, the annotations in the grid will be editable
 * 		showParent : if true, a link to the parent object will appear in the grid
 * 		noInitialLoad : if true, the grid will not be loaded immediately upon creation
 * 		pageSize : if defined, the grid will be paged on the client side, with the defined page size
 */
Ext.Gemma.AnnotationGrid = function ( div, config ) {
	
	this.readMethod = config.readMethod; delete config.readMethod;
	this.readParams = config.readParams; delete config.readParams;
	this.editable = config.editable; delete config.editable;
	this.showParent = config.showParent; delete config.showParent;
	this.noInitialLoad = config.noInitialLoad; delete config.noInitialLoad;
	this.pageSize = config.pageSize; delete config.pageSize;
	
	/* keep a reference to ourselves to avoid convoluted scope issues below...
	 */
	var thisGrid = this;
	
	/* establish default config options...
	 */
	var superConfig = {
		renderTo : div
	};
	
	if ( this.pageSize ) {
		superConfig.ds = new Ext.Gemma.PagingDataStore( {
			proxy : new Ext.data.DWRProxy( this.readMethod ),
			reader : new Ext.data.ListRangeReader( {id:"id"}, Ext.Gemma.AnnotationGrid.getRecord() ),
			pageSize : this.pageSize
		} );
		superConfig.bbar = new Ext.Gemma.PagingToolbar( {
			pageSize : this.pageSize,
			store : superConfig.ds
		} );
	} else {
		superConfig.ds = new Ext.data.Store( {
			proxy : new Ext.data.DWRProxy( this.readMethod ),
			reader : new Ext.data.ListRangeReader( {id:"id"}, Ext.Gemma.AnnotationGrid.getRecord() )
		} );
	}
	superConfig.ds.setDefaultSort('className');
	
	superConfig.cm = new Ext.grid.ColumnModel( [
		{ header: "Class", dataIndex: "className" },
		{ header: "Term", dataIndex: "termName", renderer: Ext.Gemma.AnnotationGrid.getTermStyler() },
		{ header: "Parent", dataIndex: "parentLink", renderer: Ext.Gemma.AnnotationGrid.getParentStyler(), hidden: this.showParent ? false: true }
	] );
	superConfig.cm.defaultSortable = true;
	var CATEGORY_COLUMN = 0;
	var VALUE_COLUMN = 1;
	var PARENT_COLUMN = 2;
	if ( this.editable ) {
		this.categoryCombo = new Ext.Gemma.MGEDCombo( { lazyRender : true } );
		var categoryEditor = new Ext.grid.GridEditor( this.categoryCombo );
		this.categoryCombo.on( "select", function ( combo, record, index ) { categoryEditor.completeEdit(); } );
		superConfig.cm.setEditor( CATEGORY_COLUMN, categoryEditor );
		
		this.valueCombo = new Ext.Gemma.CharacteristicCombo( { lazyRender : true } );
		var valueEditor = new Ext.grid.GridEditor( this.valueCombo );
		this.valueCombo.on( "select", function ( combo, record, index ) { valueEditor.completeEdit(); } );
		superConfig.cm.setEditor( VALUE_COLUMN, valueEditor );
	}
	
	superConfig.selModel = new Ext.grid.RowSelectionModel();
	
	superConfig.autoExpandColumn = this.showParent ? 2 : 1;
	superConfig.autoHeight = true;
	superConfig.loadMask = true;
	superConfig.bbar = [];
	superConfig.tbar = [];

	for ( property in config ) {
		superConfig[property] = config[property];
	}
	Ext.Gemma.AnnotationGrid.superclass.constructor.call( this, superConfig );
	
	/* these functions have to happen after we've called the super-constructor so that we know
	 * we're a Grid...
	 */
	if ( this.editable ) {
		this.on( "beforeedit", function( e ) {
			var row = e.record.data;
			var col = this.getColumnModel().getColumnId( e.column );
			if ( col == VALUE_COLUMN ) {
				var f = this.valueCombo.setCategory.bind( this.valueCombo );
				f( row.className, row.classUri );
			}
		} );
		this.on( "afteredit", function( e ) {
			var col = this.getColumnModel().getColumnId( e.column );
			if ( col == CATEGORY_COLUMN ) {
				var f = this.categoryCombo.getTerm.bind( this.categoryCombo );
				var term = f();
				e.record.set( "className", term.term );
				e.record.set( "classUri", term.uri );
			} else if ( col == VALUE_COLUMN ) {
				var f = this.valueCombo.getCharacteristic.bind( this.valueCombo );
				var c = f();
				e.record.set( "termName", c.value );
				e.record.set( "termUri", c.valueUri );
			}
			this.getView().refresh();
		} );
	}
	
	this.on( "celldblclick", function ( grid, rowIndex, cellIndex ) {
		var record = grid.getStore().getAt( rowIndex );
		var column = grid.getColumnModel().getColumnId( cellIndex )
		if ( column == PARENT_COLUMN ) {
			record.expanded = record.expanded ? 0 : 1;
			grid.getView().refresh( true );
		}
	}, this );
	
	this.getStore().on( "load", function () {
		this.autoSizeColumns();
		this.doLayout();
	}, this );
	
	if ( ! this.noInitialLoad )
		this.getStore().load( { params : this.getReadParams() } );
	
	/* if the toolbars weren't passed in, destroy the default elements that were created...
	 * we're doing this so that we can have the option of adding toolbars later...
	 */
	if ( ! config.tbar ) { this.getTopToolbar().destroy(); }
	if ( ! config.bbar ) { this.getBottomToolbar().destroy(); }
};

/* static methods
 */
Ext.Gemma.AnnotationGrid.getRecord = function() {
	if ( Ext.Gemma.AnnotationGrid.record == undefined ) {
		Ext.Gemma.AnnotationGrid.record = Ext.data.Record.create( [
			{ name:"id", type:"int" },
			{ name:"classUri", type:"string" },
			{ name:"className", type:"string" },
			{ name:"termUri", type:"string" },
			{ name:"termName", type:"string" },
			{ name:"parentLink", type:"string" },
			{ name:"parentDescription", type:"string" },
			{ name:"parentOfParentLink", type:"string" },
			{ name:"parentOfParentDescription", type:"string" }
		] );
	}
	return Ext.Gemma.AnnotationGrid.record;
};

Ext.Gemma.AnnotationGrid.formatTermWithStyle = function( value, uri ) {
	var class = uri ? "unusedWithUri" : "unusedNoUri";
	var description = uri || "free text";
	return String.format( "<span class='{0}' title='{2}'>{1}</span>", class, value, description );
};

Ext.Gemma.AnnotationGrid.getTermStyler = function() {
	if ( Ext.Gemma.AnnotationGrid.termStyler == undefined ) {
		/* apply a CSS class depending on whether or not the characteristic has a URI.
		 */
		Ext.Gemma.AnnotationGrid.termStyler = function ( value, metadata, record, row, col, ds ) {
			return Ext.Gemma.AnnotationGrid.formatTermWithStyle( value, record.data.termUri );
		}
	}
	return Ext.Gemma.AnnotationGrid.termStyler;
};

Ext.Gemma.AnnotationGrid.formatParentWithStyle = function( id, expanded, parentLink, parentDescription, parentOfParentLink, parentOfParentDescription ) {
	if ( parentOfParentLink ) {
		var value = String.format( "{0}<br> from {1}", parentLink, parentOfParentLink );
	} else {
		var value = parentLink;
	}
	return expanded ? value.concat( String.format( "<div style='white-space: normal;'>{0}</div>", parentDescription ) ) : value;
};

Ext.Gemma.AnnotationGrid.getParentStyler = function() {
	if ( Ext.Gemma.AnnotationGrid.parentStyler == undefined ) {
		/* apply a CSS class depending on whether or not the characteristic has a URI.
		 */
		Ext.Gemma.AnnotationGrid.parentStyler = function ( value, metadata, record, row, col, ds ) {
			return Ext.Gemma.AnnotationGrid.formatParentWithStyle( record.id, record.expanded, record.data.parentLink, record.data.parentDescription, record.data.parentOfParentLink, record.data.parentOfParentDescription );
		}
	}
	return Ext.Gemma.AnnotationGrid.parentStyler;
};

Ext.Gemma.AnnotationGrid.convertToCharacteristic = function( record ) {
	var c = {
		id : record.id,
		category : record.className,
		value : record.termName
	};
	/* if we don't have a valueURI set, don't return URI fields or
	 * a VocabCharacteristic will be created when we only want a
	 * Characteristic...
	 */
	if ( record.termUri ) {
		c.categoryUri = record.classUri;
		c.valueUri = record.termUri;
	}
	return c;
};

/* instance methods...
 */
Ext.extend( Ext.Gemma.AnnotationGrid, Ext.grid.EditorGridPanel, {
	
	refresh : function( params ) {
		var reloadOpts = { callback: this.getView().refresh };
		if ( params ) {
			reloadOpts.params = params
		}
		this.getStore().reload( reloadOpts );
	},

	getReadParams : function() {
		return ( typeof this.readParams == "function" ) ? this.readParams() : this.readParams;
	},
	
	getSelectedIds : function() {
		var selected = this.getSelectionModel().getSelections();
		var ids = [];
		for ( var i=0; i<selected.length; ++i ) {
			ids.push( selected[i].id );
		}
		return ids;	
	},
	
	getSelectedCharacteristics : function() {
		var selected = this.getSelectionModel().getSelections();
		var chars = [];
		for ( var i=0; i<selected.length; ++i ) {
			var row = selected[i].data;
			chars.push( Ext.Gemma.AnnotationGrid.convertToCharacteristic( row ) );
		}
		return chars;	
	},
	
	getEditedCharacteristics : function() {
		var chars = [];
		this.getStore().each( function( record ) {
			if ( record.dirty ) {
				var row = record.data;
				chars.push( Ext.Gemma.AnnotationGrid.convertToCharacteristic( row ) );
			}
		} );
		return chars;
	},
	
	autoSizeColumns: function() {
	    for (var i = 0; i < this.colModel.getColumnCount(); i++) {
    		this.autoSizeColumn(i);
	    }
	},

	autoSizeColumn: function(c) {
		var w = this.view.getHeaderCell(c).firstChild.scrollWidth;
		for (var i = 0, l = this.store.getCount(); i < l; i++) {
			w = Math.max(w, this.view.getCell(i, c).firstChild.scrollWidth);
		}
		this.colModel.setColumnWidth(c, w);
		return w;
	}
	
} );