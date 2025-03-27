package dstu.csae.galois.extended;

import dstu.csae.exceptions.ExceptionMessageConstants;
import dstu.csae.galois.Field;
import dstu.csae.index.Index;
import dstu.csae.polynomial.Polynomial;
import lombok.Getter;

import java.util.*;
import java.util.stream.IntStream;

public class ExtendedField {

    public static final Polynomial COMPOSITION_NEUTRAL_ELEM = Polynomial.ZERO;
    public static final Polynomial MULTIPLICATION_NEUTRAL_ELEM = Polynomial.ONE;
    @Getter
    private final Field field;
    private final int degree;
    @Getter
    private final Polynomial polynomial;
    private final TreeMap<Integer, Polynomial> elements = new TreeMap<>();

    public ExtendedField(Field field, Polynomial polynomial)
        throws IllegalArgumentException{
        if(Objects.isNull(field)){
            throw new IllegalArgumentException(ExceptionMessageConstants.FIELD_IS_NULL);
        }
        Optional<Boolean> checkIrreducible = field.isIrreducible(polynomial);
        if(checkIrreducible.isEmpty()){
            throw new IllegalArgumentException(ExceptionMessageConstants.POLYNOMIAL_IS_NULL);
        }
        if(!checkIrreducible.get()){
            throw new IllegalArgumentException(
                    String.format(ExceptionMessageConstants.POLYNOMIAL_IS_REDUCIBLE,
                            polynomial, field));
        }
        this.field = field;
        this.polynomial = polynomial;
        this.degree = polynomial.getDegree();
        generateElements();
    }


    public Optional<Polynomial> get(int index){
        if(!isInBounds(index)){
            return Optional.empty();
        }
        return Optional.of(elements.get(index));
    }

    private void generateElements(){
        int characteristic = field.getCharacteristic();
        int elementCount = (int)Math.pow(field.getCharacteristic(), degree);
        int[][] coefficients = new int[elementCount][degree];
        int period = 1;
        int currentDegree = 0;
        while(currentDegree < degree){
            int currentElementIndex = 0;
            int currentValue = 0;
            while(currentElementIndex < elementCount) {
                for (int i = 0; i < period; i++) {
                    coefficients[currentElementIndex][currentDegree] = currentValue;
                    currentElementIndex++;
                }
                currentValue = (currentValue + 1) % characteristic;
            }
            period *= characteristic;
            currentDegree ++;
        }
        IntStream.range(0, coefficients.length)
                .forEach(index -> elements.put(index, new Polynomial(coefficients[index])));
    }

    public Optional<Polynomial> bringToField(Polynomial p){
        return ExtendedFieldOperations.bringToField(this, p);
    }

    public boolean isInField(ExtendedField field, Polynomial p){
        if(Objects.isNull(p)){
            return false;
        }
        return elements.containsValue(p);
    }

    public Optional<Polynomial> add(Polynomial first, Polynomial second){
        return ExtendedFieldOperations.addition(this, first, second);
    }

    public Optional<Polynomial> multiply(Polynomial first, Polynomial second){
        return ExtendedFieldOperations.multiplication(this, first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExtendedField that = (ExtendedField) o;
        return Objects.equals(field, that.field) && Objects.equals(polynomial, that.polynomial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, polynomial);
    }

    @Override
    public String toString() {
        return String.format("GF(%s%s)(%s)",
                field.getCharacteristic(),
                Index.toSuperscript(String.valueOf(degree)),
                polynomial);
    }

    private boolean isInBounds(int number){
        return number > 0 && number < elements.size();
    }
}
