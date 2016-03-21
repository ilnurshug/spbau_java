/**
 * Created by ilnur on 16.03.16.
 */
public interface Function2<A1, A2, R> {
    R apply(A1 lhs, A2 rhs);

    default <R1> Function2<A1, A2, R1> compose(Function1<? super R, R1> g) {
        return (lhs, rhs) -> g.apply(apply(lhs, rhs));
    }

    default <T extends A1> Function1<A2, R> bind1(T a1) {
        return a2 -> apply(a1, a2);
    }

    default <T extends A2> Function1<A1, R> bind2(T a2) {
        return a1 -> apply(a1, a2);
    }

    default Function1<A1, Function1<A2, R>> curry()
    {
        return x -> bind1(x);
    }
}
