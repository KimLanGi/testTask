module com.example.testtask {
    // JavaFX модули
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    // Библиотеки, которые вы используете
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;
    requires kotlinx.coroutines.core;
    requires io.ktor.client.core;
    requires io.ktor.client.content.negotiation;
    requires kotlinx.serialization.json;
    requires io.ktor.serialization.kotlinx.json;
    requires io.ktor.client.cio;
    requires java.desktop;

    // Экспортируем ваш основной пакет
    opens com.example.testtask to javafx.fxml;
    exports com.example.testtask;
}
