package main2;


public abstract class ProviderFactory {

    public int parallelism;

    public void work(ProviderJob providerJob) {
        System.out.println("ProviderJob working..." + providerJob.provider.caleHref);

        startWork(providerJob);
    }

    abstract public void startWork(ProviderJob providerJob);
}
