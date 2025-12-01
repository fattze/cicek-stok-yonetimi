package tr.com.cicekstok.ui.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import tr.com.cicekstok.enumlar.AltTur;
import tr.com.cicekstok.enumlar.AltTurHaritasi;
import tr.com.cicekstok.enumlar.DinamikAltTur;
import tr.com.cicekstok.enumlar.Kategori;
import tr.com.cicekstok.enumlar.Birim;          // ✅ birim enum'u
import tr.com.cicekstok.model.Urun;
import tr.com.cicekstok.servis.EnvanterServisi;
import tr.com.cicekstok.ui.util.SceneManager;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.EnumMap;                       // ✅ birime göre toplama için
import java.util.Map;
import java.util.stream.Collectors;

public class KategoriController {

    @FXML
    private Label kategoriBaslik;

    @FXML
    private FlowPane altTurKapsayici;

    private static Kategori seciliKategori;

    // Stokları hesaplamak için servis
    private final EnvanterServisi servis =
            tr.com.cicekstok.UygulamaBaglami.servis;

    /** Ana ekrandan çağrılır */
    public static void kategoriAyarla(Kategori kategori) {
        seciliKategori = kategori;
    }

    @FXML
    public void geriDon() {
        SceneManager.showScene("/tr/com/cicekstok/ui/view/ana.fxml", "Ana Menü");
    }

    @FXML
    public void initialize() {
        if (seciliKategori == null) return;

        // Başlık
        kategoriBaslik.setText(seciliKategori.name().replace("_", " "));

        // Alt türleri al (enum + dinamik, gizlenmeyenler)
        AltTur[] altTurler = AltTurHaritasi.turleri(seciliKategori);

        altTurKapsayici.getChildren().clear();

        for (AltTur altTur : altTurler) {
            VBox kart = new VBox();
            kart.setStyle("""
                -fx-padding: 15;
                -fx-border-color: #cccccc;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-background-color: #f7f7f7;
                """);
            kart.setSpacing(10);
            kart.setPrefSize(150, 140);
            kart.setAlignment(Pos.CENTER); // hepsini ortala

            // 1) Resim
            ImageView resim = resimViewOlustur(altTur);
            if (resim != null) {
                kart.getChildren().add(resim);
            }

            // 2) Ad etiketi (dinamik için getAd(), enum için NAME → "NAME")
            Label isim = new Label(altTurAdi(altTur));
            isim.setFont(new Font(16));
            kart.getChildren().add(isim);

            // 3) Birime göre stok etiketi (çok satırlı)
            Label stokLabel = new Label(birimBazliStokMetni(altTur));
            stokLabel.setFont(new Font(14));
            stokLabel.setWrapText(true); // uzun olursa alt satıra geçsin
            kart.getChildren().add(stokLabel);

            // Eğer tıklanmasın istiyorsan BU SATIRI EKLEME:
            // kart.setOnMouseClicked(e -> altTurTiklandi(altTur));

            altTurKapsayici.getChildren().add(kart);
        }
    }

    /** Bu alt türe ait TÜM stok toplamını hesaplar (toplam adet) */
    private int stokHesapla(AltTur altTur) {
        List<Urun> urunler = servis.altTurListe(altTur);
        if (urunler == null) return 0;
        return urunler.stream()
                .mapToInt(Urun::getStok)
                .sum();
    }

    /**
     * Bu alt türe ait ürünleri BİRİMİNE göre gruplayıp
     * her birimi ayrı satırda yazar.
     *
     * Örnek çıktı:
     * demet: 10
     * dal: 24
     */
    private String birimBazliStokMetni(AltTur altTur) {
        List<Urun> urunler = servis.altTurListe(altTur);
        if (urunler == null || urunler.isEmpty()) {
            return "Stok: 0";
        }

        // Birime göre toplamları tut
        Map<Birim, Integer> harita = new EnumMap<>(Birim.class);

        for (Urun u : urunler) {
            Birim birim = u.getBirim();   // ❗ Urun'daki metot ismi farklıysa burayı değiştir
            int miktar = u.getStok();     // stok miktarı

            if (birim == null) continue;

            harita.merge(birim, miktar, Integer::sum);
        }

        if (harita.isEmpty()) {
            return "Stok: 0";
        }

        // "demet: 10" , "dal: 24" satırlarını üret
        String satirlar = harita.entrySet().stream()
                .map(e -> birimGosterim(e.getKey()) + ": " + e.getValue())
                .collect(Collectors.joining("\n")); // her birimi yeni satıra yaz

        // İstersen başına "Stok:\n" da ekleyebilirsin:
        // return "Stok:\n" + satirlar;
        return satirlar;
    }

    /** Birim adını ekranda nasıl göstermek istiyorsan buradan kontrol et. */
    private String birimGosterim(Birim birim) {
        // Enum adını tamamen küçük harfe çevir (Türkçe locale ile)
        String raw = birim.name().toLowerCase(new Locale("tr", "TR")); // Örn: "uzun_dal"

        // Alt çizgileri boşluğa çevir ve her kelimenin baş harfini büyüt
        String[] parts = raw.split("_");
        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty()) continue;

            // İlk harfi büyüt, kalanını olduğu gibi ekle
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                sb.append(part.substring(1));
            }
            sb.append(" ");
        }

        return sb.toString().trim(); // Örn: "Uzun Dal"
    }


    /** Kartta gösterilecek isim: dinamik için getAd(), enum için "GUL" -> "GUL" */
    private String altTurAdi(AltTur altTur) {
        if (altTur instanceof DinamikAltTur dat) {
            // Kullanıcının eklerken girdiği görünen ad
            return dat.getAd();
        }
        // Enum alt türler için
        return altTur.toString().replace("_", " ");
    }

    /** Alt tür için resim yüklemeye çalışır; yoksa null döner */
    private ImageView resimViewOlustur(AltTur altTur) {
        // 1) Kod: dosya adı için baz
        String kod;
        if (altTur instanceof DinamikAltTur dat) {
            // Dinamik alt türlerde kod = formatKod sonucu (alt_tur_dinamik.txt ile uyumlu)
            kod = dat.getKod().toLowerCase(Locale.ROOT);
        } else if (altTur instanceof Enum<?> e) {
            // Enum alt türlerde enum sabit adı
            kod = e.name().toLowerCase(Locale.ROOT);
        } else {
            // Güvenlik için fallback
            kod = altTur.toString().toLowerCase(Locale.ROOT);
        }

        // 2) Türkçe karakterleri düzelt (kod zaten sade olabilir, zarar vermez)
        kod = kod
                .replace("ç", "c").replace("Ç", "c")
                .replace("ğ", "g").replace("Ğ", "g")
                .replace("ı", "i").replace("I", "i")
                .replace("İ", "i")
                .replace("ö", "o").replace("Ö", "o")
                .replace("ş", "s").replace("Ş", "s")
                .replace("ü", "u").replace("Ü", "u");

        // Sırasıyla .png, .jpg, .jpeg dene
        String basePath = "/tr/com/cicekstok/img/" + kod;
        URL resUrl = null;
        for (String ext : new String[]{".png", ".jpg", ".jpeg"}) {
            resUrl = getClass().getResource(basePath + ext);
            if (resUrl != null) break;
        }

        if (resUrl == null) {
            System.out.println("Resim bulunamadı: " + basePath + "[.png/.jpg/.jpeg]");
            return null; // resim yoksa kart sadece yazıyla görünecek
        }

        Image img = new Image(resUrl.toExternalForm());
        ImageView view = new ImageView(img);
        view.setFitWidth(80);
        view.setPreserveRatio(true);
        return view;
    }
}
