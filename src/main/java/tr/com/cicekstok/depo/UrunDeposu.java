package tr.com.cicekstok.depo;

import tr.com.cicekstok.model.Urun;
import java.util.List;
import java.util.Optional;

public interface UrunDeposu {

    void kaydet(Urun urun);

    void sil(String id);

    Optional<Urun> idIleGetir(String id);

    List<Urun> tumunuGetir();
}
