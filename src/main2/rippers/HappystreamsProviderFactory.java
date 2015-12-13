package main2.rippers;

import main2.EpisodeProvider.Status;
import main2.ProviderFactory;
import main2.ProviderJob;

public class HappystreamsProviderFactory extends ProviderFactory {
    public HappystreamsProviderFactory() {
        parallelism = 10;
    }

    @Override
    public void startWork(ProviderJob providerJob) {
        try {
            HappystreamsRipper ripper = new HappystreamsRipper(providerJob);
        } catch (Throwable e) {
            System.err.println("Error working...");
            e.printStackTrace();
            providerJob.provider.errorCount++;
            if (providerJob.provider.errorCount > 20)
                providerJob.provider.status = Status.ERROR;
            else
                providerJob.provider.status = Status.UNWORKED;
        }
    }
}
