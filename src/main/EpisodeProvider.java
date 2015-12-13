package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public String getFolder() {
        String username = System.getProperty("user.name");
        switch (username) {
            case "nz":
                return "download/" + episode.name + "/";
            default:
                return "/mnt/nfs/share/plex/Series/" + episode.name + "/";
        }
    }

    public String getFileName(String extension) {
        return episode.name + "-" + episode.period + "-" + providerId + "_" + index + "." + extension;
    }

    public void download(String extension, String url2) throws Throwable {
        System.out.println("Start Download: " + url2);

        File folder = new File(getFolder());
        if (!folder.exists())
            folder.mkdirs();

        URL url = new URL(url2);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        connection
                .setRequestProperty(
                        "User-Agent",
                        Main3.USER_AGENT);
        InputStream input = connection.getInputStream();
        byte[] buffer = new byte[1024 * 50];
        int n;

        OutputStream output = new FileOutputStream(new File(getFolder() + getFileName(extension)));

        int bytesRead = 0;
        int n2 = 0;
        long last = System.currentTimeMillis();
        while ((n = input.read(buffer)) != -1) {
            bytesRead += n;
            n2 += n;
            long dt = System.currentTimeMillis() - last;

            if (dt > 1000) {
                double dts = dt / 1000.0;
                double nkb = n2 / 1024.0;
                double totalMB = bytesRead / 1024.0 / 1024.0;
                System.out.println(url2 + " ("
                        + totalMB + " mb) kb/s:" + nkb / dts);
                n2 = 0;
                last = System.currentTimeMillis();
            }
            output.write(buffer, 0, n);
        }
        output.close();
        System.out.println("Done:" + url2 + " " + Math.round(bytesRead / 1024.0 / 1024.0) + " mb");
    }


}
