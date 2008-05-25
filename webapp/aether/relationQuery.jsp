<%@ taglib uri="http://www.makumba.org/view-hql" prefix="mak" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<h2>Create new query</h2>
<mak:newForm type="RelationQuery" action="relationQuery.jsp">
Description: <mak:input field="description" cols="100"/><br><br>
Query: <mak:input field="query" cols="100" rows="5"/><br>
Arguments (comma-separated): <mak:input field="arguments"/> (if none, fromURL is default)<br>
Supported arguments: fromURL, rowName, previousURL<br>
<br>
<input type="submit" value="Save"/>
</mak:newForm>
<br>

<h2>Existing queries</h2>
<mak:list from="RelationQuery rq">
<h3><mak:value expr="rq.description"/></h3>
<mak:value expr="rq.query"/> <br>
Arguments: <mak:value expr="rq.arguments"/><br>
<a href="relationQueryEdit.jsp?relationQuery=<mak:value expr="rq.id"/>">[Edit]</a>&nbsp;<mak:deleteLink object="rq" action="relationQuery.jsp">[Trash]</mak:deleteLink><br>
</mak:list>