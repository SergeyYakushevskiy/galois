package dstu.csae.galois;

import dstu.csae.exceptions.ExceptionMessageConstants;
import dstu.csae.exceptions.ReverseElementEvaluationException;
import dstu.csae.math.ArithmeticFunctions;
import dstu.csae.polynomial.Polynomial;
import lombok.Getter;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

public class Field implements Cloneable{

    @Getter private int characteristic;
    private int[] elements;
    public static final int ADDITION_NEUTRAL_ELEMENT = 0;
    public static final int MULTIPLICATION_NEUTRAL_ELEMENT = 1;
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

    public Optional<Polynomial> bringToField(Polynomial polynomial){
        return Optional.ofNullable(FieldOperations.bringToField(this, polynomial));
    }

    public int add(int first, int second){
        return FieldOperations.addition(this, first, second);
    }

    public Optional<Polynomial> add(Polynomial first, Polynomial second){
        return Optional.ofNullable(FieldOperations.addition(this, first, second));
    }

    public int subtract(int reduced, int subtracted){
        return FieldOperations.subtraction(this, reduced, subtracted);
    }

    public Optional<Polynomial> subtract(Polynomial reduced, Polynomial subtracted){
        return Optional.ofNullable(FieldOperations.subtraction(this, reduced, subtracted));
    }

    public int multiply(int first, int second){
        return FieldOperations.multiplication(this, first, second);
    }

    public Optional<Polynomial> multiply(Polynomial first, Polynomial second){
        return Optional.ofNullable(FieldOperations.multiplication(this, first, second));
    }

    public int divide(int divisible, int divisor)
            throws ArithmeticException{
        return FieldOperations.division(this, divisible, divisor);
    }

    public Optional<Polynomial> divide(Polynomial divisible, Polynomial divisor){
        return Optional.ofNullable(FieldOperations.division(this, divisible, divisor));
    }

    public int mod(int divisible, int divisor){
        return FieldOperations.mod(this, divisible, divisor);
    }

    public Optional<Polynomial> mod(Polynomial divisible, Polynomial divisor){
        return Optional.ofNullable(FieldOperations.mod(this, divisible, divisor));
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

    public boolean isInField(Polynomial polynomial){
        return FieldOperations.isInField(this, polynomial);
    }

    public boolean isIrreducible(Polynomial polynomial)
            throws IllegalArgumentException{
        Optional.ofNullable(polynomial).orElseThrow(() ->
                new IllegalArgumentException(ExceptionMessageConstants.POLYNOMIAL_IS_NULL));
        return FieldOperations.isIrreducible(this, polynomial);
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
        return String.format("GF(%d)",
                characteristic);
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
