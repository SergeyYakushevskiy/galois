package dstu.csae.polynomial;

import dstu.csae.exceptions.ExceptionMessageConstants;

import java.util.Optional;

class PolynomialOperations {

    public static Optional<Polynomial> addition(Polynomial first, Polynomial second){
        if(first == null || second == null){
            return Optional.empty();
        }
        int[] firstC = Polynomial.removeLastZero(first);
        int[] secondC = Polynomial.removeLastZero(second);

        return Optional.of(new Polynomial(addition(firstC, secondC)));
    }

    public static Optional<Polynomial> subtraction(Polynomial reduced, Polynomial subtracted){
        if(reduced == null || subtracted == null){
            return Optional.empty();
        }
        int[] reducedC = Polynomial.removeLastZero(reduced);
        int[] subtractedC = Polynomial.removeLastZero(subtracted);
        return Optional.of(new Polynomial(subtraction(reducedC, subtractedC)));
    }


    public static Optional<Polynomial> multiplication (Polynomial first, Polynomial second){
        if(first == null || second == null){
            return Optional.empty();
        }
        int[] firstC = first.getCoefficients();
        int[] secondC = second.getCoefficients();
        return Optional.of(new Polynomial(multiplication(firstC, secondC)));
    }

    public static Optional<Polynomial> division(Polynomial divisible, Polynomial divisor){
        if(divisible == null || divisor == null){
            return Optional.empty();
        }
        if(divisor.getDegree() == 0){
            throw new ArithmeticException(String.format(
                    ExceptionMessageConstants.POLYNOMIAL_DIVIDE_BY_ZERO,
                    divisor
            ));
        }
        if ((divisible.getDegree() == 0) || divisible.getDegree() < divisor.getDegree()){
            return Optional.of(new Polynomial());
        }
        int[] divisibleC = Polynomial.removeLastZero(divisible);
        int[] divisorC = Polynomial.removeLastZero(divisor);
        return Optional.of(new Polynomial(division(divisibleC, divisorC)));
    }

    public static Optional<Polynomial> remains(Polynomial divisible, Polynomial divisor){
        if(divisible == null || divisor == null){
            return Optional.empty();
        }
        if(divisor.getDegree() == 0){
            throw new ArithmeticException(String.format(
                    ExceptionMessageConstants.POLYNOMIAL_DIVIDE_BY_ZERO,
                    divisor
            ));
        }
        if ((divisible.getDegree() == 0)){
            return Optional.of(new Polynomial());
        }
        if(divisible.getDegree() < divisor.getDegree()){
            return Optional.of(divisible.clone());
        }
        int[] divisibleC = Polynomial.removeLastZero(divisible);
        int[] divisorC = Polynomial.removeLastZero(divisor);
        return Optional.of(new Polynomial(
                subtraction(divisibleC,
                        multiplication(
                                division(divisibleC, divisorC),
                                divisorC
                        ))));
    }

    private static int[] addition(int[] first, int[] second){
        int minLength = Math.min(first.length, second.length);
        int maxLength = Math.max(first.length, second.length);
        int[] addition = new int[maxLength];
        for(int i = 0; i < minLength; i ++){
            addition[i] += first[i];
            addition[i] += second[i];
        }
        for(int i = minLength; i < maxLength; i ++){
            if(i < first.length - 1){
                addition[i] += first[i];
            }
            if(i < second.length - 1){
                addition[i] += second[i];
            }
        }
        return addition;
    }

    private static int[] subtraction(int[] reduced, int[] subtracted){
        int minLength = Math.min(reduced.length, subtracted.length);
        int maxLength = Math.max(reduced.length, subtracted.length);
        int[] subtraction = new int[maxLength];
        for(int i = 0; i < minLength; i ++){
            subtraction[i] += reduced[i];
            subtraction[i] -= subtracted[i];
        }
        for(int i = minLength; i < maxLength; i ++){
            if(i < reduced.length - 1){
                subtraction[i] += reduced[i];
            }
            if(i < subtracted.length - 1){
                subtraction[i] -= subtracted[i];
            }
        }
        return subtraction;
    }

    private static int[] multiplication(int[] first, int[] second){
        int[] multiplication = new int[first.length + second.length - 1];
        for(int i = 0; i < first.length; i ++){
            for(int j = 0; j < second.length; j ++){
                multiplication[i + j] += first[i] * second[j];
            }
        }
        return multiplication;
    }

    private static int[] division(int[] divisible,int[] divisor){
        int[] division = new int[divisible.length - divisor.length + 1];
        int[] current;
        int divisibleDegree = divisible.length - 1;
        int divisorDegree = divisor.length - 1;
        while(divisibleDegree >= divisorDegree){
            current = new int[divisibleDegree];
            division[divisibleDegree - divisorDegree] = divisible[divisibleDegree] / divisor[divisorDegree];
            current[divisibleDegree - divisorDegree] = division[divisibleDegree - divisorDegree];
            divisible = subtraction(divisible, multiplication(current, divisor));
            divisibleDegree--;
        }
        return division;
    }
}
