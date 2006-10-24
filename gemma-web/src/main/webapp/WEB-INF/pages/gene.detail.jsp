<%@ include file="/common/taglibs.jsp"%>
<jsp:useBean id="gene" scope="request"
    class="ubic.gemma.model.genome.GeneImpl" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <body>
        <h2>
            <fmt:message key="gene.details" />
        </h2>
        <table width="100%" cellspacing="10">
            <tr>
                <td valign="top">
                    <b>
                        <fmt:message key="gene.name" />
                    </b>
                </td>
                <td>
                	<%if (gene.getName() != null){%>
                    	<jsp:getProperty name="gene" property="name" />
                    <%}else{
                    	out.print("No name available");
                    }%>
                </td>
            </tr>
            
            <tr>
                <td valign="top">
                    <b>
                        <fmt:message key="gene.officialSymbol" />
                    </b>
                </td>
                <td>
                	<%if (gene.getOfficialSymbol() != null){%>
                    	<jsp:getProperty name="gene" property="officialSymbol" />
                    <%}else{
                    	out.print("No official symbol available");
                    }%>
                </td>
            </tr>
            
            <tr>
                <td valign="top">
                    <b>
                        <fmt:message key="gene.ncbi" />
                    </b>
                </td>
                <td>

                	<%if (gene.getNcbiId() != null){%>
                    	<jsp:getProperty name="gene" property="ncbiId" />
                    	<a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=full_report&list_uids=<%out.print(gene.getNcbiId()); %>">
                    	(NCBI link)</a>
                    <%}else{
                    	out.print("No NCBI ID available");
                    }%>
                </td>
            </tr>
        
            <tr>
                <td valign="top">
                    <b>
                        <fmt:message key="gene.description" />
                    </b>
                </td>
                <td>
                	<%if (gene.getDescription() != null){%>
                    <textarea name="" rows=5 cols=80 readonly=true><jsp:getProperty name="gene" property="description" /></textarea><
                    <%}else{
                    	out.print("Description unavailable");
                    }%>
                </td>
            </tr>
            
            <tr>
                <td valign="top">
                    <b>
                        <fmt:message key="gene.taxon" />
                    </b>
                </td>
                <td>
                	<%if (gene.getTaxon() != null) {
                    	out.print(gene.getTaxon().getScientificName());
                	} else{
                    	out.print("Taxon unavailable");
                    }%>
                </td>
            </tr>
         </table>
 
         <%
		if ( gene.getAliases().size() > 0 ) {
		%>
         <h3>
			<fmt:message key="gene.aliases" />
		</h3>
		<% 
		} 
		%>
		        
         <display:table name="gene.aliases" class="list" requestURI="" id="aliasList" 
			pagesize="10">
			<display:column property="alias" sortable="true" maxWords="20"/>
			<display:setProperty name="basic.empty.showtable" value="false" />
		</display:table> 
		
        <%
		if ( gene.getAccessions().size() > 0 ) {
		%>
         <h3>
			<fmt:message key="gene.accessions" />
		</h3>
		<% 
		} 
		%>
		
         <display:table name="gene.accessions" class="list" requestURI="" id="accessionsList" 
			pagesize="10" decorator="ubic.gemma.web.taglib.displaytag.gene.GeneWrapper">
			<display:column property="accession" sortable="true" maxWords="20"/>
			<display:setProperty name="basic.empty.showtable" value="false" />
		</display:table>  
		
		<br />
		There are <b>
		<a href="/Gemma/gene/showCompositeSequences.html?id=<%out.print(gene.getId());%>"><c:out value="${compositeSequenceCount}"/> </a>
		</b>
		composite sequences associated with this gene.
		<br />

    </body>
</html>
