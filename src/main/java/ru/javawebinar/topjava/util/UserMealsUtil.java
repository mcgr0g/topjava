package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.TimeUtil.isBetweenHalfOpen;


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

//        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
//        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // return filtered list with excess. Implemented by cycles
        List<UserMealWithExcess> filteredElemets = new ArrayList<>();
        Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
        // считаем общее количество калогий в день
        for (UserMeal element : meals) {
            LocalDate ld = element.getDateTime().toLocalDate();
            if (!caloriesPerDayMap.containsKey(ld)) {
                caloriesPerDayMap.put(ld, element.getCalories());
            } else {
                caloriesPerDayMap.put(ld, caloriesPerDayMap.get(ld) + element.getCalories());
            }
        }
        for (UserMeal element : meals)
            // считаем приемы пищи поподающие под фильтр. Если такие есть то к ним проверяем превышение дневного порога на калории
            if (TimeUtil.isBetweenHalfOpen(element.getDateTime().toLocalTime(), startTime, endTime)) {
                boolean overcomeDayLimit = false;
                if (caloriesPerDayMap.get(element.getDateTime().toLocalDate()) > caloriesPerDay)
                    overcomeDayLimit = true;
                filteredElemets.add(new UserMealWithExcess(element.getDateTime(), element.getDescription(), element.getCalories(), overcomeDayLimit));
            }
        return filteredElemets;
    }


    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // Implement by streams

        // считаем общее количество калорий по дням
        Map<LocalDate, Integer> caloriesPerDayMap = meals.stream().collect(
                Collectors.groupingBy(um -> um.getDateTime().toLocalDate()
                        , Collectors.summingInt(UserMeal::getCalories)
                ));

        //работаем с приемами пищи, подходящие под фильтр
        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(um -> new UserMealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories(),
                        caloriesPerDayMap.get(um.getDateTime().toLocalDate()) > caloriesPerDay)
                )
                .collect(Collectors.toList());
    }
}
