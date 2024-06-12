package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.InMemoryMealDao;
import ru.javawebinar.topjava.dao.MealDao;
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
import java.time.temporal.ChronoUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);

    private MealDao dao;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        dao = new InMemoryMealDao();
        log.info("MealServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        Integer id = idParam == null || idParam.isEmpty() ? null : Integer.parseInt(idParam);
        String action = req.getParameter("action");

        log.info("Received GET request with action: {} and id: {}", action, id);

        if (action == null) {
            displayAllMeals(req, resp);
            return;
        }

        Meal meal;
        switch (action) {
            case "add":
                log.info("Action: add, preparing empty meal for addition");
                meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 0);
                break;
            case "delete":
                log.info("Action: delete, deleting meal with id: {}", id);
                dao.delete(id);
                resp.sendRedirect("meals");
                return;
            case "edit":
                log.info("Action: edit, editing meal with id: {}", id);
                meal = dao.getById(id);
                break;
            default:
                log.error("Unknown action: {}", action);
                displayAllMeals(req, resp);
                return;
        }
        req.setAttribute("meal", meal);
        req.getRequestDispatcher("mealEdit.jsp").forward(req, resp);
    }

    private void displayAllMeals(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("No action specified or unknown action, displaying all meals");
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
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String idParam = req.getParameter("id");
        Integer id = idParam == null || idParam.isEmpty() ? null : Integer.parseInt(idParam);

        log.info("Received POST request with id: {}", id);

        LocalDateTime dateTime = LocalDateTime.parse(req.getParameter("datetime"));
        String description = req.getParameter("description");
        int calories = Integer.parseInt(req.getParameter("calories"));

        if (id == null) {
            Meal meal = dao.create(new Meal(dateTime, description, calories));
            log.info("Meal created with id: {}", meal.getId());
        } else {
            Meal meal = dao.update(new Meal(id, dateTime, description, calories));
            if (meal == null) {
                log.error("Meal with id: {} not found", id);
            } else {
                log.info("Meal updated with id: {}", meal.getId());
            }
        }
        log.info("Sending redirect to meals");
        resp.sendRedirect("meals");
    }
}
