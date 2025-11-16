# Çalışan Üretim Performansı İçin Bulanık Mantık Modeli  
## (Tecrübe – Yaş – Cinsiyet Temelli Tahmin)

Öğrenci Adı Soyadı: Nadir Şensoy - Mustafa Erdoğan
Öğrenci No: G231210373 - B221210308
Ders / Dönem: Bulanık Mantık ve Yapay Sinir Ağlarına Giriş
Teslim Tarihi: 15.11.2025  

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

Bu bölümde, modelde kullanılan girdi değişkenlerinin (iş tecrübesi, yaş, cinsiyet) üretim performansı üzerindeki etkisi, yeni `arastirma.txt` dosyasında derlenen akademik çalışmalara dayanarak özetlenmektedir.

### 2.1 İş Tecrübesi, Öğrenme / Deneyim Eğrileri ve Kariyer Platosu

İş tecrübesi–performans ilişkisi, klasik öğrenme eğrisi ve deneyim eğrisi yaklaşımlarıyla açıklanır. Wright’ın (1936) uçak endüstrisinde gözlemlediği öğrenme eğrisi, kümülatif üretim arttıkça birim başına işçilik süresinin öngörülebilir bir oranda azaldığını göstermektedir (Çalmaşur, Daştan, & Karaca, 2020). Yani çalışan, işe yeni başladığı dönemde aynı parçayı üretmek için daha fazla zaman harcar; tecrübe kazandıkça çevrim süresi kısalır ve vardiya başına üretilen parça sayısı artar.

Boston Consulting Group tarafından geliştirilen deneyim eğrisi yaklaşımı ise bu etkiyi sadece bireysel öğrenmeye değil, süreç iyileştirmeleri, organizasyonel bilgi birikimi ve verimlilik artışlarının tümüne bağlar (Çalmaşur vd., 2020). Ancak literatür, bu artışın sınırsız olmadığını; belirli bir kıdemden sonra performans artışının yavaşladığını ve “kariyer platosu” olarak adlandırılan bir durağanlık dönemine girildiğini göstermektedir (Özden’e atıfla, bkz. .2). Bu üç evre (hızlı öğrenme, yetkinlik ve plato), modeldeki “Az”, “Orta” ve “Yüksek/Kıdemli” tecrübe kümelerinin mantıksal temelini oluşturur.

Araştırma bulguları, bu aşamaların kaçıncı yılda başladığına dair evrensel bir sınır önermemektedir; bunun bağlama ve iş analizine göre belirlenmesi gerektiği vurgulanmaktadır (Öztürk, 2008). Bu nedenle modelde kullanılan 0–5 (Az), 5–15 (Orta) ve 10–30+ (Yüksek) aralıkları, literatürdeki kavramsal çerçeve ile uyumlu, fakat bağlam-spesifik bir mühendislik varsayımı olarak korunmuştur. Kod tarafında bu aralıkları değiştirmeyi gerektirecek herhangi bir ampirik zorunluluk bulunmadığı için FCL dosyalarında değişiklik yapılmamıştır.

### 2.2 Yaş Faktörünün Fiziksel Üretim Performansına Etkisi

Yaş, özellikle sanayi ve inşaat gibi fiziksel efor gerektiren işlerde performansı etkileyen önemli bir değişkendir. Türkiye’de yaşlı nüfusun işgücüne katılım oranı artsa da, 65 yaş ve üzeri grubun istihdamı daha çok tarım ve hizmet sektörlerinde yoğunlaşmakta, sanayi ve benzeri ağır işlerde ise düşük kalmaktadır (İlke Vakfı, 2024). Bu durum, fiziksel olarak zorlayıcı işlerin yaşlı işgücü için sürdürülebilirliğinin sınırlı olduğuna işaret etmektedir.

İş sağlığı ve güvenliği literatürü, yaşla birlikte kas gücü, dayanıklılık ve koordinasyon gibi göstergelerde düşüş yaşanabileceğini, bunun da ergonomik olarak kötü tasarlanmış istasyonlarda performans kaybına yol açabileceğini belirtir (Bölükbaşı’na atıfla, bkz. .4). Bununla birlikte, modelin bağlamı gereği üretim hattında hâlâ çalışabilen “yaşlı” işçilerin zaten belirli bir minimum sağlık ve fiziki kapasite eşiğini karşıladığı (healthy worker effect) varsayılmaktadır (.3). Bu nedenle yaş değişkeni modelde “Genç, Orta, Yaşlı” kümeleri ile temsil edilmiş; sınırlar 18–35 (Genç), 30–50 (Orta) ve 45–60 (Yaşlı) olarak, literatürde önerilen kesin yaş eşikleri olmadığı için bağlam-spesifik ve ergonomi odaklı bir sınıflandırma şeklinde korunmuştur.

### 2.3 Cinsiyet Değişkeninin Antropometrik ve Ergonomik Temeli

Cinsiyet (0 = Kadın, 1 = Erkek) bu modelde bir yetkinlik veya beceri göstergesi değil, iş istasyonu tasarımındaki antropometrik farklılıkları temsil eden bir vekil değişkendir. Türkiye’de yapılan antropometrik çalışmalarda erkeklerin ortalama kol uzunluğu ve bazı vücut ölçülerinin kadınlara göre daha yüksek olduğu, bunun da standart (özellikle erkek ölçülerine göre) tasarlanmış hatlarda erişim ve duruş farkları yaratabildiği gösterilmiştir (.5).

Eğer bir hattın çalışma yüksekliği ve erişim mesafeleri çoğunlukla belirli bir cinsiyetin antropometrik profiline göre ayarlanmışsa, diğer grupta yer alan çalışanlar için ergonomik uyumsuzluk (mismatch) ortaya çıkabilir (.5). Bu uyumsuzluk, daha sık eğilme/uzanma, dengesiz duruş ve fazladan hareket sayısı nedeniyle çevrim süresini uzatarak bir vardiyada üretilen parça sayısını düşürebilir. Dolayısıyla modelde cinsiyet değişkeni, “iş tasarımı ile çalışan profili arasındaki uyum”u temsil etmek üzere korunmuş; FCL dosyalarındaki 0–1 kodlaması güncel araştırma ile tutarlıdır ve değiştirilmesine gerek yoktur.

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

### 5.2 Çıktı Agregasyonu ve COG/COA Sonuçları

Bu girişler için `Main` sınıfı çalıştırıldığında, `ParcaSayisi` çıkış değişkeni için COG ve COA durulama yöntemleriyle elde edilen tahmini parç a sayısı değerleri konsola yazdırılmakta ve COG için agregasyon grafiği gösterilmektedir. Çıktının üyelik fonksiyonları, agregasyon (taralı alan) ve COG crisp noktası Şekil 2'de verilen `parca_uyelikler.png` ekran görüntüsünde birlikte gösterilmiştir.

Ayrıca, COG ve COA yöntemlerinin sayısal çıktılarının yan yana karşılaştırıldığı konsol görüntüsü, Şekil 3'te (`cog_coa_karsilastirma.png`) sunulmuştur. Bu şekil, aynı giriş için iki durulama yönteminin birbirine oldukça yakın sonuçlar verdiğini görsel olarak özetlemektedir.

---

## 6. Durulama Yöntemlerinin Karşılaştırılması (COG vs COA)

COG ve COA yöntemleri, aynı kural tabanı ve üyelik fonksiyonları üzerinde, yalnızca durulama aşamasında farklılık göstermektedir. Bu projede hem örnek giriş (Tecrübe=10, Cinsiyet=1, Yaş=44) hem de `Scenarios` sınıfındaki sabit senaryolar için her iki yöntem de çalıştırılmıştır.

Ancak yeni araştırma setinde COG ve COA yöntemlerinin matematiksel tanımlarını ve teorik avantaj/dezavantajlarını doğrudan karşılaştıran, atıf verilebilir nitelikte bir akademik kaynak bulunmamıştır. Mevcut kaynaklar (örneğin Güler & Demirkaya, 2022) bulanık kümeler ve üyelik fonksiyonlarını ele almakta, fakat durulama aşamasına ve COG/COA kıyasına girmemektedir. Bu nedenle bu raporda COG/COA farkı, yalnızca projedeki uygulama sonuçları (Şekil 3’teki konsol çıktısı) üzerinden nitel olarak yorumlanmış; yeni, kaynak gösterilemeyen teorik iddialar eklenmemiştir.

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

Geliştirilen kural tabanı ve üyelik fonksiyonları, literatürde yer alan genel eğilimler ve özellikle Öztürk’ün (2008) üretim işletmesi vaka çalışmasında kullanılan performans derecelendirme yaklaşımı ile uyumlu olacak şekilde tasarlanmıştır. Çıktı kümesi “Düşük–Orta–Yüksek”, Öztürk’teki “Yetersiz / Normal / Yeterli” dilsel ölçeğini temsil edecek biçimde aralıklara yayılmış; “Orta” ve “Yüksek” bölgeleri daha geniş, “Düşük” bölgesi ise nispeten dar tutulmuştur (Öztürk, 2008).

Durulama aşamasında COG ve COA yöntemleri uygulanmış; senaryo çıktılarında her iki yöntemin de benzer eğilimler ürettiği, COG değerlerinin ise grafiksel olarak daha “merkezî” ve sezgisel sonuçlar verdiği gözlenmiştir. Ancak COG/COA farkına ilişkin ayrıntılı teorik değerlendirme için, yeni araştırma setinde kaynaklı (atıf yapılabilir) akademik çalışma bulunmadığından, yorumlar proje çıktılarıyla sınırlı tutulmuştur.

Sonuç olarak, COG yöntemi bu tür bir karar destek problemi için ana durulama yöntemi olarak tercih edilmiş, COA ise alternatif ve karşılaştırma amaçlı ikinci yöntem olarak kullanılmaya devam etmiştir. Model, kullanılacak veriye ve sektörel koşullara göre üyelik fonksiyonları ve kural tabanı güncellenerek farklı iş ortamlarına uyarlanabilir. İleride yapılacak çalışmalarda, modele ek girdi değişkenleri (örneğin eğitim seviyesi, iş istasyonu ergonomi skoru, motivasyon düzeyi vb.) eklenmesi ve gerçek saha verisi ile istatistiksel kalibrasyon yapılması planlanabilir.


## 9. Kaynakça (APA Biçimi)

Çalmaşur, G., Daştan, H., & Karaca, Z. (2020). Bilgi yoğun hizmetler alt sektörlerinde öğrenme eğrileri. *Ömer Halisdemir Üniversitesi İktisadi ve İdari Bilimler Fakültesi Dergisi, 13*(1), 1–15. https://dergipark.org.tr/en/download/article-file/1202699

Güler, A., & Demirkaya, H. (2022). Devlet okullarındaki idari yöneticilerin okul seçimlerinin bulanık DEMATEL yöntemi ile incelenmesi. *Aksaray Üniversitesi İktisadi ve İdari Bilimler Fakültesi Dergisi, 14*(3), 231–240. https://dergipark.org.tr/tr/download/article-file/1858425

İlke Vakfı. (2024). *Dezavantajlı Çalışanlar: Yaşlılar*. https://ilke.org.tr/files/netstk/50/web/115/5134/dosyalar/dezavantajli_calisanlar_w.pdf

ISARDER. (2022). *İş ve İnsan Dergisi / Journal of Human and Work, 14*(4) [Tam sayı]. https://www.isarder.org/2022/vol.14_issue.4_full_issue.pdf

Mardin Artuklu Üniversitesi. (t.y.). *Mardin Artuklu Üniversitesi öğrencilerinin antropometrik özellikleri* [Makale]. https://dergipark.org.tr/en/download/article-file/1208767

Özden, Y. (2001). *Çokuluslu işletmelerde performans yönetimi* [Tez]. Dokuz Eylül Üniversitesi. (Özden’e atıf: .2). https://avesis.deu.edu.tr/dosya?id=cfb6edf6-64e4-4511-9510-ca788640678b

Bölükbaşı, P. (2007). Yaşlı işgücünün iş sağlığı ve iş güvenliği sorunları. *Çalışma ve Toplum, 12*, 185–204. (Bölükbaşı’na atıf: .4). https://dergipark.org.tr/tr/download/article-file/337652

Öztürk, S. (2008). *Bir üretim işletmesinde bireysel performans değerleme sistemi kurulumu ve bir karar destek sistemi tasarımı* (Yüksek lisans tezi). Başkent Üniversitesi Fen Bilimleri Enstitüsü, Ankara. https://core.ac.uk/download/pdf/147013362.pdf


Not: Bu metni Word’e aktarırken sayfa düzeni, yazı tipi ve satır aralıklarını ödev formatına göre yeniden düzenlemeniz ve kendi ad–numara bilgilerinizi girmeniz gerekmektedir.
