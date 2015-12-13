package main2.rippers;

import main2.EpisodeProvider.Status;
import main2.FindUrls;
import main2.Main3;
import main2.ProviderJob;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class StreamcloudRipper {
    private ProviderJob providerJob;
    private String providerUrl;
    private Elements form;

    public StreamcloudRipper(ProviderJob providerJob) throws Throwable {
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
    }

    private void postForm() throws Throwable {
        Thread.sleep(1000 * 10);
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
        urlParameters += "referer=" + URLEncoder.encode(providerJob.provider.caleHref, "UTF-8") + "&";
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
        int lineNumber = 1;
        StringBuffer s = new StringBuffer();

        while ((line = rd.readLine()) != null) {
            lineNumber++;
            s.append(line);
        }
        rd.close();

        List<String> urls = FindUrls.extractMp4Urls(s.toString());

        if (urls.size() > 0) {
            download(providerJob.provider.getFileName(), providerUrl, urls.get(0));
        } else {
            throw new Error("MP4 Url not there");
        }
    }

    private void download(final String name, final String providerHref, final String url2) throws MalformedURLException, IOException, FileNotFoundException {
        System.out.println("Start:" + url2);
        URL url = new URL(url2);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        connection
                .setRequestProperty(
                        "User-Agent",
                        Main3.USER_AGENT);
        InputStream input = connection.getInputStream();
        byte[] buffer = new byte[1024 * 50];
        int n = -1;

        OutputStream output = new FileOutputStream(new File(name));

        int bytesRead = 0;
        int n2 = 0;
        long last = System.currentTimeMillis();
        while ((n = input.read(buffer)) != -1) {
            bytesRead += n;
            n2 += n;
            long dt = System.currentTimeMillis() - last;

            long reportMs = 5000;
            if (dt > reportMs) {
                double dts = dt / 1000.0;
                double nkb = n2 / 1024.0;
                double totalMB = bytesRead / 1024.0 / 1024.0;
                System.out.println(url2 + " ("
                        + totalMB + " mb) kb/s:" + nkb / dts + " to " + name);
                n2 = 0;
                last = System.currentTimeMillis();
            }
            output.write(buffer, 0, n);
        }
        output.close();
        System.out.println("Done:" + url2 + " " + Math.round(bytesRead / 1024.0 / 1024.0) + " mb");
    }

}
