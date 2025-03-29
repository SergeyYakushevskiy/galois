package dstu.csae.galois.extended;

import dstu.csae.galois.Field;
import dstu.csae.polynomial.Polynomial;

import java.util.*;
import java.util.stream.IntStream;

public class ExtendedFieldOperations {

    static int addition(ExtendedField extendedField, int first, int second){
        if(Objects.isNull(extendedField)){
            return -1;
        }
        first = bringToField(extendedField, first);
        second = bringToField(extendedField, second);
        return extendedField.additionMatrix[first][second];
    }

    static Polynomial addition(ExtendedField extendedField, Polynomial first, Polynomial second){
        if(checkNullable(extendedField, first, second)){
            return null;
        }
        int firstI = extendedField.indexOf(bringToField(extendedField, first));
        int secondI = extendedField.indexOf(bringToField(extendedField, second));
        int additionI = extendedField.additionMatrix[firstI][secondI];
        return extendedField.get(additionI);
    }

    static int subtraction(ExtendedField extendedField, int reduced, int subtracted){
        if(Objects.isNull(extendedField)){
            return -1;
        }
        reduced = bringToField(extendedField, reduced);
        subtracted = bringToField(extendedField, subtracted);
        subtracted = inverseOfAddition(extendedField, subtracted);
        return extendedField.additionMatrix[reduced][subtracted];
    }

    static Polynomial subtraction(ExtendedField extendedField, Polynomial reduced, Polynomial subtracted){
        if(checkNullable(extendedField, reduced, subtracted)){
            return null;
        }
        int reducedI = extendedField.indexOf(bringToField(extendedField, reduced));
        int subtractedI = extendedField.indexOf(
                inverseOfAddition(extendedField, bringToField(extendedField, subtracted)));
        int subtractionI = extendedField.additionMatrix[reducedI][subtractedI];
        return extendedField.get(subtractionI);
    }

    static int multiplication(ExtendedField extendedField, int first, int second){
        if(Objects.isNull(extendedField)){
            return -1;
        }
        first = bringToField(extendedField, first);
        second = bringToField(extendedField, second);
        return extendedField.multiplicationMatrix[first][second];
    }

    static Polynomial multiplication(ExtendedField extendedField, Polynomial first, Polynomial second){
        if(checkNullable(extendedField, first, second)){
            return null;
        }
        int firstI = extendedField.indexOf(bringToField(extendedField, first));
        int secondI = extendedField.indexOf(bringToField(extendedField, second));
        int multiplicationI = extendedField.multiplicationMatrix[firstI][secondI];
        return extendedField.get(multiplicationI);
    }

    static Polynomial division(ExtendedField extendedField, Polynomial divisible, Polynomial divisor){
        if(checkNullable(extendedField, divisible, divisor)){
            return null;
        }
        int divisibleI = extendedField.indexOf(bringToField(extendedField, divisible));
        int divisorI = extendedField.indexOf(
                inverseOfMultiplication(extendedField, bringToField(extendedField, divisor)));
        int divisionI = extendedField.multiplicationMatrix[divisibleI][divisorI];
        return extendedField.get(divisionI);
    }

    static int division(ExtendedField extendedField, int divisible, int divisor){
        if(Objects.isNull(extendedField)){
            return -1;
        }
        divisible = bringToField(extendedField, divisible);
        divisor = bringToField(extendedField, divisor);
        divisor = inverseOfAddition(extendedField, divisor);
        return extendedField.multiplicationMatrix[divisible][divisor];
    }

    static int powMod(ExtendedField extendedField, int number, int degree){
        if(Objects.isNull(extendedField)){
            return -1;
        }
        if(!isInBounds(extendedField, number)){
            number = bringToField(extendedField, number);
        }
        if(degree < 0){
            number = inverseOfMultiplication(extendedField, number);
            degree = -degree;
        }
        if(degree == 1){
            return number;
        }
        int multiplier = 1;
        while (degree != 1){
            if(degree % 2 != 0){
                multiplier = multiplication(extendedField, multiplier, number);
                degree --;
            }
            number = multiplication(extendedField, number, number);
            degree /= 2;
        }
        return multiplication(extendedField, multiplier, number);
    }

    static Polynomial powMod(ExtendedField extendedField, Polynomial polynomial, int degree){
        if(checkNullable(extendedField, polynomial)){
            return null;
        }
        int polynomialI = extendedField.indexOf(bringToField(extendedField, polynomial));
        return extendedField.get(powMod(extendedField, polynomialI, degree));
    }

    static Polynomial inverseOfAddition(ExtendedField extendedField, Polynomial polynomial){
        if(checkNullable(extendedField, polynomial)){
            return null;
        }
        Polynomial bring = Optional.ofNullable(bringToField(extendedField, polynomial))
                .orElse(extendedField.ZERO);
        int indexOfBring = extendedField.indexOf(bring);
        return extendedField.get(inverseOfAddition(extendedField, indexOfBring));
    }

    static Polynomial inverseOfMultiplication(ExtendedField extendedField, Polynomial polynomial) {
        if (checkNullable(extendedField, polynomial)) {
            return null;
        }
        Polynomial bring = Optional.ofNullable(bringToField(extendedField, polynomial))
                .orElse(extendedField.ZERO);
        if (bring.equals(extendedField.ZERO)) {
            return null;
        }
        int indexOfBring = extendedField.indexOf(bring);
        return extendedField.get(inverseOfMultiplication(extendedField, indexOfBring));
    }

    static int bringToField(ExtendedField extendedField, int index){
        if(checkNullable(extendedField)){
            return 0;
        }
        int elementCount = extendedField.size();
        index %= elementCount;
        return index < 0 ? index + elementCount : index;
    }

    static Polynomial bringToField(ExtendedField extendedField, Polynomial polynomial){
        if(checkNullable(extendedField, polynomial)){
            return null;
        }
        Field field = extendedField.getField();
        Optional<Polynomial> bringPolynomial = field.bringToField(polynomial);
        if(bringPolynomial.isEmpty()) {
            return null;
        }
        Polynomial result = bringPolynomial.get();
        bringPolynomial = field.mod(result, extendedField.getPolynomial());
        return bringPolynomial.orElse(null);
    }

    static int inverseOfAddition(ExtendedField extendedField, int index){
        if(!isInBounds(extendedField, index)){
            return -1;
        }
        return inverseOf(extendedField.additionMatrix, index, 0);
    }

    static int inverseOfMultiplication(ExtendedField extendedField, int index){
        if(!isInBounds(extendedField, index)){
            return -1;
        }
        return inverseOf(extendedField.multiplicationMatrix, index, 1);
    }

    private static int inverseOf(int[][] operationMatrix, int index, int neutralElement){
        OptionalInt inverse = IntStream.range(0, operationMatrix.length)
                .filter(i -> operationMatrix[index][i] == neutralElement)
                .findFirst();
        if(inverse.isEmpty()){
            return -1;
        }
        return inverse.getAsInt();
    }

    static boolean isInBounds(ExtendedField extendedField, int number){
        return number >= 0 && number < extendedField.size();
    }

    static boolean isPrimitive(ExtendedField extendedField, int element){
        if(Objects.isNull(extendedField)){
            return false;
        }
        HashSet<Integer> generatingElements = new HashSet<>(){{add(0);}};
        for(int i = 1; i <= extendedField.size(); i++){
            generatingElements.add(powMod(extendedField, element, i));
        }
        return generatingElements.size() == extendedField.size();
    }

    static boolean isPrimitive(ExtendedField extendedField, Polynomial polynomial){
        if(checkNullable(extendedField, polynomial)){
            return false;
        }
        if(!extendedField.isInField(polynomial)){
            return false;
        }
        int polynomialI = extendedField.indexOf(polynomial);
        return isPrimitive(extendedField, polynomialI);
    }

    private static boolean checkNullable(Object ... objects){
        return Arrays.stream(objects).anyMatch(Objects::isNull);
    }

}
