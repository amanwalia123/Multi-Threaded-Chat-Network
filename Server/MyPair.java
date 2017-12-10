//Class to provide basic functionality of Tuple
class MyPair<F,S> {
    private F first;
    private S second;

    public MyPair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() { return first; }
    public S getSecond() { return second; }


    public void setFirst(F element) { first = element; }
    public void setSecond(S element) { second = element; }
}
