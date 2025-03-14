package dstu.csae.polynomial;

import dstu.csae.exceptions.EmptyCoefficientsException;
import dstu.csae.exceptions.ExceptionMessageConstants;
import dstu.csae.index.Index;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class Polynomial implements Comparable<Polynomial>, Cloneable{
    public static final Polynomial ZERO = new Polynomial(new int[]{0});
    public static final Polynomial ONE = new Polynomial(new int[]{1});


    private int[] coefficients;

    public Polynomial() throws EmptyCoefficientsException{
        this(1);
    }

    public Polynomial(int degree) throws EmptyCoefficientsException{
        this(new int[degree + 1]);
    }

    public Polynomial(int[] coefficients) throws EmptyCoefficientsException{
        setCoefficients(coefficients);
    }

    protected void setCoefficients(int[] coefficients) throws EmptyCoefficientsException{
        if(coefficients == null || coefficients.length == 0){
            throw new EmptyCoefficientsException(ExceptionMessageConstants.EMPTY_COEFFICIENTS);
        }
        this.coefficients = coefficients;
    }

    public int getDegree(){
        return removeLastZero(this).length - 1;
    }

    public int get(int degree) throws IndexOutOfBoundsException{
        isInBounds(degree);
        return coefficients[degree];
    }

    protected int[] getCoefficients(){
        return Arrays.copyOf(coefficients, coefficients.length);
    }

    public Optional<BigInteger> evaluate(int x){
        return evaluate(BigInteger.valueOf(x));
    }

    public Optional<BigInteger> evaluate(BigInteger x){
        if(x == null){
            return Optional.empty();
        }
        BigInteger result = BigInteger.ZERO;
        for(int deg = 0; deg < coefficients.length; deg++){
            result = x.pow(deg)
                    .multiply(BigInteger.valueOf(coefficients[deg]))
                    .add(result);
        }
        return Optional.of(result);
    }

    public void set(int degree, int coefficient){
        isInBounds(degree);
        coefficients[degree] = coefficient;
    }

    public void add(Polynomial p){
        Optional<Polynomial> result = PolynomialOperations.addition(this, p);
        result.ifPresent(polynomial -> setCoefficients(polynomial.getCoefficients()));
    }

    public void subtract(Polynomial p){
        Optional<Polynomial> result = PolynomialOperations.subtraction(this, p);
        result.ifPresent(polynomial -> setCoefficients(polynomial.getCoefficients()));
    }

    public void multiply(Polynomial p){
        Optional<Polynomial> result = PolynomialOperations.multiplication(this, p);
        result.ifPresent(polynomial -> setCoefficients(polynomial.getCoefficients()));
    }

    public void divide(Polynomial p){
        Optional<Polynomial> result = PolynomialOperations.division(this, p);
        result.ifPresent(polynomial ->  setCoefficients(polynomial.getCoefficients()));
    }

    public void mod(Polynomial p){
        Optional<Polynomial> result = PolynomialOperations.remains(this, p);
        result.ifPresent(polynomial ->  setCoefficients(polynomial.getCoefficients()));
    }

    private void isInBounds(int degree) throws IndexOutOfBoundsException{
        if(degree >= 0 && degree < coefficients.length) {
            return;
        }
        throw new IndexOutOfBoundsException(String.format(
                ExceptionMessageConstants.INDEX_OUT_OF_POLYNOMIAL,
                this,
                degree
        ));
    }

    public void removeLastZero(){
        this.coefficients = removeLastZero(this);
    }

    static int[] removeLastZero(Polynomial p){
        int lastIndex = p.coefficients.length - 1;
        for(int i = lastIndex; i > 1; i --){
            if (p.coefficients[i] != 0){
                break;
            }
            lastIndex --;
        }
        return Arrays.copyOf(p.coefficients,lastIndex + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Polynomial that = (Polynomial) o;
        return Objects.deepEquals(removeLastZero(this), removeLastZero(that));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(removeLastZero(this));
    }

    @Override
    public String toString(){
        int start = 0;
        while(coefficients[start] == 0){
            start ++;
            if(start == coefficients.length - 1){
                start = 0;
                break;
            }
        }
        StringBuilder out = new StringBuilder(String.valueOf(coefficients[start]));
        String monomial;
        if(start != 0){
            monomial = "x" + Index.toSuperscript(String.valueOf(start));
            out.append(monomial);
        }
        for(int index = start + 1; index < coefficients.length; index ++){
            int elem = coefficients[index];
            if (elem == 0){
                continue;
            }
            monomial = elem > 0 ? "+" : "";
            monomial += elem + "x" + Index.toSuperscript(String.valueOf(index));
            out.append(monomial);
        }
        return out.toString();
    }

    @Override
    public Polynomial clone() {
        try {
            Polynomial clone = (Polynomial) super.clone();
            clone.coefficients = getCoefficients();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public int compareTo(Polynomial o) {
        if(o == null){
            return 1;
        }
        if(this.equals(o)){
            return 0;
        }
        Polynomial division = PolynomialOperations.division(this, o).orElse(Polynomial.ZERO);
        int degree = division.getDegree();
        return Integer.compare(division.get(degree), 0);
    }
}
