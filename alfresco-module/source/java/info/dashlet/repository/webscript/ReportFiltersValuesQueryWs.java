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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;


/**
 * Class ReportFiltersValuesQueryWs.
 */
public class ReportFiltersValuesQueryWs extends AbstractWebScript{

	/** La Constante logger. */
	private static final Logger logger = Logger.getLogger(ReportFiltersValuesQueryWs.class);

	/** El mimetype service. */
	private MimetypeService mimetypeService;
	
	/** El securized search service. */
	private SearchService securizedSearchService;
	
	/** El person service. */
	private PersonService personService;
	
	/** El node service. */
	private NodeService nodeService;
	
	/** El dictionary service. */
	private DictionaryService dictionaryService;
	
	/** El namespace service. */
	private NamespaceService namespaceService;

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
	 * Obtiene person service.
	 *
	 * @return el person service
	 */
	public PersonService getPersonService() {
		return personService;
	}
	
	/**
	 * Establece el valor de person service.
	 *
	 * @param personService el nuevo person service
	 */
	public void setPersonService(PersonService personService) {
		this.personService = personService;
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
	 * Obtiene dictionary service.
	 *
	 * @return el dictionary service
	 */
	public DictionaryService getDictionaryService() {
		return dictionaryService;
	}
	
	/**
	 * Establece el valor de dictionary service.
	 *
	 * @param dictionaryService el nuevo dictionary service
	 */
	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
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
	 * Establece el valor de mimetype service.
	 *
	 * @param mimetypeService el nuevo mimetype service
	 */
	public void setMimetypeService(MimetypeService mimetypeService) {
		this.mimetypeService = mimetypeService;
	}
	
	/**
	 * Obtiene mimetype service.
	 *
	 * @return el mimetype service
	 */
	public MimetypeService getMimetypeService() {
		return mimetypeService;
	}


	/* (non-Javadoc)
	 * @see org.springframework.extensions.webscripts.WebScript#execute(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.WebScriptResponse)
	 */
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res)
			throws IOException {

		res.setContentEncoding("UTF-8");
		String byType=req.getServiceMatch().getTemplateVars().get("bytype");
		if(byType==null || byType.trim().equals("")){
			throw new WebScriptException(Status.STATUS_BAD_REQUEST,"No se ha especificado correctamente la URL");
		}
		String q=req.getParameter("term");

		if(q==null || q.trim().equals("")){
			throw new WebScriptException(Status.STATUS_BAD_REQUEST,"No se ha especificado el parámetro \"term\" con la consulta");
		}

		//Devuelve una lista de los mimetypes del sistema (filtra por q)
		if(byType.trim().equalsIgnoreCase("mimetype")){
			List <String> mimeTypes=mimetypeService.getMimetypes();
			ArrayList<String> filteredMimeTypes=new ArrayList<String>();

			for(String mimeType: mimeTypes){
				if(mimeType.toLowerCase().contains(q.toLowerCase())){
					filteredMimeTypes.add(mimeType);

				}
			}
			JSONArray result=new JSONArray(filteredMimeTypes);

			res.getWriter().append(result.toString());
		//Devuelve una lista de los usuarios del sistema (filtra por q)
		}else if(byType.trim().equalsIgnoreCase("user")){
			//TODO: reemplazar por la llamada getPeople(args...)
			Set<NodeRef> allPeople= personService.getAllPeople();
			ArrayList<String> filteredUserNames=new ArrayList<String>();

			Iterator<NodeRef> it=allPeople.iterator();

			while(it.hasNext()){
				NodeRef people=it.next();
				String userName=nodeService.getProperty(people,QName.createQName("http://www.alfresco.org/model/content/1.0", "userName") ).toString();
				if(userName.toLowerCase().contains(q.toLowerCase())){
					filteredUserNames.add(userName);
				}
			}

			JSONArray result=new JSONArray(filteredUserNames);
			res.getWriter().append(result.toString());
		//Devuelve una lista de los tipos del sistema (filtra por q)
		}else if(byType.trim().equalsIgnoreCase("type")){
			ArrayList<String> filteredAbreviatedTypeNames=new ArrayList<String>();

			Collection<QName> allTypes = dictionaryService.getAllTypes();
			Iterator<QName> it = allTypes.iterator();
			while(it.hasNext()){
				QName type=it.next();

				QName myQNameResolved = type.getPrefixedQName(namespaceService);
				String prefixLocal[] = QName.splitPrefixedQName(myQNameResolved.toPrefixString());
				String abreviatedTypeName= prefixLocal[0]+":"+type.getLocalName();

				if(abreviatedTypeName.toLowerCase().contains(q.toLowerCase())){

					filteredAbreviatedTypeNames.add(abreviatedTypeName);
				}

			}


			JSONArray result=new JSONArray(filteredAbreviatedTypeNames);
			res.getWriter().append(result.toString());
		//Devuelve una lista de los aspectos del sistema (filtra por q)	
		}else if(byType.trim().equalsIgnoreCase("aspect")){
			ArrayList<String> filteredAbreviatedAspectNames=new ArrayList<String>();
			Collection<QName> allAspects = dictionaryService.getAllAspects();
			Iterator<QName> it = allAspects.iterator();
			while(it.hasNext()){
				QName aspect=it.next();
				QName myQNameResolved = aspect.getPrefixedQName(namespaceService);
				String prefixLocal[] = QName.splitPrefixedQName(myQNameResolved.toPrefixString());
				String abreviatedAspectName= prefixLocal[0]+":"+aspect.getLocalName();
				if(abreviatedAspectName.toLowerCase().contains(q.toLowerCase())){
					filteredAbreviatedAspectNames.add(abreviatedAspectName);
				}
			}
			JSONArray result=new JSONArray(filteredAbreviatedAspectNames);
			res.getWriter().append(result.toString());

		//devuelve una lista de las rutas en formato amigable de los elementos cuyo nombre comience por 
		// el valor de q
		}else if(byType.trim().equalsIgnoreCase("path")){
			String query="PATH: \"/app:company_home//*\" AND @cm\\:name:\""+q+"*\" AND TYPE:\\{http\\://www.alfresco.org/model/content/1.0\\}folder";
			ResultSet resultSet =securizedSearchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_LUCENE, query);
			ArrayList<String> filteredPathNames=new ArrayList<String>();
			
			Iterator<ResultSetRow> it = resultSet.iterator();
			while(it.hasNext()){
				ResultSetRow row = it.next();
				String path=resolvePath(row.getNodeRef(),nodeService);
				filteredPathNames.add(path);
			}

			JSONArray result=new JSONArray(filteredPathNames);
			res.getWriter().append(result.toString());
		}

	}


	/**
	 * A partir de un noderef, devuelve el path de éste de una manera human-readable (tiene en cuenta
	 * el locale usado)
	 *
	 * @param nodeRef la referencia al nodo
	 * @param nodeService el node service
	 * @return el path human-readable
	 */
	private String resolvePath(NodeRef nodeRef, NodeService nodeService) {
		String nodeName=nodeService.getProperty(nodeRef, QName.createQName("http://www.alfresco.org/model/content/1.0", "name")).toString();
		List<ChildAssociationRef> childAssocs = nodeService.getParentAssocs(nodeRef);
		if(childAssocs.size()<1){
			return "";
		}else{
			ChildAssociationRef childAssoc = childAssocs.get(0);
			NodeRef parentRef = childAssoc.getParentRef();
			return resolvePath(parentRef, nodeService)+"/"+nodeName;
		}


	}
}