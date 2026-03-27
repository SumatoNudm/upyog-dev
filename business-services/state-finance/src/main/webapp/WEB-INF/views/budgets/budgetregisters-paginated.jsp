<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Budget Registers</title>

    <!-- DataTables CSS -->
    <link rel="stylesheet"
          href="https://cdn.datatables.net/1.13.6/css/jquery.dataTables.min.css"/>

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>

    <!-- DataTables JS -->
    <script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
</head>

<body>

<h2>Budget Registers</h2>

<table id="budgetsTable" class="display">
    <thead>
    <tr>
        <th>ID</th>
        <th>Starting Date</th>
        <th>Ending Date</th>
    </tr>
    </thead>
</table>

<script>
    $(document).ready(function () {
        $('#budgetsTable').DataTable({
            processing: true,
            serverSide: true,
            ajax: {
                url: '<c:url value="/budgets/datatables"/>',
                type: 'GET'
            },
            columns: [
                {data: 'id'},
                {data: 'startingDate'},
                {data: 'endingDate'}
            ],
            pageLength: 10
        });
    });
</script>

</body>
</html>
