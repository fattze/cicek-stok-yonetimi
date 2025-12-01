package tr.com.cicekstok.depo;

import tr.com.cicekstok.enumlar.AltTur;
import tr.com.cicekstok.enumlar.AltTurHaritasi;
import tr.com.cicekstok.enumlar.Birim;
import tr.com.cicekstok.enumlar.Kategori;
import tr.com.cicekstok.model.Urun;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Ürün verileri basit CSV (noktalı virgül ';') dosyasında tutulur.
 *
 * FORMAT:
 *   id;ad;kategori;altTur;birim;stok;notlar
 */
public class DosyaUrunDeposu implements UrunDeposu {

    private final Path dosyaYolu;

    public DosyaUrunDeposu(Path dosyaYolu) {
        this.dosyaYolu = dosyaYolu;
        try {
            Path parent = dosyaYolu.getParent();
            if (parent != null) Files.createDirectories(parent);
            if (Files.notExists(dosyaYolu)) Files.createFile(dosyaYolu);
        } catch (IOException e) {
            throw new RuntimeException("Veri dosyası oluşturulamadı: " + dosyaYolu, e);
        }
    }

    // =========================================================
    //  Temel CRUD
    // =========================================================

    @Override
    public List<Urun> tumunuGetir() {
        try {
            return Files.readAllLines(dosyaYolu, StandardCharsets.UTF_8).stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(this::satirdanUrun)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Veri dosyası okunamadı: " + dosyaYolu, e);
        }
    }

    @Override
    public Optional<Urun> idIleGetir(String id) {
        return tumunuGetir().stream()
                .filter(u -> id != null && id.equals(u.getId()))
                .findFirst();
    }

    @Override
    public void kaydet(Urun u) {
        List<Urun> hepsi = tumunuGetir();
        hepsi.removeIf(x -> Objects.equals(x.getId(), u.getId()));
        hepsi.add(u);
        tumunuYaz(hepsi);
    }

    @Override
    public void sil(String id) {
        List<Urun> hepsi = tumunuGetir();
        hepsi.removeIf(x -> Objects.equals(x.getId(), id));
        tumunuYaz(hepsi);
    }

    // =========================================================
    //  Yardımcılar
    // =========================================================

    private void tumunuYaz(List<Urun> urunler) {
        List<String> satirlar = urunler.stream()
                .map(this::urundenSatir)
                .toList();
        try {
            Files.write(dosyaYolu, satirlar, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException("Veri dosyasına yazılamadı: " + dosyaYolu, e);
        }
    }

    private String temiz(String s) {
        return s == null ? "" : s.replace(";", ",");
    }

    /**
     * AltTür'ü dosyaya yazarken enum sabit adını kullanırız (GUL, ORKIDE...).
     */
    private String altTurKodu(AltTur altTur) {
        if (altTur == null) return "";
        if (altTur instanceof Enum<?>) {
            return ((Enum<?>) altTur).name();
        }
        return altTur.toString();
    }

    /**
     * Urun -> CSV satırına çevirir.
     *
     * id;ad;kategori;altTur;birim;stok;notlar
     */
    private String urundenSatir(Urun u) {
        return String.join(";",
                temiz(u.getId()),
                temiz(u.getAd()),
                u.getKategori() == null ? "" : u.getKategori().name(),
                temiz(altTurKodu(u.getAltTur())),
                u.getBirim() == null ? "" : u.getBirim().name(),
                Integer.toString(u.getStok()),
                temiz(u.getNotlar())
        );
    }

    /**
     * CSV satırını Urun nesnesine çevirir.
     *
     * Beklenen format:
     *  id;ad;kategori;altTur;birim;stok;notlar
     */
    private Urun satirdanUrun(String s) {
        String[] a = (s == null ? "" : s).split(";", -1);
        Urun u = new Urun();

        if (a.length > 0) u.setId(a[0]);
        if (a.length > 1) u.setAd(bosDegil(a[1]));

        // kategori
        Kategori kategori = null;
        if (a.length > 2 && !a[2].isBlank()) {
            try {
                kategori = Kategori.valueOf(a[2].trim());
            } catch (Exception ignore) { }
        }
        u.setKategori(kategori);

        // altTur
        if (a.length > 3 && kategori != null && !a[3].isBlank()) {
            AltTur altTur = AltTurHaritasi.altTurBul(kategori, a[3].trim());
            u.setAltTur(altTur);
        }

        // birim
        if (a.length > 4 && !a[4].isBlank()) {
            try {
                u.setBirim(Birim.valueOf(a[4].trim()));
            } catch (Exception ignore) { }
        }

        // stok
        if (a.length > 5) {
            u.setStok(safeIntOrZero(a[5]));
        }

        // notlar
        if (a.length > 6 && !a[6].isBlank()) {
            u.setNotlar(a[6]);
        }

        return u;
    }

    private String bosDegil(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private int safeIntOrZero(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
