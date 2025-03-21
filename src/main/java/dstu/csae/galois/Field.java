package dstu.csae.galois;

import dstu.csae.exceptions.ExceptionMessageConstants;
import dstu.csae.exceptions.ReverseElementEvaluationException;
import dstu.csae.math.ArithmeticFunctions;
import dstu.csae.polynomial.Polynomial;
import lombok.Getter;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Field implements Cloneable{

    //TODO: определить методы основных операций над полиномами в расширенном поле
    //TODO: сложение, умножение, деление, остаток, обратные элементы по операциям, возведение в степень
    //TODO: проверка на принадлежность полю
    //TODO: Интерфейс "Операция", где хранятся все перегрузы конкретной операции: с полями и без
    //TODO: Критерий Эйзенштейна
    


    @Getter private int characteristic;
    private int[] elements;

    private Field(){}

    public Field(int characteristic){
        if(!ArithmeticFunctions.isPrime(characteristic) || characteristic < 1){
            throw new InvalidParameterException(
                    String.format(ExceptionMessageConstants.NUMBER_IS_NOT_PRIME,
                            characteristic)
            );
        }
        this.characteristic = characteristic;
        generateElements();
    }

    public int[] getElements(){
        return Arrays.copyOf(elements, elements.length);
    }

    public int bringToField(int number){
        return FieldOperations.bringToField(this, number);
    }

    public int add(int first, int second){
        return FieldOperations.addition(this, first, second);
    }

    public int subtract(int reduced, int subtracted){
        return FieldOperations.subtraction(this, reduced, subtracted);
    }

    public int multiply(int first, int second){
        return FieldOperations.multiplication(this, first, second);
    }

    public int divide(int divisible, int divisor)
            throws ArithmeticException{
        return FieldOperations.division(this, divisible, divisor);
    }

    public int powMod(int number, int degree){
        return FieldOperations.powMod(this, number, degree);
    }

    public int inverseOfAddition(int number){
        return FieldOperations.inverseOfAddition(this, number);
    }

    public int inverseOfMultiplication(int number)
            throws ReverseElementEvaluationException {
        return FieldOperations.inverseOfMultiplication(this, number);
    }

    public boolean isInField(int number){
        return FieldOperations.isInField(this, number);
    }

    public boolean isIrreducible(Polynomial p)
            throws IllegalArgumentException{
        Optional.ofNullable(p).orElseThrow(() ->
                new IllegalArgumentException(ExceptionMessageConstants.POLYNOMIAL_IS_NULL));
        return FieldOperations.isIrreducible(this, p);
    }

    private void generateElements(){
        elements = IntStream.range(0, characteristic).toArray();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return characteristic == field.characteristic;
    }

    @Override
    public int hashCode() {
        return characteristic;
    }

    @Override
    public String toString() {
        return String.format("GF(%d){%s}",
                characteristic,
                Arrays.stream(elements)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(", ")));
    }

    @Override
    public Field clone() {
        try {
            Field clone = (Field) super.clone();
            clone.elements = getElements();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
