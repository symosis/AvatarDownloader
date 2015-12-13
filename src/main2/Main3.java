package main2;

import main2.rippers.StreamcloudProviderFactory;
import main2.rippers.VidupmeProviderFactory;

public class Main3 {
    public static final String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4";

    public static void main(String[] args) {
        RipManager manager = new RipManager();
        manager.model = new RipModel();

         addEpisode(manager, 2, 1);
        for (int season = 1; season <= 4; season++) {
            for (int episode = 1; episode <= 30; episode++) {
//                addEpisode(manager, episode, season);
            }
        }

        manager.getProviderUrls();
        System.out.println("Complete Provider Cale Urls");

//		http://vidzi.tv/
//		new ProviderWorker(manager.model, "vidzi.tv", new VidziProviderFactory()).start();
        new ProviderWorker(manager.model, "streamcloud", new StreamcloudProviderFactory()).start();
        new ProviderWorker(manager.model, "vidup.me", new VidupmeProviderFactory()).start();
    }

    private static void addEpisode(RipManager manager, int episode, int seasonInt) {
        String season = "" + seasonInt;
        String period = "s" + season + "_e" + episode;

        String iName = "" + episode;
        if (iName.length() < 2)
            iName = "0" + iName;
        String periodName = "s" + season + "_e" + iName;
//		manager.model.addEpisode("big_bang_theory_" + periodName, "http://watchseries.ag/episode/big_bang_theory_" + period + ".html");
//        manager.model.addEpisode("korra_" + periodName, "http://watchseries.ag/episode/the_legend_of_korra_" + period + ".html");
//        manager.model.addEpisode("avatar_" + periodName, "http://watchseries.ag/episode/avatar:_the_last_airbender_" + period + ".html");
        manager.model.addEpisode("you_are_the_worst_" + periodName, "http://watchseries.ag/episode/You_re_the_Worst_" + period + ".html");
    }
}
