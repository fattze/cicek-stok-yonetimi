package tr.com.cicekstok;

import tr.com.cicekstok.depo.DosyaUrunDeposu;
import tr.com.cicekstok.depo.KullaniciDeposu;
import tr.com.cicekstok.depo.UrunDeposu;
import tr.com.cicekstok.servis.EnvanterServisi;

import java.nio.file.Paths;

public final class UygulamaBaglami {

    public static final EnvanterServisi servis;
    public static final KullaniciDeposu kullaniciDeposu;

    // Aktif oturum açan kullanıcının adı
    public static String aktifKullanici;

    static {
        UrunDeposu urunDeposu =
                new DosyaUrunDeposu(Paths.get("veri", "urunler.txt"));

        servis = new EnvanterServisi(urunDeposu);

        kullaniciDeposu =
                new KullaniciDeposu(Paths.get("veri", "users.txt"));
    }

    private UygulamaBaglami() { }
}
