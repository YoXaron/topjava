package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealDao implements MealDao {
    private final Map<Integer, Meal> meals;
    private final AtomicInteger idCounter = new AtomicInteger(0);

    public InMemoryMealDao() {
        this.meals = new ConcurrentHashMap<>();
        MealsUtil.meals.forEach(this::create);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal getById(int id) {
        return meals.get(id);
    }

    @Override
    public Meal create(Meal meal) {
        meal.setId(idCounter.incrementAndGet());
        meals.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public Meal update(Meal meal) {
        return meals.replace(meal.getId(), meal) == null ? null : meal;
    }

    @Override
    public void delete(int id) {
        meals.remove(id);
    }
}
