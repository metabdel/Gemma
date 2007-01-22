<%@ include file="/common/taglibs.jsp"%>
<jsp:useBean id="compositeSequence" scope="request"
    class="ubic.gemma.model.expression.designElement.CompositeSequenceImpl" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
  <title> <fmt:message key="compositeSequence.title" /> </title>
  
  <table id="csTableList">
  <tr>
  <td>
        <h2>
            <fmt:message key="compositeSequence.title" />
        </h2>
        <table width="100%" >
            <tr>
                <td valign="top">
                    <b>
                        <fmt:message key="compositeSequence.name" />
                    </b>
                </td>
                <td>
                	<%if (compositeSequence.getName() != null){%>
                    	<jsp:getProperty name="compositeSequence" property="name" />
                    <%}else{
                    	out.print("No name available");
                    }%>
                </td>
            </tr>      
            <tr>
                <td valign="top">
                    <b>
                        <fmt:message key="compositeSequence.description" />
                    </b>
                </td>
                <td>
                	<%if (compositeSequence.getDescription() != null){%>
                    	<jsp:getProperty name="compositeSequence" property="description" />
                    <%}else{
                    	out.print("No description available");
                    }%>
                </td>
            </tr>  
            <tr>
                <td valign="top">
                    <b>
                    	Taxon
                    </b>
                </td>
                <td>
                	<%if (compositeSequence.getBiologicalCharacteristic().getTaxon() != null){%>
                    	${ compositeSequence.biologicalCharacteristic.taxon.scientificName}
                    <%}else{
                    	out.print("No taxon information available");
                    }%>
                </td>
            </tr>       
            <tr>
                <td valign="top">
                    <b>
                        Biosequence
                    </b>
                </td>
                <td>
                	<%if (compositeSequence.getBiologicalCharacteristic().getName() != null){%>
                		${compositeSequence.biologicalCharacteristic.name }
                    <%}else{
                    	out.print("No name available");
                    }%>
                </td>
            </tr>      
            <tr>
                <td valign="top">
                    <b>
                        Sequence length
                    </b>
                </td>
                <td>
                	<%if (compositeSequence.getBiologicalCharacteristic().getSequence() != null) {
                		out.print(compositeSequence.getBiologicalCharacteristic().getSequence().length());
                    }else{
                    	out.print("No sequence available");
                    }%>
                </td>
            </tr>  
            <tr>
                <td valign="top">
                    <b>
						Sequence
                    </b>
                </td>
                <td>
                	<%if (compositeSequence.getBiologicalCharacteristic().getSequence() != null ){
                		String sequence = compositeSequence.getBiologicalCharacteristic().getSequence();
                		String formattedSequence = "";
                		int nextIndex = 0;
                		for (int i = 0; i < sequence.length() - 80; i += 80) {
                		 	formattedSequence += sequence.substring(i,i+80);  
                		 	formattedSequence += "<br />";
                		 	nextIndex = i+80;
                		}
                		if ( (sequence.length() % 80) != 0) {
                		 	formattedSequence += sequence.substring(nextIndex, sequence.length());
                		 	formattedSequence += "<br />";
                		}
                	%>
                	<div class="clob">
                	<% 
                		out.print(formattedSequence);
                	%>
                	</div>
                    <%}else{
                    	out.print("No sequence available");
                    }%>
                </td>
            </tr>    
        </table>
 </td>
 </tr>
 <tr>
 <td>
		<display:table name="blatResults"  requestURI="" id="blatResult" style="width:100%;"
             pagesize="200"
             decorator="ubic.gemma.web.taglib.displaytag.expression.designElement.CompositeSequenceWrapper"
             class="scrollTable"
             >		 
			<display:column property="blatResult" sortable="true" title="Alignment" />
			<display:column property="blatScore" sortable="true" title="S" />		
			<display:column property="blatIdentity" sortable="true" title="I" />	
			<display:column property="geneProducts" title="GeneProducts" />
			<display:column property="genes" title="Genes" />	
            <display:setProperty name="basic.empty.showtable" value="true" />      
        </display:table>
</td>
</tr>
</table>		


    </body>
</html>
