import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int kServers = 5;          // Toplam sunucu sayısı
        int totalRequests = 10000; // Simüle edilecek toplam istek (request) sayısı

        System.out.println("=== Yük Dengeleyici (Load Balancer) Simülasyonu Başlıyor ===");
        System.out.println("Sunucu Sayısı: " + kServers);
        System.out.println("Toplam İstek: " + totalRequests);
        System.out.println("Durum: Sunucu performansları zamanla değişiyor (Non-stationary) ve gürültülü.\n");

        // ---------------------------------------------------------
        // TEST 1: Klasik Round-Robin Algoritması
        // ---------------------------------------------------------
        NonStationaryServers envRoundRobin = new NonStationaryServers(kServers, 0.01, 0.1);
        double totalLatencyRR = 0.0;
        int rrIndex = 0;

        for (int i = 0; i < totalRequests; i++) {
            int chosenServer = rrIndex % kServers;
            rrIndex++;
            double latency = envRoundRobin.getLatency(chosenServer);
            totalLatencyRR += latency;
        }

        double avgLatencyRR = totalLatencyRR / totalRequests;

        // ---------------------------------------------------------
        // TEST 2: Agentic (Öğrenen) Softmax Algoritması
        // ---------------------------------------------------------
        NonStationaryServers envSoftmax = new NonStationaryServers(kServers, 0.01, 0.1);
        SoftmaxLoadBalancer softmaxAgent = new SoftmaxLoadBalancer(kServers, 0.1, 0.1);
        double totalLatencySoftmax = 0.0;

        for (int i = 0; i < totalRequests; i++) {
            int chosenServer = softmaxAgent.selectServer();
            double latency = envSoftmax.getLatency(chosenServer);
            totalLatencySoftmax += latency;
            softmaxAgent.updateKnowledge(chosenServer, latency);
        }

        double avgLatencySoftmax = totalLatencySoftmax / totalRequests;

        // ---------------------------------------------------------
        // SONUÇLARIN YAZDIRILMASI
        // ---------------------------------------------------------
        System.out.println("=== SONUÇLAR ===");
        System.out.printf("Round-Robin Ortalama Gecikme : %.4f saniye\n", avgLatencyRR);
        System.out.printf("Softmax Ortalama Gecikme     : %.4f saniye\n", avgLatencySoftmax);

        if (avgLatencySoftmax < avgLatencyRR) {
            double improvement = ((avgLatencyRR - avgLatencySoftmax) / avgLatencyRR) * 100;
            System.out.printf("\nBAŞARILI: Softmax ajanımız Round-Robin'den %%%.2f daha hızlı çalıştı!\n", improvement);
        } else {
            System.out.println("\nİLGİNÇ: Round-Robin bu senaryoda şans eseri daha iyi sonuç verdi.");
        }
    }
}

// ---------------------------------------------------------
// YARDIMCI SINIFLAR (Aynı dosya içinde kalabilir)
// ---------------------------------------------------------

class NonStationaryServers {
    private final int kServers;
    private final double[] trueLatencies;
    private final double driftStd;
    private final double noiseStd;
    private final Random random;

    public NonStationaryServers(int kServers, double driftStd, double noiseStd) {
        this.kServers = kServers;
        this.driftStd = driftStd;
        this.noiseStd = noiseStd;
        this.random = new Random();
        this.trueLatencies = new double[kServers];

        for (int i = 0; i < kServers; i++) {
            this.trueLatencies[i] = 0.5 + random.nextDouble();
        }
    }

    public double getLatency(int serverIndex) {
        double currentLatency = trueLatencies[serverIndex] + random.nextGaussian() * noiseStd;
        for (int i = 0; i < kServers; i++) {
            trueLatencies[i] += random.nextGaussian() * driftStd;
        }
        return Math.max(0.01, currentLatency);
    }
}

class SoftmaxLoadBalancer {
    private final int kServers;
    private final double tau;
    private final double alpha;
    private final double[] qValues;
    private final Random random;

    public SoftmaxLoadBalancer(int kServers, double temperature, double alpha) {
        this.kServers = kServers;
        this.tau = temperature;
        this.alpha = alpha;
        this.qValues = new double[kServers];
        this.random = new Random();
    }

    public int selectServer() {
        double[] preferences = new double[kServers];
        double maxPref = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < kServers; i++) {
            preferences[i] = qValues[i] / tau;
            if (preferences[i] > maxPref) {
                maxPref = preferences[i];
            }
        }

        double[] expPrefs = new double[kServers];
        double sumExp = 0.0;
        for (int i = 0; i < kServers; i++) {
            expPrefs[i] = Math.exp(preferences[i] - maxPref);
            sumExp += expPrefs[i];
        }

        double[] probabilities = new double[kServers];
        for (int i = 0; i < kServers; i++) {
            probabilities[i] = expPrefs[i] / sumExp;
        }

        double randVal = random.nextDouble();
        double cumulativeProbability = 0.0;

        for (int i = 0; i < kServers; i++) {
            cumulativeProbability += probabilities[i];
            if (randVal <= cumulativeProbability) {
                return i;
            }
        }
        return kServers - 1;
    }

    public void updateKnowledge(int serverIndex, double latency) {
        double reward = -latency;
        qValues[serverIndex] = qValues[serverIndex] + alpha * (reward - qValues[serverIndex]);
    }
}
