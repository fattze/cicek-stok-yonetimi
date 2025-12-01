# Çiçek Stok Yönetimi

JavaFX ile geliştirilmiş, çiçekçi veya küçük ölçekli mağazalar için tasarlanmış basit bir stok yönetim uygulaması.  
Farklı çiçek kategorileri ve alt türleri için stok takibi, arama, güncelleme ve raporlama işlemlerini görsel bir arayüz üzerinden yapmayı sağlar.

---

## İçindekiler

- [Genel Bakış](#genel-bakış)
- [Özellikler](#özellikler)
- [Ekran Görüntüleri](#ekran-görüntüleri)
- [Kullanılan Teknolojiler](#kullanılan-teknolojiler)
- [Kurulum](#kurulum)
- [Kullanım](#kullanım)
- [Proje Mimarisi](#proje-mimarisi)
- [Veri Saklama Yaklaşımı](#veri-saklama-yaklaşımı)
- [Geliştirme Amacı](#geliştirme-amacı)
- [Gelecekte Geliştirilebilecek Noktalar](#gelecekte-geliştirilebilecek-noktalar)

---

## Genel Bakış

Bu proje, veritabanı kullanmadan dosya tabanlı bir yapı ile stok takibinin nasıl yapılabileceğini göstermek için geliştirilmiştir.  
Çiçek kategorileri ve alt türleri **enum** yapıları ile modellenmiş; arayüz, **JavaFX + FXML + CSS** kullanılarak tasarlanmıştır.

Uygulama ile:

- Çiçek kategorileri ve alt türleri için stok ekleme/azaltma işlemleri,
- Ürün arama ve filtreleme,
- Kategori bazlı stok dağılımını görselleştirme (PieChart),
- Yeni ürün ekleme ve ürün silme

gibi işlemler yapılabilmektedir.

---

## Özellikler

- **Giriş Sistemi**
  - Kullanıcı adı ve şifre ile giriş ekranı
  - Basit kayıt/giriş akışı

- **Dashboard / Ana Ekran**
  - Toplam stok miktarı
  - Kritik stoktaki ürün sayısı
  - Toplam ürün ve kategori sayıları
  - Kategori bazlı stok dağılımını gösteren pasta grafik
  - Her kategori için kartlar ve kategoriye ait toplam stok bilgisi

- **Ürün Sorgulama**
  - Ürün adı, kategori ve alt tür filtreleri
  - “Sadece kritik stokları göster” seçeneği
  - Tablo üzerinden tüm ürünlerin listelenmesi
  - Alt kısımda, bulunan ürün sayısı ve toplam stok bilgisi

- **Stok İşlemleri**
  - Mevcut ürünler için stok artırma / azaltma
  - Stok güncellendiğinde bilgilendirme mesajları
  - Stok hareketlerinin tablo üzerinden takip edilebilmesi

- **Yeni Ürün Ekleme**
  - Kategori ve birim seçimi
  - Ürün adı girme
  - Ürün için görsel dosyası seçme
  - Kayıt sonrası ürünün sistemde kullanılabilir hale gelmesi

- **Ürün Silme**
  - Kategori ve ürün seçerek kayıtlı ürünü silme
  - İşlem sonrası kullanıcıya bilgilendirme mesajı

- **Modern Arayüz**
  - Arka planda sabit çiçek görseli
  - Kart tabanlı tasarım
  - Tema uyumlu butonlar ve etiketler
  - Aynı stilin tüm ekranlarda devam etmesi

---

## Ekran Görüntüleri

> Not: Bu bölümdeki resimler `docs` klasörü altında tutulmaktadır.

### 1. Giriş Ekranı

Uygulamanın açıldığında karşılayan ekran.  
Kullanıcı adı ve şifre alanları ile “Kayıt Ol” ve “Giriş Yap” butonları bulunur.

![Giriş ekranı](docs/giris_ekrani.png)

---

### 2. Ana Ekran (Dashboard)

Sol tarafta menü, ortada stok özet bilgileri ve sağda kategori bazlı stok dağılımını gösteren pasta grafik yer alır.  
Alt kısımda her kategori için kartlar ve bu kategoriye ait toplam stok bilgisi gösterilir.

![Ana ekran](docs/ana_ekran.png)

---

### 3. Ürün Sorgulama Ekranı

Tüm ürünlerin listelendiği ekran.  
Üstte arama alanı, kategori ve alt tür filtreleri; altta ise ürünlerin detaylı olarak bulunduğu tablo vardır.  
“Bulunan ürün sayısı / toplam stok / kritik stokta ürün sayısı” gibi özetler tablo altında gösterilir.

![Ürün sorgulama ekranı](docs/urun_sorgulama.png)

---

## Kullanılan Teknolojiler

- **Dil:** Java  
- **Arayüz:** JavaFX, FXML, CSS  
- **Grafikler:** JavaFX PieChart  
- **Geliştirme Ortamı:** IntelliJ IDEA  
- **Veri Saklama:** TXT dosyaları (dosya tabanlı yapı, veritabanı yok)

---

## Kurulum

### Gereksinimler

- Java JDK 17 veya üstü  
- Maven 3.x  
- IDE (önerilen): IntelliJ IDEA  
- İşletim sistemi: Windows / Linux / macOS

### Adımlar

1. **Projeyi klonla**

   - `git clone https://github.com/fattze/cicek-stok-yonetimi.git`
   - `cd cicek-stok-yonetimi`

2. **Maven bağımlılıklarını indir**

   - `mvn clean install`

3. **IDE ile çalıştırma (önerilen)**  
   - IntelliJ IDEA’yı aç.  
   - `File > Open` ile proje klasörünü seç.  
   - Maven projeleri senkronize olduktan sonra  
     `tr.com.cicekstok.Main` sınıfını bularak Run et.

4. **Komut satırından çalıştırma (opsiyonel)**  

   - `mvn javafx:run`

---

## Kullanım

1. **Uygulamayı Başlatma**
   - Uygulama açıldığında ilk olarak giriş ekranı gelir.
   - Kullanıcı adı / şifre ile giriş yapıldıktan sonra ana ekrana yönlendirilir.

2. **Ana Ekran (Dashboard)**
   - Üst kısımda toplam stok, toplam ürün, kritik stoktaki ürün sayısı gibi özet bilgiler yer alır.
   - Sağ tarafta kategori bazlı stok dağılımını gösteren pasta grafik (PieChart) bulunur.
   - Alt bölümde her kategori için bir kart ve o kategoriye ait toplam stok miktarı gösterilir.

3. **Stok Ekleme / Azaltma**
   - Menüden ilgili stok ekranına geçilir.
   - Kategori ve alt tür seçilir, eklenecek veya azaltılacak miktar yazılır.
   - İşlem sonrası stok değerleri güncellenir ve kullanıcıya bilgi mesajı gösterilir.

4. **Yeni Ürün Ekleme**
   - “Ürün Ekle” ekranından kategori, alt tür, birim ve ürün adı seçilir/girilir.
   - İstenirse ürün için bir görsel dosyası atanabilir.
   - Kaydet butonu ile yeni ürün veri dosyasına eklenir ve listelerde görünmeye başlar.

5. **Ürün Sorgulama**
   - “Ürün Sorgulama” ekranında:
     - Ürün adı, kategori veya alt tür filtreleri kullanılabilir.
     - “Sadece kritik stokları göster” seçeneği ile kritik seviyedeki ürünler hızlıca listelenebilir.
   - Alt tarafta bulunan ürün sayısı, toplam stok miktarı gibi özet bilgiler yer alır.

6. **Ürün Silme**
   - “Ürün Silme” ekranından kategori / ürün seçilerek sistemden tamamen kaldırılabilir.
   - Silme sonrası ilgili kayıt TXT dosyasından da silinir.

---

## Proje Mimarisi
Uygulamanın bellek tarafındaki ana veri yapısı, tüm ürünleri tutan bir `List<Urun>` listesidir.  
Servis katmanı, bu liste üzerinde dolaşarak kategori / alt tür bazlı filtreleme, arama ve gruplama işlemlerini yapar.  
`AltTurHaritasi` gibi enum tabanlı yapılar ile de her kategoriye ait alt türlere hızlıca erişmek için basit ama işlevli bir haritalama sağlanır.

Proje temel olarak birkaç ana katmandan oluşur:

- **Enumlar (enumlar paketi)**
  - `Kategori`, `AltTur`, `AltTurHaritasi` gibi yapılar ile
    çiçek kategorileri ve alt türleri tip güvenli bir şekilde temsil edilir.
  - Böylece yanlış yazım hatalarının ve geçersiz kategori/alt tür kullanımının önüne geçilir.

- **Model (model paketi)**
  - `Urun` sınıfı, sistemdeki her bir ürünün:
    - kategori,
    - alt tür,
    - ürün adı,
    - birim,
    - stok miktarı,
    - kritik stok seviyesi
    gibi alanlarını tutar.

- **Servis Katmanı (servis paketi)**
  - `EnvanterServisi` sınıfı:
    - TXT dosyasından veri okuma,
    - yeni satır ekleme,
    - stok artırma/azaltma,
    - ürün silme,
    - filtreleme ve arama işlemlerini kapsar.
  - Böylece dosya işlemleri arayüzden bağımsız, tek bir noktadan yönetilir.

- **Arayüz / Controller Katmanı (ui.controller paketi)**
  - `AnaController`, `KategoriController`, `EklemeController`, `SilmeController` vb.
    sınıflar, FXML dosyalarındaki bileşenleri yönetir.
  - Controller’lar, kullanıcı etkileşimlerini alır ve gerekli servis metodlarını çağırır.

- **Yardımcı Yapılar (ui.util vb.)**
  - `SceneManager`: Farklı ekranlar arasında geçişleri yönetir.
  - `UygulamaBaglami`: Servis gibi nesnelerin tek yerden yönetilmesini sağlar.

Bu mimari ile arayüz (UI) ve iş mantığı (servis, model) birbirinden ayrılmış, daha okunabilir ve geliştirilebilir bir yapı amaçlanmıştır.

---

## Veri Saklama Yaklaşımı

Bu projede veritabanı yerine TXT dosyaları kullanılmıştır.

- Tüm ürün kayıtları `veri` klasörü altında tutulan bir veya birkaç metin dosyasında saklanır.
- Her satır, tek bir ürünü temsil eder. Örnek bir satır mantığı:

  - `id;kategori;altTur;urunAdi;birim;stokMiktari;kritikSeviye`

- Uygulama açıldığında:
  - Servis katmanı bu dosyayı okur,
  - Satırları `Urun` nesnelerine dönüştürür,
  - Listeler ve arayüz bu nesneler üzerinden doldurulur.

- Stok güncelleme, yeni ürün ekleme veya silme gibi işlemler:
  - Önce bellek üzerindeki listeyi günceller,
  - Ardından ilgili TXT dosyasına geri yazar.

**Avantajları:**

- Veritabanı kurulumuna gerek yok.
- Küçük projeler ve eğitim amaçlı uygulamalar için anlaşılır ve basit.

**Dezavantajları:**

- Çok büyük veri setlerinde performans sınırlı olabilir.
- Aynı anda birden fazla kullanıcının çalıştığı senaryolara uygun değildir.
- Karmaşık sorgular için esnekliği veritabanı kadar güçlü değildir.

---

## Geliştirme Amacı

Bu proje, özellikle aşağıdaki konuları pratikte görmek için geliştirilmiştir:

- Enum yapılarının gerçek bir senaryoda nasıl kullanılacağını göstermek  
  (kategori/alt tür modellemesi).
- Temel veri yapıları ve Java koleksiyonları ile çalışma pratiği kazanmak:  
  `List<Urun>` üzerinden filtreleme / arama yapmak,  
  enum tabanlı haritalama yapıları (`AltTurHaritasi`) ile kategori → alt tür ilişkisini yönetmek.
- Dosya tabanlı veri saklama ile basit bir stok yönetimi akışını simüle etmek.
- Model–servis–controller ayrımı ile mimari katmanların önemini göstermek.
- JavaFX ile:
  - FXML kullanarak ekran tasarlama,
  - CSS ile tema/renk/stil düzenleme,
  - Controller’lar ile kullanıcı etkileşimlerini yönetme üzerine pratik yapmak.
- Küçük ama gerçekçi bir senaryoda stok takibi, ürün arama ve raporlama gibi iş süreçlerini kod tarafında modellemek.

Projeyi aynı zamanda ders kapsamında sunulabilecek, hem teknik hem görsel tarafı olan bir örnek uygulama haline getirmek hedeflenmiştir.

---

## Gelecekte Geliştirilebilecek Noktalar

- **Veritabanına Geçiş**
  - TXT dosyaları yerine SQLite veya PostgreSQL gibi bir veritabanına geçilerek
    daha güvenli ve performanslı bir yapı kurulabilir.
  - Stok hareketleri için ayrı bir “hareket geçmişi” tablosu tutulabilir.

- **Kullanıcı Sistemi Geliştirme**
  - Rol bazlı (admin, personel vb.) yetkilendirme eklenebilir.
  - Kullanıcıların yaptığı işlemler loglanabilir.

- **Raporlama ve Dışa Aktarım**
  - Stok raporlarını PDF veya Excel olarak dışa aktarma.
  - Belirli tarih aralıkları için satış / stok raporları üretme.

- **Kategori / Alt Tür Yönetimi Arayüzü**
  - Kategori ve alt türleri sadece koddan değil,
    arayüz üzerinden de ekleyip düzenleyebileceğimiz bir ekran eklenebilir.

- **Çoklu Dil Desteği**
  - Arayüz metinleri için dil dosyaları kullanılarak
    Türkçe/İngilizce gibi seçenekler eklenebilir.

- **Test Otomasyonu**
  - Servis katmanı için birim testleri (JUnit) yazılarak
    dosya okuma-yazma ve stok hesaplama işlemlerinin doğruluğu garanti edilebilir.

---
