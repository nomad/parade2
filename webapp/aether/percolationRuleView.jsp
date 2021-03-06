<%@ taglib uri="http://www.makumba.org/view-hql" prefix="mak" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<%-- Makumba Generator - START OF  *** OBJECT ***  PAGE FOR OBJECT PercolationRule --%>
<mak:object from="PercolationRule percolationRule" where="percolationRule.id=:percolationRule">
  <fieldset style="text-align:right;">
  <legend>PercolationRule <i><mak:value expr="percolationRule.subject" /></i></legend>
  <table>
  <%-- Makumba Generator - START OF NORMAL FIELDS --%>
    <tr>
      <th>subject</th>
      <td><mak:value expr="percolationRule.subject"/></td>
    </tr>
    <tr>
      <th>predicate</th>
      <td><mak:value expr="percolationRule.predicate"/></td>
    </tr>
    <tr>
      <th>object</th>
      <td><mak:value expr="percolationRule.object"/></td>
    </tr>
    <tr>
      <th>consumption</th>
      <td><mak:value expr="percolationRule.consumption"/></td>
    </tr>
    <tr>
      <th>propagation depth limit</th>
      <td><mak:value expr="percolationRule.propagationDepthLimit"/></td>
    </tr>
    <tr>
      <th>description</th>
      <td><mak:value expr="percolationRule.description"/></td>
    </tr>
    <tr>
      <th>active</th>
      <td><mak:value expr="percolationRule.active"/></td>
    </tr>
    <tr>
      <th><a href="/aether/relationQuery.jsp">relationQueries for next relations</a></th>
      <td><ul><mak:list from="IN(percolationRule.relationQueries) rq"><li><a href="relationQueryEdit.jsp?relationQuery=<mak:value expr="rq.id"/>"><mak:value expr="rq.description"/></a></li></mak:list></ul></td>
    </tr>
  </table>
</fieldset>
  <%-- Makumba Generator - END OF NORMAL FIELDS --%>

  <%-- Makumba Generator - START OF SETS --%>

  <%-- Makumba Generator - END OF SETS --%>

</table>
</fieldset>
<a href="percolationRuleList.jsp">Back to percolationRule list</a>
</mak:object>

<%-- Makumba Generator - END OF *** OBJECT ***  PAGE FOR OBJECT PercolationRule --%>
