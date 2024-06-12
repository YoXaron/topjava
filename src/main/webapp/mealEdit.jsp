<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
    <title>Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>${action} meal</h2>
<form method="post" action="meals" enctype="application/x-www-form-urlencoded">
    <input type="hidden" name="id" value="${meal.id}">
    Дата и время: <input type="datetime-local" id="datetime" name="datetime" value="${datetime}" required><br><br>
    Описание: <input type="text" name="description" value="${meal.description}" placeholder="Описание" required><br><br>
    Калории: <input type="number" name="calories" value="${meal.calories}" min="0" required><br><br>
    <hr>
    <button type="submit">Сохранить</button>
    <button type="button" onclick="window.history.back()">Отменить</button>
</form>
</body>
</html>
