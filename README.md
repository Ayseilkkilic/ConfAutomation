# Android Cihazlarla Paralel Test Otomasyonu

Bu proje, Appium ve Behave kullanarak birden fazla Android cihaz üzerinde senaryoları paralel çalıştırmak için yapılandırılmıştır. Cihazlar `deviceList.json` dosyası üzerinden yönetilir. Gelişmiş bir `runner.py` script'i sayesinde testlerin dağıtımı, Appium sunucularının başlatılması ve sonuçların takibi otomatikleştirilmiştir.

## 📁 Klasör Yapısı

```
conf_automation/
├── common/           # Ortak fonksiyonlar ve locator tanımları
├── deviceList.json   # Cihazların isim ve IP/UDID bilgileri
├── environment.py    # Test öncesi driver başlatma, sonrası kapama işlemleri
├── features/         # Feature dosyaları ve adım tanımları
├── locators/         # Sayfa objelerine ait locator sınıfları
├── pages/            # Sayfa sınıfları (POM)
├── runner.py         # Testlerin platform ve tag bazlı başlatıcısı
└── README.md         # Proje açıklamaları
```

## 🚀 Kurulum

1. Gerekli bağımlılıkları yükleyin:
   ```bash
   pip install -r requirements.txt
   ```

2. Appium'un sisteminize kurulu ve PATH'e ekli olduğundan emin olun:
   ```bash
   npm install -g appium
   ```

3. `deviceList.json` içeriğini, test etmek istediğiniz Android cihazların IP/UDID bilgilerine göre düzenleyin.

## ▶️ Kullanım

Projeyi başlatmak için:

```bash
python runner.py
```

Ardından sistem sizden şu formatta giriş bekler:
```
Platform ve tag(ler)ini girin (örnek: ekran1=@login,@search), bitirmek için boş bırakın:
```

Örnek:
```
ekran1=@smoketest,@checkout
ekran2=@payment
allDevice=@smoke
```

- `ekran1`, `ekran2` gibi isimler `deviceList.json`'da tanımlı cihazlar olmalıdır.
- `allDevice` ifadesiyle sadece o anda bağlı olan tüm cihazlar hedeflenir.
- Her cihaz için ayrı Appium sunucusu ve Behave süreci başlatılır.
- Cihazların bağlılığı `adb devices` komutu ile kontrol edilir, bağlı olmayanlar otomatik olarak elenir.

## 🧵 Paralel Test Mantığı

- Her cihaz için farklı bir Appium portu dinamik olarak atanır (`4730`dan başlar).
- `device_ports.json` dosyası, cihaz-adı → port eşleşmesini `environment.py` ile paylaşır.
- `allDevice` kullanıldığında, her cihaz adına özel Behave thread'leri çalıştırılır (`--define=platform=cihaz_adi`).
- Bu sayede her cihaz kendi Appium sunucusuna doğru şekilde bağlanır.
- Cihaz sayısı yüksekse `ThreadPoolExecutor` gibi yapılarla sınırlandırma eklenebilir.

## ⚠️ Notlar

- Aynı portu paylaşan iki cihaz aynı anda çalıştırılamaz.
- `environment.py` içinde Appium portları dinamik şekilde cihaz sırasına göre atanır.
- Appium'un her cihaz için doğru şekilde başlatıldığından emin olun.

## 🛠 Geliştirme

- Allure entegrasyonu istenirse tekrar aktif hale getirilebilir.
- Cihaz sayısı arttıkça portlar çakışmayacak şekilde dinamik artar (`4730` ve sonrası).

## 📌 Gereksinimler

- Python 3.8+
- Appium
- ADB (Android Debug Bridge)
- Android cihazlar veya emülatörler

## 👨‍💻 Katkıda Bulunma

--
