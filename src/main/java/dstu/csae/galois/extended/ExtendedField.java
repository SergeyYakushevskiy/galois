package dstu.csae.galois.extended;

import dstu.csae.exceptions.ExceptionMessageConstants;
import dstu.csae.galois.Field;
import dstu.csae.polynomial.Polynomial;
import lombok.Getter;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExtendedField {

    private Field field;
    @Getter private int degree;
    private Polynomial polynomial;
    private Polynomial[] elements;

    private ExtendedField(){}

    public ExtendedField(Field field, Polynomial polynomial)
            throws InvalidParameterException{
        setField(field);
        setPolynomial(polynomial);
        generateElements();
    }
    public Field getField(){
        return field.clone();
    }

    public Polynomial getPolynomial(){
        return polynomial.clone();
    }

    public Polynomial[] getElements(){
        return (Polynomial[]) Arrays.stream(elements)
                .map(Polynomial::clone)
                .toArray();
    }

    public void setField(Field field)
            throws InvalidParameterException{
        Optional.ofNullable(field).orElseThrow(() ->
                new InvalidParameterException(ExceptionMessageConstants.FIELD_IS_NULL));
        this.field = field.clone();
    }

    public void setPolynomial(Polynomial polynomial)
            throws InvalidParameterException{
        Optional.ofNullable(polynomial).orElseThrow(() ->
                new InvalidParameterException(ExceptionMessageConstants.POLYNOMIAL_IS_NULL));
        if(!field.isIrreducible(polynomial)){
            throw new InvalidParameterException(
                    String.format(ExceptionMessageConstants.POLYNOMIAL_IS_REDUCIBLE, polynomial, field)
            );
        }
        this.polynomial = polynomial.clone();
    }

    private void generateElements(){
        elements = new Polynomial[field.getCharacteristic() * degree];
        //TODO : дописать генерацию элементов
    }

    @Override
    public String toString() {
        final String FORMAT = "GF(%d%d, %s){%s}";
        final String DELIMITER = ", ";
        return String.format(FORMAT, field.getCharacteristic(), degree, polynomial,
                Arrays.stream(elements)
                        .map(String :: valueOf)
                        .collect(Collectors.joining(DELIMITER)));
    }
}
