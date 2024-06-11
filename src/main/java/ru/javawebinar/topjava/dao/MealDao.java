package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {

    List<Meal> getAll();

    Meal getById(String uuid);

    void save(Meal meal);

    void update(Meal meal);

    void delete(String uuid);
}
