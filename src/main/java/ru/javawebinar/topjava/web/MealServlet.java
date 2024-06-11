package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealInMemoryDaoImpl;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MealServlet extends HttpServlet {

    private MealDao dao;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        dao = new MealInMemoryDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuid = req.getParameter("uuid");
        String action = req.getParameter("action");

        if (action == null) {
            req.setAttribute(
                    "meals",
                    MealsUtil.filteredByStreams(
                            dao.getAll(),
                            LocalTime.MIN,
                            LocalTime.MAX,
                            MealsUtil.CALORIES_PER_DATE
                    )
            );
            req.getRequestDispatcher("meals.jsp").forward(req, resp);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        Meal meal;
        switch (action) {
            case "add":
                meal = Meal.empty;
                req.setAttribute("datetime", meal.getDateTime().format(formatter));
                break;
            case "delete":
                dao.delete(uuid);
                resp.sendRedirect("meals");
                return;
            case "edit":
                meal = dao.getById(uuid);
                req.setAttribute("datetime", meal.getDateTime().format(formatter));
                break;
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }

        req.setAttribute("meal", meal);
        req.getRequestDispatcher("meal-edit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String uuid = req.getParameter("uuid");
        final boolean isNotCreated = dao.getById(uuid) == null;
        Meal meal;
        if (isNotCreated) {
            meal = Meal.empty;
        } else {
            meal = dao.getById(uuid);
        }

        meal.setDateTime(LocalDateTime.parse(req.getParameter("datetime")));
        meal.setDescription(req.getParameter("description"));
        meal.setCalories(Integer.parseInt(req.getParameter("calories")));

        if (isNotCreated) {
            dao.save(meal);
        } else {
            dao.update(meal);
        }
        resp.sendRedirect("meals");
    }
}
