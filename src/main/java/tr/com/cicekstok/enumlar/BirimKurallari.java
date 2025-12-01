package tr.com.cicekstok.enumlar;

import java.util.List;
import java.util.Map;

public class BirimKurallari {

    private static final Map<Kategori, List<Birim>> HARITA = Map.of(
            Kategori.KESME_CICEK,      List.of(Birim.DAL, Birim.DEMET),
            Kategori.SAKSI_BITKISI,    List.of(Birim.SAKSI),
            Kategori.AKSESUAR,         List.of(Birim.ADET),
            Kategori.DOLGU_YESILLIK,   List.of(Birim.DEMET, Birim.DAL),
            Kategori.KURUTULMUS_CICEK, List.of(Birim.DEMET, Birim.DAL),
            Kategori.HEDIYELIK,        List.of(Birim.ADET),
            Kategori.HAZIR_URUN,       List.of(Birim.ADET)
    );

    /** UI tarafında ComboBox doldurmak için */
    public static List<Birim> izinliBirimler(Kategori kategori) {
        return HARITA.getOrDefault(kategori, List.of(Birim.ADET));
    }

    /** Güvenlik: seçilen birim bu kategori için geçerli mi? */
    public static boolean birimUygunMu(Kategori kategori, Birim birim) {
        return izinliBirimler(kategori).contains(birim);
    }
}
