# Çalışan Üretim Performansı – Bulanık Mantık Modeli Raporu

Öğrenci Adı Soyadı: <AD SOYAD>
Öğrenci No: <NUMARA>
Ders / Dönem: <DERS KODU - 2025 Güz>
Teslim Tarihi: 09.11.2025

Not: Bu rapor taslağı, ödev teslim formatına uygun bölümlerle hazırlanmış bir iskelet olup; çıktı, görsel ve kaynaklar son çalıştırma ekran görüntüleri ile güncellenmelidir. Yazı tipi/puan (11pt Times New Roman) ve sayfa düzeni Word/PDF aktarımında ayarlanmalıdır.

## 1. Giriş

Bu çalışmada, bir çalışanın belirli bir periyotta üreteceği parça sayısı; iş tecrübesi (yıl), cinsiyet (0=Kadın, 1=Erkek) ve yaş (yıl) değişkenlerine bağlı olarak bulanık mantık ile tahminlenmiştir. Model, jFuzzyLogic kütüphanesi ve IEC 61131-7 uyumlu FCL dosyaları ile gerçekleştirilmiştir.

## 2. Literatür ve Değişken Seçimi (Referanslı)

- Üretim performansında tecrübe ve yaşın etkisi (ör. iş öğrenme eğrileri, ergonomi ve yaşlanma etkisi). [Örnek kaynak: Wright, 1936; Ilgın & Ornek, 2019]
- Cinsiyet değişkeninin iş akışları ve üretkenlik üzerindeki etkisi (iş tanımı ve ergonomi bağlamında).

Kaynaklar bölümünde en az 3–5 akademik/kitap/rapor referansı verilmelidir. Metin içinde uygun atıf yapılmalıdır.

## 3. Model Tasarımı

### 3.1. Girdi/Çıktı Sınırları ve Üyelik Fonksiyonları

- Tecrübe: Az (0–5), Orta (5–15), Yüksek (10–30+)
- Cinsiyet: 0 (Kadın), 1 (Erkek) – ikili bulanık kümeler
- Yaş: Genç (18–35), Orta (30–50), Yaşlı (45–60)
- Çıkış (Parça Sayısı): Düşük (0–100), Orta (50–350), Yüksek (300–500+)

Bu sınırların seçim mantığı ve literatür desteği açıklanmalıdır (neden bu aralıklar?).

### 3.2. Kural Tabanı

Toplam 18 kural kullanılmıştır. Örnek:

- IF Tecrübe=Yüksek AND Yaş=Genç THEN Parça Sayısı=Yüksek
- IF Tecrübe=Az AND Yaş=Yaşlı THEN Parça Sayısı=Düşük

Tüm kurallar `src/pkt/CalisanUretim.fcl` dosyasında listelenmiştir.

### 3.3. Çıkarım ve Durulama (Defuzzification)

- Çıkarım: AND=MIN, ACT=MIN, ACCU=MAX
- Durulama yöntemleri: COG (Ağırlık Merkezi) ve COA (Alan Merkezi) denenmiş ve karşılaştırılmıştır.

## 4. Örnek Çalışma ve Hesap Detayı (COG)

Örnek girdi: Tecrübe=1, Cinsiyet=1 (Erkek), Yaş=22.

Adımlar:
1) Girdi üyelik dereceleri hesaplanır (Tecrübe: Az≈μ_Az(1), Orta≈μ_Orta(1), vb.).
2) Her kural için ateşlenme derecesi: α_i = min(μ_Tecrübe, μ_Yaş, μ_Cinsiyet)
3) Her kuralın çıktısı: ilgili çıkış teriminin üyelik fonksiyonu μ_Term(x) α_i ile kesilir (clipping)
4) Agregasyon: μ_agg(x) = max_i min(α_i, μ_Term_i(x))
5) COG formülü ile durulama:

$$ y^* = \frac{\int x \, \mu_{agg}(x)\, dx}{\int \mu_{agg}(x)\, dx} $$

Bu örnekte program çıktısı: COG ≈ 240, COA ≈ 220.

Örnek konsol çıktısı:

```
Tecrübe (yıl): 1
Cinsiyet (0 = Kadın, 1 = Erkek): 1
Yaş (yıl): 22
[COG] Tahmini Üretilen Parça Sayısı: 240
[COA] Tahmini Üretilen Parça Sayısı: 220
```

Ekran görüntüsü (Şekil 1): `COG_girdi_uyelikleri.png` (Tecrübe, Yaş, Cinsiyet üyelik fonksiyonları) – buraya eklenecek.
Ekran görüntüsü (Şekil 2): `COG_parcaSayisi_aggregasyon.png` (çıktı agregasyon + crisp nokta) – buraya eklenecek.

Not: Rapora bu örnek için ara değerler (α_i) ve en azından seçili 2–3 x noktasında μ_agg(x) örneklemesi eklenebilir.

## 5. Durulama Yöntemleri Karşılaştırması (COG vs COA)

Aşağıdaki senaryolar denenmiştir:

| Tecrübe | Cinsiyet | Yaş | COG | COA |
|---------|----------|-----|-----|-----|
| 5 | 1 | 30 | 422 | 425 |
| 2 | 0 | 22 | 200 | 200 |
| 12 | 1 | 35 | 414 | 415 |
| 0 | 0 | 55 | 33 | 30 |
| 20 | 1 | 50 | 422 | 425 |

Yorum: COG ve COA sonuçları büyük ölçüde benzer; bazı uç kombinasyonlarda (düşük tecrübe + yaşlı gibi) küçük farklar gözlenmiştir.

## 6. Taralı Alan / Çıktı Aggregasyon Görselleri

- JFuzzyChart ile COG modelinde giriş ve çıkış üyelik fonksiyonları + agregasyon alanı görselleştirilmiştir.
- COA yöntemi için yalnızca sayısal karşılaştırma verilmiş, grafik tekrarı önlenerek rapor sade tutulmuştur.
- İsteğe bağlı minimal çizgi görseli: `ParcaSayisi_COG_crisp.png` (OutputAreaPlotter ile üretilen dikey çizgi).
- Şekil 3: COG Üyelik Fonksiyonları (eklenecek)
- Şekil 4: COG Çıktı Aggregasyon ve Crisp Değer (eklenecek)

## 7. Sonuç ve Değerlendirme

Model, giriş değişkenlerine göre parça sayısını sezgisel ve yorumlanabilir biçimde tahmin etmektedir. Farklı durulama yöntemleri benzer sonuçlar vermiş; kural tabanı ve üyelik fonksiyonları işin doğasına göre uyarlanabilir.
