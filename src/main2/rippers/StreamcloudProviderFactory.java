package main2.rippers;

import main2.EpisodeProvider.Status;
import main2.ProviderFactory;
import main2.ProviderJob;

public class StreamcloudProviderFactory extends ProviderFactory
{
	public StreamcloudProviderFactory()
	{
		parallelism = 10;
	}

	@Override
	public void startWork(ProviderJob providerJob)
	{
		try
		{
			StreamcloudRipper ripper = new StreamcloudRipper(providerJob);
		}
		catch (Throwable e)
		{
			System.err.println("Error working...");
			e.printStackTrace();
			providerJob.provider.status = Status.ERROR;
		}
	}
}
