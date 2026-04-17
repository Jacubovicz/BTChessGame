module org.portfolio.benttchessgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires java.sql;


    opens org.portfolio.benttchessgame to javafx.fxml;
    exports org.portfolio.benttchessgame;
    exports UILogic.controllers;
    opens UILogic.controllers to javafx.fxml;
}