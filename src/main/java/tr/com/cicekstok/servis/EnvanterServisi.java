package tr.com.cicekstok.servis;

import tr.com.cicekstok.depo.UrunDeposu;
import tr.com.cicekstok.enumlar.AltTur;
import tr.com.cicekstok.enumlar.Birim;
import tr.com.cicekstok.enumlar.Kategori;
import tr.com.cicekstok.model.Urun;
import tr.com.cicekstok.veriyapilari.AVLTree;
import tr.com.cicekstok.veriyapilari.IndexMap;
import tr.com.cicekstok.veriyapilari.Trie;

import java.util.*;

public class EnvanterServisi {

    private final UrunDeposu depo;

    // --- Veri Yapıları ---
    private final AVLTree<String> idIndex = new AVLTree<>();
    private final Trie adIndex = new Trie();
    private final IndexMap indexMap = new IndexMap();

    public EnvanterServisi(UrunDeposu depo) {
        this.depo = Objects.requireNonNull(depo);

        // Depodaki tüm ürünleri veri yapılarına yükle
        List<Urun> urunler = depo.tumunuGetir();
        for (Urun u : urunler) {
            idIndex.insert(u.getId());
            adIndex.insert(u.getAd());
            indexMap.add(u);
        }
    }

    // =============================================================
    // MEVCUT ÜRÜN EKLEME YANİ TANIMLILARDAN
    // =============================================================
    public void urunEkle(Urun u) {
        if (u == null) throw new IllegalArgumentException("Ürün null olamaz");
        if (u.getAd() == null || u.getAd().isBlank())
            throw new IllegalArgumentException("Ürün adı boş olamaz");
        if (u.getKategori() == null)
            throw new IllegalArgumentException("Kategori seçilmelidir");
        if (u.getAltTur() == null)
            throw new IllegalArgumentException("Alt tür seçilmelidir");

        // Yeni Urun modelinde getStok() = toplam stok gibi davranıyor
        if (u.getStok() < 0)
            throw new IllegalArgumentException("Stok negatif olamaz");

        boolean yeni = !idIndex.contains(u.getId());

        depo.kaydet(u);

        if (yeni) {
            idIndex.insert(u.getId());
            adIndex.insert(u.getAd());
            indexMap.add(u);
        } else {
            indexMap.update(u);
        }
    }

    // =============================================================
    // YENİ: BİRİMLİ STOK ARTIRMA / AZALTMA
    // =============================================================

    /**
     * Verilen kategori + alt tür için, SEÇİLEN BİRİMDE stok artırır.
     * Örn: 5 DEMET, 10 ADET gibi ayrı ayrı tutulur.
     */
    /**
     * Verilen kategori + alt tür + BİRİM için stok artırır.
     * Aynı kombinasyon varsa stok ekler, yoksa yeni ürün satırı oluşturur.
     */
    public void stokArtir(Kategori kategori, AltTur altTur, Birim birim, int miktar) {

        if (kategori == null) throw new IllegalArgumentException("Kategori seçilmelidir");
        if (altTur == null) throw new IllegalArgumentException("Alt tür seçilmelidir");
        if (birim == null) throw new IllegalArgumentException("Birim seçilmelidir");
        if (miktar <= 0) throw new IllegalArgumentException("Miktar pozitif olmalıdır");

        // Bu alt türdeki ürünleri getir
        List<Urun> liste = indexMap.getByAltTur(altTur);

        Urun hedef = null;

        // 🔹 Artık BİRİM'i de kıyaslıyoruz
        for (Urun u : liste) {
            if (u.getKategori() == kategori &&
                    u.getAltTur() == altTur &&
                    u.getBirim() == birim) {
                hedef = u;
                break;
            }
        }

        if (hedef != null) {
            // Mevcut satıra stok ekle
            hedef.setStok(hedef.getStok() + miktar);
            urunEkle(hedef); // kaydet + indexMap.update vs.

        } else {
            // Bu kategori + alt tür + birim için ürün yok, yeni satır oluştur
            Urun yeni = new Urun();
            yeni.setKategori(kategori);
            yeni.setAltTur(altTur);
            yeni.setBirim(birim);

            // Ürün adını alt türden üret (istersen sabit metin de verebilirsin)
            String ad = altTur.toString().replace("_", " ");
            yeni.setAd(ad);

            yeni.setStok(miktar);

            urunEkle(yeni); // yeni ürün olarak ekle
        }
    }

    /**
     * Verilen kategori + alt tür + BİRİM için stok azaltır.
     */
    public void stokAzalt(Kategori kategori, AltTur altTur, Birim birim, int miktar) {

        if (kategori == null) throw new IllegalArgumentException("Kategori seçilmelidir");
        if (altTur == null) throw new IllegalArgumentException("Alt tür seçilmelidir");
        if (birim == null) throw new IllegalArgumentException("Birim seçilmelidir");
        if (miktar <= 0) throw new IllegalArgumentException("Miktar pozitif olmalıdır");

        List<Urun> liste = indexMap.getByAltTur(altTur);

        Urun hedef = null;

        for (Urun u : liste) {
            if (u.getKategori() == kategori &&
                    u.getAltTur() == altTur &&
                    u.getBirim() == birim) {
                hedef = u;
                break;
            }
        }

        if (hedef == null)
            throw new IllegalArgumentException("Bu kategori, alt tür ve birimde ürün bulunamadı!");

        int yeniStok = hedef.getStok() - miktar;

        if (yeniStok < 0)
            throw new IllegalArgumentException("Stok yetersiz!");

        hedef.setStok(yeniStok);
        urunEkle(hedef);
    }


    // =============================================================
    // ARAMALAR VE LİSTELEME
    // =============================================================
    public Urun urunAraId(String id) {
        if (id == null || id.isBlank()) return null;
        if (!idIndex.contains(id)) return null;
        return depo.idIleGetir(id).orElse(null);
    }

    public List<String> urunAraIsim(String kelime) {
        if (kelime == null || kelime.isBlank()) return Collections.emptyList();
        return adIndex.searchPrefix(kelime);
    }

    public List<Urun> kategoriListe(Kategori kategori) {
        return indexMap.getByKategori(kategori);
    }

    public List<Urun> altTurListe(AltTur altTur) {
        return indexMap.getByAltTur(altTur);
    }

    public List<Urun> tumunuListele() {
        return depo.tumunuGetir();
    }

    public void urunGuncelle(Urun guncel) {
        if (guncel == null) {
            throw new IllegalArgumentException("Güncellenecek ürün null olamaz");
        }

        // ID sistemde var mı? (yeni mi, mevcut mu)
        boolean yeni = !idIndex.contains(guncel.getId());

        // Depoya yaz (txt tarafında kaydet / güncelle)
        depo.kaydet(guncel);

        // Indexleri güncelle
        if (yeni) {
            // normalde burası pek çalışmaz, çünkü güncelleme için zaten id var
            idIndex.insert(guncel.getId());
            adIndex.insert(guncel.getAd());
            indexMap.add(guncel);
        } else {
            indexMap.update(guncel);
        }
    }

    public void urunSil(String id) {
        if (id == null || id.isBlank()) return;
        depo.sil(id);
    }

    /**
     * Verilen alt türdeki TÜM ürünleri (stokları 0 olsa bile) siler.
     */
    public void altTurdekiTumUrunleriSil(AltTur altTur) {
        if (altTur == null) return;

        // Bu alt türdeki ürünleri listeden çek
        List<Urun> urunler = altTurListe(altTur);
        for (Urun u : urunler) {
            urunSil(u.getId());
        }
    }
}
