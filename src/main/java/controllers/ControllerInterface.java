package controllers;

public interface ControllerInterface {
    String getOne(spark.Request req, spark.Response res);
    String getAll(spark.Request req, spark.Response res);
    String store(spark.Request req, spark.Response res);
    String update(spark.Request req, spark.Response res);
    String destroy(spark.Request req, spark.Response res);
}
