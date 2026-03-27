<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Budget Registers</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            border: 1px solid #ccc;
            padding: 8px;
        }
        th {
            background: #f2f2f2;
        }
        .pagination {
            margin-top: 15px;
        }
        .pagination a {
            margin: 0 5px;
            text-decoration: none;
        }
        .pagination span {
            font-weight: bold;
        }
    </style>
</head>

<body>

<h2>Budget Registers</h2>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Starting Date</th>
        <th>Ending Date</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${budgets}" var="budget">
        <tr>
            <td>${budget.id}</td>
            <td>${budget.startingDate}</td>
            <td>${budget.endingDate}</td>
        </tr>
    </c:forEach>

    <c:if test="${empty budgets}">
        <tr>
            <td colspan="3">No records found</td>
        </tr>
    </c:if>
    </tbody>
</table>

<div class="pagination">
    <c:if test="${currentPage > 0}">
        <a href="?page=${currentPage - 1}&size=${pageSize}">Previous</a>
    </c:if>

    <span>Page ${currentPage + 1} of ${totalPages}</span>

    <c:if test="${currentPage + 1 < totalPages}">
        <a href="?page=${currentPage + 1}&size=${pageSize}">Next</a>
    </c:if>
</div>

</body>
</html>
