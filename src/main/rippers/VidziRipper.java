package main.rippers;

import main.EpisodeProvider.Status;
import main.FindUrls;
import main.Main3;
import main.ProviderJob;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class VidziRipper {
    private ProviderJob providerJob;
    private String providerUrl;
    private Elements form;

    public VidziRipper(ProviderJob providerJob) throws Throwable {
        this.providerJob = providerJob;

        loadCale();

        loadProviderPage();

        postForm();

        providerJob.provider.status = Status.COMPLETED;
    }

    private void loadCale() throws Throwable {
        Connection con = Jsoup.connect(providerJob.provider.caleHref);
        con.userAgent(Main3.USER_AGENT);
        Document doc = con.get();
        Elements button = doc.select(".mybutton");
        providerUrl = button.attr("href");
        System.out.println("Got provider URL " + providerJob.provider + " >> " + providerUrl);
    }

    private void loadProviderPage() throws IOException {
        Connection con = Jsoup.connect(providerUrl);
        con.userAgent(Main3.USER_AGENT);
        Document doc = con.get();
        form = doc.select("form");
//		System.out.println(form);
    }

    private void postForm() throws Throwable {
        Thread.sleep(1000 * 1);

        URL url = new URL(providerUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        connection.setRequestProperty("Origin", "http://happystreams.net");
        connection
                .setRequestProperty(
                        "Cookie",
                        "__cfduid=d0b6d3a0204e9d5d81b1e2fecb751730c1446319476; betaCookie=1; lang=1; __utmt=1; file_id=177387; aff=32; ref_url=http%3A%2F%2Fwatchseries.ag%2Fopen%2Fcale%2F42875064.html; __utma=171482035.1830120384.1446319478.1448925067.1449358786.3; __utmb=171482035.29.10.1449358786; __utmc=171482035; __utmz=171482035.1449358786.3.3.utmcsr=watchseries.ag|utmccn=(referral)|utmcmd=referral|utmcct=/open/cale/42875064.html");
        connection.setRequestProperty("Referer", providerUrl);
        connection
                .setRequestProperty(
                        "User-Agent",
                        Main3.USER_AGENT);

        String urlParameters = "";
        for (Element element : form.select("input")) {
            urlParameters += element.attr("name") + "=" + URLEncoder.encode(element.attr("value"), "UTF-8") + "&";
        }
        connection.setRequestProperty("Content-Length",
                Integer.toString(urlParameters.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");

        connection.setUseCaches(false);
        connection.setDoOutput(true);

        // Send request
        DataOutputStream wr = new DataOutputStream(
                connection.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.close();

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer s = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            s.append(line);
        }
        rd.close();

        List<String> urls = FindUrls.extractMp4Urls(s.toString());

        if (urls.size() > 0) {
            Thread.sleep(1000 * 1);
            providerJob.provider.download("mp4", urls.get(0));
        } else {
            throw new Error("MP4 Url not there");
        }
    }
}
