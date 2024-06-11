package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class MealInMemoryDaoImpl implements MealDao {

    private final List<Meal> meals;

    public MealInMemoryDaoImpl() {
        this.meals = new CopyOnWriteArrayList<>(MealsUtil.meals);
    }


    @Override
    public List<Meal> getAll() {
        return meals;
    }

    @Override
    public Meal getById(String uuid) {
        return meals.stream()
                .filter(m -> Objects.equals(m.getUuid(), uuid))
                .findFirst().orElse(null);
    }

    @Override
    public void save(Meal meal) {
        meals.add(meal);
    }

    @Override
    public void update(Meal meal) {
        delete(meal.getUuid());
        save(meal);
    }

    @Override
    public void delete(String uuid) {
        meals.removeIf(m -> Objects.equals(m.getUuid(), uuid));
    }
}
