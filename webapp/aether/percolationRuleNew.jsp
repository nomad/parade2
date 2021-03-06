<%@ taglib uri="http://www.makumba.org/view-hql" prefix="mak" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<%-- Makumba Generator - START OF  *** NEW ***  PAGE FOR OBJECT PercolationRule --%>
<fieldset style="text-align:right;">
  <legend>New PercolationRule</legend>
    <jsp:include page="aetherTypes.jsp"/>
  
<mak:newForm type="PercolationRule" action="percolationRuleView.jsp" name="percolationRule" >
  <table>
  <%-- Makumba Generator - START OF NORMAL FIELDS --%>
    <tr>
      <th><label for="subject">S<span class="accessKey">u</span>bject</label></th>
      <td><mak:input field="subject" styleId="subject" accessKey="u" /></td>
    </tr>
    <tr>
      <th><label for="predicate"><span class="accessKey">p</span>redicate</label></th>
      <td><mak:input field="predicate" styleId="predicate" accessKey="p" /></td>
    </tr>
    <tr>
      <th><label for="object"><span class="accessKey">o</span>bject</label></th>
      <td><mak:input field="object" styleId="object" accessKey="o" /></td>
    </tr>
    <tr>
      <th><label for="consumption">Co<span class="accessKey">n</span>sumption</label></th>
      <td><mak:input field="consumption" styleId="consumption" accessKey="n" /></td>
    </tr>
    <tr>
      <th><label for="propagationDepthLimit">propagation depth limit (-1 if none)</label></th>
      <td><mak:input field="propagationDepthLimit" /></td>
    </tr>
    <tr>
      <th><label for="description"><span class="accessKey">d</span>escription</label></th>
      <td><mak:input field="description" styleId="description" accessKey="d" /></td>
    </tr>
  <%-- Makumba Generator - END OF NORMAL FIELDS --%>
    <tr>
      <td>  <input type="submit" value="Add" accessKey="A">  <input type="reset" accessKey="R">  <input type="reset" value="Cancel" accessKey="C" onClick="javascript:back();">  </td>
    </tr>
  </table>
</fieldset>
</mak:newForm>

<%-- Makumba Generator - END OF *** NEW ***  PAGE FOR OBJECT PercolationRule --%>
