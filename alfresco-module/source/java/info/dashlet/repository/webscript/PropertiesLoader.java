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

package info.dashlet.repository.webscript;

import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.icu.util.Calendar;

public class PropertiesLoader {


	private static Log logger = LogFactory.getLog(PropertiesLoader.class);
	private static final String CURRENT_YEAR_LABEL="current";

	private static volatile boolean loadedProperties = false;

	/** Property file name */
	private static final String PROPERTY_DEFAULT_FILE_NAME = "info/dashlet/repository/config-default.properties";
	private static final String PROPERTY_EXT_FILE_NAME = "info/dashlet/repository/config.properties";

	private static final String _SIZE_RANGES_KEY = "size.ranges";
	private static final String _LUCENE_MIN_DOC_YEAR = "lucene.min.document.year";
	private static final String _LUCENE_MAX_DOC_YEAR = "lucene.max.document.year";

	private static final String _DEFAULT_SIZE_RANGES_VALUE = "0-256K,256K-512K,512K-1M,1M-2M,2M-5M,5M-10M,10M-25M,25M-40M,40M-55M,55M-70M,70M-85M,85M-100M,100M-MAX";;
	private static final String _DEFAULT_LUCENE_MAX_DOC_YEAR_VALUE = CURRENT_YEAR_LABEL;
	private static final String _DEFAULT_LUCENE_MIN_DOC_YEAR_VALUE = "1998";


	private static String defaultSizeRanges = _DEFAULT_SIZE_RANGES_VALUE;
	private static String defaultLuceneMinimumDocumentYear = _DEFAULT_LUCENE_MIN_DOC_YEAR_VALUE;
	private static String defaultLuceneMaximumDocumentYear = _DEFAULT_LUCENE_MAX_DOC_YEAR_VALUE;


	private static synchronized void loadProperties()
	{

		if (loadedProperties){
			return;
		}

		try{

			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTY_EXT_FILE_NAME);
			if(is == null){
				is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTY_DEFAULT_FILE_NAME);
			}

			Properties props = new Properties();
			if(is!=null){
				props.load(is);
			}

			// Add defaults for any properties not set
			if (props.getProperty(_SIZE_RANGES_KEY) != null){
				defaultSizeRanges = props.getProperty(_SIZE_RANGES_KEY);
			}
			if (props.getProperty(_LUCENE_MIN_DOC_YEAR) != null){
				defaultLuceneMinimumDocumentYear = props.getProperty(_LUCENE_MIN_DOC_YEAR);
			}
			if (props.getProperty(_LUCENE_MAX_DOC_YEAR) != null){
				defaultLuceneMaximumDocumentYear = props.getProperty(_LUCENE_MAX_DOC_YEAR);
			}

		}
		catch (Exception e){
			logger.error("No se pueden cargar las propiedades de configuracion del dashlet, usando props por defecto: " + e.getMessage());
		}

		loadedProperties = true;
	}

	public static String getDefaultSizeRanges(){
		if (!loadedProperties){
			loadProperties();
		}
		return defaultSizeRanges;
	}

	public static Integer getDefaulLucenetMinDocumentYear(){
		if (!loadedProperties){
			loadProperties();
		}
		return toInt(defaultLuceneMinimumDocumentYear);
	}



	public static Integer getDefaultLuceneMaxDocumentYear(){
		if (!loadedProperties){
			loadProperties();
		}
		return toInt(defaultLuceneMaximumDocumentYear);
	}
	
	private static Integer toInt(String integer) {
		
		if(integer.equalsIgnoreCase(CURRENT_YEAR_LABEL)){
			return GregorianCalendar.getInstance().get(Calendar.YEAR);
		}else{
			return Integer.valueOf(integer);	
		}
		
		
	}

}