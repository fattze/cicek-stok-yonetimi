package tr.com.cicekstok.veriyapilari;

import tr.com.cicekstok.enumlar.AltTur;
import tr.com.cicekstok.enumlar.Kategori;
import tr.com.cicekstok.model.Urun;

import java.util.*;

public class IndexMap {

    // Kategori → Ürün listesi
    private final Map<Kategori, List<Urun>> kategoriMap = new HashMap<>();

    // Alt tür → Ürün listesi
    private final Map<AltTur, List<Urun>> altTurMap = new HashMap<>();


    // ----------------------------------------------------
    // ÜRÜN EKLE (hem kategori hem alt tür indexlerine ekler)
    // ----------------------------------------------------
    public void add(Urun u) {
        // --- Kategori indexi ---
        kategoriMap
                .computeIfAbsent(u.getKategori(), k -> new ArrayList<>())
                .add(u);

        // --- Alt tür indexi ---
        if (u.getAltTur() != null) {
            altTurMap
                    .computeIfAbsent(u.getAltTur(), t -> new ArrayList<>())
                    .add(u);
        }
    }

    // ----------------------------------------------------
    // ÜRÜN GÜNCELLE (önce kaldırıp tekrar ekleme)
    // ----------------------------------------------------
    public void update(Urun u) {
        remove(u);
        add(u);
    }

    // ----------------------------------------------------
    // ÜRÜN SİL (kategori ve alt tür haritalarından)
    // ----------------------------------------------------
    public void remove(Urun u) {

        // Kategori listesinden sil
        List<Urun> katList = kategoriMap.get(u.getKategori());
        if (katList != null) {
            katList.removeIf(x -> x.getId().equals(u.getId()));
        }

        // Alt tür listesinden sil
        if (u.getAltTur() != null) {
            List<Urun> altList = altTurMap.get(u.getAltTur());
            if (altList != null) {
                altList.removeIf(x -> x.getId().equals(u.getId()));
            }
        }
    }

    // ----------------------------------------------------
    // Kategoriye göre liste döndür
    // ----------------------------------------------------
    public List<Urun> getByKategori(Kategori kat) {
        return kategoriMap.getOrDefault(kat, Collections.emptyList());
    }

    // ----------------------------------------------------
    // Alt türe göre liste döndür
    // ----------------------------------------------------
    public List<Urun> getByAltTur(AltTur altTur) {
        return altTurMap.getOrDefault(altTur, Collections.emptyList());
    }
}
