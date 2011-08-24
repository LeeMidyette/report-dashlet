/*
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
*/

package info.dashlet.repository.webscript;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.model.Repository;
import org.alfresco.repo.search.impl.lucene.ADMLuceneIndexerAndSearcherFactory;
import org.alfresco.repo.search.impl.lucene.LuceneSearcher;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.alfresco.util.Pair;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;


/**
 * Class ReportStacksWs.
 */
public class ReportStacksWs extends AbstractWebScript {

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
	
	/** El securized search service. */
	private SearchService securizedSearchService;
	
	/** El file folder service. */
	private FileFolderService fileFolderService;
	
	/** El node service. */
	private NodeService nodeService;
	
	/** El namespace service. */
	private NamespaceService namespaceService;


	/**
	 * Obtiene adm lucene indexer and searcher factory.
	 *
	 * @return el adm lucene indexer and searcher factory
	 */
	public ADMLuceneIndexerAndSearcherFactory getAdmLuceneIndexerAndSearcherFactory() {
		return admLuceneIndexerAndSearcherFactory;
	}
	
	/**
	 * Establece el valor de adm lucene indexer and searcher factory.
	 *
	 * @param admLuceneIndexerAndSearcherFactory el nuevo adm lucene indexer and searcher factory
	 */
	public void setAdmLuceneIndexerAndSearcherFactory(
			ADMLuceneIndexerAndSearcherFactory admLuceneIndexerAndSearcherFactory) {
		this.admLuceneIndexerAndSearcherFactory = admLuceneIndexerAndSearcherFactory;
	}


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
	 * Obtiene securized search service.
	 *
	 * @return el securized search service
	 */
	public SearchService getSecurizedSearchService() {
		return securizedSearchService;
	}
	
	/**
	 * Establece el valor de securized search service.
	 *
	 * @param securizedSearchService el nuevo securized search service
	 */
	public void setSecurizedSearchService(SearchService securizedSearchService) {
		this.securizedSearchService = securizedSearchService;
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

	static{	
		searchBy.put("mimetype", new String [] {"@{http://www.alfresco.org/model/content/1.0}content.mimetype","@\\{http\\://www.alfresco.org/model/content/1.0\\}content.mimetype:\"%s\""});
		searchBy.put("created",  new String [] {"@{http://www.alfresco.org/model/content/1.0}created","@cm\\:created:%s"});
		searchBy.put("modified", new String [] {"@{http://www.alfresco.org/model/content/1.0}modified","@cm\\:modified:%s"});
		searchBy.put("creator",  new String [] {"@{http://www.alfresco.org/model/content/1.0}creator","@cm\\:creator:\"%s\""});
		searchBy.put("modifier", new String [] {"@{http://www.alfresco.org/model/content/1.0}modifier","@cm\\:modifier:\"%s\""});
		searchBy.put("aspect",   new String [] {"ASPECT","EXACTASPECT:\"%s\""});
		searchBy.put("type",     new String [] {"TYPE","EXACTTYPE:\"%s\""});



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

	}
	
	/* (non-Javadoc)
	 * @see org.springframework.extensions.webscripts.WebScript#execute(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.WebScriptResponse)
	 */
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res)
			throws IOException {

		res.setContentEncoding("UTF-8");
		try{
			String by=req.getParameter("by");
			if(by==null || by.trim().equals("") || searchBy.get(by)==null){
				throw new WebScriptException(Status.STATUS_BAD_REQUEST,"El parámetro by es incorrecto.");
			}
			logger.debug(String.format("Usando Parámetro by=%s", by));

			String filterJsonString=req.getParameter("filter");

			String manual_filter=req.getParameter("manual_filter");

			boolean precissionMode=new Boolean(req.getParameter("precission_mode"));


			Pair<String, ArrayList<Pair<String,String>>> filters=buildFiltersFromParams(filterJsonString);


			String luceneFilter=filters.getFirst()+" "+manual_filter;
			ArrayList<Pair<String, String>> manualFilter = filters.getSecond();
			ArrayList<String []> result=null;
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
			if((by.equals("created") || by.equals("modified")) && result.size()>0){


				String [][] resultArray=result.toArray(new String [1][]);

				/* Ordena los resultados de manera creciente según la fecha (año-mes-dia)*/
				Arrays.sort(resultArray,new Comparator<String[]>(){
					@Override
					public int compare(String[] el1, String[] el2) {
						return el1[0].compareTo(el2[0]);
					}
				});


				HashSet<String> yearsSet=new HashSet<String>();


				/* Buscamos en el array de resultados ordenados y obtenemos el conjunto de años de todas las fechas que tienen algún contenido*/
				for(String [] component:resultArray){
					String [] date=component[0].split("-");
					yearsSet.add(date[0]);


				}
				/*Creamos la matriz "dispersa" datainfo, donde 
				 * el primer índice son los años y el segundo índice son los meses.
				 * El valor indicado por los indices irá acumulando para cada día del correspondiente año-mes.
				 */
				String currentYear="";
				int yearIndex=0;
				int[][] dataInfo = new int[yearsSet.size()][12];

				ArrayList<String[]> totalizedResult=new ArrayList<String[]>(result.size());

				totalizedResult.ensureCapacity(result.size());
				totalizedResult.add(new String[0]);

				String currentYearMonth="";
				int indexYM=0;

				for(String [] component:resultArray){
					String [] date=component[0].split("-");
					if(currentYear.equals("")){
						currentYear=date[0];
					}
					if(!date[0].equals(currentYear) ){ currentYear=date[0]; yearIndex++;}

					int mes=Integer.parseInt(date[1])-1;
					int cantidad=Integer.parseInt(component[1]);
					dataInfo[yearIndex][mes] +=cantidad;

					/*	Aprovechamos para totalizar los documentos por año/mes en vez de por año/mes/dia para devolverlos
					 *  y poder pintar la gráfica de líneas mostrando los documentos en el eje del tiempo y también
					 *  los datos de la tabla*/
					String dateYearMonth=currentYear+"-"+date[1];
					if(currentYearMonth.equals("")){ currentYearMonth=dateYearMonth; indexYM=0;}

					if(!currentYearMonth.equals(dateYearMonth)){
						currentYearMonth=dateYearMonth;
						indexYM++;
						totalizedResult.add(new String[0]);
					}
					totalizedResult.set(indexYM,new String[]{dateYearMonth,new Integer(dataInfo[yearIndex][mes]).toString()});

				}


				result=totalizedResult;

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

				gRaphaelMonthYearResult=new JSONObject();
				try {
					gRaphaelMonthYearResult.put("data", data);
					gRaphaelMonthYearResult.put("xYear", xYears);
					gRaphaelMonthYearResult.put("yMonth", yMonths);
					gRaphaelMonthYearResult.put("xLabel", xLabel);
					gRaphaelMonthYearResult.put("yLabel", yLabel);

				} catch (JSONException e) {
					throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,"Error añadiendo datos al resultado JSON");
				}




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


			//JSONObject jsonObjectArray=new JSONObject();

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
		LuceneSearcher searcher=admLuceneIndexerAndSearcherFactory.getSearcher(rootNodeRef.getStoreRef(), true);

		List<Pair<String,Integer>> listaGlobalTerminos=searcher.getTopTerms(values[0], LIMIT_TOP_TERM);

		return listaGlobalTerminos;
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

		//Para cada término existente en el índice, recuperamos la cantidad real que arroja la consulta con los filtros pertinentes
		for(Pair<String,Integer> par:listaGlobalTerminos){
			String query=String.format(values[1], par.getFirst())+" "+luceneFilter;

			logger.debug(String.format("consulta ejecutada: %s",query));

			ResultSet resultSet = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_LUCENE, query);
			int cuenta=0;


			Iterator<ResultSetRow> it=resultSet.iterator();


			boolean precissionModeApplied=false;
			/*En el modo precissionMode intenta desglosar los términos por los que agrupamos cuya definición en el modelo
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
			if(precissionMode && by.equals("creator") || by.equals("modifier")){
				Hashtable<String,Integer> distinctValues=new Hashtable<String,Integer>();
				while(it.hasNext()){
					ResultSetRow row=it.next();
					Serializable valorPropiedad=row.getValues().get(values[0].substring(1));
					if(!valorPropiedad.equals(par.getFirst())){
						Integer currentNumOccurrences=distinctValues.get(valorPropiedad);
						int newNumOcurrences=(currentNumOccurrences!=null?currentNumOccurrences+1:1);
						distinctValues.put(valorPropiedad.toString(), newNumOcurrences);
					}
					logger.debug(String.format("Valor real de lo buscado por by en el result set para el termino: %s",par.getFirst()));
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

							String propertyKey=searchBy.get(exactMatchFilterComponent.getFirst())[0];

							if(exactMatchFilterComponent.getSecond().equals(row.getValues().get(propertyKey.substring(1)))){
								fullMatch=fullMatch && true;
							}else{
								fullMatch=fullMatch && false;
								break;
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

}
