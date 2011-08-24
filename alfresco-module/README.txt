Report dashlet for Alfresco Share
=======================================================

Description
-----------
This module provides webscripts used by Report Dashlet. This provides
information (chart and table) from repository contents stacked by some property value.


Author
------
Pedro Salido López (psalido@dashlet.info - http://www.dashlet.info)


Pre-requisites
--------------
Alfresco 3.4 or better (this package tested on Alfresco Enterprise 3.4SP1).


Installation
------------
1. ALWAYS BACKUP YOUR ORIGINAL ALFRESCO.WAR FILE BEFORE APPLYING *ANY* AMPS
   TO YOUR INSTALLATION!!
   Although the apply_amps script will backup your alfresco.war, it's better
   to manually back it up prior to installing *any* AMP files, so that you
   can roll back to a pristine binary if needed.
2. Optionally you can build amp file from source. Run ant from alfresco-module
   proyect folder. Generated AMP will be located at build/dist
3. Copy the provided AMP file into the ${ALFRESCO_HOME}/amps directory
4. Run the ${ALFRESCO_HOME}/apply_amps[.sh|.bat] script to install the AMP
   into your Alfresco instance





