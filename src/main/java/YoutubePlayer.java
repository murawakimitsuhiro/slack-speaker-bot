import javafx.application.Application;
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

    private Stage stage;
    private WebEngine webEngine;

    public static void main(String[] args) {
        YoutubePlayer.launch(args);
    }

    @Override
    public void start(final Stage stage) {
        try {
            slackManager = new SlackManager();
            slackManager.reseiveRequestObservable
                    .subscribe(content -> {
                        this.loadNewWebSite(content);
                    });
        } catch (Exception e) {
            System.out.println("can't open slack Manager");
            System.out.println(e);
        }

        // Create the WebView
        WebView webView = new WebView();

        // Create the WebEngine
        webEngine = webView.getEngine();

        // LOad the Start-Page
        webEngine.load("https://google.com");

        // Update the stage title when a new web page title is available
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>()
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
        VBox root = new VBox();
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
    }

    void loadNewWebSite(String searchContent) {
        webEngine.load("https://www.youtube.com/watch?v=8lx0vLTH_yg");

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>()
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
    }
}
