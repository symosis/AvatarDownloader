package main.rippers;

import main.EpisodeProvider.Status;
import main.ProviderFactory;
import main.ProviderJob;

public class VidupmeProviderFactory extends ProviderFactory {
    public VidupmeProviderFactory() {
        parallelism = 2;
    }

    @Override
    public void startWork(ProviderJob providerJob) {
        try {
            VidupmeRipper ripper = new VidupmeRipper(providerJob);
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
