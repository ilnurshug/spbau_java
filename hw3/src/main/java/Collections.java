/**
 * Created by ilnur on 16.03.16.
 */

import java.util.Iterator;
import java.util.LinkedList;

public class Collections {

    public static <A, R> LinkedList<R> map(Iterable<A> a, Function1<A, R> f) {
        LinkedList<R> res = new LinkedList<R>();
        for (A arg : a) {
            res.addLast(f.apply(arg));
        }
        return res;
    }

    public static <A> LinkedList<A> filter(Iterable<A> a, Predicate<A> p) {
        LinkedList<A> res = new LinkedList<A>();
        for (A arg : a) {
            if (p.apply(arg)) {
                res.addLast(arg);
            }
        }
        return res;
    }

    public static <A> LinkedList<A> takeWhile(Iterable<A> a, Predicate<A> p) {
        LinkedList<A> res = new LinkedList<A>();
        for (A arg : a) {
            if (p.apply(arg)) {
                res.addLast(arg);
            }
            else {
                break;
            }
        }
        return res;
    }

    public static <A> LinkedList<A> takeUnless(Iterable<A> a, Predicate<A> p) {
        return takeWhile(a, p.not());
    }

    public static <A> A foldl(Function2<A, A, A> f, A init, Iterable<A> a) {
        return _foldl(f, init, a.iterator());
    }

    public static <A> A foldr(Function2<A, A, A> f, A init, Iterable<A> a) {
        return _foldr(f, init, a.iterator());
    }

    private static <A> A _foldl(Function2<A, A, A> f, A init, Iterator<A> it) {
        // foldl f z (x:xs) = foldl f (f z x) xs

        if (it == null || !it.hasNext()) {
            return init;
        }

        A val = it.next();
        return _foldl(f, f.apply(init, val), it);
    }

    private static <A> A _foldr(Function2<A, A, A> f, A init, Iterator<A> it) {
        // foldr f z (x:xs) = f x (foldr f z xs)

        if (it == null || !it.hasNext()) {
            return init;
        }

        A val = it.next();
        return f.apply(val, _foldr(f, init, it));
    }
}
