<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <%@ page isELIgnored="false" %>
        <meta charset="UTF-8">
    </head>
    <body>
        <form method="get" >
            <c:forEach items="${message}" var="items">
                <c:choose>
					<c:when test="${items.value.isParent}">
	                	<c:choose>
	                        <c:when test="${items.value.path != null}">
	                        	<c:out value="[back]" /><a href='<c:url value='/list/${items.key}' />'><c:out value="../"/></a><br/>
	                        </c:when>
	                        <c:otherwise>
	                        	<c:out value="[back]" /><a href='<c:url value='/' />'><c:out value="../"/></a><br/>
	                        </c:otherwise>
	                    </c:choose>
	                </c:when>
                	<c:otherwise>
		                <c:choose>
		                    <c:when test="${items.value.isFile}">
		                        <c:if test="${items.value.isFile}">
		                        	<c:out value="[file]" /><a href='<c:url value='/list/${items.key}' />'><c:out value="${items.value.path}"/></a><br/>
	                        	</c:if>
		                    </c:when>
		                    <c:otherwise>
		                        <c:if test="${!items.value.isFile}">
		                        	<c:out value="[folder]" /><a href='<c:url value='/list/${items.key}' />'><c:out value="${items.value.path}"/></a><br/>
	                        	</c:if>
		                    </c:otherwise>
		                </c:choose>
                	</c:otherwise>
                </c:choose>
            </c:forEach>
        </form>
    </body>
</html>