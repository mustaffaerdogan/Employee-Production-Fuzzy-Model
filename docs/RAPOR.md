# Çalışan Üretim Performansı İçin Bulanık Mantık Modeli  
## (Tecrübe – Yaş – Cinsiyet Temelli Tahmin)

Öğrenci Adı Soyadı: …  
Öğrenci No: …  
Ders / Dönem: …  
Teslim Tarihi: 09.11.2025  

---

## 1. Giriş

Günümüz üretim sistemlerinde çalışan performansının doğru tahmin edilmesi; kapasite planlama, vardiya düzenleme, iş gücü planlaması ve prim sistemlerinin tasarımı açısından kritik bir öneme sahiptir. Özellikle manuel montaj hatları gibi insan odaklı süreçlerde, bir çalışanın belirli bir periyotta (örneğin 8 saatlik vardiyada) üreteceği parça sayısı; iş tecrübesi, yaş, cinsiyet, ergonomik iş tasarımı ve motivasyon gibi çok sayıda faktörden etkilenmektedir. Bu ilişkiler çoğu zaman doğrusal değildir ve klasik istatistiksel modellerin (basit lineer regresyon vb.) varsayımlarını ihlal eder.

Bu projede, bir çalışanın üreteceği parça sayısını tahmin etmek için üç girdi değişkenine dayalı bir bulanık mantık modeli geliştirilmiştir:

- İş Tecrübesi (yıl)  
- Cinsiyet (0 = Kadın, 1 = Erkek)  
- Yaş (yıl)  

Çıkış değişkeni ise, bir vardiyada üretilen parça sayısıdır. Model, jFuzzyLogic kütüphanesi ve IEC 61131-7 uyumlu FCL dosyaları kullanılarak gerçekleştirilmiştir. Durulama (defuzzification) aşamasında iki farklı yöntem (Ağırlık Merkezi – COG ve Alan Merkezi – COA) uygulanmış ve sonuçları senaryolar üzerinden karşılaştırılmıştır.

---

## 2. Çalışan Performansını Etkileyen Faktörler ve Literatür Özeti

Bu bölümde, modelde kullanılan girdi değişkenlerinin (iş tecrübesi, yaş, cinsiyet) üretim performansı üzerindeki etkisi literatür bulguları ile özetlenmektedir. Ayrıntılı analizler `arastirma.txt` dosyasında yapılan çalışmadan özetlenmiştir.

### 2.1 İş Tecrübesi, Öğrenme Eğrileri ve Performans

Üretim performansı üzerindeki en temel değişkenlerden biri iş tecrübesidir. Wright (1936) tarafından havacılık endüstrisinde ortaya konan öğrenme eğrisi teorisi, bir görevin kümülatif tekrar sayısı iki katına çıktığında bu göreve harcanan süre veya maliyetin belirli bir oranda azaldığını öne sürer. Çeşitli çalışmalar, tecrübe ile performans arasında güçlü ancak doğrusal olmayan bir ilişki bulunduğunu göstermektedir. İlk yıllarda öğrenme hızlıdır, performans üstel biçimde artar; ilerleyen yıllarda ise artış hızlanarak değil, yavaşlayarak devam eder ve belli bir noktadan sonra “performans platosu”na ulaşılır.

Bu yapı, tecrübenin “Az, Orta, Yüksek” gibi dilsel terimlerle ifade edilmesini ve bu terimlerin üst üste binen üyelik fonksiyonları ile temsil edilmesini uygun hale getirmektedir. Dolayısıyla klasik “Performans = β₀ + β₁·Tecrübe” tipi doğrusal modellerin yakalayamadığı karmaşıklık, bulanık mantık yaklaşımı ile daha gerçekçi biçimde modellenebilmektedir.

### 2.2 Yaş Faktörünün Ergonomik ve Fizyolojik Etkileri

Yaş, özellikle fiziksel işlerde performansı etkileyen önemli bir faktördür. Ergonomi ve iş fizyolojisi literatürü, kas gücü, aerobik kapasite ve reaksiyon süresi gibi göstergelerin genellikle 25–35 yaş aralığında zirve yaptığını, 40–45 yaş sonrasında ise kademeli bir azalış gösterdiğini ortaya koymaktadır. Bununla birlikte, yaşın tek başına performansı belirlemediği; uzun yıllara dayalı tecrübe, görev bilgisi, hata oranındaki azalma ve iş temposundaki istikrar gibi faktörlerin yaşa bağlı fizyolojik dezavantajları telafi edebildiği vurgulanmaktadır.

Bu nedenle modelde “Genç, Orta, Yaşlı” yaş kümeleri, hem fizyolojik eşikler hem de tecrübe ile etkileşim dikkate alınarak tanımlanmıştır. Örneğin, “Tecrübe Yüksek ve Yaş Yaşlı” durumunda performans orta-yüksek düzeyde kalabilirken, “Tecrübe Az ve Yaş Yaşlı” kombinasyonu daha düşük performansa işaret etmektedir.

### 2.3 Cinsiyet Değişkeninin Ergonomi ve Antropometri Açısından Değerlendirilmesi

Cinsiyet değişkeni, bu modelde bir önyargı unsuru olarak değil, ergonomik uyum açısından bir vekil (proxy) değişken olarak ele alınmıştır. Literatür, istatistiksel olarak erkeklerin ortalama kas kütlesi ve özellikle üst gövde gücü bakımından daha yüksek değerlere sahip olduğunu, buna karşılık bazı ince motor görevlerinde kadınların daha avantajlı olabileceğini göstermektedir. Ayrıca boy, kol uzunluğu gibi antropometrik farklılıklar iş istasyonu tasarımını ve erişim mesafelerini etkileyebilir.

Modelde cinsiyet “0 = Kadın, 1 = Erkek” olarak sayısal bir girdi şeklinde alınmıştır. Bu değişken, “işin gerektirdiği fiziksel zorlanma” ile “çalışanın ortalama fizyolojik kapasitesi” arasındaki potansiyel uyumsuzluğu temsil etmektedir. Ergonomik açıdan iyi tasarlanmamış işlerde bu uyumsuzluk, bir vardiyada üretilen parça sayısına yansıyabilmektedir.

---

## 3. Bulanık Model Tasarımı

Model, FCL dilinde tanımlanan iki dosya üzerinde kuruludur:

- `CalisanUretim.fcl` – COG durulama yöntemi  
- `CalisanUretim_COA.fcl` – COA durulama yöntemi  

Bu dosyalar, `src/pkt` paketinde yer almakta ve Java tarafında `CalisanUretim` sınıfı ile yüklenmektedir.

### 3.1 Girdi ve Çıktı Değişkenleri

Girdi değişkenleri ve üyelik fonksiyon aralıkları özetle şu şekilde tasarlanmıştır:

- **Tecrübe (yıl)**  
  - Az: 0–5  
  - Orta: 5–15  
  - Yüksek: 10–30+  

Bu aralıklar, kariyer başlangıç dönemini (0–5), yetkinliğe geçiş sürecini (5–15) ve ustalık/plato dönemini (10–30+) temsil eder. Orta ve Yüksek kümeleri arasında (10–15) bilinçli bir örtüşme bırakılmıştır.

- **Yaş (yıl)**  
  - Genç: 18–35  
  - Orta: 30–50  
  - Yaşlı: 45–60  

Genç kümesi fiziksel kapasitenin zirvede olduğu dönemi; yaşlı kümesi ise ergonomik risklerin ve yorgunluk etkisinin arttığı dönemi temsil eder. 30–50 aralığında hem Genç-Orta hem Orta-Yaşlı kümeleri arasında örtüşme bulunmaktadır.

- **Cinsiyet (0–1)**  
  - 0: Kadın  
  - 1: Erkek  

Cinsiyet değişkeni, ikili bir girdi olarak işlenmiş; FCL içinde buna göre uygun üyelik fonksiyonları tanımlanmıştır.

- **Çıkış: Parça Sayısı (adet)**  
  - Düşük: 0–100  
  - Orta: 50–350  
  - Yüksek: 300–500+  

Bu aralıklar, benzer üretim hatlarına ilişkin vaka çalışmaları esas alınarak belirlenmiştir. 50–100 ve 300–350 aralıklarında önemli örtüşmeler bulunmakta, böylece modelin küçük girdi değişimlerine karşı pürüzsüz ve tutarlı cevap üretmesi sağlanmaktadır.

### 3.2 Kural Tabanı

Modelde toplam 18 adet IF–THEN kuralı bulunmaktadır. Kuralların genel formu:

- IF Tecrübe is Yüksek AND Yaş is Genç THEN ParcaSayisi is Yüksek  
- IF Tecrübe is Az AND Yaş is Yaşlı THEN ParcaSayisi is Düşük  
- IF Tecrübe is Orta AND Yaş is Orta AND Cinsiyet is Erkek THEN ParcaSayisi is Orta/Yüksek  
- vb.

Bu kurallar, hem literatürdeki genel eğilimlere hem de sezgisel uzman bilgisine dayanarak oluşturulmuştur. Kural tabanı, `CalisanUretim.fcl` dosyasında ayrıntılı olarak yer almaktadır.

### 3.3 Çıkarım Mekanizması ve Durulama Yöntemleri

Modelde jFuzzyLogic’in sunduğu klasik Mamdani tipi çıkarım mekanizması kullanılmıştır:

- AND operatörü: MIN  
- OR operatörü (kullanıldığı yerlerde): MAX  
- Aktivasyon (ACT): MIN  
- Agregasyon (ACCU): MAX  

Durulama aşamasında iki yöntem denenmiştir:

1. **Ağırlık Merkezi (Center of Gravity – COG)**  
	- `CalisanUretim.fcl` dosyasında `METHOD : COG` ile tanımlanmıştır.  
	- Birleştirilmiş çıktı şeklinin kütle merkezini hesaplar:  
	  $$ z_{COG} = \frac{\int z \cdot \mu(z) \, dz}{\int \mu(z) \, dz} $$  
	- Endüstride en sık kullanılan ve en stabil yöntemdir.

2. **Alan Merkezi (Center of Area – COA / Bisector of Area – BOA)**  
	- `CalisanUretim_COA.fcl` dosyasında `METHOD : COA` ile tanımlanmıştır.  
	- Çıktı alanını iki eşit parçaya bölen noktayı bulur:  
	  $$ \int_{a}^{z_{COA}} \mu(z)\, dz = \int_{z_{COA}}^{b} \mu(z)\, dz $$  
	- Çoğunlukla COG ile karşılaştırma ve hassasiyet analizi için tercih edilir.

---

## 4. Java Uygulaması ve Sistem Mimarisi

Model, Java dili ve jFuzzyLogic kütüphanesi üzerinde gerçekleştirilmiştir. Uygulama mimarisi üç temel sınıfa dayanır:

- `CalisanUretim`  
- `Main`  
- `Scenarios`  

### 4.1 `CalisanUretim` Sınıfı

`CalisanUretim`, FCL dosyasını yükleyen ve verilen girişler için durulama sonucunu hesaplayan model sınıfıdır. İki kurucu metodu vardır:

1. **COG modeli (varsayılan):**  
	```java
	public CalisanUretim(double tecrube, double cinsiyet, double yas)
	```  
	Bu kurucu `CalisanUretim.fcl` dosyasını yükler, `Tecrube`, `Cinsiyet` ve `Yas` değişkenlerini atar ve COG durulama sonucu olan crisp `ParcaSayisi` değeri hesaplanır.

2. **Alternatif FCL ile model (COA vs):**  
	```java
	public CalisanUretim(String fclResourceName, double tecrube, double cinsiyet, double yas)
	```  
	Bu kurucu, verilen FCL dosya adını önce classpath üzerinden, bulunamazsa `src/pkt` veya `bin/pkt` altındaki dosya sistemi yolundan arar. Böylece alternatif durulama yöntemleri (burada COA) için aynı kural tabanını farklı defuzz stratejileriyle kullanmak mümkün hale gelmiştir.

Sınıfın `toString()` metodu, FIS içinden `ParcaSayisi` değişkenini okuyarak kullanıcı dostu bir metin döndürür:

```java
@Override
public String toString() {
	 double parcaSayisi = fis.getVariable("ParcaSayisi").getValue();
	 return "Tahmini Üretilen Parça Sayısı: " + Math.round(parcaSayisi);
}
```

### 4.2 `Main` Sınıfı: Etkileşimli Çalıştırma

`Main` sınıfı, kullanıcıdan konsol üzerinden veri alan ve COG/COA sonuçlarını gösteren ana uygulamadır. Program akışı şu şekildedir:

1. Kullanıcıdan sırasıyla tecrübe (yıl), cinsiyet (0/1) ve yaş (yıl) değerleri alınır.  
2. COG modeli oluşturulur:
	```java
	CalisanUretim modelCOG = new CalisanUretim(tecrube, cinsiyet, yas);
	System.out.println("[COG] " + modelCOG);
	```
3. COG modelinin üyelik fonksiyonları ve çıkış grafikleri, `JFuzzyChart` ile gösterilir (isteğe bağlı kapatılabilir):
	```java
	if (showChartsCOG) JFuzzyChart.get().chart(modelCOG.getModel());
	```
4. Aynı giriş değerleri için COA modeli çalıştırılır:
	```java
	CalisanUretim modelCOA = new CalisanUretim("CalisanUretim_COA.fcl", tecrube, cinsiyet, yas);
	System.out.println("[COA] " + modelCOA);
	```
5. Grafikler sadece COG modeli için açılarak görsel tekrar ve karışıklık önlenmiştir; COA sadece sayısal kıyas amaçlı kullanılmaktadır.

İsteğe bağlı olarak, `OutputAreaPlotter` sınıfı ile COG/COA crisp değerlerinin dikey çizgi olarak gösterildiği basit PNG çıktıları üretilebilmektedir (yorum satırı olarak eklenmiştir).

### 4.3 `Scenarios` Sınıfı: Senaryo Bazlı Karşılaştırma

`Scenarios` sınıfı, sabit 5 adet senaryo için COG ve COA sonuçlarını yan yana yazdıran bir test aracıdır. Örneğin:

- (Tecrübe=5, Cinsiyet=1, Yaş=30)  
- (Tecrübe=2, Cinsiyet=0, Yaş=22)  
- (Tecrübe=12, Cinsiyet=1, Yaş=35)  
- (Tecrübe=0, Cinsiyet=0, Yaş=55)  
- (Tecrübe=20, Cinsiyet=1, Yaş=50)  

Her senaryo için konsola:

```text
Tecrube=5.00, Cinsiyet=1, Yas=30 => COG=..., COA=...
```

şeklinde bir çıktı alınmakta ve bu değerler rapordaki karşılaştırma tablosunda kullanılmaktadır.

---

## 5. Örnek Çalışma ve COG/COA Çıktıları

Bu bölümde, raporda kullanılan ekran görüntüleriyle bire bir uyumlu olacak şekilde tek bir örnek girdi seti üzerinden COG ve COA durulama sonuçları özetlenmektedir. Örnek giriş değerleri:

- Tecrübe = 10 yıl  
- Cinsiyet = 1 (Erkek)  
- Yaş = 44  

### 5.1 Girdi Üyelik Derecelerinin Yorumu

Bu kombinasyonda Tecrübe=10 yılı, kullanılan üyelik fonksiyonlarına göre hem "Orta" hem de "Yüksek" kümelerine belirli derecelerde ait olmakta; Yaş=44 ise "Orta" ve "Yaşlı" kümeleri arasında bir geçiş bölgesine düşmektedir. Cinsiyet=1 değeri ise "Erkek" kümesine tam üyelik anlamına gelir. Girdi üyelik fonksiyonlarının şekli ve bu örneğin hangi bölgelerde kaldığı, Şekil 1'de gösterilen ekran görüntüsü ile desteklenmektedir (`girdi_uyelikler.png`).

<img width="310" height="556" alt="girdi_uyelikleri" src="https://github.com/user-attachments/assets/e36f34bd-ee00-4fa7-86bc-3b1ebe75ecc8" />


### 5.2 Çıktı Agregasyonu ve COG/COA Sonuçları

Bu girişler için `Main` sınıfı çalıştırıldığında, `ParcaSayisi` çıkış değişkeni için COG ve COA durulama yöntemleriyle elde edilen tahmini parç a sayısı değerleri konsola yazdırılmakta ve COG için agregasyon grafiği gösterilmektedir. Çıktının üyelik fonksiyonları, agregasyon (taralı alan) ve COG crisp noktası Şekil 2'de verilen `parca_uyelikler.png` ekran görüntüsünde birlikte gösterilmiştir.

<img width="306" height="179" alt="parca_uyelikleri" src="https://github.com/user-attachments/assets/dc932274-7520-49b3-b0ec-01ebb19a388c" />

Ayrıca, COG ve COA yöntemlerinin sayısal çıktılarının yan yana karşılaştırıldığı konsol görüntüsü, Şekil 3'te (`cog_coa_karsilastirma.png`) sunulmuştur. Bu şekil, aynı giriş için iki durulama yönteminin birbirine oldukça yakın sonuçlar verdiğini görsel olarak özetlemektedir.

<img width="1236" height="358" alt="cog_coa_karsilastirma" src="https://github.com/user-attachments/assets/6beee0db-de6a-4694-8db1-9009882b00a7" />

---

## 6. Durulama Yöntemlerinin Karşılaştırılması (COG vs COA)

COG ve COA yöntemleri, aynı kural tabanı ve üyelik fonksiyonları üzerinde, yalnızca durulama aşamasında farklılık göstermektedir. Bu projede, hem örnek giriş (Tecrübe=10, Cinsiyet=1, Yaş=44) hem de `Scenarios` sınıfındaki sabit senaryolar için her iki yöntem de çalıştırılmıştır.

Şekil 3'te (`cog_coa_karsilastirma.png`), COG ve COA yöntemlerine ait tahmini parça sayısı değerlerinin konsol çıktısı ekran görüntüsü verilmiştir. Bu ekran görüntüsü, her iki yöntemin de çoğu durumda birbirine çok yakın sonuçlar ürettiğini ve modelin genel eğilimini değiştirmediğini göstermektedir.

---

## 7. Grafikler ve “Taralı Alan” Görselleri

Modelin davranışını daha iyi anlamak için jFuzzyLogic’in sunduğu grafik araçları kullanılmıştır:

- Girdi üyelik fonksiyonları (Tecrübe, Yaş, Cinsiyet)  
- Çıkış üyelik fonksiyonları (Parça Sayısı)  
- Çıktı agregasyon şekli ve crisp değer (COG)

`Main` sınıfında COG modeli için `JFuzzyChart.get().chart(modelCOG.getModel());` çağrısı ile hem giriş hem de çıkış üyelik fonksiyonlarının ve özellikle `ParcaSayisi` değişkeninin “taralı alan” şeklindeki birleşik çıktısının grafiği elde edilmiştir. Bu grafiklerden alınan ekran görüntüleri rapora aşağıdaki gibi eklenebilir:

- Şekil 1: Tecrübe, Yaş ve Cinsiyet üyelik fonksiyonları (COG modeli)  
- Şekil 2: Parça Sayısı agregasyon grafiği ve COG crisp noktasının gösterildiği taralı alan  

İsteğe bağlı olarak, `OutputAreaPlotter` sınıfı ile sadece crisp değeri işaretleyen basit bir dikey çizgi grafiği (örneğin `ParcaSayisi_COG.png`) üretilerek rapora eklenebilir. Ancak taralı alanı tam anlamıyla gösteren asıl görsel, JFuzzyChart tarafından üretilen çıkış grafiğidir.

---

## 8. Sonuç ve Değerlendirme

Bu proje kapsamında, çalışan üretim performansını (parça sayısı) tahmin etmek amacıyla, iş tecrübesi, yaş ve cinsiyet girdilerine dayalı bir bulanık mantık modeli geliştirilmiş ve jFuzzyLogic kütüphanesi ile Java ortamında uygulanmıştır. Model:

- Tecrübe–performans ilişkisini öğrenme eğrileri ve performans platosu kavramları ile uyumlu şekilde,  
- Yaş faktörünü fizyolojik kapasite ve ergonomik risklerle bağlantılı olarak,  
- Cinsiyet değişkenini ise antropometrik ve fiziksel iş kapasitesi bağlamında tarafsız bir vekil değişken olarak ele almıştır.

Geliştirilen kural tabanı ve üyelik fonksiyonları, literatürde yer alan genel eğilimler ve sektörel vaka analizleri ile tutarlı bir şekilde tasarlanmıştır. Durulama aşamasında COG ve COA yöntemleri uygulanmış, senaryolar üzerinden yapılan karşılaştırmada:

- Her iki yöntemin de benzer sonuçlar ürettiği,  
- COG yönteminin daha pürüzsüz ve stabil bir çıktı yüzeyi sağladığı,  
- COA’nın ise özellikle asimetrik durumlarda alanı ikiye bölme mantığı ile farklılaşabildiği  

gözlemlenmiştir.

Sonuç olarak, COG yöntemi bu tür bir karar destek problemi için ana durulama yöntemi olarak tercih edilmiş, COA ise alternatif ve karşılaştırma amaçlı ikinci yöntem olarak rapora dahil edilmiştir. Model, kullanılacak veriye ve sektörel koşullara göre üyelik fonksiyonları ve kural tabanı güncellenerek farklı iş ortamlarına uyarlanabilir. İleride yapılacak çalışmalarda, modele ek girdi değişkenleri (örneğin eğitim seviyesi, iş istasyonu ergonomi skoru, motivasyon düzeyi vb.) eklenmesi ve gerçek saha verisi ile istatistiksel kalibrasyon yapılması planlanabilir.


Not: Bu metni Word’e aktarırken sayfa düzeni, yazı tipi ve satır aralıklarını ödev formatına göre yeniden düzenlemeniz ve kendi ad–numara bilgilerinizi girmeniz gerekmektedir.
