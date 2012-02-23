/*
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
*/

/*Code from alfresco share: alfresco/site-webscripts/org/alfresco/modules/about-share.get.js */

function main()
{
   // Call the repo to collect server meta-data
   var conn = remote.connect("alfresco");
   var res = conn.get("/api/server");
   var json = eval('(' + res + ')');

   // Create model and defaults
   model.serverEdition = "Unknown";
   model.serverVersion = "Unknown (Unknown)";
   model.serverSchema = "Unknown";

   // Check if we got a positive result
   if (json.data)
   {
      model.serverEdition = json.data.edition;
      model.serverVersion = json.data.version;
      model.serverSchema = json.data.schema;
   }
}

main();