<#--
 *  Content Reports Dashlet for Alfresco (http://www.dashlet.info)
 *  
 *  Copyright (C) 2011, 2012 Pedro Salido López <psalido@dashlet.info>
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  NOTICE
 *  This product includes software used under license from Alfresco Software, Inc., 
 *  but it is not an Alfresco product and has not been tested, endorsed, or 
 *  approved by Alfresco Software, Inc. or any of its affiliates.
-->
<#-- Hidden iframe is used by yui-history module -->
<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />

<#assign el=args.htmlid?html>

<script type="text/javascript">//<![CDATA[
	 var reportDashletName="Alfresco.dashlet.Report";
   var url_context='${page.url.context}';
   var locale='${locale}';
   Alfresco.util.addMessages(${messages}, reportDashletName)
//]]></script>


   <div  id="${args.htmlid}-panel" class="admin-console-body report-console" <#if args.height??>style="height: ${args.height}px;"</#if> >
   
	<div class="title">${msg("header.dashletTitle")}</div>
	<br/>
	<div id="group-by-container" >

			<label><h3>${msg("label.groupBy")} </h3><select id="by_type" >
				<option value="-" selected ></option>
				<option value="mimetype">${msg("label.mimetype")}</option>
				<option value="created">${msg("label.creationDate")}</option>
				<option value="creator">${msg("label.creator")}</option>
				<option value="modified">${msg("label.modificationDate")}</option>
				<option value="modifier">${msg("label.modificator")}</option>
				<option value="type">${msg("label.typeModel")}</option>
				<option value="size">${msg("label.size")}</option>
			</select></label>
			<div id="ajax-loader-container" >
					<img  src="${page.url.context}/res/components/reports/images/ajax_load_trans.gif" />
			</div>		


		<div id="filter-by-container" class="container" >
			<label><h3>${msg("label.filterBy")} </h3><select disabled id="filter_type">
			</select></label>
		</div>
	</div>

		<br class="clear" />
		<div id="filters-panel" class="yui-skin-default">
			<div id="mimetype-filter" class="initial-hidden" >
				<label><h4>${msg("label.mimetype")}:</h4><input id="mimetype-filter-value" type="text" /> </label>
				<span class="yui-button yui-link-button"><span class="first-child"> <a id='add-mimetype-filter-button' class="add-filter-button" href="#">${msg("label.add")}</a> </span></span>
			</div>
			<div id="created-filter" class="initial-hidden">
				<label><h4>${msg("label.date")}:</h4><input id="created-filter-value" type="text" /> </label>
				<span class="yui-button yui-link-button"><span class="first-child"> <a  id='add-created-filter-button' class="add-filter-button" href="#">${msg("label.add")}</a> </span></span>
			</div>
			<div id="modified-filter" class="initial-hidden">
				<label><h4>${msg("label.date")}:</h4><input id="modified-filter-value" type="text" /> </label>
				<span class="yui-button yui-link-button"><span class="first-child"> <a  id='add-modified-filter-button' class="add-filter-button" href="#">${msg("label.add")}</a> </span></span>
			</div>
			
			<div id="creator-filter" class="initial-hidden">
				<label><h4>${msg("label.creator")}:</h4><input id="creator-filter-value" type="text" /> </label>
				<span class="yui-button yui-link-button"><span class="first-child"> <a  id='add-creator-filter-button' class="add-filter-button" href="#">${msg("label.add")}</a> </span></span>
			</div>
			<div id="modifier-filter" class="initial-hidden">
				<label><h4>${msg("label.modifier")}:</h4><input id="modifier-filter-value" type="text" /> </label>
				<span class="yui-button yui-link-button"><span class="first-child"> <a  id='add-modifier-filter-button' class="add-filter-button" href="#">${msg("label.add")}</a> </span></span>
			</div>


			<div id="exact_type-filter" class="initial-hidden">
				<label><h4>${msg("label.exactType")}:</h4><input id="exact_type-filter-value" type="text" /> </label>
				<span class="yui-button yui-link-button"><span class="first-child"> <a  id='add-exact_type-filter-button' class="add-filter-button" href="#">${msg("label.add")}</a> </span></span>
			</div>

			<div id="type_and_subtypes-filter" class="initial-hidden">
				<label><h4>${msg("label.typeAndSubtypes")}:</h4><input id="type_and_subtypes-filter-value" type="text" /> </label>
				<span class="yui-button yui-link-button"><span class="first-child"> <a  id='add-type_and_subtypes-filter-button' class="add-filter-button" href="#">${msg("label.add")}</a> </span></span>
			</div>

			<div id="exact_aspect-filter" class="initial-hidden">
				<label><h4>${msg("label.exactAspect")}:</h4><input id="exact_aspect-filter-value" type="text" /> </label>
				<span class="yui-button yui-link-button"><span class="first-child"> <a  id='add-exact_aspect-filter-button' class="add-filter-button" href="#">${msg("label.add")}</a> </span></span>
			</div>

			<div id="aspect_and_subaspects-filter" class="initial-hidden">
				<label><h4>${msg("label.aspectAndSubaspects")}:</h4><input id="aspect_and_subaspects-filter-value" type="text" /> </label>
				<span class="yui-button yui-link-button"><span class="first-child"> <a  id='add-aspect_and_subaspects-filter-button' class="add-filter-button" href="#">${msg("label.add")}</a> </span></span>
			</div>

			<div id="path-filter" class="initial-hidden">
				<label><h4>${msg("label.path")}:</h4><input id="path-filter-value" type="text" /> </label>
				<span class="yui-button yui-link-button"><span class="first-child"> <a  id='add-path-filter-button' class="add-filter-button" href="#">${msg("label.add")}</a> </span></span>
			</div>

			<div id="size-filter" class="initial-hidden">
				<label><h4>${msg("label.size")}:</h4><input id="size-filter-value" type="text" /> </label>
				<span class="yui-button yui-link-button"><span class="first-child"> <a  id='add-size-filter-button' class="add-filter-button" href="#">${msg("label.add")}</a> </span></span>
			</div>

		</div>
		
		<div id="selected-filters-container" class="container" >
				<div id="selected-filters-panel" class="left half">
				<label><h4>${msg("label.filters")}  </h4>
					
						<span class="no-filter" ><i>${msg("label.noFilters")}</i></span><ul id='selected-filters'></ul>
						
						</label>
				</div>
		
				<div id="advanced-filters-panel" class="left half">
					<label>
					 <h4 class="advanced-filters-options-toggle left" >${msg("label.advancedOptions")}</h4><span  id="advanced-filters-options-icon"  class="left advanced-filters-options-toggle ui-icon ui-icon-plusthick" ></span> 
						 <div id="advanced-filters-option-container" class="clear initial-hidden"  >
							<div id="manual-filters-container">
									<p><i>${msg("label.advancedOptionManualFilter")}:</i></p><br/>
								  <p id="manual-filters-area" > AND TYPE:"cm:content" </p>
								  <p><i>${msg("label.advancedOptionManualFilterHelp")}</i></p>
							</div>
							<div id="check-precission-mode-container"><br/><input type="checkbox" name="check-precission-mode" id="check-precission-mode"/> <b>${msg("label.precissionMode")}</b> <br/>
								<i>${msg("label.precissionModeHelp")}</i>
							</div>
						</div>
					</label>		
					
				
				</div>
		</div>

		<div id="flash-messages-container" ><div class="initial-hidden border"  id="flash-messages" ></div></div>
		<hr/>
		<div id="button-container" >
			<div id="downloadify-container"   class="initial-hidden" > <div id="downloadify-wrapper" > <div id="downloadify" class="text-center" > </div><div class="text-center"><i>${msg("label.downloadGraphic")}</i></div></div></div>
			<div id="change-button-container" class="initial-hidden" ><div id="change-button-container-wrapper" > <a id="change-button"  href="#"><img src="${page.url.context}/res/components/reports/images/change.png" /></a><div><i>${msg("label.changeGraphic")}</i></div></div></div>
		</div>
		<br class="clear" />
		
		
		<div id="gRaphael-container" >
			<div id="gRaphael-wrapper" class="double">
				<div id="gRaphael" class="relative half left" > </div>
				<div id="gRaphael2" class="relative half left block" > </div>
			</div>
		</div>

		<hr/>
		<div id="accordion-collapsible" >
			<h2 ><a href=#" >${msg("label.dataTable")}</a></h2>
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="datatable">
			</table>
		</div>




   </div>



