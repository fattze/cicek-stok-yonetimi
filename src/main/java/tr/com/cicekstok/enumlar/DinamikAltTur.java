package tr.com.cicekstok.enumlar;

import java.util.Locale;
import java.util.Objects;

/**
 * Çalışma zamanında eklenen alt türler.
 * AltTur interface’ini implemente eder.
 */
public final class DinamikAltTur implements AltTur {

    private final Kategori kategori;
    /** Dosyada tutulacak kod (ENUM formatında): ORNEK, SAKAYIK_GUL vb. */
    private final String kod;

    /** Bu alt türün varsayılan birimi (DAL, DEMET, SAKSI, ADET ...) */
    private Birim birim;   // 🔹 yeni alan

    // ========= CONSTRUCTORLAR =========

    public DinamikAltTur(Kategori kategori, String kod) {
        this(kategori, kod, null);
    }

    public DinamikAltTur(Kategori kategori, String kod, Birim birim) {
        this.kategori = kategori;
        this.kod = kod;
        this.birim = birim;
    }

    // ========= GETTER / SETTER =========

    public Kategori getKategori() {
        return kategori;
    }

    public String getKod() {
        return kod;
    }

    public Birim getBirim() {
        return birim;
    }

    public void setBirim(Birim birim) {
        this.birim = birim;
    }

    /**
     * AltTur arayüzünden gelen metod:
     * Kullanıcıya gösterilecek, okunabilir ad.
     * Örn: "SAKAYIK_GUL" -> "Sakayik Gul"
     */
    @Override
    public String getAd() {
        String lower = kod.toLowerCase(Locale.ROOT);
        lower = lower.replace('_', ' ');

        StringBuilder sb = new StringBuilder();
        boolean yeniKelime = true;

        for (char ch : lower.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                yeniKelime = true;
                sb.append(ch);
            } else if (yeniKelime) {
                sb.append(Character.toUpperCase(ch));
                yeniKelime = false;
            } else {
                sb.append(ch);
            }
        }
        return sb.toString().trim();
    }

    /**
     * toString()’i kod’u döndürecek şekilde bırakıyoruz.
     * Dosyada saklarken / enum gibi kullanırken işimize yarıyor.
     */
    @Override
    public String toString() {
        return kod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DinamikAltTur)) return false;
        DinamikAltTur that = (DinamikAltTur) o;
        return kategori == that.kategori && kod.equals(that.kod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kategori, kod);
    }

    /**
     * Kullanıcının girdiği metni ENUM koduna çevir:
     * "Şakayık gül" -> "SAKAYIK_GUL"
     */
    public static String formatKod(String giris) {
        if (giris == null) return "";
        String s = giris.trim().toUpperCase(new Locale("tr", "TR"));

        // Türkçe karakterleri sadeleştir
        s = s.replace('Ç', 'C')
                .replace('Ğ', 'G')
                .replace('İ', 'I')
                .replace('Ö', 'O')
                .replace('Ş', 'S')
                .replace('Ü', 'U');

        // Boşlukları alt çizgiye çevir
        s = s.replaceAll("\\s+", "_");

        // Harf, rakam ve '_' dışındakileri at
        s = s.replaceAll("[^A-Z0-9_]", "");

        return s;
    }
}
