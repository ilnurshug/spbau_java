/**
 * Created by ilnur on 16.03.16.
 */
public interface Function1<A, R> {
    R apply(A arg);

    default <R1> Function1<A, R1> compose(Function1<? super R, R1> g) {
        // g(f(x))
        return a -> g.apply(apply(a));
    }
}
