/**
 * Basic 2-tuple class for holding data.
 * @param <A>
 * @param <B>
 */
public class Pair<A, B> {

    private final A left;
    private final B right;

    public Pair(A left, B right) {
        this.left = left;
        this.right = right;
    }

    public A getLeft() {
        return left;
    }

    public B getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + ", " + right + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Triplet)) {
            return false;
        }
        Pair<A,B> otherCopy = (Pair<A,B>) other;
        // May cause null pointer exception if null are valid values for A and B.
        return otherCopy.left.equals(this.left) && otherCopy.right.equals(this.right);
    }

}
