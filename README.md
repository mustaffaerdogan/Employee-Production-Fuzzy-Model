# Çalışan Üretim Performansı Tahmin Sistemi

Bulanık mantık (Fuzzy Logic) kullanarak çalışanların üretim performansını tahmin eden Java uygulaması.

## 📋 İçindekiler

- [Özellikler](#özellikler)
- [Gereksinimler](#gereksinimler)
- [Kurulum](#kurulum)
- [Kullanım](#kullanım)
- [Proje Yapısı](#proje-yapısı)
- [Bulanık Mantık Modeli](#bulanık-mantık-modeli)
- [Geliştirme](#geliştirme)
- [Lisans](#lisans)

## ✨ Özellikler

- **Bulanık Mantık Tabanlı Tahmin**: Çalışanların tecrübe, yaş ve cinsiyet bilgilerine göre üretim performansını tahmin eder
- **Görselleştirme**: Üyelik fonksiyonlarını ve bulanık mantık kurallarını grafik olarak gösterir
- **Esnek Model**: FCL (Fuzzy Control Language) dosyası ile kolayca düzenlenebilir kural seti

## 🔧 Gereksinimler

- **Java**: JDK 8 veya üzeri
- **jFuzzyLogic Kütüphanesi**: Proje içinde `lib/jFuzzyLogic.jar` olarak mevcuttur
- **IDE**: Eclipse (önerilen) veya herhangi bir Java IDE

## 📦 Kurulum

1. Projeyi klonlayın:
```bash
git clone <repository-url>
cd BulanikOdev
```

2. Eclipse'te projeyi açın:
   - File → Import → Existing Projects into Workspace
   - Proje klasörünü seçin
   - Import işlemini tamamlayın

3. jFuzzyLogic.jar'ın classpath'te olduğundan emin olun:
   - Proje sağ tık → Properties → Java Build Path → Libraries
   - `lib/jFuzzyLogic.jar` dosyasının ekli olduğunu kontrol edin

## 🚀 Kullanım

1. Projeyi çalıştırın (`Main.java` içindeki `main` metodunu çalıştırın)

2. Program sizden şu bilgileri isteyecektir:
   - **Tecrübe (yıl)**: Çalışanın iş tecrübesi (örn: 5, 10, 15)
   - **Cinsiyet**: 0 = Kadın, 1 = Erkek
   - **Yaş (yıl)**: Çalışanın yaşı (örn: 25, 40, 55)

3. Program, girdiğiniz değerlere göre:
   - Üyelik fonksiyonlarını grafik olarak gösterecek
   - Tahmini üretilen parça sayısını hesaplayacak

### Örnek Kullanım

```
Tecrübe (yıl): 10
Cinsiyet (0 = Kadın, 1 = Erkek): 1
Yaş (yıl): 35
```

## 📁 Proje Yapısı

```
BulanikOdev/
├── src/
│   └── pkt/
│       ├── Main.java              # Ana uygulama sınıfı
│       ├── CalisanUretim.java     # Bulanık mantık model sınıfı
│       └── CalisanUretim.fcl      # Bulanık mantık kuralları (FCL)
├── lib/
│   └── jFuzzyLogic.jar            # jFuzzyLogic kütüphanesi
├── bin/                           # Derlenmiş class dosyaları
├── .gitignore                     # Git ignore dosyası
├── .gitattributes                 # Git attributes dosyası
└── README.md                      # Bu dosya
```

## 🧠 Bulanık Mantık Modeli

### Giriş Değişkenleri

#### 1. Tecrübe (yıl)
- **Az**: 0-5 yıl arası
- **Orta**: 5-15 yıl arası
- **Yüksek**: 10+ yıl

#### 2. Cinsiyet
- **Kadın**: 0
- **Erkek**: 1

#### 3. Yaş (yıl)
- **Genç**: 18-35 yaş arası
- **Orta**: 30-50 yaş arası
- **Yaşlı**: 45-60 yaş arası

### Çıkış Değişkeni

#### Parça Sayısı
- **Düşük**: 0-100 parça
- **Orta**: 50-350 parça
- **Yüksek**: 300-500+ parça

### Kural Seti

Model, 18 adet bulanık mantık kuralı içermektedir. Kurallar, tecrübe, yaş ve cinsiyet kombinasyonlarına göre üretim performansını belirler.

**Örnek Kurallar:**
- Yüksek tecrübe + Genç yaş → Yüksek üretim
- Az tecrübe + Yaşlı → Düşük üretim
- Orta tecrübe + Orta yaş + Erkek → Yüksek üretim

Detaylı kurallar için `src/pkt/CalisanUretim.fcl` dosyasına bakabilirsiniz.

### Defuzzification

Çıkış değeri, **COG (Center of Gravity)** yöntemi ile hesaplanmaktadır.

## 💻 Geliştirme

### Modeli Düzenleme

Bulanık mantık kurallarını veya üyelik fonksiyonlarını değiştirmek için `src/pkt/CalisanUretim.fcl` dosyasını düzenleyin.

### Sonuçları Görüntüleme

`Main.java` dosyasındaki 22. satırdaki yorumu kaldırarak sonuçları konsola yazdırabilirsiniz:

```java
System.out.println(model);
```

## 📄 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.

## 👥 Katkıda Bulunanlar

- Proje geliştiricisi

## 📝 Notlar

- jFuzzyLogic kütüphanesi proje içinde `lib/jFuzzyLogic.jar` olarak bulunmaktadır
- FCL dosyası IEC 61131-7 standardına uygun olarak yazılmıştır
- Grafik görüntüleme için jFuzzyLogic'in JFuzzyChart sınıfı kullanılmaktadır

