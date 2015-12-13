package main2;

import java.util.ArrayList;

public class RipModel {
    public ArrayList<Episode> episodes = new ArrayList<>();

    public String name;
    public String url;

    public void addEpisode(String name, String wsUrl) {
        Episode episode = new Episode(name, wsUrl);
        episodes.add(episode);
    }

    public ProviderJob requestJob(String providerId) {
        // 1. get Episode with most work to do and matching provider

        Episode choosen = null;

        for (Episode episode : episodes) {
            if (episode.hasUnworkedProvider(providerId)) {
                // System.out.println("choosen: " + episode.getRunningJobs());
                if (choosen == null
                        || choosen.getCompleted() > episode.getCompleted()
                        || choosen.getRunningJobs() > episode.getRunningJobs()) {
                    choosen = episode;
                }
            }
        }

        boolean quitIfOneSuccess = true;
        if (quitIfOneSuccess)
            if (choosen != null && choosen.getCompleted() > 0)
                return null;

        if (choosen == null) {
            System.err.println("no more jobs");
            return null;
        }

        return choosen.createProviderJob(providerId);
    }

}
