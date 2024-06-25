package me.nobeld.noblewhitelist.model;

public class PairData<T, E> {
    private T first;
    private E second;

    protected PairData(T first, E second) {
        this.first = first;
        this.second = second;
    }

    public static <T, E> PairData<T, E> of(T first, E second) {
        return new PairData<>(first, second);
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public E getSecond() {
        return second;
    }

    public void setSecond(E second) {
        this.second = second;
    }
}
