package main;

import main.EpisodeProvider.Status;

import java.util.ArrayList;

public class Episode {
    public ArrayList<EpisodeProvider> providers = new ArrayList<>();
    public String name;
    public String period;
    public String wsUrl;

    public Episode(String name, String period, String wsUrl) {
        this.name = name;
        this.period = period;
        this.wsUrl = wsUrl;
    }

    public void addCale(int index, String href, String providerName) {
        // System.out.println("Adding Cale: " + href + " " + providerName);

        EpisodeProvider provider = new EpisodeProvider(index, providerName, href, this);
        providers.add(provider);

    }

    public boolean hasUnworkedProvider(String providerId) {
        for (EpisodeProvider provider : providers) {
            if (provider.providerId.equals(providerId) && provider.status == Status.UNWORKED)
                return true;
        }
        return false;
    }

    public ProviderJob createProviderJob(String providerId) {
        for (EpisodeProvider provider : providers) {
            if (provider.providerId.equals(providerId) && provider.status == Status.UNWORKED) {
                provider.status = Status.WORKING;
                return new ProviderJob(provider);
            }
        }

        System.err.println("Could not create job: " + name + " " + providerId);
        return null;
    }

    public int getCompleted() {
        int result = 0;
        for (EpisodeProvider provider : providers) {
            if (provider.status == Status.COMPLETED)
                result++;
        }
        return result;
    }

    public int getRunningJobs() {
        int result = 0;
        for (EpisodeProvider provider : providers) {
            if (provider.status == Status.WORKING)
                result++;
        }
        return result;
    }
}
