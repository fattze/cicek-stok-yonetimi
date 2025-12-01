package tr.com.cicekstok.depo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class KullaniciDeposu {

    private final Path dosya;

    public KullaniciDeposu(Path dosya) {
        this.dosya = dosya;
        try {
            Path parent = dosya.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(dosya)) {
                Files.createFile(dosya);
            }
        } catch (IOException e) {
            throw new RuntimeException("Kullanıcı dosyası oluşturulamadı: " + dosya, e);
        }
    }

    /**
     * users.txt içindeki tüm kullanıcıları okur.
     * Format:  kullaniciAdi;sifre
     */
    public Map<String, String> tumKullanicilar() {
        Map<String, String> map = new HashMap<>();

        if (Files.notExists(dosya)) {
            return map;
        }

        try (Stream<String> satirlar = Files.lines(dosya, StandardCharsets.UTF_8)) {
            satirlar
                    .filter(s -> s != null && !s.isBlank())
                    .forEach(s -> {
                        String[] p = s.split(";", 2);
                        if (p.length == 2) {
                            String k = p[0].trim();
                            String sifre = p[1].trim();
                            if (!k.isEmpty()) {
                                map.put(k, sifre);
                            }
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("users.txt okunamadı: " + dosya, e);
        }

        return map;
    }

    /**
     * Yeni kullanıcı ekler veya var olan kullanıcının şifresini günceller.
     * Tüm dosyayı baştan yazar.
     */
    public void kaydet(String kullaniciAdi, String sifre) {
        if (kullaniciAdi == null || kullaniciAdi.isBlank()) {
            throw new IllegalArgumentException("Kullanıcı adı boş olamaz.");
        }
        if (sifre == null || sifre.isBlank()) {
            throw new IllegalArgumentException("Şifre boş olamaz.");
        }

        Map<String, String> map = tumKullanicilar();
        map.put(kullaniciAdi.trim(), sifre.trim());

        try {
            var satirlar = map.entrySet().stream()
                    .map(e -> e.getKey() + ";" + e.getValue())
                    .toList();

            Files.write(dosya, satirlar, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("users.txt yazılamadı: " + dosya, e);
        }
    }
}
