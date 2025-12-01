package tr.com.cicekstok.enumlar;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Kategori -> Alt tür eşlemesini yöneten merkez sınıf.
 *
 * - Enum ile tanımlı sabit alt türler (Örn: GUL, PAPATYA...)
 * - Çalışma zamanında eklenen DinamikAltTur nesneleri
 * - Dinamik alt türler:  veri/alt_tur_dinamik.txt içinde kalıcı tutulur
 * - Enum alt türler gizlenebilir: veri/alt_tur_gizli.txt içinde saklanır
 *
 * Not:
 *   UI tarafında kategori ekranında görünen alt tür listesi
 *   doğrudan turleri(kategori) metodundan gelir.
 */
public final class AltTurHaritasi {

    private AltTurHaritasi() { }

    // Çalışma zamanında eklenen (dinamik) alt türler
    // Örn: KESME_CICEK -> [DinamikAltTur(...), ...]
    private static final Map<Kategori, List<AltTur>> DINAMIK = new HashMap<>();

    // Güçlü (enum) alt türler için "gizli" kod listesi
    // Örn: KESME_CICEK -> [ "GUL", "PAPATYA" ]
    private static final Map<Kategori, Set<String>> GIZLI_ENUM_KODLAR = new HashMap<>();

    // Dinamik alt türler config dosyası
    // Yeni format: KATEGORI;ALT_TUR_KODU;BIRIM (BIRIM opsiyonel)
    private static final Path CONFIG_DOSYA =
            Paths.get("veri", "alt_tur_dinamik.txt");

    // Gizli enum alt türler config dosyası
    private static final Path GIZLI_CONFIG_DOSYA =
            Paths.get("veri", "alt_tur_gizli.txt");

    // Sınıf yüklenirken config'leri oku
    static {
        configYukle();       // dinamik alt türler
        gizliConfigYukle();  // gizlenen enum alt türler
    }

    // =========================================================
    //  Kategori için TÜM alt türler (enum + dinamik, gizlenmişler hariç)
    // =========================================================
    public static AltTur[] turleri(Kategori kategori) {
        if (kategori == null) return new AltTur[0];

        List<AltTur> sonuc = new ArrayList<>();
        Set<String> gizli = GIZLI_ENUM_KODLAR.getOrDefault(kategori, Collections.emptySet());

        // Sabit enum alt türleri (gizli olanları atlıyoruz)
        switch (kategori) {
            case KESME_CICEK -> {
                for (KesmeCicekTuru t : KesmeCicekTuru.values()) {
                    if (!gizli.contains(t.name())) sonuc.add(t);
                }
            }
            case SAKSI_BITKISI -> {
                for (SaksiBitkisiTuru t : SaksiBitkisiTuru.values()) {
                    if (!gizli.contains(t.name())) sonuc.add(t);
                }
            }
            case AKSESUAR -> {
                for (AksesuarTuru t : AksesuarTuru.values()) {
                    if (!gizli.contains(t.name())) sonuc.add(t);
                }
            }
            case DOLGU_YESILLIK -> {
                for (DolguYesillikTuru t : DolguYesillikTuru.values()) {
                    if (!gizli.contains(t.name())) sonuc.add(t);
                }
            }
            case KURUTULMUS_CICEK -> {
                for (KurutulmusCicekTuru t : KurutulmusCicekTuru.values()) {
                    if (!gizli.contains(t.name())) sonuc.add(t);
                }
            }
            case HEDIYELIK -> {
                for (HediyelikTuru t : HediyelikTuru.values()) {
                    if (!gizli.contains(t.name())) sonuc.add(t);
                }
            }
            case HAZIR_URUN -> {
                for (HazirUrunTuru t : HazirUrunTuru.values()) {
                    if (!gizli.contains(t.name())) sonuc.add(t);
                }
            }
        }

        // Dinamik alt türler
        List<AltTur> ekstra = DINAMIK.get(kategori);
        if (ekstra != null) {
            sonuc.addAll(ekstra);
        }

        return sonuc.toArray(new AltTur[0]);
    }

    // =========================================================
    //  Yeni dinamik alt tür ekleme (UI burayı kullanacak)
    // =========================================================

    /**
     * Eski imza: birim olmadan çağıran yerler için geriye dönük uyum.
     */
    public static DinamikAltTur yeniDinamikAltTur(Kategori kategori, String kullaniciMetni) {
        return yeniDinamikAltTur(kategori, kullaniciMetni, null);
    }

    /**
     * Yeni: kategori + ad + birim ile dinamik alt tür ekleme.
     */
    public static DinamikAltTur yeniDinamikAltTur(Kategori kategori,
                                                  String kullaniciMetni,
                                                  Birim birim) {
        if (kategori == null) {
            throw new IllegalArgumentException("Kategori seçili değil.");
        }

        // Kullanıcı ne yazarsa yazsın formatlamayı DinamikAltTur.formatKod halleder
        String kod = DinamikAltTur.formatKod(kullaniciMetni);
        if (kod.isBlank()) {
            throw new IllegalArgumentException("Alt tür adı geçersiz.");
        }

        List<AltTur> liste = DINAMIK.computeIfAbsent(kategori, k -> new ArrayList<>());

        // Aynı kodda dinamik alt tür zaten varsa tekrar ekleme
        for (AltTur a : liste) {
            if (a instanceof DinamikAltTur dat && dat.getKod().equals(kod)) {

                // Eğer önceden birim yoksa ve şimdi birim verildiyse, set et ve config'e yaz
                if (dat.getBirim() == null && birim != null) {
                    dat.setBirim(birim);
                    configTumunuYaz();
                }

                return dat; // zaten var, onu döndür
            }
        }

        DinamikAltTur altTur = new DinamikAltTur(kategori, kod, birim);
        liste.add(altTur);

        // Her değişiklikten sonra config’i baştan yaz (kalıcılık)
        configTumunuYaz();

        return altTur;
    }

    /**
     * Dosyadan okurken dinamik alt türleri tekrar belleğe eklemek için.
     * (Config'e tekrar yazmaz.)
     */
    public static void dinamikKaydet(Kategori kategori, DinamikAltTur altTur) {
        if (kategori == null || altTur == null) return;
        DINAMIK
                .computeIfAbsent(kategori, k -> new ArrayList<>())
                .add(altTur);
    }

    // =========================================================
    //  Dosyadaki koddan AltTur bulma (DosyaUrunDeposu burayı kullanıyor)
    // =========================================================
    public static AltTur altTurBul(Kategori kategori, String kod) {
        if (kategori == null || kod == null || kod.isBlank()) return null;

        String temizKod = kod.trim();

        // 1) Önce enum sabitlerinde dene
        try {
            return switch (kategori) {
                case KESME_CICEK -> KesmeCicekTuru.valueOf(temizKod);
                case SAKSI_BITKISI -> SaksiBitkisiTuru.valueOf(temizKod);
                case AKSESUAR -> AksesuarTuru.valueOf(temizKod);
                case DOLGU_YESILLIK -> DolguYesillikTuru.valueOf(temizKod);
                case KURUTULMUS_CICEK -> KurutulmusCicekTuru.valueOf(temizKod);
                case HEDIYELIK -> HediyelikTuru.valueOf(temizKod);
                case HAZIR_URUN -> HazirUrunTuru.valueOf(temizKod);
            };
        } catch (IllegalArgumentException ignore) {
            // enum’da yoksa devam...
        }

        // 2) Dinamik listede ara
        List<AltTur> liste = DINAMIK.get(kategori);
        if (liste != null) {
            for (AltTur a : liste) {
                if (a instanceof DinamikAltTur dat && dat.getKod().equals(temizKod)) {
                    return dat;
                }
            }
        }

        // 3) Hâlâ bulunamadıysa, dosyadan gelen kodu dinamik alt tür kabul et (config'e yazma)
        DinamikAltTur yeni = new DinamikAltTur(kategori, temizKod);
        dinamikKaydet(kategori, yeni);
        return yeni;
    }

    // =========================================================
    //  Dinamik alt tür silme (tamamen kaldırma)
    // =========================================================
    public static boolean dinamikSil(Kategori kategori, String kod) {
        if (kategori == null || kod == null) return false;
        List<AltTur> liste = DINAMIK.get(kategori);
        if (liste == null) return false;

        boolean removed = liste.removeIf(a ->
                (a instanceof DinamikAltTur dat && dat.getKod().equals(kod))
        );

        if (removed) {
            configTumunuYaz();
        }
        return removed;
    }

    // =========================================================
    //  Enum alt tür "gizleme" (sabit enum'lar için)
    // =========================================================
    public static boolean enumAltTurGizle(Kategori kategori, AltTur altTur) {
        if (kategori == null || altTur == null) return false;
        if (!(altTur instanceof Enum<?> e)) return false; // sadece enum olanları gizleriz

        String kod = e.name(); // GUL, PAPATYA, ORKIDE...
        GIZLI_ENUM_KODLAR
                .computeIfAbsent(kategori, k -> new HashSet<>())
                .add(kod);

        gizliConfigTumunuYaz();
        return true;
    }

    /**
     * AltTurSilController için:
     * - Dinamik alt tür ise gerçekten siler (ve config’ten kaldırır)
     * - Enum alt tür ise gizli listeye ekler (ve config'e yazar)
     *   -> Uygulama her açıldığında gizli kalır.
     */
    public static boolean gizleAltTur(Kategori kategori, AltTur altTur) {
        if (kategori == null || altTur == null) return false;

        if (altTur instanceof DinamikAltTur dat) {
            return dinamikSil(kategori, dat.getKod());
        }

        // Enum ise gizle
        return enumAltTurGizle(kategori, altTur);
    }

    // =========================================================
    //  DİNAMİK ALT TÜRLER CONFIG (alt_tur_dinamik.txt)
    //  Format (yeni):  KATEGORI_ADI;ALT_TUR_KODU;BIRIM
    //                 Eski satırlar: KATEGORI_ADI;ALT_TUR_KODU  da okunur.
    // =========================================================
    private static void configYukle() {
        if (Files.notExists(CONFIG_DOSYA)) return;

        try {
            Files.lines(CONFIG_DOSYA, StandardCharsets.UTF_8)
                    .filter(s -> s != null && !s.isBlank())
                    .forEach(line -> {
                        String[] parca = line.split(";", -1); // boş kolonları da al
                        if (parca.length < 2) return;

                        try {
                            Kategori k = Kategori.valueOf(parca[0].trim());
                            String kod = parca[1].trim();
                            if (kod.isEmpty()) return;

                            Birim birim = null;
                            if (parca.length >= 3) {
                                String birimStr = parca[2].trim();
                                if (!birimStr.isEmpty()) {
                                    try {
                                        birim = Birim.valueOf(birimStr);
                                    } catch (IllegalArgumentException ignore) {
                                        // Geçersizse birim null kalır
                                    }
                                }
                            }

                            DinamikAltTur altTur = new DinamikAltTur(k, kod, birim);
                            dinamikKaydet(k, altTur);
                        } catch (Exception ignore) {
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void configTumunuYaz() {
        List<String> satirlar = new ArrayList<>();

        for (var entry : DINAMIK.entrySet()) {
            Kategori k = entry.getKey();
            for (AltTur a : entry.getValue()) {
                if (a instanceof DinamikAltTur dat) {
                    Birim b = dat.getBirim();
                    if (b != null) {
                        satirlar.add(k.name() + ";" + dat.getKod() + ";" + b.name());
                    } else {
                        // Eski formatı da koruyabiliriz, ama tutarlılık için 3 kolonlu da yazılabilir.
                        satirlar.add(k.name() + ";" + dat.getKod());
                    }
                }
            }
        }

        try {
            Path parent = CONFIG_DOSYA.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            Files.write(CONFIG_DOSYA, satirlar, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    //  GİZLİ ENUM ALT TÜRLER CONFIG (alt_tur_gizli.txt)
    //  Format:  KATEGORI_ADI;ENUM_ADI   (ör: KESME_CICEK;GUL)
    // =========================================================
    private static void gizliConfigYukle() {
        if (Files.notExists(GIZLI_CONFIG_DOSYA)) return;

        try {
            Files.lines(GIZLI_CONFIG_DOSYA, StandardCharsets.UTF_8)
                    .filter(s -> s != null && !s.isBlank())
                    .forEach(line -> {
                        String[] parca = line.split(";", 2);
                        if (parca.length < 2) return;

                        try {
                            Kategori k = Kategori.valueOf(parca[0].trim());
                            String kod = parca[1].trim();
                            if (kod.isEmpty()) return;

                            GIZLI_ENUM_KODLAR
                                    .computeIfAbsent(k, kk -> new HashSet<>())
                                    .add(kod);
                        } catch (Exception ignore) {
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void gizliConfigTumunuYaz() {
        List<String> satirlar = new ArrayList<>();

        for (var entry : GIZLI_ENUM_KODLAR.entrySet()) {
            Kategori k = entry.getKey();
            for (String kod : entry.getValue()) {
                satirlar.add(k.name() + ";" + kod);
            }
        }

        try {
            Path parent = GIZLI_CONFIG_DOSYA.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            Files.write(GIZLI_CONFIG_DOSYA, satirlar, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
