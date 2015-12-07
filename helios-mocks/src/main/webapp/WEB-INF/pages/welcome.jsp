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
                    <c:when test="${items.value.isFile}">
                        <c:if test="${items.value.isFile}"><c:out value="[file]" /></c:if><a href='<c:url value='/list/${items.key}' />'><c:out value="${items.value.path}"/></a><br/>
                    </c:when>
                    <c:otherwise >
                        <c:if test="${! items.value.isFile}"><c:out value="[folder]" /></c:if><a href='<c:url value='/list/${items.key}' />'><c:out value="${items.value.path}"/></a><br/>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </form>
    </body>
</html>
