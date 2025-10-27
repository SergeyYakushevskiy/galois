package dstu.csae.galois.extended;

import dstu.csae.exceptions.ExceptionMessageConstants;
import dstu.csae.galois.Field;
import dstu.csae.galois.GaloisField;
import dstu.csae.index.Index;
import dstu.csae.polynomial.Polynomial;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class GaloisFieldExtension implements Field {

    public final Polynomial ZERO;
    public final Polynomial ONE;
    @Getter
    private final GaloisField galoisField;
    @Getter
    private final int degree;
    @Getter
    private final Polynomial polynomial;
    private final ArrayList<Polynomial> elements;
    final int[][] additionMatrix;
    final  int[][] multiplicationMatrix;


    public GaloisFieldExtension(GaloisField galoisField, Polynomial polynomial)
        throws IllegalArgumentException{
        if(Objects.isNull(galoisField)){
            throw new IllegalArgumentException(ExceptionMessageConstants.FIELD_IS_NULL);
        }
        if(Objects.isNull(polynomial)){
            throw new IllegalArgumentException(ExceptionMessageConstants.POLYNOMIAL_IS_NULL);
        }
        if(!galoisField.isIrreducible(polynomial)){
            throw new IllegalArgumentException(
                    String.format(ExceptionMessageConstants.POLYNOMIAL_IS_REDUCIBLE,
                            polynomial, galoisField));
        }
        this.galoisField = galoisField;
        this.polynomial = polynomial;
        this.degree = polynomial.getDegree();
        elements = generateElements();
        ZERO = elements.get(0);
        ONE = elements.get(1);
        additionMatrix = generateAdditionMatrix();
        multiplicationMatrix = generateMultiplicationMatrix();
    }

    public int getCharacteristic(){
        return elements.size();
    }

    public Polynomial get(int index){
        if(!isInBounds(index)){
            return null;
        }
        return elements.get(index);
    }

    public int indexOf(Polynomial polynomial){
        if(!isInField(polynomial)){
            return -1;
        }
        return elements.indexOf(polynomial);
    }

    private ArrayList<Polynomial> generateElements(){
        int characteristic = galoisField.getCharacteristic();
        int elementCount = (int)Math.pow(galoisField.getCharacteristic(), degree);
        int[][] coefficients = new int[elementCount][degree];
        int period = 1;
        int currentDegree = 0;
        ArrayList<Polynomial> elements = new ArrayList<>();
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
                .forEach(index -> elements.add(new Polynomial(coefficients[index])));
        return elements;
    }

    private int[][] generateAdditionMatrix(){
        int[][] additionMatrix = new int[elements.size()][elements.size()];
        for(int i = 0; i < elements.size(); i ++){
            for(int j = 0; j < elements.size(); j ++){
                Optional<Polynomial> addition = galoisField.add(elements.get(i), elements.get(j));
                if(addition.isPresent()){
                    Polynomial added = addition.get();
                    added = galoisField.mod(added, polynomial).orElse(Polynomial.ZERO);
                    additionMatrix[i][j] = elements.indexOf(added);
                }
            }
        }
        return additionMatrix;
    }

    private int[][] generateMultiplicationMatrix(){
        int[][] multiplicationMatrix = new int[elements.size()][elements.size()];
        for(int i = 0; i < elements.size(); i ++){
            for(int j = 0; j < elements.size(); j ++){
                Optional<Polynomial> multiplication = galoisField.multiply(elements.get(i), elements.get(j));
                if(multiplication.isPresent()){
                    Polynomial multiplied = multiplication.get();
                    multiplied = galoisField.mod(multiplied, polynomial).orElse(Polynomial.ONE);
                    multiplicationMatrix[i][j] = elements.indexOf(multiplied);
                }
            }
        }
        return multiplicationMatrix;
    }

    public boolean isNormalized(Polynomial p) {
        if (p == null) {
            return false;
        }

        int characteristic = this.getCharacteristic();

        for (int i = 0; i <= p.getDegree(); i++) {
            int coeff = p.get(i);
            if (coeff < 0 || coeff >= characteristic) {
                return false;
            }
        }

        return true;
    }

    public Polynomial findFirstPrimitive() {
        for (int i = 1; i < this.elements.size(); i++) {
            Polynomial candidate = this.elements.get(i);
            if (this.isPrimitive(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    public int findFirstIntPrimitive(){
        return indexOf(findFirstPrimitive());
    }

    public int add(int first, int second){
        return ExtendedFieldOperations.addition(this, first, second);
    }

    public Optional<Polynomial> add(Polynomial first, Polynomial second){
        return Optional.ofNullable(ExtendedFieldOperations.addition(this, first, second));
    }

    public int subtract(int reduced, int subtracted){
        return ExtendedFieldOperations.subtraction(this, reduced, subtracted);
    }

    public Optional<Polynomial> subtract(Polynomial reduced, Polynomial subtracted){
        return Optional.ofNullable(ExtendedFieldOperations.subtraction(this, reduced, subtracted));
    }

    public int multiply(int first, int second){
        return ExtendedFieldOperations.multiplication(this, first, second);
    }

    public Optional<Polynomial> multiply(Polynomial first, Polynomial second){
        return Optional.ofNullable(ExtendedFieldOperations.multiplication(this, first, second));
    }

    public int divide(int divisible, int divisor){
        return ExtendedFieldOperations.division(this, divisible, divisor);
    }

    public Optional<Polynomial> divide(Polynomial divisible, Polynomial divisor){
        return Optional.ofNullable(ExtendedFieldOperations.division(this, divisible, divisor));
    }

    public int powMod(int number, int degree){
        return ExtendedFieldOperations.powMod(this, number, degree);
    }

    public Optional<Polynomial> powMod(Polynomial polynomial, int degree){
        return Optional.ofNullable(ExtendedFieldOperations.powMod(this, polynomial, degree));
    }

    public int bringToField(int number){
        return ExtendedFieldOperations.bringToField(this, number);
    }

    public Polynomial bringToField(Polynomial p){
        return Optional.ofNullable(ExtendedFieldOperations.bringToField(this, p))
                .orElse(get(0));
    }

    public int inverseOfAddition(int element){
        return ExtendedFieldOperations.inverseOfAddition(this, element);
    }

    public Optional<Polynomial> inverseOfAddition(Polynomial polynomial){
        return Optional.ofNullable(ExtendedFieldOperations.inverseOfAddition(this, polynomial));
    }

    public int inverseOfMultiplication(int element){
        return ExtendedFieldOperations.inverseOfMultiplication(this, element);
    }

    public Optional<Polynomial> inverseOfMultiplication(Polynomial polynomial){
        return Optional.ofNullable(ExtendedFieldOperations.inverseOfMultiplication(this, polynomial));
    }

    public boolean isInField(Polynomial polynomial){
        if(Objects.isNull(polynomial)){
            return false;
        }
        return elements.contains(polynomial);
    }

    public boolean isInBounds(int index){
        return ExtendedFieldOperations.isInBounds(this, index);
    }

    public boolean isPrimitive(int element){
        return ExtendedFieldOperations.isPrimitive(this, element);
    }

    public boolean isPrimitive(Polynomial polynomial){
        return ExtendedFieldOperations.isPrimitive(this, polynomial);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GaloisFieldExtension that = (GaloisFieldExtension) o;
        return Objects.equals(galoisField, that.galoisField) && Objects.equals(polynomial, that.polynomial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(galoisField, polynomial);
    }

    @Override
    public String toString() {
        return String.format("GF(%s%s)(%s)",
                galoisField.getCharacteristic(),
                Index.toSuperscript(String.valueOf(degree)),
                polynomial);
    }

}
