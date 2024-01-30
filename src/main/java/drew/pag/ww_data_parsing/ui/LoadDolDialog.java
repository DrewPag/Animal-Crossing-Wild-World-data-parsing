package drew.pag.ww_data_parsing.ui;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 *
 * @author drewpag
 */
public class LoadDolDialog{
    
    public static String display(){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        String mainDolPath = "";
        
        GridPane pane = new GridPane();
        
        Scene scene = new Scene(pane, 500, 500);
        stage.setTitle("main.dol parsing");
        stage.setScene(scene);
        stage.showAndWait();
        
        return mainDolPath;
    }    
}
