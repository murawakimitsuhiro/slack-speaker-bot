import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.beans.value.ChangeListener;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;
import rx.Observable;

import static javafx.concurrent.Worker.State;

public class YoutubePlayer extends Application {

    private SlackManager slackManager;

    private VBox root;
    private WebView webView;
    private WebEngine webEngine;

    public static void main(String[] args) {
        YoutubePlayer.launch(args);
    }

    @Override
    public void start(final Stage stage) {
        // Create the WebView
        this.webView = new WebView();

        // Create the WebEngine
        this.webEngine = webView.getEngine();

        // LOad the Start-Page
        this.webEngine.load("https://google.com");

        // Update the stage title when a new web page title is available
        this.webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>()
        {
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState)
            {
                if (newState == State.SUCCEEDED)
                {
                    //stage.setTitle(webEngine.getLocation());
                    stage.setTitle(webEngine.getTitle());
                }
            }
        });

        // Create the VBox
        root = new VBox();
        // Add the WebView to the VBox
        root.getChildren().add(webView);

        // Set the Style-properties of the VBox
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        // Create the Scene
        Scene scene = new Scene(root);
        // Add  the Scene to the Stage
        stage.setScene(scene);
        // Display the Stage
        stage.show();

        try {
            slackManager = new SlackManager();
            slackManager.reseiveRequestObservable
                    .subscribe(content -> {
                        System.out.println("fefefefe");
                        Platform.runLater(() ->
                                webEngine.load("https://youtube.com")
                        );
                    });
        } catch (Exception e) {
            System.out.println("can't open slack Manager");
            System.out.println(e);
        }
    }
}
