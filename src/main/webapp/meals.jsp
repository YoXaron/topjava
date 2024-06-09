<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Meals</title>
    <style>
        table, th, td {
            border:1px solid black;
        }
        .excess-true {
            color: red;
        }
        .excess-false {
            color: green;
        }
    </style>
</head>
<body>
<h2>Meals</h2>
<table>
    <tr>
        <th>Дата</th>
        <th>Время</th>
        <th>Описание</th>
        <th>Калории</th>
    </tr>
    <jsp:useBean id="meals" scope="request" type="java.util.List"/>
    <c:forEach items="${meals}" var="meal">
        <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
        <tr class="${meal.excess ? 'excess-true' : 'excess-false'}">
            <td>
                <p>${meal.dateTime.toLocalDate()}</p>
            </td>
            <td>
                <p>${meal.dateTime.toLocalTime()}</p>
            </td>
            <td>
                <p>${meal.description}</p>
            </td>
            <td>
                <p>${meal.calories}</p>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
