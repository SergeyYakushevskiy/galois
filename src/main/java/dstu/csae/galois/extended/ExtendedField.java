package dstu.csae.galois.extended;

import dstu.csae.exceptions.ExceptionMessageConstants;
import dstu.csae.galois.Field;
import dstu.csae.polynomial.Polynomial;
import lombok.Getter;

import java.util.*;
import java.util.stream.IntStream;

public class ExtendedField {

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
        return field + "(" + polynomial + ')';
    }

    private boolean isInBounds(int number){
        return number > 0 && number < elements.size();
    }

    public static void main(String[] args) {
        Field field = new Field(3);
        ExtendedField ex = new ExtendedField(field, new Polynomial(new int[]{1, 0, 0, 1}));

    }
}
