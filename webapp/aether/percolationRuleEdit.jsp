<%@ taglib uri="http://www.makumba.org/view-hql" prefix="mak" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<%-- Makumba Generator - START OF  *** EDIT ***  PAGE FOR OBJECT PercolationRule --%>
<mak:object from="PercolationRule percolationRule" where="percolationRule.id=:percolationRule">
  <fieldset style="text-align:right;">
  <legend>Edit PercolationRule <i><mak:value expr="percolationRule.subject" /></i></legend>
    <jsp:include page="aetherTypes.jsp"/>
  
  <mak:editForm object="percolationRule" action="percolationRuleView.jsp" method="post">
    <table>
    <%-- Makumba Generator - START OF NORMAL FIELDS --%>
      <tr>
        <th><label for="subject">Su<span class="accessKey">b</span>ject</label></th>
        <td><mak:input field="subject" styleId="subject" accessKey="b" /></td>
      </tr>
      <tr>
        <th><label for="predicate">Pred<span class="accessKey">i</span>cate</label></th>
        <td><mak:input field="predicate" styleId="predicate" accessKey="i" /></td>
      </tr>
      <tr>
        <th><label for="object">Ob<span class="accessKey">j</span>ect</label></th>
        <td><mak:input field="object" styleId="object" accessKey="j" /></td>
      </tr>
      <tr>
        <th><label for="consumption">Consu<span class="accessKey">m</span>ption</label></th>
        <td><mak:input field="consumption" styleId="consumption" accessKey="m" /></td>
      </tr>
      <tr>
        <th><label for="propagationDepthLimit">propagation depth limit (-1 if none)</label></th>
        <td><mak:input field="propagationDepthLimit" /></td>
      </tr>
      <tr>
        <th><label for="description">Descrip<span class="accessKey">t</span>ion</label></th>
        <td><mak:input field="description" styleId="description" accessKey="t" /></td>
      </tr>
      <tr>
        <th><label for="relations">next relations</label></th>
        <td><mak:input field="relationQueries"><mak:list from="RelationQuery rq" orderBy="rq.description"><mak:option value="rq.id"> <mak:value expr="rq.description"/></mak:option></mak:list></mak:input></td>
      </tr>
      <tr>
        <th><label for="active">act<span class="accessKey">i</span>ve</label></th>
        <td><mak:input field="active" styleId="active" accessKey="i" /></td>
      </tr>
      
      <input type="hidden" name="percolationRule" value="<mak:value expr="percolationRule.id"/>"/>

      <tr>
        <td>    <input type="submit" value="Save changes" accessKey="S">    <input type="reset" accessKey="R">    <input type="reset" value="Cancel" accessKey="C" onClick="javascript:back();">    </td>
      </tr>
    </table>
</fieldset>
  </mak:editForm>
  <%-- Makumba Generator - END OF NORMAL FIELDS --%>

  <%-- Makumba Generator - START OF SETS --%>

  <%-- Makumba Generator - END OF SETS --%>

</table>
</fieldset>
</mak:object>
<br>
<%-- Makumba Generator - END OF *** EDIT ***  PAGE FOR OBJECT PercolationRule --%>
