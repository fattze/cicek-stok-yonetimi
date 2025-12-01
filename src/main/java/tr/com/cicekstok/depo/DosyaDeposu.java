package tr.com.cicekstok.depo;

import tr.com.cicekstok.enumlar.*;
import tr.com.cicekstok.model.Urun;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DosyaDeposu implements UrunDeposu {

    private final Path dosyaYolu;

    public DosyaDeposu(String dosyaAdi) {
        this.dosyaYolu = Paths.get(dosyaAdi);
        try {
            Path parent = dosyaYolu.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(dosyaYolu)) {
                Files.createFile(dosyaYolu);
            }
        } catch (IOException e) {
            throw new RuntimeException("Dosya oluşturulamadı: " + dosyaAdi, e);
        }
    }

    @Override
    public void kaydet(Urun urun) {
        List<Urun> tumu = tumunuGetir();
        tumu.removeIf(u -> Objects.equals(u.getId(), urun.getId()));
        tumu.add(urun);
        yaz(tumu);
    }

    @Override
    public void sil(String id) {
        List<Urun> tumu = tumunuGetir();
        tumu.removeIf(u -> Objects.equals(u.getId(), id));
        yaz(tumu);
    }

    @Override
    public Optional<Urun> idIleGetir(String id) {
        return tumunuGetir().stream()
                .filter(u -> Objects.equals(u.getId(), id))
                .findFirst();
    }

    @Override
    public List<Urun> tumunuGetir() {
        List<Urun> liste = new ArrayList<>();

        if (!Files.exists(dosyaYolu)) {
            return liste;
        }

        try (BufferedReader br = Files.newBufferedReader(dosyaYolu)) {
            String satir;

            while ((satir = br.readLine()) != null) {
                if (satir.isBlank()) continue;

                String[] p = satir.split("\\|", -1); // -1 => boş kolonları da al

                Urun u = new Urun();

                // 0: ID
                if (p.length > 0 && !p[0].isBlank()) {
                    u.setId(p[0]);
                }

                // 1: Ad
                if (p.length > 1 && !p[1].isBlank()) {
                    u.setAd(p[1]);
                }

                // 2: Kategori
                if (p.length > 2 && !p[2].isBlank()) {
                    u.setKategori(Kategori.valueOf(p[2]));
                }

                // 3: Alt Tür (kategori'ye göre çöz)
                if (p.length > 3 && !p[3].isBlank() && u.getKategori() != null) {
                    u.setAltTur(altTurCozumle(u.getKategori(), p[3]));
                }

                // 4: Birim
                if (p.length > 4 && !p[4].isBlank()) {
                    u.setBirim(Birim.valueOf(p[4]));
                }

                // 5: Stok
                if (p.length > 5 && !p[5].isBlank()) {
                    try {
                        u.setStok(Integer.parseInt(p[5]));
                    } catch (NumberFormatException e) {
                        u.setStok(0);
                    }
                }

                // 6: Notlar
                if (p.length > 6 && !p[6].isBlank()) {
                    u.setNotlar(p[6]);
                }

                liste.add(u);
            }
        } catch (IOException e) {
            throw new RuntimeException("Dosya okunamadı!", e);
        }

        return liste;
    }

    private void yaz(List<Urun> liste) {
        try (BufferedWriter bw = Files.newBufferedWriter(dosyaYolu)) {
            for (Urun u : liste) {

                String kategoriStr = u.getKategori() != null ? u.getKategori().name() : "";
                String altTurStr = "";
                if (u.getAltTur() != null) {
                    // Enum'ların toString()'i genelde name() ile aynı, valueOf için yeterli
                    altTurStr = u.getAltTur().toString();
                }
                String birimStr = u.getBirim() != null ? u.getBirim().name() : "";
                String stokStr = Integer.toString(u.getStok());
                String notStr = u.getNotlar() != null ? u.getNotlar() : "";

                String satir =
                        nullToEmpty(u.getId()) + "|" +
                                nullToEmpty(u.getAd()) + "|" +
                                kategoriStr + "|" +
                                altTurStr + "|" +
                                birimStr + "|" +
                                stokStr + "|" +
                                notStr;

                bw.write(satir);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Dosya yazılamadı!", e);
        }
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    // --------------------------------------------------------
    // ALT TÜRLERİ ENUM’A GERİ DÖNÜŞTÜRME
    // --------------------------------------------------------
    // DosyaDeposu içinde

    private AltTur altTurCozumle(Kategori kategori, String ad) {
        if (kategori == null || ad == null || ad.isBlank()) return null;

        // Önce mevcut enum’larda arıyoruz
        try {
            return switch (kategori) {
                case KESME_CICEK -> KesmeCicekTuru.valueOf(ad);
                case SAKSI_BITKISI -> SaksiBitkisiTuru.valueOf(ad);
                case AKSESUAR -> AksesuarTuru.valueOf(ad);
                case DOLGU_YESILLIK -> DolguYesillikTuru.valueOf(ad);
                case KURUTULMUS_CICEK -> KurutulmusCicekTuru.valueOf(ad);
                case HEDIYELIK -> HediyelikTuru.valueOf(ad);
                case HAZIR_URUN -> HazirUrunTuru.valueOf(ad);
            };
        } catch (IllegalArgumentException ex) {
            // Enum'larda yoksa: dinamik alt tür olarak kabul ediyoruz
            DinamikAltTur d = new DinamikAltTur(kategori, ad);
            AltTurHaritasi.dinamikKaydet(kategori, d);
            return d;
        }
    }

}
