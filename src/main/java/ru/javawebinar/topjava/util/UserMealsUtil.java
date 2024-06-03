package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
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
        System.out.println(filteredByOneStream(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
        for (UserMeal meal : meals) {
            caloriesPerDayMap.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> mealsWithExcess = new ArrayList<>();
        for (UserMeal meal : meals) {
            boolean excess = caloriesPerDayMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                mealsWithExcess.add(toUserMealWithExcess(meal, excess));
            }
        }
        return mealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> {
                    boolean excess = caloriesPerDayMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
                    return toUserMealWithExcess(meal, excess);
                })
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByOneStream(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream().collect(new UserMealWithExcessCollector(startTime, endTime, caloriesPerDay));
    }

    public static UserMealWithExcess toUserMealWithExcess(UserMeal meal, boolean excess) {
        return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }

    private static class UserMealWithExcessCollector
            implements Collector<UserMeal, UserMealWithExcessCollector.Accumulator, List<UserMealWithExcess>> {

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
                LocalDate date = meal.getDateTime().toLocalDate();
                acc.caloriesByDate.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                    acc.mealsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(meal);
                }
            };
        }

        @Override
        public BinaryOperator<Accumulator> combiner() {
            return (acc1, acc2) -> {
                throw new UnsupportedOperationException("UserMealWithExcessCollector does not support parallel processing");
            };
        }

        @Override
        public Function<Accumulator, List<UserMealWithExcess>> finisher() {
            return acc -> acc.mealsByDate.entrySet().stream()
                    .flatMap(entry -> {
                        LocalDate date = entry.getKey();
                        List<UserMeal> dailyMeals = entry.getValue();
                        boolean excess = acc.caloriesByDate.get(date) > caloriesPerDay;
                        return dailyMeals.stream()
                                .map(meal -> toUserMealWithExcess(meal, excess));
                    })
                    .collect(Collectors.toList());
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.singleton(Characteristics.UNORDERED);
        }

        private static class Accumulator {
            private final Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
            private final Map<LocalDate, List<UserMeal>> mealsByDate = new HashMap<>();
        }
    }
}
