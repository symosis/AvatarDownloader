package main;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Loader {

    private int season;
    private int episode;
    private String hostPort;
    private String name;
    public String spec;
    public String filename;

    public Loader(int season, int episode, String name) {
        this.season = season;
        this.episode = episode;
        this.name = name;

        spec = "http://watchseries.ag/episode/" + name + "s" + season
                + "_e" + episode + ".html";

        filename = removeShit(name) + String.format("%02d", season) + "e"
                + String.format("%02d", episode) + ".flv";


    }

    public int start() {
        return getEpisode(spec);
    }

    private int getEpisode(String spec) {
        ArrayList<String> lines = http(spec);


        for (String inputLine : lines) {
            if (inputLine.indexOf("middle;\">happystreams") != -1) {
                System.out.println(inputLine);
                String search = "href=\"/open/cale/";
                int index = inputLine.indexOf(search) + search.length();
                String id = inputLine.substring(index, index + 7);
                System.out.println(id);

                return extractUrl(id);
            }
        }

        System.err.println("Error getEpisode: " + spec);

        return 1;
    }

    private int extractUrl(String id) {
        ArrayList<String> lines2 = http("http://watchseries.ag//open/cale/"
                + id + ".html");
        for (String inputLine : lines2) {
            if (inputLine.indexOf("http://happystreams.net") != -1) {
                System.out.println(inputLine);
                String search = "http://happystreams.net/";
                int index = inputLine.indexOf(search) + search.length();
                String urlRest = inputLine.substring(index);
                String url = urlRest.substring(0, urlRest.indexOf("\""));

                return openNext(search + url, id);
            }
        }
        return 2;
    }

    private int openNext(String string, String id) {
        System.out.println("**** " + string);

        ArrayList<String> lines2 = http(string);
        ArrayList<String> data = new ArrayList<>();
        for (String inputLine : lines2) {

            if (inputLine.indexOf("hidden") != -1) {

                String search = "value=\"";
                int index = inputLine.indexOf(search) + search.length();
                String urlRest = inputLine.substring(index);
                String url = urlRest.substring(0, urlRest.indexOf("\""));
                System.out.println(inputLine);
                System.out.println("\t[" + url + "]" + data.size());
                data.add(url);
            }
        }

        return openDL(data, id);
    }

    private int openDL(ArrayList<String> data, String id) {
        System.out.println("**** faking post");
        HttpURLConnection connection = null;
        int result = 5;
        try {
            // Create connection
            URL url = new URL("http://happystreams.net/dl");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Origin", "http://happystreams.net");
            connection.setRequestProperty("Referer",
                    "http://happystreams.net/cmve2msgoeva");
            connection
                    .setRequestProperty(
                            "User-Agent",
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");

            String urlParameters = "op=" + data.get(0) + "&usr_login=&id="
                    + data.get(2) + "&fname=" + data.get(3)
                    + "&referer=http%3A%2F%2Fwatchseries.ag%2Fopen%2Fcale%2F"
                    + id + ".html&hash=" + data.get(5)
                    + "&imhuman=Proceed+to+video";
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

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if
            // not Java 5+
            String line;
            while ((line = rd.readLine()) != null) {

                // System.out.println("DL: " + line);

                if (line.indexOf("<span id='vplayer'><img src=") != -1) {
                    System.out.println("**** SERVER: " + line);
                    String ipSearch = "http://";
                    int indexA = line.indexOf(ipSearch);
                    int from = indexA + ipSearch.length();
                    String hostPort = line.substring(from,
                            line.indexOf("/", from));
                    System.out.println("**** hostPort: " + hostPort);
                    this.hostPort = hostPort;
                }
                if (line.indexOf("|flv|") != -1) {
                    System.out.println("**** finding url in line: " + line);
                    System.out.println(line);
                    String searchA = "|flv|";
                    int indexA = line.indexOf(searchA);
                    int indexB = line.indexOf("|file|");

                    String dlId = line.substring(indexA + searchA.length(),
                            indexB);
                    System.out.println("**** dlId: " + dlId);

                    // http://89.46.102.246:8777/ouw2gsrlj7awvk4say6gx3vbmh4r2ukpm3iy3paimr4y4ra2oz25sxgwgwda/v.flv?start=0
                    // http://89.46.102.246:8777/ouw2gsrlj7awvk4say6gx3vbmh4r2ukpm3iy3paimr4y4ra2oz25sxgwgwda/v.flv?start=0
                    // http://94.176.148.18:8777/o4w2hmlmj7awvk4say6gxz5xngwk6mbivywz37idjrz5safohlaqpoyxsagq/v.flv?start=0

                    result = readFile(dlId);

                    break;
                }
                if (line.indexOf("|mp4|") != -1) {
                    System.out.println("**** finding url in line: " + line);
                    System.out.println(line);
                    String searchA = "|mp4|";
                    int indexA = line.indexOf(searchA);
                    int indexB = line.indexOf("|file|");

                    String dlId = line.substring(indexA + searchA.length(),
                            indexB);
                    System.out.println("**** dlId: " + dlId);

                    // http://89.46.102.246:8777/ouw2gsrlj7awvk4say6gx3vbmh4r2ukpm3iy3paimr4y4ra2oz25sxgwgwda/v.flv?start=0
                    // http://89.46.102.246:8777/ouw2gsrlj7awvk4say6gx3vbmh4r2ukpm3iy3paimr4y4ra2oz25sxgwgwda/v.flv?start=0
                    // http://94.176.148.18:8777/o4w2hmlmj7awvk4say6gxz5xngwk6mbivywz37idjrz5safohlaqpoyxsagq/v.flv?start=0

                    result = readFile(dlId);

                    break;
                }
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    private int readFile(String dlId) {
        System.out.println("*** Sending Post...");
        int result = 10;
        try {
            // 94.176.148.18:8777/o4w2hmlmj7awvk4say6gxz5xngwk6mbivywz37idjrz5safohlaqpoyxsagq/v.flv?start=0
            URL url = new URL("http://" + this.hostPort + "/" + dlId
                    + "/v.flv?start=0");
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestProperty("Referer",
                    "http://happystreams.net/dl");
            connection
                    .setRequestProperty(
                            "User-Agent",
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
            InputStream input = connection.getInputStream();
            byte[] buffer = new byte[1024 * 50];
            int n = -1;


            OutputStream output = new FileOutputStream(new File("download/"
                    + filename));

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
                    System.out.println(spec + " ("
                            + totalMB + " mb) kb/s:" + nkb / dts);
                    n2 = 0;
                    last = System.currentTimeMillis();
                }
                output.write(buffer, 0, n);
            }
            output.close();
            System.out.println("Done:" + spec + " " + Math.round(bytesRead / 1024.0 / 1024.0) + " mb");
            result = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String removeShit(String name2) {
        return name2.replace(':', '_').replace('.', '_').replace('/', '_');
    }

    private ArrayList<String> http(String spec) {
        ArrayList<String> lines = new ArrayList<>();

        try {
            URL yahoo = new URL(spec);
            URLConnection yc = yahoo.openConnection();
            yc.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                lines.add(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

}
