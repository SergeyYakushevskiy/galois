package dstu.csae;

import dstu.csae.galois.GaloisField;
import dstu.csae.galois.extended.GaloisFieldExtension;
import dstu.csae.polynomial.Polynomial;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GaloisFieldExtensionMultiplyInverseTest {


    static List<Integer> provideFieldElements(GaloisFieldExtension field) {
        return field.getElements().stream()
                .map(field::indexOf)
                .filter(index -> index > 0)
                .toList();
    }

    @ParameterizedTest
    @MethodSource("provideTestFields")
    void multiplyInverseTest(GaloisFieldExtension field) {
        for(int element : provideFieldElements(field)){
            int inverse = field.inverseOfMultiplication(element);
            int mult = field.multiply(element, inverse);
            assertEquals(field.indexOf(field.ONE), mult);
        }
    }

    static Stream<GaloisFieldExtension> provideTestFields() {
        GaloisField baseField = new GaloisField(2);
        return Stream.of(new GaloisFieldExtension(baseField, new Polynomial(new int[]{1, 0, 1, 1})));
    }
}
