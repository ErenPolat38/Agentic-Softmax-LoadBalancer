# Dağıtık Sistemler için Softmax Tabanlı Yük Dengeleyici (Load Balancer)

Bu proje, gecikme (latency) süreleri zamanla değişen (non-stationary) ve gürültülü (noisy) olan K adet sunucudan oluşan bir kümeye gelen istekleri optimize eden zeki bir yük dengeleyici simülasyonudur.

Klasik Round-Robin yaklaşımı yerine Pekiştirmeli Öğrenme (Reinforcement Learning) tabanlı Softmax Action Selection algoritması kullanılarak toplam bekleme süresi minimize edilmiştir.

## Özellikler (Features)
* **Non-Stationary Ortam Simülasyonu:** Sunucu performanslarının zaman içindeki kaymasını (drift) simüle eden ortam.
* **Agentic Yaklaşım:** Ortamı gözlemleyen ve Q-Değerlerini güncelleyen öğrenen bir ajan.
* **Nümerik Stabilite:** Softmax implementasyonunda oluşabilecek `Overflow` hatalarına karşı matematiksel koruma.
* **Karşılaştırmalı Analiz:** Klasik Round-Robin ile AI tabanlı Softmax algoritmasının aynı ortamda 10.000 istek üzerinden performans kıyaslaması.

## Kullanılan Teknolojiler
* **Dil:** Java 
* **Tasarım:** Nesne Yönelimli Programlama (OOP)
* **Yapay Zeka Destekli Geliştirme (Agentic Workflow):** Gemini

## Nümerik Stabilite Çözümü
Softmax fonksiyonu hesaplanırken $e^x$ işlemi sırasında değerlerin çok büyümesi (Overflow) veya çok küçülmesi (Underflow) ihtimaline karşı şu yöntem uygulanmıştır:
$$P(a) = \frac{e^{Q(a) - \max(Q)}}{\sum e^{Q(i) - \max(Q)}}$$
Bu sayede en yüksek üstel değer $e^0 = 1$ olarak sabitlenmiş ve programın sonsuz (Infinity) değerler üretmesi engellenmiştir.

## Kurulum ve Çalıştırma
1. Bu repoyu bilgisayarınıza klonlayın:
   `git clone [repo_linkiniz]`
2. Projeyi favori IDE'nizde (ör. IntelliJ IDEA) açın.
3. `src` dizini altındaki `Main.java` dosyasını çalıştırın.
4. Konsol üzerinden iki algoritmanın ortalama gecikme sonuçlarını (saniye cinsinden) gözlemleyin.
