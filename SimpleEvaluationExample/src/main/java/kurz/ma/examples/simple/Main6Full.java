package kurz.ma.examples.simple;

public class Main6Full {

    public static void main(String[] args) {
        final Loop loop = new Loop();
        loop.loop6(4, 60); // 160
        final Recursion recursion = new Recursion();
        recursion.recursion6(3, 50); // 200
        recursion.recursion6(3, 20); // 60
        final Loop loop1 = new Loop();
        loop1.loop6(6, 24); // 96
        Worker.work6(130); // 130
        loop.loop(5, 20); // 100
        recursion.recursion(5, 30); // 150
        Worker.work6(100); // 100
    }
}
