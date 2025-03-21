package dstu.csae.polynomial;

import dstu.csae.exceptions.ExceptionMessageConstants;
import dstu.csae.galois.Operations;

import java.util.Optional;

public class PolynomialOperations extends Operations {

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


}
