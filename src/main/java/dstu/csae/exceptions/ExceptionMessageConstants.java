package dstu.csae.exceptions;

public interface ExceptionMessageConstants {
    String EMPTY_COEFFICIENTS = "Невозможно инициализировать полином из пустых коэффициентов";
    String INDEX_OUT_OF_POLYNOMIAL = "В полиноме %s нет монома степени %d";
    String POLYNOMIAL_DIVIDE_BY_ZERO = "Невозможно выполнить деление на полином %s";
    String NUMBER_DIVIDE_BY_ZERO = "Попытка деления числа %d на 0";
    String REVERSE_ELEMENT_DOES_NOT_EXIST = "Обратный элемент для числа %d не существует";
    String NUMBER_IS_NOT_PRIME = "Число %d не является простым";
    String FIELD_IS_NULL = "Поле не задано";
    String POLYNOMIAL_IS_NULL = "Полином не задан";
    String POLYNOMIAL_IS_REDUCIBLE = "Полином %s является приводимым над полем %s";
}
