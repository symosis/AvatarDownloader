package main2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RipManager
{

	public RipModel model;

	public RipManager()
	{
	}

	public void getProviderUrls()
	{
		ExecutorService pool = Executors.newFixedThreadPool(3);

		for (final Episode episode : model.episodes)
		{
			pool.execute(new Runnable()
			{
				public void run()
				{
					loadEpisodePage(episode);
				}
			});
		}

		pool.shutdown();
		try
		{
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private void loadEpisodePage(Episode episode)
	{
		System.out.println("Load episode page: " + episode.wsUrl);
		try
		{
			Connection con = Jsoup.connect(episode.wsUrl);
			con.userAgent(Main3.USER_AGENT);

			Document doc = con.get();
			Elements links = doc.select(".tab-english .buttonlink");
			int index = 0;
			for (Element link : links)
			{
				episode.addCale(index, "http://watchseries.ag" + link.attr("href"), link.attr("title"));
				index++;
			}
			
			System.out.println("INDEX: " + index);
		}
		catch (Throwable e)
		{
			System.err.println("Error: Load episode page: " + episode.wsUrl);
		}
	}
	
	
}
