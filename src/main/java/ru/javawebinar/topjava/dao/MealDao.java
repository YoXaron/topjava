package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {

    List<Meal> getAll();

    Meal getById(Integer id);

    Meal create(Meal meal);

    Meal update(Meal meal);

    void delete(Integer id);
}
