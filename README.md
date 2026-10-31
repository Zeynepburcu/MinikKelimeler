# Minik Kelimeler

4–6 yaş çocuklar için İngilizce ve Almanca kelime uygulaması (Android).

- 5 konu × 10 kelime: hayvanlar, yiyecekler, taşıtlar, dışarıda, renkler
- **Öğren:** büyük resim, kelime sesli okunur (telefonun kendi seslendirme motoru, internet gerekmez)
- **Oyna:** "Where is the dog?" / "Wo ist der Hund?" — doğru resme dokun, yıldız kazan
- Almanca'da artikeller renkli: der = mavi, die = kırmızı, das = yeşil

## Derleme

GitHub Actions ile: `.github/workflows/build-apk.yml` dosyası her yüklemede APK üretir
(Actions → son çalışma → Artifacts → MinikKelimeler-apk).

## Kelime eklemek / değiştirmek

`app/src/main/java/com/minikkelimeler/app/Words.kt` dosyasını düzenleyin.
Resimler `app/src/main/res/drawable-nodpi/` klasöründedir.

## Lisanslar

Emoji görselleri: Microsoft Fluent Emoji, MIT lisansı — bkz. `THIRD_PARTY_LICENSE_FLUENT_EMOJI.txt`.
