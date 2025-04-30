package dstu.csae.galois;

public interface Field<T> {

    int getCharacteristic();

    T findFirstPrimitive();
}
