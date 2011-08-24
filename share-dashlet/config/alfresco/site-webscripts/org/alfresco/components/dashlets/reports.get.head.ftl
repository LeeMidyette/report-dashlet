<#--
 *  Content Reports Dashlet for Alfresco (http://www.dashlet.info)
 *  
 *  Copyright (C) 2011 Pedro Salido López <psalido@dashlet.info>
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
<#include "../component.head.inc">
<!-- Content Report Dashlet -->
<style type="text/css" media="screen">
@import "${page.url.context}/res/components/reports/css/demo_table.css";
@import "${page.url.context}/res/components/reports/css/tageditor.css";
@import "${page.url.context}/res/components/reports/css/ui.daterangepicker.css";
@import "${page.url.context}/res/components/reports/css/redmond/jquery-ui-1.8.14.custom.css";
</style>

<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/reports/css/custom.css" />

<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/jquery-1.6.2.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/jqueryjson/jquery.json-2.2.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/jqueryui/jquery-ui-1.8.14.custom.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/jqueryui/i18n/jquery.ui.datepicker-es.js"></@script>  
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/datarangepicker/daterangepicker.jQuery.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/datarangepicker/i18n/datarangepicker.jQuery-es.js"></@script>

<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/downloadify/swfobject.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/downloadify/downloadify.js"></@script>

<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/editinplace/jquery.autogrowtextarea.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/editinplace/jquery.editinplace.js"></@script>

 
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/jquery.dataTables.js"></@script>

<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/raphael/raphael.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/raphael/g.raphael.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/raphael/g.pie.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/raphael/g.line.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/raphael/g.dot.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/raphael/g.bar.js"></@script>                                                                

<@script type="text/javascript" src="${page.url.context}/res/components/reports/js/dashlet.content.report.js"></@script>