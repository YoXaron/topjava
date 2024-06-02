package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class UserMealWithExcessCollector implements Collector<UserMeal, UserMealWithExcessCollector.Accumulator, List<UserMealWithExcess>> {

    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int caloriesPerDay;

    public UserMealWithExcessCollector(LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.caloriesPerDay = caloriesPerDay;
    }

    @Override
    public Supplier<Accumulator> supplier() {
        return Accumulator::new;
    }

    @Override
    public BiConsumer<Accumulator, UserMeal> accumulator() {
        return (acc, meal) -> {
            acc.meals.add(meal);
            acc.dailyCalories.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        };
    }

    @Override
    public BinaryOperator<Accumulator> combiner() {
        return (acc1, acc2) -> {
            acc1.meals.addAll(acc2.meals);
            acc2.dailyCalories.forEach(
                    (date, calories) -> acc1.dailyCalories.merge(date, calories, Integer::sum)
            );
            return acc1;
        };
    }

    @Override
    public Function<Accumulator, List<UserMealWithExcess>> finisher() {
        return acc -> {
            List<UserMealWithExcess> list = new ArrayList<>();
            for (UserMeal meal : acc.meals) {
                LocalDate date = meal.getDateTime().toLocalDate();
                int dailyCalories = acc.dailyCalories.getOrDefault(date, 0);
                boolean excess = dailyCalories > caloriesPerDay;

                if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                    list.add(UserMealsUtil.toUserMealWithExcess(meal, excess));
                }
            }
            return list;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.singleton(Characteristics.UNORDERED);
    }

    public static class Accumulator {
        private final Map<LocalDate, Integer> dailyCalories = new HashMap<>();
        private final List<UserMeal> meals = new ArrayList<>();
    }
}
