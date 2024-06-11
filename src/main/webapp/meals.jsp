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
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<a href="meals?null&action=add">
    <button>Добавить</button>
</a>
<br>
<br>
<table>
    <tr>
        <th>Дата</th>
        <th>Описание</th>
        <th>Калории</th>
        <th colspan=2>Действие</th>
    </tr>
    <jsp:useBean id="meals" scope="request" type="java.util.List"/>
    <c:forEach items="${meals}" var="meal">
        <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
        <tr class="${meal.excess ? 'excess-true' : 'excess-false'}">
            <td>
                <p>${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}</p>
            </td>
            <td>
                <p>${meal.description}</p>
            </td>
            <td>
                <p>${meal.calories}</p>
            </td>
            <td><a href="meals?uuid=${meal.uuid}&action=edit">Изменить</a></td>
            <td><a href="meals?uuid=${meal.uuid}&action=delete">Удалить</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
