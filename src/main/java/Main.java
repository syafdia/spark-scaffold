import spark.Spark;

public class Main {
    public static void main(String[] args) {
        Spark.staticFileLocation("/public");
        Routes.register();
    }
}
