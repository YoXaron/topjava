package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryMealDao implements MealDao {

    private final Map<Integer, Meal> meals;
    private Integer idCounter = 0;

    public InMemoryMealDao() {
        this.meals = new ConcurrentHashMap<>();
        MealsUtil.meals.forEach(this::create);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal getById(Integer id) {
        if (id == null) {
            return null;
        }
        return meals.get(id);
    }

    @Override
    public synchronized Meal create(Meal meal) {
        if (meal.getId() == null) {
            meal.setId(++idCounter);
        }
        meals.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public synchronized Meal update(Meal meal) {
        if (meal.getId() == null || !meals.containsKey(meal.getId())) {
            throw new NoSuchElementException("Meal with id " + meal.getId() + " does not exist.");
        }
        meals.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public synchronized void delete(Integer id) {
        meals.remove(id);
    }
}
