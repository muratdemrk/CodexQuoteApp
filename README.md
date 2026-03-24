# Özlü Sözler

Java + XML tabanlı, Android Studio'da açılıp çalıştırılabilecek şekilde hazırlanmış modern bir quote uygulamasıdır. Uygulama `dummyjson.com` üzerinden rastgele söz çeker, kategori bilgisini Gemini API ile üretir, favorileri Room ile saklar ve WorkManager ile günlük bildirim gönderir.

## Özellikler

- Rastgele özlü söz çekme
- Yazar ve kategori gösterimi
- Gemini ile kategori üretimi, hata halinde `Genel` fallback'i
- Room ile kalıcı favoriler
- Navigation Drawer ile ekran geçişleri
- Ayarlar ekranından koyu tema yönetimi
- Ayarlar ekranından bildirim aç/kapat ve saat seçimi
- Android 13+ bildirim izni yönetimi
- WorkManager ile günlük bildirim planlama
- Türkçe ve İngilizce dil desteği

## Mimari

Proje katmanlı şekilde düzenlenmiştir:

- `ui/`: Activity, adapter ve ViewModel sınıfları
- `data/network/`: Retrofit servisleri ve API istemcileri
- `data/model/`: API ve domain modelleri
- `data/local/`: Room entity, DAO ve database sınıfları
- `data/repository/`: Network + local veri akışını yöneten repository
- `util/`: tema, ayar, kategori, bildirim ve scheduler yardımcıları
- `worker/`: günlük bildirim için WorkManager worker sınıfı

## Kullanılan Teknolojiler

- AndroidX AppCompat
- Material Components / Material 3 uyumlu tema
- Retrofit
- Gson
- RecyclerView
- Room
- WorkManager
- SharedPreferences
- ViewBinding

## Gemini API Key Yapılandırması

Gizli anahtar source code içine gömülmemiştir. `app/build.gradle` dosyası anahtarı `local.properties` içinden okuyup `BuildConfig.GEMINI_API_KEY` alanına aktarır.

Android Studio projesini açtıktan sonra kök dizindeki `local.properties` dosyanıza şu satırı ekleyin:

```properties
GEMINI_API_KEY=YOUR_KEY
```

Notlar:

- Gerçek anahtarı kod içine yazmayın.
- `local.properties` dosyası `.gitignore` içinde tutulur.
- Anahtar boş bırakılırsa uygulama çalışmaya devam eder ve kategori için `Genel` kullanır.
- `local.properties` içine anahtar ekledikten sonra mutlaka `Sync Project with Gradle Files` veya yeniden derleme yapın.

## Public Quote API

Rastgele söz için aşağıdaki public endpoint kullanılır:

- `https://dummyjson.com/quotes/random`

Bu endpoint yazar ve söz metni döndürür; kategori bilgisi dönmediği için Gemini entegrasyonu devreye girer.

## Bildirim Akışı

1. Uygulama ilk açıldığında Android 13+ cihazlarda bildirim izni istenir.
2. İzin verilirse ve bildirimler açıksa günlük worker planlanır.
3. Ayarlardan bildirim kapatılırsa mevcut worker iptal edilir.
4. Saat değiştirilirse worker seçilen saate göre yeniden planlanır.
5. Bildirime tıklandığında uygulama ana ekrana açılır.

## Launcher Icon

Projede uygulamaya özel adaptive launcher icon tanımı vardır:

- `res/mipmap-anydpi-v26/ic_launcher.xml`
- `res/mipmap-anydpi-v26/ic_launcher_round.xml`
- `res/drawable/ic_launcher_foreground.xml`

İsterseniz Android Studio Image Asset aracı ile aynı isimleri koruyarak PNG/Vectör varyasyonlarını değiştirebilirsiniz.

## Çalıştırma

1. Android Studio ile `android-ozlu-sozler` klasörünü açın.
2. `local.properties` dosyanıza `sdk.dir` ve isteğe bağlı `GEMINI_API_KEY` değerini ekleyin.
3. Gradle Sync çalıştırın.
4. API 33-36 arası bir emülatör veya gerçek cihaz seçin.
5. Uygulamayı başlatın.

## Not

Bu çalışma ortamında Gradle CLI ve `gradle-wrapper.jar` hazır olmadığı için komut satırından derleme doğrulaması yapılamadı. Proje dosyaları Android Studio senkronizasyonuna uygun şekilde hazırlanmıştır; gerekirse Android Studio wrapper dosyalarını yeniden oluşturabilir.
# CodexQuoteApp
