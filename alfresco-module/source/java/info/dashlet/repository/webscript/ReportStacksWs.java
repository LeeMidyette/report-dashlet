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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.httpclient.HttpClientFactory;
import org.alfresco.repo.management.subsystems.SwitchableApplicationContextFactory;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.search.impl.lucene.ADMLuceneIndexerAndSearcherFactory;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.repo.search.impl.lucene.LuceneSearcher;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.LimitBy;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.alfresco.util.Pair;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.ibm.icu.util.Calendar;


/**
 * Class ReportStacksWs.
 */
public class ReportStacksWs extends AbstractWebScript implements ApplicationContextAware {

	/** La Constante logger. */
	private static final Logger logger = Logger.getLogger(ReportStacksWs.class);

	/** La Constante LIMIT_TOP_TERM. */
	private static final int LIMIT_TOP_TERM = 200;

	/** El adm lucene indexer and searcher factory. */
	private ADMLuceneIndexerAndSearcherFactory admLuceneIndexerAndSearcherFactory;

	/** El repository. */
	private Repository repository;

	/** El search service. */
	private SearchService searchService;

	/** El file folder service. */
	private FileFolderService fileFolderService;

	/** El node service. */
	private NodeService nodeService;

	/** El namespace service. */
	private NamespaceService namespaceService;

	/** Indica initialization completed. */
	private boolean initializationCompleted=false;

	/** The application context. */
	private static ApplicationContext applicationContext;





	/**
	 * Obtiene repository.
	 *
	 * @return el repository
	 */
	public Repository getRepository() {
		return repository;
	}

	/**
	 * Establece el valor de repository.
	 *
	 * @param repository el nuevo repository
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	/**
	 * Obtiene namespace service.
	 *
	 * @return el namespace service
	 */
	public NamespaceService getNamespaceService() {
		return namespaceService;
	}

	/**
	 * Establece el valor de namespace service.
	 *
	 * @param namespaceService el nuevo namespace service
	 */
	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	/**
	 * Obtiene node service.
	 *
	 * @return el node service
	 */
	public NodeService getNodeService() {
		return nodeService;
	}

	/**
	 * Establece el valor de node service.
	 *
	 * @param nodeService el nuevo node service
	 */
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * Obtiene file folder service.
	 *
	 * @return el file folder service
	 */
	public FileFolderService getFileFolderService() {
		return fileFolderService;
	}

	/**
	 * Establece el valor de file folder service.
	 *
	 * @param fileFolderService el nuevo file folder service
	 */
	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	/**
	 * Obtiene search service.
	 *
	 * @return el search service
	 */
	public SearchService getSearchService() {
		return searchService;
	}

	/**
	 * Establece el valor de search service.
	 *
	 * @param searchService el nuevo search service
	 */
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	/** La Constante searchBy. */
	private final static Hashtable<String,String[]> searchBy=new Hashtable<String,String[]>();

	/** La Constante filterBy. */
	private final static Hashtable<String,String> filterBy=new Hashtable<String,String>();

	private static final Integer DEFAULT_OLDEST_YEAR = 1999;

	static{

		//{nombre del campo para consulta, filtro en consulta, nombre del campo para consulta solr} 
		searchBy.put("mimetype", new String [] {"@{http://www.alfresco.org/model/content/1.0}content.mimetype","@\\{http\\://www.alfresco.org/model/content/1.0\\}content.mimetype:\"%s\"","@{http://www.alfresco.org/model/content/1.0}content.mimetype"});
		searchBy.put("created",  new String [] {"@{http://www.alfresco.org/model/content/1.0}created","@cm\\:created:%s","@{http://www.alfresco.org/model/content/1.0}created"});
		searchBy.put("modified", new String [] {"@{http://www.alfresco.org/model/content/1.0}modified","@cm\\:modified:%s","@{http://www.alfresco.org/model/content/1.0}modified"});
		searchBy.put("creator",  new String [] {"@{http://www.alfresco.org/model/content/1.0}creator","@cm\\:creator:\"%s\"","@{http://www.alfresco.org/model/content/1.0}creator.__"});
		searchBy.put("modifier", new String [] {"@{http://www.alfresco.org/model/content/1.0}modifier","@cm\\:modifier:\"%s\"","@{http://www.alfresco.org/model/content/1.0}modifier.__"});
		searchBy.put("aspect",   new String [] {"ASPECT","EXACTASPECT:\"%s\"","ASPECT"});
		searchBy.put("type",     new String [] {"TYPE","EXACTTYPE:\"%s\"","TYPE"});
		searchBy.put("size", new String [] {"@{http://www.alfresco.org/model/content/1.0}content.size","@\\{http\\://www.alfresco.org/model/content/1.0\\}content.size:%s","@{http://www.alfresco.org/model/content/1.0}content.size"});



		filterBy.put("mimetype", 		"@\\{http\\://www.alfresco.org/model/content/1.0\\}content.mimetype:\"%s\"");
		filterBy.put("created",  		"@cm\\:created:%s");
		filterBy.put("modified", 		"@cm\\:modified:%s");
		filterBy.put("creator",  		"@cm\\:creator:\"%s\"");
		filterBy.put("modifier", 		"@cm\\:modifier:\"%s\"");
		filterBy.put("exact_aspect",   		"EXACTASPECT:\"%s\"");
		filterBy.put("exact_type",     		"EXACTTYPE:\"%s\"");
		filterBy.put("aspect_and_subaspects",   "ASPECT:\"%s\"");
		filterBy.put("type_and_subtypes",     "TYPE:\"%s\"");
		filterBy.put("path",     		"PATH:\"%s\"");
		filterBy.put("size", 		"@\\{http\\://www.alfresco.org/model/content/1.0\\}content.size:%s");

	}

	/* (non-Javadoc)
	 * @see org.springframework.extensions.webscripts.WebScript#execute(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.WebScriptResponse)
	 */
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res)
			throws IOException {

		if(!initializationCompleted)
			init();

		res.setContentEncoding("UTF-8");
		try{
			String by=req.getParameter("by");
			if(by==null || by.trim().equals("") || searchBy.get(by)==null){
				throw new WebScriptException(Status.STATUS_BAD_REQUEST,"El parámetro by es incorrecto.");
			}
			logger.debug(String.format("Usando Parámetro by=%s", by));


			ArrayList<String []> result=null;
			String filterJsonString=req.getParameter("filter");

			String manual_filter=req.getParameter("manual_filter");

			boolean precissionMode=new Boolean(req.getParameter("precission_mode"));


			Pair<String, ArrayList<Pair<String,String>>> filters=buildFiltersFromParams(filterJsonString);


			String luceneFilter=filters.getFirst()+" "+manual_filter;
			ArrayList<Pair<String, String>> manualFilter = filters.getSecond();
			try{
				result = search(by,luceneFilter, manualFilter,precissionMode);
			}catch(Exception e){

				logger.error("Excepcion consultando terminos de lucene ",e);
				if(e.getCause() instanceof ParseException){
					throw new WebScriptException(Status.STATUS_BAD_REQUEST,"Error en la consulta lucene: por favor, corrige la consulta",e);

				}else{
					throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,"Excepción en la búsqueda. "+e.getMessage(),e);
				}

			}


			JSONObject gRaphaelMonthYearResult=null;
			/* Si estamos recuperando contenidos por created o modified, introducimos una nueva disposición de datos
			 * para pintar la gráfica de puntos en gRaphael
			 */
			if((by.equals("created") || by.equals("modified")) ){
				gRaphaelMonthYearResult=transformDateResultsToSparseGRaphaelFormat(result);

			}

			/*Continuamos con el procesamiento normal para devolver los datos para tabla y gráficas*/
			ArrayList<Integer> gRaphaelData=new ArrayList<Integer>(); 
			ArrayList<String> gRaphaelLabel=new ArrayList<String>();

			for(String[] component: result){
				gRaphaelLabel.add(component[0]);
				gRaphaelData.add(Integer.parseInt(component[1]));
			}

			JSONObject dataTableResult=new JSONObject();
			JSONObject gRaphaelResult=new JSONObject();
			JSONObject responseData = new JSONObject();


			try {
				dataTableResult.put("aaData", result);
				dataTableResult.put("aoColumns", new JSONArray("[ {sTitle:'"+by+"'} , {sTitle:'Numero'} ]"));
				dataTableResult.put("aaSorting", new JSONArray("[[ 1, \"desc\" ]]"));

				gRaphaelResult.put("data",gRaphaelData);
				gRaphaelResult.put("label",gRaphaelLabel);

				responseData.put("dataTable", dataTableResult);
				responseData.put("gRaphaelResult", gRaphaelResult);
				if(gRaphaelMonthYearResult!=null)
					responseData.put("gRaphaelMonthYearResult", gRaphaelMonthYearResult);
			} catch (JSONException e1) {
				throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,"Error añadiendo datos al resultado JSON");
			}



			String responseOk=responseData.toString();

			if(responseOk!=null)
				res.getWriter().append(responseOk);
			else
				res.getWriter().append("Error!");
		}catch(WebScriptException wex){
			renderErrorResponse(req,res,wex);
		}catch(Exception e){
			WebScriptException wex = new  WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,"Error indeterminado en la ejecución del webscript", e);
			logger.error("Error ejecutando webscript "+ReportStacksWs.class.getName(),wex);
			renderErrorResponse(req,res,wex);
		}


	}

	/**
	 * Construye filtros de los parámetros.
	 * A partir de la cadena json procedente del parámetro filter, elabora las clausulas
	 * de la consulta lucene con estos filtros que se agregarán a la consulta definitiva.
	 * 
	 * Además, si existen  filtros que se aplican sobre un campo tokenizado (caso de created y 
	 * modified), se construye una lista con estos de la forma [campo: valor].
	 * 
	 *  Esta estructura se usará para refinar manualmente los resultados de la búsqueda (hecha
	 *  para el token correspondiente a valor) con el valor exacto por el que queremos buscar
	 *
	 * @param filterJsonString el/la filter json string
	 * @return Un par cuya primera componente es la cadena con clausulas de la consulta lucene según los filtros
	 * y cuya segunda componente es una lista con los filtros manuales  para refinar los resultados de búsqueda
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private Pair<String, ArrayList<Pair<String, String>>> buildFiltersFromParams(String filterJsonString) {

		String luceneFilter="";
		ArrayList<Pair<String,String>>  manualFilter=new ArrayList<Pair<String,String>>();
		if(filterJsonString!=null && !filterJsonString.trim().equals("")){
			//{mimetype: %s,created: %s, modified: %s, creator: %s, modifier: %s, aspect: %s, type: %s, path: %s}
			JSONObject filterJsonObject=null;
			try {
				filterJsonObject=new JSONObject(filterJsonString);
			} catch (JSONException e) {
				throw new WebScriptException(Status.STATUS_BAD_REQUEST,"El parámetro filter es incorrecto, debe estar en notación JSON");			
			}

			Iterator it=filterJsonObject.keys();
			while(it.hasNext()){


				String key=(String)it.next();
				List<Pair<String,Integer>> listaGlobalTerminosForKey=null;

				if(key.equals("creator") || key.equals("modifier")){
					listaGlobalTerminosForKey=getListaGlobalTerminos(key);
				}




				if(filterBy.get(key)==null){
					throw new WebScriptException(Status.STATUS_BAD_REQUEST,String.format("El filtro %s, no es admitido",key));
				}


				Object valueObj;
				try {
					valueObj = filterJsonObject.get(key);
				} catch (JSONException e1) {
					throw new WebScriptException(Status.STATUS_BAD_REQUEST,String.format("El filtro %s no tiene un valor",key));						
				}
				if(valueObj instanceof JSONArray){
					JSONArray multivalueJSon= (JSONArray) valueObj;
					int numValues=multivalueJSon.length();
					ArrayList<String> multivalue=new ArrayList<String>();
					for(int i=0; i<numValues;i++){
						String valor;
						try {
							valor = multivalueJSon.getString(i);
						} catch (JSONException e) {
							throw new WebScriptException(Status.STATUS_BAD_REQUEST,String.format("El valor \"%s\"  para el filtro %s no es un string",i,key));
						}
						if(key.equals("created") || key.equals("modified")){
							multivalue.add(valor.replace("-", "\\-"));
						}else{
							luceneFilter=setFiltersKeyValue(key,valor,manualFilter,luceneFilter,listaGlobalTerminosForKey);
						}
					}
					if(key.equals("created") || key.equals("modified")){
						if(multivalue.size()>2){
							throw new WebScriptException(Status.STATUS_BAD_REQUEST,String.format("El filtro %s no admite más de 2 valores, que representan un rango de fechas",key));
						}
						String valor="["+StringUtils.join(multivalue.toArray(new String[1])," TO ")+"]";
						luceneFilter=setFiltersKeyValue(key,valor,manualFilter,luceneFilter,listaGlobalTerminosForKey);
					}
				}else{
					String valor;
					try {
						valor = filterJsonObject.getString(key);

					} catch (JSONException e) {
						throw new WebScriptException(Status.STATUS_BAD_REQUEST,String.format("El valor para el filtro %s no es un string",key));
					}
					luceneFilter=setFiltersKeyValue(key,valor,manualFilter,luceneFilter,listaGlobalTerminosForKey);

				}
			}


		}


		return new Pair<String, ArrayList<Pair<String, String>> >(luceneFilter, manualFilter);


	}
	/* Set filter for value*/
	/**
	 * Establece los valores que deberán aparecer en los campos de la consulta lucene para filtrar 
	 * por estos. 
	 * 
	 * Si filtramos por creator o modifier, el valor no será el que hayamos pasado, si no el que corresponda según se describe
	 * en @see ReportStacksWs.fakeTokenizeValue.
	 * 
	 * Si filtramos por path, debemos convertir el path dado (localizado y human-readable) por un path con prefijos y codificado
	 * adecuadamente para el campo especial PATH
	 *
	 * @param key el/la key
	 * @param valor el/la valor
	 * @param manualFilter el/la manual filter
	 * @param luceneFilter el/la lucene filter
	 * @param listaGlobalTerminosForKey el/la lista global terminos for key
	 * @return el/la string
	 */
	private String setFiltersKeyValue(String key, String valor, ArrayList<Pair<String, String>> manualFilter, String luceneFilter, List<Pair<String, Integer>> listaGlobalTerminosForKey) {
		if(key.equals("creator") || key.equals("modifier")){

			String newValor=fakeTokenizeValue(valor,listaGlobalTerminosForKey);
			if(!newValor.equals(valor) && !newValor.equals("")){
				manualFilter.add(new Pair<String,String>(key,valor));
				valor=newValor;
			}
		}
		if(key.equals("path")){
			try {
				valor = convertPathToLucenePath(valor,nodeService );

			} catch (FileNotFoundException e) {
				throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,"Hubo un error resolviendo el PATH dado",e);
			}




		}
		if(key.equals("size")){
			//TODO: adecuar a una regex
			String tmp=valor.trim().toUpperCase();

			String preffix="";
			String suffix="";
			if(tmp.startsWith("[") || tmp.startsWith("{")){
				preffix=tmp.substring(0,1);
				tmp=tmp.substring(1);
			}
			if(tmp.endsWith("]") || tmp.endsWith("}")){
				suffix=tmp.substring(tmp.length()-1, tmp.length());
				tmp=tmp.substring(0, tmp.length()-1);
			}

			String [] numbers=tmp.split("-");
			if(numbers.length > 1){
				long min=translateToBytes(numbers[0]);
				long max=translateToBytes(numbers[1]);

				tmp=preffix+min+" TO "+max+suffix;
			}else{
				tmp=""+translateToBytes(tmp);
			}


			valor=tmp;
		}
		luceneFilter+=" AND "+"+"+String.format(filterBy.get(key),valor);
		return luceneFilter;
	}


	/**
	 * Obtiene lista global terminos para ese campo usando los términos almacenados en lucene
	 *
	 * @param key el/la key
	 * @return el lista global terminos
	 */
	private List<Pair<String, Integer>> getListaGlobalTerminos(String key) {
		String [] values=searchBy.get(key);
		NodeRef rootNodeRef=nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		List<Pair<String,Integer>> listaGlobalTerminos=null;
		if(key.equals("created") || key.equals("modified")){


			Serializable oldestDate="";
			Serializable newestDate="";
			Serializable [] minMaxDate=getMinMaxRepositoryDocumentDate(key);
			oldestDate=minMaxDate[0];
			newestDate=minMaxDate[1];

			Integer oldestYear=DEFAULT_OLDEST_YEAR;
			Integer newestYear=GregorianCalendar.getInstance().get(Calendar.YEAR);

			SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy");
			if(oldestDate instanceof java.util.Date && Integer.valueOf(simpleDateformat.format(oldestDate))!=null){
				oldestYear=Integer.valueOf(simpleDateformat.format(oldestDate));
			}
			if(newestDate instanceof java.util.Date && Integer.valueOf(simpleDateformat.format(newestDate))!=null){
				newestYear=Integer.valueOf(simpleDateformat.format(newestDate));
			}

			listaGlobalTerminos=new ArrayList<Pair<String,Integer>>();
			for(int year=oldestYear; year <= newestYear;year++){
				for(int month=1; month<=12;month++){
					listaGlobalTerminos.add(new Pair<String,Integer>( Integer.toString(year) + "-"+ Integer.toString(month),1 ) );
				}
			}
		}else{
			if(admLuceneIndexerAndSearcherFactory!=null){//lucene config
				LuceneSearcher searcher=admLuceneIndexerAndSearcherFactory.getSearcher(rootNodeRef.getStoreRef(), true);

				listaGlobalTerminos=searcher.getTopTerms(values[0], LIMIT_TOP_TERM);
			}else{//solr config
				CustomSolrSearcher solrSearcher=CustomSolrSearcher.getInstance(); 
				listaGlobalTerminos=solrSearcher.getTopTerms(values[2], LIMIT_TOP_TERM);
			}
			if(key.equals("size")){
				listaGlobalTerminos=groupSizesByConf(listaGlobalTerminos);
			}

		}

		return listaGlobalTerminos;
	}

	private Serializable[] getMinMaxRepositoryDocumentDate(String key) {
		String filter=String.format(filterBy.get(key), "[MIN TO MAX]");

		Serializable oldestDate="";
		Serializable newestDate="";

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);

		sp.setQuery("PATH:\"//.\" AND "+filter);
		sp.addSort(searchBy.get(key)[0], true);
		sp.setLimit(1);
		sp.setLimitBy(LimitBy.FINAL_SIZE);

		ResultSet result = searchService.query(sp);
		List<NodeRef> nodeRefs = result.getNodeRefs();

		if(nodeRefs.size()>0){
			oldestDate=nodeService.getProperty(nodeRefs.get(0), QName.createQName(searchBy.get(key)[0].substring(1)));
		}
		result.close();

		sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);

		sp.setQuery("PATH:\"//.\" AND "+filter);
		sp.addSort(searchBy.get(key)[0], false);
		sp.setLimit(1);
		sp.setLimitBy(LimitBy.FINAL_SIZE);

		result = searchService.query(sp);
		nodeRefs = result.getNodeRefs();
		if(nodeRefs.size()>0){
			newestDate=nodeService.getProperty(nodeRefs.get(0), QName.createQName(searchBy.get(key)[0].substring(1)));
		}
		result.close();
		return new Serializable[]{oldestDate,newestDate};
	}

	private List<Pair<String, Integer>> groupSizesByConf(List<Pair<String, Integer>> listaGlobalTerminos) {

		ArrayList<Pair<Long, Integer>> translatedListaGlobalterminos=new ArrayList<Pair<Long, Integer>>();

		for(Pair<String,Integer> pair:listaGlobalTerminos){
			Long byteSizeGroup=org.alfresco.repo.search.impl.lucene.analysis.NumericEncoder.decodeLong(pair.getFirst());
			Pair<Long,Integer> translatedPair=new Pair<Long,Integer>(byteSizeGroup,pair.getSecond());
			translatedListaGlobalterminos.add(translatedPair);
		}
		ClassLoader cl = this.getClass().getClassLoader();
		InputStream is = cl.getResourceAsStream("info/dashlet/repository/sizegroups.properties");

		Properties props = new Properties();
		try {
			props.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String rangesStr=props.getProperty("ranges");
		String [] groups=rangesStr.split(",");

		ArrayList<Pair<String, Integer>> formattedListaGlobalterminos=new ArrayList<Pair<String, Integer>>();

		for(String groupSize: groups){
			String []groupSizeSplitted=groupSize.split("-");
			long min=translateToBytes(groupSizeSplitted[0]);
			long max=translateToBytes(groupSizeSplitted[1]);

			Pair<String,Integer> pair=new Pair<String, Integer>(groupSize, 0);
			ArrayList<Pair<Long,Integer>> toRemove=new ArrayList<Pair<Long,Integer>>();
			for(Pair<Long,Integer> translatedPair:translatedListaGlobalterminos){
				long groupValue=translatedPair.getFirst();
				if(groupValue>=min && groupValue<max){
					pair.setSecond(pair.getSecond()+translatedPair.getSecond());
					toRemove.add(translatedPair);
				}

			}
			logger.debug("Numero de doc para el termino:"+pair.getFirst()+" "+pair.getSecond());
			formattedListaGlobalterminos.add(pair);
			translatedListaGlobalterminos.removeAll(toRemove);
		}


		return formattedListaGlobalterminos;
	}

	private long translateToBytes(String strSize) {
		long bytes=0;

		strSize=strSize.trim().toUpperCase();

		if(strSize.endsWith("MAX")){
			return Long.MAX_VALUE;
		}
		if(strSize.endsWith("MIN")){
			return Long.MIN_VALUE;
		}

		int endIndex=strSize.length();
		if(strSize.endsWith("M") ||strSize.endsWith("K") || strSize.endsWith("G")){
			endIndex=strSize.length()-1;
		}
		bytes=Long.parseLong(strSize.substring(0, endIndex));

		if(strSize.endsWith("K")){
			bytes=bytes*1024;
		}
		else if(strSize.endsWith("M")){
			bytes=bytes*1024*1024;
		}
		else if(strSize.endsWith("G")){
			bytes=bytes*1024*1024*1024;
		}

		return bytes;
	}

	/**
	 * Dado un valor para un campo del modelo, necesitamos tokenizar ese valor.
	 * Solo lo haremos para unos determinados valores que ya sabemos que se tokenizan (inicialmente los valores para los campos
	 * creator y modifier)
	 * 
	 * El valor para el filtro en esos campos inicialmente no está dado como token, si no que es la palabra completa.
	 * 
	 * Si se deja así el sistema se encargará de tokenizarlo para la búsqueda.
	 * Esta tokenización va en función del locale vigente para cada ejecución (determinado por el webclient o la configuración global).
	 * 
	 * Los campos modificador y creador son tokenizados también, sin embargo, si fueron creados con otra configuración de idioma
	 * se habrán tokenizado de una manera distinta y esto puede ocasionar a que no se encuentre nada con los valores dados.
	 * 
	 * Así, aplicaremos una falsa tokenización guiada por los tokens ya existentes, haciendo corresponder al valor
	 * el token que encaje al principio de la cadena y que tenga mayor longitud (en caso de haber varios).
	 * 
	 * Esto solo solucionará el problema siguiente:
	 * Valor tokenizado a una palabra (raíz) más pequeña que él valor inicial. (token resultante más pequeño que valor al indexar)
	 * Posteriomrnete en cada ejecución de la búsqueda,
	 * la tokenización con el locale vigente arroja como resultado toda la palabra. (token resultante igual al valor en búsqueda)
	 * 
	 * En el caso de suceder lo contrario, es decir, inicialmente una tokenización en la que el mismo valor es el token (token resultante igual al valor al principio),
	 * pero posteriormente en la búsqueda la tokenización hace que el token sea más pequeño que el valor (token resultante más pequeño que el valor en la búsqueda),
	 * hará que no se encuentren resultados.
	 *
	 * @param valor el/la valor
	 * @param listaGlobalTerminos el/la lista global terminos
	 * @return el/la string
	 */
	private String fakeTokenizeValue(String valor, List<Pair<String,Integer>>  listaGlobalTerminos) {
		String token="";
		for(Pair<String,Integer> par:listaGlobalTerminos){
			if(valor.startsWith(par.getFirst())){
				if(par.getFirst().length() > token.length())
					token=par.getFirst();
			}
		}
		return token;
	}

	/**
	 * Realiza la búsqueda.
	 * Para cada uno de los términos del campo by realiza una búsqueda de lucene aplicando los filtros dados.
	 * El resultado puede ser filtrado manualmente (según exactMatchFilter) recorriendo el resultset si existen filtros manuales
	 * 
	 * En el caso de estar agrupando por creator o modificator, es posible que el resultado para cada término
	 * abarque varios valores (ya que estos campos están tokenizados y se agrupará por el token). Si precissionMode
	 * está activado se intentará realizar agrupaciones más pequeñas por valores exactos recorriendo el resultset
	 * y comprobando los valores manualmente (puede tardar mucho en función del número de elementos que arroje
	 * el resultado de la búsqueda aplicando los posibles filtros)
	 *
	 * @param by El campo por el que agrupar
	 * @param luceneFilter El filtro lucene para consultar cada uno de los términos
	 * @param exactMatchFilter Estructura con los filtros manuales
	 * @param precissionMode el/la precission mode
	 * @return Lista de parejas de valor/cuenta para las ocurrencias de los términos para el campo dado por by
	 */
	private ArrayList<String[]> search(String by, String luceneFilter, ArrayList<Pair<String, String>> exactMatchFilter, boolean precissionMode) {

		ArrayList<String[]> resultFiltered = new ArrayList<String[]>();

		boolean resolvePreffix=by.equals("type") || by.equals("aspect");

		String [] values=searchBy.get(by);

		List<Pair<String,Integer>> listaGlobalTerminos=getListaGlobalTerminos(by);

		/* Para cada término existente en el índice, recuperamos la cantidad real que arroja la consulta con los filtros pertinentes.
		 * En el caso de las agrupaciones por tamaño, los términos los hemos compuesto artificialmente agregando, por cada grupo de tamaño especificado
		 * en el properties, los grupos del índice que encajaban en los intervalos. Los valores artificiales distan mucho de las consultas reales,
		 * por lo que concretamente en este caso se podría omitir.
		 */

		for(Pair<String,Integer> par:listaGlobalTerminos){
			String query=String.format(values[1], formatValue(by,par.getFirst()))+" "+luceneFilter;

			logger.debug(String.format("consulta ejecutada: %s",query));

			SearchParameters sp = new SearchParameters();
			sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			sp.setLanguage(SearchService.LANGUAGE_LUCENE);
			sp.setQuery(query);

			ResultSet resultSet = searchService.query(sp);
			int cuenta=0;


			Iterator<ResultSetRow> it=resultSet.iterator();


			boolean precissionModeApplied=false;
			/* En el modo precissionMode intenta desglosar los términos por los que agrupamos cuya definición en el modelo
			 * indica que son tokenizados.
			 * Inicialmente los términos de los que disponemos son tokens, no los valores completos, por lo que
			 * quizá el término agrupe varios valores que nos interesa desglosar. Por ejemplo, usuarios psalido y 
			 * psalidito, que aparecen como un sólo término "psal", pero nos interesa que sean dos términos.
			 * 
			 * ¿Cómo hacemos esto? No nos queda más remedio que recorrer manualmente el resultado de la consulta y
			 * ver si el valor del término por el que agrupamos coincide con el valor del campo en todos nodos
			 * devueltos por la búsqueda. En caso de no coincidir, vamos contando las distintas ocurrencias
			 * de valores y los contaremos como términos independientes.
			 * Esto puede prolongar mucho el tiempo de consulta y sobrecargar el servidor.
			 * 
			 * El procedimiento es similar al uso de exactMatch filter, solo que en este caso en lugar de recorrer
			 * y comprobar campos por los que filtramos, lo hacemos por el campo que agrupamos (término).
			 * 
			 * Por ahora si usamos el "precissionMode" no vamos a permitir el uso de exactMatch, aunque
			 * se podría hacer en un futuro combinando las dos comprobaciones manuales.
			 * 
			 */ 
			if(precissionMode && (by.equals("creator") || by.equals("modifier"))){
				Hashtable<String,Integer> distinctValues=new Hashtable<String,Integer>();
				while(it.hasNext()){
					ResultSetRow row=it.next();
					try{
						Serializable valorPropiedad=row.getValues().get(values[0].substring(1));
						if(!valorPropiedad.equals(par.getFirst())){
							Integer currentNumOccurrences=distinctValues.get(valorPropiedad);
							int newNumOcurrences=(currentNumOccurrences!=null?currentNumOccurrences+1:1);
							distinctValues.put(valorPropiedad.toString(), newNumOcurrences);
						}
						logger.debug(String.format("Valor real de lo buscado por by en el result set para el termino: %s",par.getFirst()));
					}catch(Exception e){
						logger.warn("Problema consultando la propiedad de un nodo con precission mode (report dashlet)", e);
					}
				}

				if(distinctValues.keySet().size()>0){
					precissionModeApplied=true;
					logger.debug(String.format("Resultados para %s. Total desnaturalizado: %s, Total Consulta Filtrada: %s",par.getFirst(),par.getSecond(),resultSet.length()));
					for(String value:distinctValues.keySet()){

						logger.debug(String.format("\t Desglosados para %s desglosados en: %s, cuenta :%s",par.getFirst(),value,distinctValues.get(value)));
						addComponent(value,distinctValues.get(value),resultFiltered, resolvePreffix);
					}

				}else{
					cuenta=resultSet.length();
				}


			}else{

				/* Si existen filtros exactMatch (ver descripción del método fakeTokenizeValue), se aplican recorriendo
				 * el resultset intentando ver cuando el valor de la propiedad dada coincide con el valor de la propiedad esperado
				 * (exact match). 
				 * Esto lo hacemos para refinar los resultados al filtrar por un campo tokenizado y cuando dos valores o más valores
				 * de la propiedad tokenizan al mismo token,
				 * (por la naturaleza de lucene no podemos distinguir cuando la propiedad tiene asignado un valor u otro, solo 
				 * el token).
				 * 
				 */
				if(exactMatchFilter.size()>0){

					it=resultSet.iterator();
					while(it.hasNext()){

						ResultSetRow row=it.next();
						boolean fullMatch=true;
						for(Pair<String,String> exactMatchFilterComponent:exactMatchFilter){

							try{
								String propertyKey=searchBy.get(exactMatchFilterComponent.getFirst())[0];

								if(exactMatchFilterComponent.getSecond().equals(row.getValues().get(propertyKey.substring(1)))){
									fullMatch=fullMatch && true;
								}else{
									fullMatch=fullMatch && false;
									break;
								}
							}catch(Exception e){
								fullMatch=false;
								logger.warn("Problema consultando la propiedad de un nodo con el filtrado manual por match (report dashlet)", e);
							}
						}
						if(fullMatch){
							cuenta++;
						}
					}
				}else{
					cuenta=resultSet.length();
				}
			}
			if(!precissionModeApplied){
				logger.debug(String.format("Resultados para %s. Total desnaturalizado: %s, Total Consulta Filtrada: %s, Total Filtro Manual: %s",par.getFirst(),par.getSecond(),resultSet.length(),cuenta));
				addComponent(par.getFirst(),cuenta,resultFiltered, resolvePreffix);
			}
		}
		return resultFiltered;
	}

	private String formatValue(String by, String value) {
		if(by.equals("size")){
			String []groupSizeSplitted=value.split("-");
			long min=translateToBytes(groupSizeSplitted[0]);
			long max=translateToBytes(groupSizeSplitted[1]);
			if(min!=0){
				min++;
			}
			return "["+min+" TO "+max+"]";
		}
		return value;

	}

	/**
	 * Añade una pareja valor/cuenta al resultado de la búsqueda.
	 * resultFiltered es una lista de parejas de String ([])
	 * 
	 * La primera cadena de la pareja es uno de los valores por el que
	 * hemos agrupado.
	 * 
	 * La segunda cadena es la representación decimal del número total de ocurrencias.
	 * 
	 * si resolvePreffix es true se intentará obtener una representación compacta del
	 * valor por el que agrupamos (caso por ejemplo de ser este un QName, cuando
	 * agrupamos por aspectos o tipos)
	 *
	 * @param value el/la value
	 * @param cuenta el/la cuenta
	 * @param resultFiltered el/la result filtered
	 * @param resolvePreffix el/la resolve preffix
	 */
	private void addComponent(String value, int cuenta,	ArrayList<String[]> resultFiltered,boolean resolvePreffix) {
		if(cuenta>0){
			String [] component = new String [2];
			if(resolvePreffix){
				component[0]=resolvePreffix(value);
			}else{
				component[0]=value;	
			}

			component[1]=Integer.toString(cuenta);

			resultFiltered.add(component);
		}


	}


	/**
	 * Resuelve el prefijo abreviado de un Qname dado por la cadena
	 * qNameValue
	 *
	 * @param qNamevalue el/la q namevalue
	 * @return el/la string
	 */
	private String resolvePreffix(String qNamevalue) {
		QName qname=QName.createQName(qNamevalue);
		return resolvePreffix(qname);
	}


	/**
	 * Resuelve el prefijo abreviado de un Qname dado 
	 * 
	 * @param qname el/la qname
	 * @return el/la string
	 */
	private String resolvePreffix(QName qname) {
		QName myQNameResolved = qname.getPrefixedQName(namespaceService);
		String prefixLocal[] = QName.splitPrefixedQName(myQNameResolved.toPrefixString());
		return prefixLocal[0]+":"+qname.getLocalName();
	}

	/**
	 * Nos ayuda a renderizar los errores usando un ftl personalizado como en el DeclaredWebscript cuando se especifica redirect=true.
	 * 
	 * Es invocado en el catch global que se encarga de coger todas las Webscriptexceptions (y otras excepciones) y
	 * toma el status code y el mensaje para la respuesta pasarselo al modelo.
	 * 
	 * Aunque se le pasa también la excepción, por ahora no se tiene en cuenta en la plantilla ftl que usamos
	 * (formato text, indicado explícitamente)
	 *
	 * @param req el/la req
	 * @param res el/la res
	 * @param wex el/la wex
	 * @throws IOException Señala que ha ocurrido una excepción de E/S
	 */
	private void renderErrorResponse(WebScriptRequest req, WebScriptResponse res,WebScriptException wex) throws IOException{
		Status status = new Status();
		Cache cache = new Cache(getDescription().getRequiredCache());
		Map<String, Object> model = new HashMap<String, Object>(8, 1.0f);

		status.setCode(wex.getStatus(),wex.getMessage());
		status.setException(wex.getCause());
		status.setRedirect(true);

		model.put("status", status);
		model.put("cache", cache);

		String format="text";
		Map<String, Object> templateModel = createTemplateParameters(req, res, model);

		sendStatus(req, res, status, cache, format, templateModel);



	}

	/**
	 * Convert path to lucene path.
	 *
	 * @param targetPath el/la target path
	 * @param nodeService el/la node service
	 * @return el/la string
	 * @throws FileNotFoundException la file not found exception
	 */
	private String convertPathToLucenePath(final String targetPath,NodeService nodeService)
			throws  FileNotFoundException
			{

		NodeRef result          = null;
		NodeRef companyHome     = repository.getCompanyHome();
		String companyHomeName = nodeService.getProperty(companyHome, QName.createQName("http://www.alfresco.org/model/content/1.0", "name")).toString();
		String companyHomePath = "/" + companyHomeName;


		String  cleanTargetPath = targetPath.replaceAll("/+", "/");

		if (cleanTargetPath.startsWith(companyHomePath))
		{
			cleanTargetPath = cleanTargetPath.substring(companyHomePath.length());
		}

		if (cleanTargetPath.startsWith("/"))
		{
			cleanTargetPath = cleanTargetPath.substring(1);
		}

		if (cleanTargetPath.endsWith("/"))
		{
			cleanTargetPath = cleanTargetPath.substring(0, cleanTargetPath.length() - 1);
		}

		if (cleanTargetPath.length() == 0)
		{
			result = companyHome;
		}
		else
		{
			result = fileFolderService.resolveNamePath(companyHome, Arrays.asList(cleanTargetPath.split("/"))).getNodeRef();
		}

		List<ChildAssociationRef> parentAssocs = nodeService.getParentAssocs(result);
		String lucenePath="";
		while( parentAssocs.size() >= 1){
			ChildAssociationRef childAssocRef=parentAssocs.get(0);
			String componentPath=resolvePreffix(childAssocRef.getQName());
			int indexOfColon=componentPath.indexOf(":");
			String preffix=componentPath.substring(0,indexOfColon);
			String name=componentPath.substring(indexOfColon+1);
			String componentPathEncoded=preffix+":"+ISO9075.encode(name);
			lucenePath="/"+componentPathEncoded+lucenePath;
			parentAssocs = nodeService.getParentAssocs(childAssocRef.getParentRef());


		}
		logger.debug(String.format("PATH dada  : %s",targetPath));
		logger.debug(String.format("PATH lucene: %s",lucenePath));
		return(lucenePath+"//*");

			}


	/**
	 * Legacy transform date results to sparse.
	 *
	 * @param result the result
	 * @return the jSON object
	 */
	public JSONObject transformDateResultsToSparseGRaphaelFormat(ArrayList<String[]> result){


		String [][] resultArray=result.toArray(new String [1][]);

		/* Ordena los resultados de manera creciente según la fecha (año-mes-dia)*/
		Arrays.sort(resultArray,new Comparator<String[]>(){
			@Override
			public int compare(String[] el1, String[] el2) {
				return el1[0].compareTo(el2[0]);
			}
		});


		HashSet<String> yearsSet=new HashSet<String>();

		if(resultArray.length>0 && resultArray[0]!=null){
			for(String [] component:resultArray){
				String [] date=component[0].split("-");
				yearsSet.add(date[0]);


			}
		}
		/*Creamos la matriz "dispersa" datainfo, donde 
		 * el primer índice son los años y el segundo índice son los meses.
		 * El valor es la cuenta de documentos de ese año/mes
		 * Aprovechamos para obtener el Set de años en los que algunos de sus meses tengan contenido.
		 */
		int yearIndex=0;
		int[][] dataInfo = new int[yearsSet.size()][12];

		String [] date=null;
		if(resultArray.length>0 && resultArray[0]!=null){
			date=resultArray[0][0].split("-");
			String currentYear=date[0];
			yearsSet.add(date[0]);



			for(String [] component:resultArray){
				date=component[0].split("-");
				if(!date[0].equals(currentYear) ){ 
					currentYear=date[0];
					yearsSet.add(currentYear);
					yearIndex++;
				}

				int mes=Integer.parseInt(date[1])-1;

				dataInfo[yearIndex][mes] = Integer.parseInt(component[1]);

			}
		}


		/*En datainfo tenemos ahora la matriz que representa para todos los años y meses, el número de documentos*/
		ArrayList<Integer> xYears = new ArrayList<Integer>();
		ArrayList<Integer> yMonths = new ArrayList<Integer>();
		ArrayList<Integer> data = new ArrayList<Integer>();

		/* Creamos las etiquetas*/
		ArrayList<String> xLabelArrayList=new ArrayList<String>();
		for(String year:yearsSet){
			xLabelArrayList.add(year);

		}
		String [] xLabel=xLabelArrayList.toArray(new String [1]);
		Arrays.sort(xLabel);
		//TODO: localizar, pero mejor en el cliente
		String [] yLabel=new String [] {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};

		/* "Linearizamos" la matriz dataInfo a 3 arrays unidimensionales, con los valores de los índices y los datos representados*/ 
		for(yearIndex=0;yearIndex<dataInfo.length;yearIndex++){
			int [] monthData=dataInfo[yearIndex];
			for(int monthIndex=0; monthIndex<monthData.length;monthIndex++){
				xYears.add(yearIndex);
				yMonths.add(monthIndex);
				data.add(monthData[monthIndex]);
			}

		}

		JSONObject gRaphaelMonthYearResult = new JSONObject();
		try {
			gRaphaelMonthYearResult.put("data", data);
			gRaphaelMonthYearResult.put("xYear", xYears);
			gRaphaelMonthYearResult.put("yMonth", yMonths);
			gRaphaelMonthYearResult.put("xLabel", xLabel);
			gRaphaelMonthYearResult.put("yLabel", yLabel);

		} catch (JSONException e) {
			throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,"Error añadiendo datos al resultado JSON");
		}

		return  gRaphaelMonthYearResult;

	}


	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		applicationContext = arg0;

	}

	/**
	 * Inicializa
	 */
	private void init() {
		/*No nos sirve configurar esto como método init del bean de spring, ya cuando está configurado lucene, 
		 * instancia el bean (e invoca a init) antes que de la inicialización del subsistema cosas del subsistema.
		 * Como en el init del bean se llama beans del subsistema que aún no han sido inicializados, 
		 * hace que falle la inicialización de alfresco.
		 * */

		//TODO, preparar también la inicialización para contexto de alfresco 3.4.X
		SwitchableApplicationContextFactory sw=(SwitchableApplicationContextFactory) applicationContext.getBean("Search");
		Object tmp=sw.getApplicationContext().getBean("search.admLuceneIndexerAndSearcherFactory");
		if(tmp instanceof org.alfresco.repo.search.impl.lucene.ADMLuceneIndexerAndSearcherFactory ){
			admLuceneIndexerAndSearcherFactory=(org.alfresco.repo.search.impl.lucene.ADMLuceneIndexerAndSearcherFactory)tmp;	
		}

		initializationCompleted=true;


	}


	/**
	 *  Class CustomSolrSearcher.
	 */
	private static class CustomSolrSearcher{
		private HttpClient httpClient;

		private static CustomSolrSearcher customSolrSearcherInstance=null;
		private CustomSolrSearcher(){
			HttpClientFactory httpClientFactory=(HttpClientFactory) applicationContext.getBean("solrHttpClientFactory");

			httpClient = httpClientFactory.getHttpClient();

			httpClient = httpClientFactory.getHttpClient();
			HttpClientParams params = httpClient.getParams();

			params.setBooleanParameter(HttpClientParams.PREEMPTIVE_AUTHENTICATION, true);

			httpClient.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), new UsernamePasswordCredentials("admin", "admin"));

		}

		public static CustomSolrSearcher getInstance(){
			if(customSolrSearcherInstance==null){
				customSolrSearcherInstance=new CustomSolrSearcher();
			}
			//return customSolrSearcherInstance;
			return new CustomSolrSearcher();
		}

		public List<Pair<String, Integer>> getTopTerms(String field,	int limitTopTerm) {
			String url="/solr/alfresco/select/?q={0}&start=0&rows=0&indent=on&facet=on&facet.field={1}&facet.limit={2}";
			URLCodec encoder = new URLCodec();
			ArrayList<Pair<String, Integer>> termList=new  ArrayList<Pair<String, Integer>>();
			try{
				Object [] obj={"*:*",encoder.encode(field, "UTF-8"),limitTopTerm};

				PostMethod post = new PostMethod(MessageFormat.format(url.toString(),obj));
				System.err.println(post.getURI());
				httpClient.executeMethod(post);

				if(post.getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY || post.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY)
				{
					Header locationHeader = post.getResponseHeader("location");
					if (locationHeader != null)
					{
						String redirectLocation = locationHeader.getValue();
						post.setURI(new URI(redirectLocation, true));
						httpClient.executeMethod(post);
					}
				}

				if (post.getStatusCode() != HttpServletResponse.SC_OK)
				{
					throw new LuceneQueryParserException("Request failed " + post.getStatusCode() + " " + post.getURI().toString());
				}
				//System.err.println("respuesta solr:"+post.getResponseBodyAsString());

				Reader reader = new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream()));

				// TODO - replace with streaming-based solution e.g. SimpleJSON ContentHandler
				JSONObject json = new JSONObject(new JSONTokener(reader));

				//System.err.println("json:"+json.toString());


				JSONObject facetCounts=json.getJSONObject("facet_counts");
				if(facetCounts!=null){
					JSONObject facetFields=facetCounts.getJSONObject("facet_fields");
					if(facetFields!=null){

						System.err.println(facetFields);
						JSONArray jsonArrayFacetFields = facetFields.getJSONArray(field);
						for(int i=0; i< jsonArrayFacetFields.length();i+=2){
							String term=(String)jsonArrayFacetFields.get(i);
							int count=(Integer)jsonArrayFacetFields.get(i+1);
							termList.add(new Pair<String,Integer>(term,count));

						}

					}
				}


			} catch(UnsupportedEncodingException e){
				e.printStackTrace();
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			return termList;
		}

	}

}
