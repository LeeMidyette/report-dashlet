/*
 *  Content Reports Dashlet for Alfresco (http://www.dashlet.info)
 *  
 *  Copyright (C) 2011 Pedro Salido López <psalido@dashlet.info>
 *
 *  The JavaScript code in this page is free software: you can
 *  redistribute it and/or modify it under the terms of the GNU
 *  General Public License (GNU GPL) as published by the Free Software
 *  Foundation, either version 3 of the License, or (at your option)
 *  any later version.  The code is distributed WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS
 *  FOR A PARTICULAR PURPOSE.  See the GNU GPL for more details.
 *
 *  As additional permission under GNU GPL version 3 section 7, you
 *  may distribute non-source (e.g., minimized or compacted) forms of
 *  that code without the copy of the GNU GPL normally required by
 *  section 4, provided you include this license notice and a URL
 *  through which recipients can access the Corresponding Source.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
jQuery.noConflict();

var jqXHR;
jQuery(document).ready(function() {

	jQuery.ajaxSetup({ scriptCharset: "utf-8" , contentType: "application/json; charset=utf-8"});


	/*resetea la cache de formularios de firefox*/
	jQuery("#by_type").find("option[value='-']").attr("selected", true);
	jQuery('#filter_type').attr('disabled',true)

	jQuery("#change-button").click(function(){


		if(jQuery('#gRaphael').position().left >= 0 ){
			left=-jQuery('#gRaphael-container').outerWidth();
		}else{
			left=0;
		}

		jQuery('#gRaphael-wrapper').children().animate({
			left: left+'',
		}, 1000, function() {
		});
		return false;
	});

	initFilterSelect(jQuery('#by_type').val());
	jQuery( "#accordion-collapsible" ).accordion({
		collapsible: true,
		active: 1
	});





	if(!jQuery.datepicker.regional[locale]){
		locale=locale.split('_');
		locale=locale[0];
		if(!jQuery.datepicker.regional[locale]){
			locale=false;
		}
	}

	if(locale){
		jQuery.datepicker.setDefaults(jQuery.datepicker.regional[locale]);
	}

	//Autocompletar mimetype
	var uriAutoCompleteMimetype = Alfresco.constants.PROXY_URI + 'info/repository/mimetype';
	jQuery('#mimetype-filter-value').autocomplete({appendTo: '#mimetype-filter', source: uriAutoCompleteMimetype});

	//Autocompletar creador y modificador
	var uriAutoCompleteUser     = Alfresco.constants.PROXY_URI + 'info/repository/user';
	jQuery('#creator-filter-value').autocomplete({appendTo:  '#creator-filter', source: uriAutoCompleteUser});
	jQuery('#modifier-filter-value').autocomplete({appendTo: '#modifier-filter', source: uriAutoCompleteUser});

	//Autocompletar tipos y subtipos
	var uriAutoCompleteType     = Alfresco.constants.PROXY_URI + 'info/repository/type';
	jQuery('#exact_type-filter-value').autocomplete({appendTo: '#exact_type-filter', source: uriAutoCompleteType});
	jQuery('#type_and_subtypes-filter-value').autocomplete({appendTo: '#type_and_subtypes-filter', source: uriAutoCompleteType});

	//Autocompletar aspectos y subaspectos
	var uriAutoCompleteAspect   = Alfresco.constants.PROXY_URI + 'info/repository/aspect';
	jQuery('#exact_aspect-filter-value').autocomplete({appendTo: '#exact_aspect-filter', source: uriAutoCompleteAspect});
	jQuery('#aspect_and_subaspects-filter-value').autocomplete({appendTo: '#aspect_and_subaspects-filter', source: uriAutoCompleteAspect});


	//Autocompletar nombres de carpeta
	var uriAutoCompletePath     = Alfresco.constants.PROXY_URI + 'info/repository/path';
	jQuery('#path-filter-value').autocomplete({appendTo: '#path-filter', source: uriAutoCompletePath});

	//La función asignada a onOpen soluciona un error de la versión de daterangepicker en IE9 que no muestra el calendario automáticamente.
	var dateRangePickerOptions = {closeOnSelect: true, posY: '0px',  onOpen: function(){ jQuery(".ui-datepicker-inline").show();}} ;


	if(locale){
		jQuery('#created-filter-value').daterangepicker( jQuery.extend(daterangepickerRegional[locale],jQuery.extend(dateRangePickerOptions,{appendTo: '#created-filter'})) );
		jQuery('#modified-filter-value').daterangepicker( jQuery.extend(daterangepickerRegional[locale],jQuery.extend(dateRangePickerOptions,{appendTo: '#modified-filter'})) );
	}else{
		jQuery('#created-filter-value').daterangepicker( jQuery.extend(dateRangePickerOptions,{appendTo: '#created-filter'}));
		jQuery('#modified-filter-value').daterangepicker( jQuery.extend(dateRangePickerOptions,{appendTo: '#modified-filter'}));
	}


	jQuery('#check-precission-mode').change(function(_event){
		selectedType=jQuery("#by_type").val();
		if(selectedType=='creator' || selectedType=='modifier'){
			refreshGraph();
		}


	});
	jQuery('#by_type').change(function(selectByTypeEvent) {

		if(jQuery(this).val()!='-'){
			jQuery("#by_type").find("option[value='-']").remove();
			initFilterSelect(jQuery(this).val());
			refreshGraph();

		}



	});

	jQuery('#filter_type').change(function(selectFilterTypeEvent){

		var filterTypeValue=jQuery('#filter_type').val();

		var filterPanel=jQuery('#filters-panel');
		filterPanel.children().hide();


		if(filterTypeValue=='mimetype' || filterTypeValue=='created' || filterTypeValue=='modified' || filterTypeValue=='modifier' || filterTypeValue=='creator'
		|| filterTypeValue=='aspect_and_subaspects' || filterTypeValue == 'type_and_subtypes' || filterTypeValue=='exact_type' || filterTypeValue=='exact_aspect' ||filterTypeValue=='path'
		|| filterTypeValue=='size'){

			var filterTypePanel=jQuery('#'+filterTypeValue+'-filter',filterPanel);
			jQuery('input',filterTypePanel).val('');
			filterTypePanel.show();
		}



	});


	jQuery('.add-filter-button').click(function(clickAddFilterEvent){

		var filterName=jQuery(this).attr('id').split('-');
		filterName=filterName[1];

		var filterValue=jQuery('#'+filterName+'-filter-value').val();

		if(!filterValue || filterValue.length==0){

			showFlashMessage($msg('error.noEmptyFilter'));
			return false;
		}
		var filterObj=[];
		filterObj[filterName]=filterValue;

		listBase=jQuery('#selected-filters');

		var isFilterSelected=false;

		listBase.children().each(function(){   var filter=jQuery(this).data('filter'); if(filter[filterName]){isFilterSelected=true; return}  });

		if(isFilterSelected){showFlashMessage($msg('error.filterAlreadyExist')); return false;};

		var item = jQuery(document.createElement('li'));
		item.data('filter',filterObj);
		item.text(filterName+': ' +filterValue);
		item.attr('title', $msg('label.removeFilter'));
		jQuery('.no-filter').hide();
		item.click(function() {
			/* if (!confirm("¿Eliminar el filtro?")) { return; }*/
			item.remove();
			refreshGraph();
			if(listBase.children().length==0 ){
				jQuery('.no-filter').show();
			}
			return false;
		});
		listBase.append(item);
		jQuery('#'+filterName+'-filter-value').val('');
		refreshGraph();
		return false;

	});


	jQuery("#manual-filters-area").editInPlace({
		callback: function(original_element, edited, original) {  return edited;  },
		field_type: "textarea",
		textarea_rows: "4",
		textarea_cols: "40",
		save_button:'<button class="inplace_save">'+$msg('label.accept')+'</button>',
		cancel_button:'<button class="inplace_cancel">'+$msg('label.cancel')+'</button>',
		show_buttons: true,
		delegate: { didOpenEditInPlace: function(aDOMNode, aSettingsDict) {
			jQuery("#manual-filters-area textarea").autoGrow({lineHeight : 16,minHeight  : 16});
		},
		didCloseEditInPlace: function(){ refreshGraph(); }
	}

});

jQuery(".advanced-filters-options-toggle").click(function(){
	if(jQuery('#filter_type').attr('disabled')){ return false;}
	jQuery('#advanced-filters-option-container').toggle(400);
	jQuery('#advanced-filters-options-icon').toggleClass('ui-icon-minusthick');
	return false;
});

});


/***********************************************************************************************************************************/
function fillData(data,success,jqXHR){

	if (data!=null && data !== undefined)
	{

		fillDataTable(data);


		if (Raphael && Raphael.type) {
			//user Raphaël normally
			//TODO: localizar
			var graphicLabel=$msg('label.contentGroupBy');
			var byType=jQuery('#by_type').val();
			var filtered='';


			filters=jQuery('#selected-filters').children();

			if(filters.length>0){
				filtered='(filtrado)'
			}

			graphTitle=graphicLabel+' '+byType+' '+filtered;

			if(data.gRaphaelResult.data.length==0){graphTitle=$msg('label.noResultsFound');
			}else{

				if(swfobject.hasFlashPlayerVersion("9")){
					jQuery('#downloadify-container').show();
					jQuery('#downloadify').children().remove();
					jQuery('#downloadify').downloadify( {
						filename: function(){
							return graphTitle+'.svg';
						},
						data: function(){
							if(jQuery('#gRaphael').position().left >= 0 ){
								return jQuery('#gRaphael').html();
							}else{
								return jQuery('#gRaphael2').html();
							}
						},
						onComplete: function(){  },
						onCancel: function(){ },
						onError: function(){alert($msg('label.saveFileError'));},
						swf: url_context+'/res/components/reports/js/downloadify/media/downloadify.swf',
						downloadImage: url_context+'/res/components/reports/js/downloadify/images/multidownload.png',
						width: 32,
						height: 32,
						transparent: true,
						append: false
					}
					);;
				}
			}



			jQuery('#change-button-container').show();


			jQuery("#ajax-loader-container").hide();

			fillDataGraph(data,graphTitle);

		} else {
			jQuery("#ajax-loader-container").hide();
			showFlashMessage($msg('label.noVectorialGraphs'),5000);
			jQuery( "#accordion-collapsible").accordion("activate",0);
		}

	}else{
		jQuery("#ajax-loader-container").hide();
		showFlashMessage($msg('label.mustBeAdmin'),5000);

	}


}
/***********************************************************************************************************************************/
function fillDataTable(data){



		dataTable_oLanguage = {
				"sProcessing": $msg('dataTable.processing'),
				"sLengthMenu": $msg('dataTable.showMENUEntries'),
				"sZeroRecords": $msg('dataTable.noRecordsFound'),
				"sEmptyTable": $msg('dataTable.noDataAvailable') ,
				"sLoadingRecords": $msg('dataTable.loading') ,
				"sInfo": $msg('dataTable.showingInfo'),
				"sInfoEmpty": $msg('dataTable.showingInfoEmpty'),
				"sInfoFiltered": $msg('dataTable.infoFiltered'),
				"sInfoPostFix": "",
				"sSearch": $msg('dataTable.search'),
				"oPaginate": {
					"sFirst":    $msg('dataTable.paginate.first'),
					"sPrevious": $msg('dataTable.paginate.previous'),
					"sNext":     $msg('dataTable.paginate.next'),
					"sLast":     $msg('dataTable.paginate.last')
				}
			};

	var additionalOptions={"bAutoWidth": false, "bDestroy": true, "oLanguage": dataTable_oLanguage };


	jQuery( "#accordion-collapsible" ).accordion("destroy");


	jQuery.extend(data.dataTable,additionalOptions);

	jQuery('#datatable').dataTable( data.dataTable);





	jQuery( "#accordion-collapsible" ).accordion({
		collapsible: true,
		active: 1
	});

	jQuery( "#accordion-collapsible" ).accordion( "resize" )




}
/*************************************************************************************************************************************/
function renderNotData(graphTitle,r){
	r.g.cross(190,80,20).attr({fill: "#000", stroke: "none", /*translation: "-100 100"*/});
	r.g.text(220, 80, graphTitle).attr({"font-size": 16,"text-anchor": "start"});
}
/*****************************************************************************************************************/
function renderTitle(graphTitle,r){
	r.g.text(320, 20, graphTitle).attr({"font-size": 20});
}
/***********************************************************************************************************************************/

function renderDateDotTagGraph(data,r){
	r.g.txtattr.font = "11px 'Fontin Sans', Fontin-Sans, sans-serif";

	xs=data.gRaphaelMonthYearResult.xYear;
	ys=data.gRaphaelMonthYearResult.yMonth;
	dataValue=data.gRaphaelMonthYearResult.data;
	axisx=data.gRaphaelMonthYearResult.xLabel;
	axisy=data.gRaphaelMonthYearResult.yLabel;
	xStep=axisx.length-1;
	yStep=axisy.length-1;

r.g.dotchart(10, 10, 580, 260, xs, ys, dataValue, {symbol: "o", max: 10, heat: true, axis: "0 0 1 1", axisxstep: xStep,axisystep: yStep, axisxlabels: axisx, axisxtype: " ", axisytype: " ", axisylabels: axisy}).hover(function () {
	this.tag = this.tag || r.g.tag(this.x, this.y, this.value, 0, this.r + 2).insertBefore(this);
	this.tag.show();
}, function () {
	this.tag && this.tag.hide();
});

}

/***********************************************************************************************************************************/
function renderDateLinearGraph(data,r){

	var dataValue=data.gRaphaelMonthYearResult.data;
	var axisx=data.gRaphaelMonthYearResult.xLabel;

	var dataX=[];
	dataX[0]=0.0;

	for(i=1;i<dataValue.length;i++){
		dataX[i]=dataX[i-1]+1.0;

	}

	groupedDataValue = [];
	groupedDataX = [];
	for(i=0;i<(dataValue.length/12);i++){
		groupedDataValue[i]=dataValue.slice(12*i,12*(i+1));
		groupedDataX[i]=[0,1,2,3,4,5,6,7,8,9,10,11];
	}

var lines = r.g.linechart(30, 40, 580, 260, dataX, dataValue, {labelsX: axisx.concat([' ']) ,axisxstep: dataX.length/12, nostroke: false, axis: "0 0 1 1", symbol: "o", smooth: true, shade: false}).hoverColumn(function () {
	this.tags = r.set();
	for (var i = 0, ii = this.y.length; i < ii; i++) {
		this.tags.push(r.g.tag(this.x, this.y[i], this.values[i], 160, 10).insertBefore(this).attr([{fill: "#fff"}, {fill: this.symbols[i].attr("fill")}]));
	}
}, function () {
	this.tags && this.tags.remove();
});
lines.symbols.attr({r: 2});

}
/*******************************************************************************************************************************/
function renderPieChart(data,r){
	r.g.txtattr.font = "12px 'Fontin Sans', Fontin-Sans, sans-serif";

	customLabel=[];
	jQuery.each(data.gRaphaelResult.label, function (index){

		label="%%.%% - "+data.gRaphaelResult.label[index];
		customLabel[index]=label;

	});

	var pie = r.g.piechart(320, 160, 100, data.gRaphaelResult.data, {legend: customLabel, legendpos: "south", href: []});
	pie.hover(function () {
		this.sector.stop();
		this.sector.scale(1.1, 1.1, this.cx, this.cy);
		if (this.label) {
			this.label[0].stop();
			this.label[0].scale(1.5);
			this.label[1].attr({"font-weight": 800});
		}
		var valueStr=(this.value.valueOf() || "0");
		this.flag = r.g.blob(this.sector.middle.x, this.sector.middle.y, valueStr+' ('+this.label[1].node.textContent+')' );
	}, function () {
		this.sector.animate({scale: [1, 1,this.cx, this.cy]}, 500, "bounce");
		if (this.label) {
			this.label[0].animate({scale: 1}, 500, "bounce");
			this.label[1].attr({"font-weight": 400});
		}
		this.flag.animate({opacity: 0}, 300, function () {this.remove();});
	});
}
/********************************************************************************************************************************/

function renderBarChart(data,r){
	r.g.txtattr.font = "12px 'Fontin Sans', Fontin-Sans, sans-serif";

	var bar=r.g.barchart(30, 50, 580, 260, data.gRaphaelResult.data,{});
	bar.hover(function () {
		var message=this.bar.value.valueOf() || "0";
		if(this.bar.value.others){
			message=$msg('label.others')+': '+message;
		}

		this.flag = r.g.popup(this.bar.x, this.bar.y, message).insertBefore(this);
	},function () {
		this.flag.animate({opacity: 0}, 300, function () {this.remove();});
	}
	);


	bar.label( data.gRaphaelResult.label);
}

/***********************************************************************************************************************************/
function fillDataGraph(data,graphTitle,width,height){


	var childrenForRemove=jQuery.merge(jQuery("#gRaphael").children(),jQuery("#gRaphael2").children());
	childrenForRemove.remove();

	jQuery('#gRaphael-wrapper').children().css({left: 0});

	width = width || jQuery('#gRaphael').get(0).offsetWidth;
	height= height|| 342; //gRaphael default height
	
	var r = Raphael("gRaphael",width,height);
	var r2 = Raphael("gRaphael2",width,height);




	/*Las llamadas a gRaphael modifican el array de datos cuando,
	* por ejemplo, meten en la única agrupación "otros" agrupaciones
	* independientes pero con poco valor. Es posible que según la
	* altura del gráfico final tengamos que repintar para ajustarla.
	* En ese caso el valor de data.gRaphaelResult.data no coincidiría,
	* por lo que vamos a hacer una copia del objeto por si hubiera
	* que repintar.
	*/
	var backupData = jQuery.extend(true, {}, data);

	if(data.gRaphaelResult.data.length==0){
		renderNotData(graphTitle,r);

	}else{
		renderTitle(graphTitle,r);

		if(data.gRaphaelMonthYearResult){
			renderDateDotTagGraph(data,r);

			renderTitle(graphTitle,r2);
			renderDateLinearGraph(data,r2);
		}else{

			renderPieChart(data,r);
			renderTitle(graphTitle,r2);
			renderBarChart(data,r2);
		}
	}

	var computedHeight=Math.max(r.canvas.getBBox().height,r2.canvas.getBBox().height);
	//Si el alto de alguno de los gráficos supera al alto establecido,
	//repintamos con la altura mayor y sumamos 15 por paddings y márgenes
	//de por ahí
	if(computedHeight > height){
		fillDataGraph(backupData,graphTitle,width,computedHeight+15);
	}

}

/*******************************************************************************************/
function showFlashMessage(text, timeout){
	timeout = timeout || 3000;
	jQuery("#flash-messages").empty();

	if(text.replace(/^\s+/g,'').replace(/\s+$/g,'').indexOf('<!DOCTYPE')==0){

		var $frame = jQuery('<iframe style="width:100%; height:300px;">');

		jQuery("#flash-messages").html($frame);
		setTimeout( function() {
			var doc = $frame[0].contentWindow.document;
			var $body = jQuery('body',doc);
			$body.html(text);
		}, 100 );

		jQuery("#flash-messages").show();

	}else{
		jQuery("#flash-messages").text(text).show();
	}
	setTimeout(function(){
		jQuery("#flash-messages").fadeOut("slow", function () {
			jQuery("#flash-messages").empty().hide();
		}); }, timeout);


	}
/***********************************************************************************************************************************/
	function initFilterSelect(byTypeValue){

		if(byTypeValue!='-'){
			jQuery('#filter_type').removeAttr('disabled');

		}

		jQuery('#filters-panel').children().hide();
		jQuery('ul',jQuery('#selected-filters-panel')).children().remove();
		jQuery('.no-filter').show();

		var optionsForFilter='<option value="-" selected ></option>';

		if(byTypeValue!='mimetype'){
			optionsForFilter+='<option value="mimetype">'+$msg('label.mimetype')+'</option>';
		}
		if(byTypeValue!='created'){
			optionsForFilter+='<option value="created">'+$msg('label.creationDate')+'</option>';
		}
		if(byTypeValue!='creator'){
			optionsForFilter+='<option value="creator">'+$msg('label.creator')+'</option>';
		}
		if(byTypeValue!='modified'){
			optionsForFilter+='<option value="modified">'+$msg('label.modificationDate')+'</option>';
		}
		if(byTypeValue!='modifier'){
			optionsForFilter+='<option value="modifier">'+$msg('label.modificator')+'</option>';
		}
		if(byTypeValue!='type'){
			optionsForFilter+='<option value="exact_type">'+$msg('label.exactType')+'</option>';
			optionsForFilter+='<option value="type_and_subtypes">'+$msg('label.typeAndSubtypes')+'</option>';
		}
		if(byTypeValue!='aspect'){
			optionsForFilter+='<option value="exact_aspect">'+$msg('label.exactAspect')+'</option>';
			optionsForFilter+='<option value="aspect_and_subaspects">'+$msg('label.aspectAndSubaspects')+'</option>';
		}
		if(byTypeValue!='size'){
			optionsForFilter+='<option value="size">'+$msg('label.size')+'</option>';
		}

		optionsForFilter+='<option value="path">'+$msg('label.repositoryPath')+'</option>';

		jQuery('#filter_type').find('option').remove().end().append(optionsForFilter);

	}

/***********************************************************************************************************************************/
/*Dada una cadena que representa una fecha (o rango de fechas con separador) según la localización que estemos usando, devuelve
la fecha o array de fechas codificadas en iso8601*/
function localizedDateRangeToIsoDateArray(localizedDataStr){
		if(locale){
			dateFormat = daterangepickerRegional[locale].dateFormat;
		}else{
			dateFormat = 'm/d/yy';
		}

		//TODO obtener el separador de la configuración de daterangepicker
		dateRange=localizedDataStr.split('-');
		startDate=dateRange[0];
		endDate=dateRange[1];

		if(startDate){
			// convierte la cadena a Date según el locale
			startDate=jQuery.datepicker.parseDate(dateFormat, startDate.trim());
			//convierte date a cadena ISO 8601
			startDate=jQuery.datepicker.formatDate('yy-mm-dd',startDate);
		}
		if(endDate){
			endDate=jQuery.datepicker.parseDate(dateFormat, endDate.trim());
			endDate=jQuery.datepicker.formatDate('yy-mm-dd',endDate);
		}


		if(endDate){
			return [startDate,endDate];
		}else{
			return startDate;
		}

}
/***********************************************************************************************************************************/
function refreshGraph(){
		jQuery("#ajax-loader-container").show();
		var stackBy=jQuery('#by_type').val();

		var listBase=jQuery('#selected-filters');



		var filter ={};


		listBase.children().each( function(){  jQuery.extend(filter,jQuery(this).data('filter'));  });


		if(filter.created){
			filter.created=localizedDateRangeToIsoDateArray(filter.created);

		}
		if(filter.modified){
			filter.modified=localizedDateRangeToIsoDateArray(filter.modified);

		}


		filterStr=jQuery.toJSON(filter);

		var manualFilterStr = jQuery('#manual-filters-area').text();

		if(jqXHR){
			jqXHR.abort();
		}

		jqXHR = jQuery.getJSON(Alfresco.constants.PROXY_URI + 'info/repository/report/stack?',{by: stackBy, filter: filterStr, manual_filter: manualFilterStr, precission_mode: jQuery('#check-precission-mode').is(':checked')}
		).success(fillData).error(function(data,success,jqXHR1) {

			if(data.statusText=='abort') return;
			if(data.statusText=='error'){ jQuery("#ajax-loader-container").hide();showFlashMessage(data.responseText,10000); }
		});




}

function $msg(key){
	return Alfresco.util.message(key,reportDashletName);
}