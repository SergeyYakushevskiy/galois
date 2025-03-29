package dstu.csae.galois;

import dstu.csae.exceptions.ExceptionMessageConstants;
import dstu.csae.exceptions.ReverseElementEvaluationException;
import dstu.csae.math.ArithmeticFunctions;
import dstu.csae.polynomial.Polynomial;

import java.util.Arrays;
import java.util.Objects;

class FieldOperations extends Operations {

    static int addition(Field field, int first, int second){
        if(first == 0 && second == 0){
            return 0;
        }
        first = bringToField(field, first);
        second = bringToField(field, second);
        if(first == 0 || second == 0){
            return Math.max(first, second);
        }
        return  bringToField(field, first + second);
    }

    static Polynomial addition(Field field, Polynomial first, Polynomial second){
        if(checkNullable(first, second)){
            return null;
        }
        int[] firstC = first.getCoefficients();
        int[] secondC = second.getCoefficients();
        return new Polynomial(addition(field, firstC, secondC));
    }

    static int subtraction(Field field, int reduced, int subtracted){
        return addition(field, reduced, inverseOfAddition(field, subtracted));
    }

    static Polynomial subtraction(Field field, Polynomial reduced, Polynomial subtracted){
        if(checkNullable(field, reduced, subtracted)){
            return null;
        }

        int[] reducedC = reduced.getCoefficients();
        int[] subtractedC = subtracted.getCoefficients();
        return new Polynomial(subtraction(field, reducedC, subtractedC));
    }

    static int multiplication(Field field, int first, int second){
        if(first == 0 || second == 0){
            return 0;
        }
        first = bringToField(field, first);
        second = bringToField(field, second);
        return bringToField(field, first * second);
    }

    static Polynomial multiplication(Field field, Polynomial first, Polynomial second){
        if(checkNullable(field, first, second)){
            return null;
        }
        if(first.equals(Polynomial.ZERO) || second.equals(Polynomial.ZERO)){
            return Polynomial.ZERO.clone();
        }
        int[] firstC = first.getCoefficients();
        int[] secondC = second.getCoefficients();
        return new Polynomial(multiplication(field, firstC, secondC));
    }

    static int division(Field field, int divisible, int divisor)
            throws ArithmeticException{
        if(divisible == 0){
            return 0;
        }
        if(divisor == 0){
            throw new ArithmeticException(
                    String.format(ExceptionMessageConstants.NUMBER_DIVIDE_BY_ZERO, divisible));
        }
        divisor = inverseOfMultiplication(field, divisor);
        return multiplication(field, divisible, divisor);
    }

    static Polynomial division(Field field, Polynomial divisible, Polynomial divisor)
            throws IllegalArgumentException{
        if(checkNullable(field, divisible, divisor)){
            return null;
        }
        if(divisor.equals(Polynomial.ZERO)) {
            throw new IllegalArgumentException(ExceptionMessageConstants.DIVIDE_BY_ZERO);
        }
        if(divisible.equals(Polynomial.ZERO)){
            return Polynomial.ZERO.clone();
        }
        int[] divisibleC = divisible.getCoefficients();
        int[] divisorC = divisor.getCoefficients();
        return new Polynomial(division(field, divisibleC, divisorC));
    }

    static int mod(Field field, int divisible, int divisor)
            throws ArithmeticException{
        if(divisible == 0){
            return 0;
        }
        if(divisor == 0){
            throw new ArithmeticException(
                    String.format(ExceptionMessageConstants.NUMBER_DIVIDE_BY_ZERO, divisible));
        }
        int division = division(field, divisible, divisor);
        return subtraction(field, divisible, multiplication(field, divisor, division));
    }

    static Polynomial mod(Field field, Polynomial divisible, Polynomial divisor)
            throws IllegalArgumentException{
        if(checkNullable(field, divisible, divisor)){
            return null;
        }
        int[] divisibleC = divisible.getCoefficients();
        int[] divisorC = divisor.getCoefficients();
        int[] rem = division(field, divisibleC, divisorC);
        rem = multiplication(field, divisorC, rem);
        rem = subtraction(field, divisibleC, rem);
        return new Polynomial(rem);
    }

    static int inverseOfAddition(Field field, int number){
        if(number == 0){
            return 0;
        }
        number = bringToField(field, number);
        return field.getCharacteristic() - number;
    }

    static int inverseOfMultiplication(Field field, int number)
            throws ReverseElementEvaluationException{
        if (number == 0){
            throw new ReverseElementEvaluationException(
                    String.format(ExceptionMessageConstants.REVERSE_ELEMENT_DOES_NOT_EXIST, number)
            );
        }
        if(!isInField(field, number)) {
            number = bringToField(field, number);
        }
        int mod = field.getCharacteristic();
        int phi = ArithmeticFunctions.getEulerFunction(mod);
        number = powMod(field, number, phi - 1);
        return bringToField(field, number);
    }

    static int powMod(Field field, int number, int degree){
        if(!isInField(field, number)){
            number = bringToField(field, number);
        }
        if(number == 0 || number == 1){
            return number;
        }
        if(degree == 0){
            return 1;
        }
        if(degree < 0){
            number = inverseOfMultiplication(field, number);
            return powMod(field, number, -degree);
        }
        int multiplier = 1;
        while(degree != 1){
            if(degree % 2 != 0){
                multiplier = bringToField(field, multiplier * number);
                degree --;
            }
            number = bringToField(field, (int)Math.pow(number, 2));
            degree /= 2;
        }
        number = bringToField(field, multiplier * number);
        return number;
    }

    static boolean isIrreducible(Field field, Polynomial polynomial){
        return !checkNullable(field, polynomial);
    }

    static int bringToField(Field field, int number){
        if(number == 0){
            return 0;
        }
        int modulo = field.getCharacteristic();
        int multiplication = number % modulo;
        multiplication = multiplication >= 0 ? multiplication : multiplication + modulo;
        return multiplication;
    }

    public static Polynomial bringToField(Field field, Polynomial polynomial){
        if(polynomial == null){
            return null;
        }
        int[] coefficients = bringToField(field, polynomial.getCoefficients());
        return new Polynomial(coefficients);
    }

    static boolean isInField(Field field, int number){
        return number >= 0 && number < field.getCharacteristic();
    }

    static boolean isInField(Field field, Polynomial polynomial){
        if(polynomial == null){
            return false;
        }
        return Arrays.stream(polynomial.getCoefficients())
                .allMatch(x -> isInField(field, x));
    }

    private static int[] addition(Field field, int[] firstC, int[] secondC){
        firstC = bringToField(field, firstC);
        secondC = bringToField(field, secondC);
        return bringToField(field, addition(firstC, secondC));
    }

    private static int[] subtraction(Field field, int[] reducedC, int[] subtractedC){
        reducedC = bringToField(field, reducedC);
        subtractedC = bringToField(field, subtractedC);
        return bringToField(field, subtraction(reducedC, subtractedC));
    }

    private static int[] multiplication(Field field, int[] firstC, int[] secondC){
        firstC = bringToField(field, firstC);
        secondC = bringToField(field, secondC);
        return bringToField(field, multiplication(firstC, secondC));
    }

    private static int[] division(Field field, int[] divisible, int[] divisor){
        divisible = bringToField(field,divisible);
        divisor = bringToField(field, divisor);
        int[] division = new int[divisible.length - divisor.length + 1];
        int[] current;
        int divisibleDegree = divisible.length - 1;
        int divisorDegree = divisor.length - 1;
        while(divisibleDegree >= divisorDegree){
            current = new int[divisibleDegree];
            division[divisibleDegree - divisorDegree] = divisible[divisibleDegree] / divisor[divisorDegree];
            current[divisibleDegree - divisorDegree] = division[divisibleDegree - divisorDegree];
            divisible = subtraction(field, divisible, multiplication(field, current, divisor));
            divisibleDegree--;
        }
        return division;
    }

    private static int[] bringToField(Field field, int[] coefficients){
        return Arrays.stream(coefficients)
                .map(x -> bringToField(field, x))
                .toArray();
    }

    private static boolean checkNullable(Object ... objects){
        return Arrays.stream(objects).anyMatch(Objects::isNull);
    }
}
