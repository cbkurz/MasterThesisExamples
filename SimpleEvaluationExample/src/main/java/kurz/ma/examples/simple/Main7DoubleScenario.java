package kurz.ma.examples.simple;

public class Main7DoubleScenario {

    public static void main(String[] args) {
        Worker.work6(308);
        new Loop().loop6(6, 48); // 192
    }

    public static void main2(String[] args) {
        new Recursion().recursion6(3, 50); // 200
        new Loop().loop6(6, 72); // 288
        Worker.work6(10);
    }

}
