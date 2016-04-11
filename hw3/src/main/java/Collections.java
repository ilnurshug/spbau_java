/**
 * Created by ilnur on 16.03.16.
 */

import java.util.Iterator;
import java.util.LinkedList;

public class Collections {

    public static <A, R> LinkedList<R> map(Iterable<A> a, Function1<? super A, R> f) {
        LinkedList<R> res = new LinkedList<R>();
        for (A arg : a) {
            res.addLast(f.apply(arg));
        }
        return res;
    }

    public static <A> LinkedList<A> filter(Iterable<A> a, Predicate<? super A> p) {
        LinkedList<A> res = new LinkedList<A>();
        for (A arg : a) {
            if (p.apply(arg)) {
                res.addLast(arg);
            }
        }
        return res;
    }

    public static <A> LinkedList<A> takeWhile(Iterable<A> a, Predicate<? super A> p) {
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

    public static <A> LinkedList<A> takeUnless(Iterable<A> a, Predicate<? super A> p) {
        return takeWhile(a, p.not());
    }

    public static <A, R> R foldl(Function2<R, ? super A, R> f, R init, Iterable<A> a) {
        return foldlHelper(f, init, a.iterator());
    }

    public static <A, R> R foldr(Function2<? super A, R, R> f, R init, Iterable<A> a) {
        return foldrHelper(f, init, a.iterator());
    }

    private static <A, R> R foldlHelper(Function2<R, ? super A, R> f, R init, Iterator<A> it) {
        // foldl f z (x:xs) = foldl f (f z x) xs

        if (it == null || !it.hasNext()) {
            return init;
        }

        A val = it.next();
        return foldlHelper(f, f.apply(init, val), it);
    }

    private static <A, R> R foldrHelper(Function2<? super A, R, R> f, R init, Iterator<A> it) {
        // foldr f z (x:xs) = f x (foldr f z xs)

        if (it == null || !it.hasNext()) {
            return init;
        }

        A val = it.next();
        return f.apply(val, foldrHelper(f, init, it));
    }
}
