package main2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import main2.EpisodeProvider.Status;

public class ProviderWorker
{
	private RipModel model;
	private String providerId;
	private ProviderFactory providerFactory;

	public ProviderWorker(RipModel model, String providerId, ProviderFactory providerFactory)
	{
		this.model = model;
		this.providerId = providerId;
		this.providerFactory = providerFactory;
	}

	public void start()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				goSync();
			}
		}).start();
	}

	protected void goSync()
	{
		ExecutorService pool = Executors.newFixedThreadPool(providerFactory.parallelism);

		Boolean running = true;
		while (running)
		{
			// System.out.println("Request");
			ProviderJob providerJob = model.requestJob(providerId);
			if (providerJob != null)
				pool.execute(runJob(providerJob));
			else
				running = false;

			waitPool(pool);
		}

		System.out.println("Shut down worker: " + providerId);
		pool.shutdown();
		try
		{
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		System.out.println("Shut down complete worker: " + providerId);
	}

	private static void waitPool(ExecutorService pool)
	{
		Future<?> x = pool.submit(new Runnable()
		{
			@Override
			public void run()
			{
			}
		});
		try
		{
			x.get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
		}
	}

	private Runnable runJob(final ProviderJob providerJob)
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				System.out.println("Running... " + providerJob.provider);

				providerFactory.work(providerJob);

			}
		};
	}
}
