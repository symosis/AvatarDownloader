package main;

import main.rippers.HappystreamsProviderFactory;

public class Main3 {
    public static final String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4";

    public static void main(String[] args) {
        RipManager manager = new RipManager();
        manager.model = new RipModel();

        manager.model.name = "star_trek-the_next_generation";
        manager.model.url = "http://watchseries.ag/episode/star_trek:_the_next_generation_";

//        manager.model.name = "z_nation";
//        manager.model.url = "http://watchseries.ag/episode/z-nation-_";

//        manager.model.name = "you_are_the_worst_";
//        manager.model.url = "http://watchseries.ag/episode/You_re_the_Worst_";

//        addEpisode(manager, 1, 1);
        for (int season = 1; season <= 1; season++) {
            for (int episode = 1; episode <= 30; episode++) {
                addEpisode(manager, season, episode);
            }
        }

        manager.getProviderUrls();
        System.out.println("Complete Provider Cale Urls");

        new ProviderWorker(manager.model, "happystreams", new HappystreamsProviderFactory()).start();
//        new ProviderWorker(manager.model, "streamcloud", new StreamcloudProviderFactory()).start();
//        new ProviderWorker(manager.model, "vidup.me", new VidupmeProviderFactory()).start();
    }

    private static void addEpisode(RipManager manager, int seasonInt, int episode) {
        String season = "" + seasonInt;
        String period = "s" + season + "_e" + episode;

        String iName = "" + episode;
        if (iName.length() < 2)
            iName = "0" + iName;
        String periodName = "s" + season + "_e" + iName;
//		manager.model.addEpisode("big_bang_theory_" + periodName, "http://watchseries.ag/episode/big_bang_theory_" + period + ".html");
//        manager.model.addEpisode("korra_" + periodName, "http://watchseries.ag/episode/the_legend_of_korra_" + period + ".html");
//        manager.model.addEpisode("avatar_" + periodName, "http://watchseries.ag/episode/avatar:_the_last_airbender_" + period + ".html");
//        manager.model.addEpisode("you_are_the_worst_" + periodName, "http://watchseries.ag/episode/You_re_the_Worst_" + period + ".html");
        manager.model.addEpisode(manager.model.name, periodName, manager.model.url + period + ".html");
    }
}
