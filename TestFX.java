package javafx;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TestFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void addData() {

        String inline = "";
        JSONArray arr = null;
        try {
            URL url = new URL("http://brandmydream.com/foodapp/user-app/api/allcombos/?userid=");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            con.connect();
            int responseCode = con.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("didnt connect!");
            } else {
                Scanner sc = new Scanner(url.openStream());
                while (sc.hasNext()) {
                    inline += sc.nextLine();
                }

                JSONParser parse = new JSONParser();
                JSONObject obj = (JSONObject) parse.parse(inline);

                arr = (JSONArray) obj.get("data");

                Statement st = JConnect.getStatement();

                for (int i = 0; i < arr.size(); i++) {
                    JSONObject obj1 = (JSONObject) arr.get(i);
                    JSONArray imgs = (JSONArray) obj1.get("comboslider");
                    JSONObject img = (JSONObject) imgs.get(0);
                    try {
                        String query = "INSERT INTO test VALUES('" + obj1.get("id").toString() + "',"
                                + " '" + obj1.get("name").toString() + "', '" + img.get("imagename").toString() + "',"
                                + " '" + img.get("imagepath").toString() + "')";
                        int rs = st.executeUpdate(query);
                        if (rs > 0) {
                            System.out.println("added successfully!");
                        } else {
                            System.out.println("Can't add data");
                        }
                    } catch(Exception e){
                        System.out.println(e+" my error");
                    }
                }

            }
        } catch (NullPointerException e) {
            System.out.println("null here");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public ResultSet getData() {
        ResultSet rs = null;
        try {
            Statement st = JConnect.getStatement();
            rs = st.executeQuery("SELECT * FROM test");
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    @Override
    public void start(Stage stage) {

        //addData();
        TableView<Data> table = new TableView<Data>();

        ObservableList<Data> data = FXCollections.observableArrayList();

        try {
            ResultSet rs = getData();
            ResultSet rs1 = getData();
            int count = 0;
            while (rs1.next()) {
                count++;
            }
            if (count != 10) {
                addData();
            }
            while (rs.next()) {
                Image image = new Image(rs.getString("imagePath"));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(300);
                imageView.setFitWidth(300);
                data.add(new Data(Integer.parseInt(rs.getString("id")), rs.getString("name"), rs.getString("imageName"), imageView));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        Scene scene = new Scene(new Group());

        stage.setTitle("Image View");
        stage.setWidth(1500);
        stage.setHeight(1000);

        final Label label = new Label("Image View");
        label.setFont(new Font("Arial", 20));

        table.setEditable(false);
        table.setFixedCellSize(300);

        TableColumn first = new TableColumn("ID");
        first.setMinWidth(100);
        //first.setStyle("-fx-background-color: BEIGE");
        first.setCellValueFactory(new PropertyValueFactory<Data, String>("id"));

        TableColumn second = new TableColumn("NAME");
        second.setMinWidth(400);
        second.setCellValueFactory(new PropertyValueFactory<Data, String>("name"));

        TableColumn third = new TableColumn("IMAGE NAME");
        third.setMinWidth(400);
        third.setCellValueFactory(new PropertyValueFactory<Data, String>("imageName"));

        TableColumn fourth = new TableColumn("IMAGE");
        fourth.setMinWidth(480);
        fourth.setCellValueFactory(new PropertyValueFactory<Data, ImageView>("image"));

        table.setItems(data);
        table.setPrefSize(1400, 900);

        table.getColumns().addAll(first, second, third, fourth);

        VBox vbox = new VBox();

        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);

        stage.show();
    }

    public static class Data {

        private int id;
        private String name;
        private String imageName;
        private ImageView image;

        private Data(int id, String name, String imageName, ImageView image) {
            this.id = id;
            this.name = name;
            this.imageName = imageName;
            this.image = image;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public ImageView getImage() {
            return image;
        }

        public void setImage(ImageView image) {
            this.image = image;
        }

    }
}
