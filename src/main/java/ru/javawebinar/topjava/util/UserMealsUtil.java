package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        System.out.println(filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
        Map<LocalDate, List<UserMeal>> mealsByDay = new HashMap<>();

        for (UserMeal meal : meals) {
            LocalDate date = meal.getDateTime().toLocalDate();
            mealsByDay.computeIfAbsent(date, k -> new ArrayList<>()).add(meal);
            caloriesPerDayMap.merge(date, meal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> mealsWithExcess = new ArrayList<>();
        for (Map.Entry<LocalDate, List<UserMeal>> entry : mealsByDay.entrySet()) {
            LocalDate date = entry.getKey();
            List<UserMeal> dailyMeals = entry.getValue();
            int dailyCalories = caloriesPerDayMap.get(date);
            boolean excess = dailyCalories > caloriesPerDay;

            for (UserMeal meal : dailyMeals) {
                if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                    mealsWithExcess.add(toUserMealWithExcess(meal, excess));
                }
            }
        }

        return mealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));

        Map<LocalDate, List<UserMeal>> mealsByDay = meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate()));

        return mealsByDay.entrySet().stream()
                .flatMap(entry -> {
                    LocalDate date = entry.getKey();
                    List<UserMeal> dailyMeals = entry.getValue();
                    int dailyCalories = caloriesPerDayMap.get(date);
                    boolean excess = dailyCalories > caloriesPerDay;

                    return dailyMeals.stream()
                            .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                            .map(meal -> toUserMealWithExcess(meal, excess));
                })
                .collect(Collectors.toList());
    }

    public static UserMealWithExcess toUserMealWithExcess(UserMeal meal, boolean excess) {
        return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }

    public static UserMeal toUserMeal(UserMealWithExcess userMealWithExcess) {
        return new UserMeal(userMealWithExcess.getDateTime(), userMealWithExcess.getDescription(), userMealWithExcess.getCalories());
    }
}
