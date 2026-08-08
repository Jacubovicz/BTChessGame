module org.portfolio.benttchessgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires java.sql;
    requires jdk.jdi;


    opens org.portfolio.benttchessgame to javafx.fxml;
    exports org.portfolio.benttchessgame;
    exports org.portfolio.benttchessgame.ui.controller;
    opens org.portfolio.benttchessgame.ui.controller to javafx.fxml;
    exports org.portfolio.benttchessgame.core;
    opens org.portfolio.benttchessgame.core to javafx.fxml;
}