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

    private WebEngine webEngine;

    public static void main(String[] args) {
        YoutubePlayer.launch(args);
    }

    @Override
    public void start(final Stage stage) {
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

        try {
            slackManager = new SlackManager();
            // url
            slackManager.reseiveUrlRequestObservable
                    .subscribe(content -> {
                        Platform.runLater(() -> this.loadNewPage(content));
                    });
            // search
            slackManager.reseiveSearchRequestObservable
                    .subscribe(keywords -> {
                        String url = youtubeSearchUrl(keywords);
                        loadNewPage(url);
                    });
        } catch (Exception e) {
            System.out.println("can't open slack Manager");
            System.out.println(e);
        }
    }

    private String youtubeSearchUrl(String[] keywards) {
        String url = "https://www.youtube.com/results?search_query=" + keywards[0];
        for (int i=1; i<keywards.length; i++) {
            url += "+" + keywards[i];
        }
        return url;
    }

    private void loadNewPage(String url) {
        System.out.print("new page loaded  :  ");
        System.out.println(url);
        Platform.runLater( () -> webEngine.load(url));
    }
}
