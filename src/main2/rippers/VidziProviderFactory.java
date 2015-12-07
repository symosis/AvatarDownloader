package main2.rippers;

import main2.EpisodeProvider.Status;
import main2.ProviderFactory;
import main2.ProviderJob;

public class VidziProviderFactory extends ProviderFactory
{
	public VidziProviderFactory()
	{
		parallelism = 1;
	}

	@Override
	public void startWork(ProviderJob providerJob)
	{
		try
		{
			VidziRipper ripper = new VidziRipper(providerJob);
		}
		catch (Throwable e)
		{
			System.err.println("Error working...");
			e.printStackTrace();
			providerJob.provider.status = Status.ERROR;
		}
	}
}
