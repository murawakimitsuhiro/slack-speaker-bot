import javafx.application.Application;
import org.riversun.slacklet.Slacklet;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.riversun.slacklet.SlackletService;
import org.riversun.xternal.simpleslackapi.SlackChannel;
import rx.subjects.PublishSubject;

import java.io.IOException;

public class SlackManager {

    SlackletService slackService;

    public PublishSubject<String> reseiveUrlRequestObservable;
    public PublishSubject<String[]> reseiveSearchRequestObservable;

    SlackManager() throws IOException {
        reseiveUrlRequestObservable = PublishSubject.create();
        reseiveSearchRequestObservable = PublishSubject.create();

        String botToken ="xoxb-298506418368-9ZHlhlb5sG7Gg9ywM2IzT42B";
        slackService = new SlackletService(botToken);

        slackService.addSlacklet(new Slacklet() {
            @Override
            public void onMessagePosted(SlackletRequest req, SlackletResponse resp) {

                // メッセージがポストされたチャンネルを取得する
                SlackChannel channel = req.getChannel();

                if ("murawaki-home-speaker".equals(channel.getName())) {
                    // #randomチャンネルだった場合

                    // メッセージ本文を取得
                    String content = req.getContent();

                    // メッセージがポストされたチャンネルに対して、BOTからメッセージを送る
//                    resp.reply("何も流れてなくない??");

                    String[] keywords = split(content);
                    if (keywords.length >= 2) {
                        reseiveSearchRequestObservable.onNext(keywords);
                    } else {
                        reseiveUrlRequestObservable.onNext(content);
                    }
                }

            }
        });

        slackService.start();

        // チャンネルに対して、（返信ではなく）メッセージを送る
        String channelName = "murawaki-home-speaker";
        slackService.sendMessageTo(channelName, "I'm ready.");
    }

    public void setObservable(PublishSubject<String> observable) {
        observable.subscribe(href -> {
            String channelName = "murawaki-home-speaker";
            slackService.sendMessageTo(channelName, "Now Playing :musical_note: " + href);
        });
    }

    private String[] split(String text) {
        return text.replaceAll("　", " ").split("[\\s]+");
    }

}
