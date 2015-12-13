package main2;

public class EpisodeProvider {
    public enum Status {
        UNWORKED,
        WORKING,
        COMPLETED,
        ERROR,
    }

    public Episode episode;
    public String caleHref;
    public String providerId;
    public Status status = Status.UNWORKED;
    public int index;
    public int errorCount = 0;

    public EpisodeProvider(int index, String providerId, String caleHref, Episode episode) {
        this.providerId = providerId;
        this.caleHref = caleHref;
        this.episode = episode;
        this.index = index;
    }

    @Override
    public String toString() {
        return "Provider " + episode.name + " " + providerId + " " + index + " " + status;
    }

    public String getFileName() {

        String username = System.getProperty("user.name");
        String name = episode.name;

        switch (username) {
            case "nz":
                return "download/" + name + "-" + providerId + "-" + index + ".mp4";
            default:
                return "/mnt/nfs/share/plex/Series/" + name + "-" + providerId + "-" + index + ".mp4";
        }
    }

}
