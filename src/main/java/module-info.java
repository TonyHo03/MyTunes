module dk.easv.mytunes {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.microsoft.sqlserver.jdbc;
    requires java.sql;
    requires java.naming;
    requires java.desktop;
    requires mp3agic;
    requires javafx.graphics;
    requires javafx.base;


    opens dk.easv.mytunes to javafx.fxml;
    exports dk.easv.mytunes;
    exports dk.easv.mytunes.gui;
    opens dk.easv.mytunes.gui to javafx.fxml;
    exports dk.easv.mytunes.be;
    opens dk.easv.mytunes.be to javafx.base;
}