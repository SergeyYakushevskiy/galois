package dstu.csae.galois;

import dstu.csae.exceptions.ExceptionMessageConstants;
import dstu.csae.exceptions.ReverseElementEvaluationException;
import dstu.csae.math.ArithmeticFunctions;

class FieldOperations{

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

    static int subtraction(Field field, int reduced, int subtracted){
        return addition(field, reduced, inverseOfAddition(field, subtracted));
    }

    static int multiplication(Field field, int first, int second){
        if(first == 0 || second == 0){
            return 0;
        }
        first = bringToField(field, first);
        second = bringToField(field, second);
        return bringToField(field, first * second);
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

    static int bringToField(Field field, int number){
        if(number == 0){
            return 0;
        }
        int modulo = field.getCharacteristic();
        int multiplication = ArithmeticFunctions.getMultipliersList(number).stream()
                .map(x -> x % modulo)
                .reduce(1, (acc, x) -> acc * x);
        multiplication %= multiplication > 0 ? modulo : -modulo;
        return multiplication;
    }

    static boolean isInField(Field field, int number){
        return number >= 0 && number < field.getCharacteristic();
    }

}
