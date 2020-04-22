/**
 * Basic 3-tuple class for holding data.
 * @param <A>
 * @param <B>
 * @param <C>
 */

public class Triplet<A, B, C> {

    private final A left;
    private final B middle;
    private final C right;

    public Triplet(A left, B middle, C right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public A getLeft() {
        return left;
    }

    public B getMiddle() {
        return middle;
    }

    public C getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + ", " + middle + ", " + right + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Triplet)) {
            return false;
        }
        Triplet<A,B,C> otherCopy = (Triplet<A,B,C>) other;
        // May cause null pointer exception if null are valid values for A,B,C.
        return otherCopy.left.equals(this.left)
                && otherCopy.middle.equals(this.middle)
                && otherCopy.right.equals(this.right);
    }
}
