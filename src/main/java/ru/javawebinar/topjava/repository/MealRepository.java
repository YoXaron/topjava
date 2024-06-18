package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository {
    // null if updated meal does not belong to userId
    Meal save(int userId, Meal meal);

    // false if meal does not belong to userId
    boolean delete(int userId, int mealId);

    // null if meal does not belong to userId
    Meal get(int userId, int mealId);

    // ORDERED dateTime desc
    List<Meal> getAll(int userId);

    List<Meal> getFilteredByDate(int userId, LocalDate startDate, LocalDate endDate);
}
