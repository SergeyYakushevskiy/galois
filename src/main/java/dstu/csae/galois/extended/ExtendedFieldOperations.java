package dstu.csae.galois.extended;

import dstu.csae.galois.Field;
import dstu.csae.polynomial.Polynomial;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ExtendedFieldOperations {
    //TODO: проверка на null +
    //TODO: приведение полинома к полю +
    //TODO: проверка полинома на вхождение в поле +
    //TODO: сложение +
    //TODO: умножение +
    //TODO: обратный элемент по сложению
    //TODO: вычитание
    //TODO: обратный элемент по умножению
    //TODO: деление
    //TODO: возведение в степень
    //TODO: проверка на примитивность

    static Optional<Polynomial> addition(ExtendedField field, Polynomial first, Polynomial second){
        Optional<Polynomial> addition = field.getField().add(first, second);
        if(addition.isPresent()){
            Polynomial added = addition.get();
            addition = field.getField().mod(added, field.getPolynomial());
        }
        return addition;
    }

    static Optional<Polynomial> multiplication(ExtendedField field, Polynomial first, Polynomial second){
        Optional<Polynomial> multiplication = field.getField().multiply(first, second);
        if(multiplication.isPresent()){
            Polynomial multiplied = multiplication.get();
            multiplication = field.getField().mod(multiplied, field.getPolynomial());
        }
        return multiplication;
    }

    static Optional<Polynomial> bringToField(ExtendedField extendedField, Polynomial polynomial){
        if(Objects.isNull(extendedField)){
            return Optional.empty();
        }
        Field field = extendedField.getField();
        Optional<Polynomial> bringPolynomial = field.bringToField(polynomial);
        if(bringPolynomial.isPresent()){
            Polynomial result = bringPolynomial.get();
            bringPolynomial = field.mod(result, extendedField.getPolynomial());
        };
        return bringPolynomial;
    }

    private static boolean checkNullable(Object ... objects){
        return Arrays.stream(objects).anyMatch(Objects::isNull);
    }


    public static void main(String[] args) {
        Field field = new Field(2);
        ExtendedField extendedField = new ExtendedField(field, new Polynomial(new int[]{1, 0, 1}));
        System.out.println(bringToField(extendedField, new Polynomial(new int[]{3, 4, 0, 1})));
    }
}
