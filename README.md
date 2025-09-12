# Android Cihazlarla Paralel Test Otomasyonu (Java)

Bu proje, Appium ve Cucumber-JVM kullanarak birden fazla Android cihaz üzerinde senaryoları paralel çalıştırmak için yapılandırılmıştır. Cihazlar `deviceList.json` dosyası üzerinden yönetilir. `ParallelRunner` sınıfı testlerin dağıtımı, Appium sunucularının başlatılması ve sonuçların takibini otomatikleştirir.

## 📁 Klasör Yapısı

```
conf_automation/
├── deviceList.json                  # Cihazların isim ve IP/UDID bilgileri
├── device_ports.json                # Çalışma anında üretilen cihaz→port eşleşmesi
├── java/
│   ├── pom.xml
│   ├── src/main/java/com/confautomation/runner/
│   │   ├── ParallelRunner.java     # Paralel test başlatıcısı
│   │   ├── AppiumServerManager.java
│   │   └── AdbUtils.java
│   └── src/test/java/com/confautomation/
│       ├── common/Base.java
│       ├── hooks/TestHooks.java
│       ├── locators/CommonLocators.java
│       ├── pages/MoviesPage.java
│       ├── steps/CommonSteps.java
│       ├── steps/MoviesSteps.java
│       └── runner/CucumberTest.java
│   └── src/test/resources/features/
│       └── movies.feature
└── README.md
```

## 🚀 Kurulum

1) Appium kurulumu ve PATH kontrolü:
```bash
npm install -g appium
```
2) Java ve Maven hazır olmalı (Java 17+, Maven 3.8+).

## ▶️ Kullanım

1) Maven bağımlılıkları (opsiyonel ilk kurulum):
```bash
cd java
mvn -q -DskipTests package
```
2) Paralel runner’ı çalıştırın:
```bash
cd java
java -cp "target/test-classes:target/classes:$(mvn -q -Dexec.classpathScope=test -DincludeScope=test -DskipTests -Dexec.executable=echo --non-recursive org.codehaus.mojo:exec-maven-plugin:3.1.0:classpath | tail -n1)" com.confautomation.runner.ParallelRunner
```
3) Konsolda şu formatla giriş yapın:
```
Platform ve tag(ler)ini girin (örnek: ekran1=@login,@search), bitirmek için boş bırakın:
```
Örnek giriş:
```
ekran1=@smoketest,@checkout
ekran2=@payment
allDevice=@smoke
```

- `ekran1`, `ekran2` gibi isimler `deviceList.json`'da tanımlı cihazlar olmalıdır.
- `allDevice` ile bağlı tüm cihazlar hedeflenir.
- Her cihaz için ayrı Appium sunucusu ve Cucumber iş parçacığı başlatılır.
- Cihazların bağlılığı `adb devices` ile kontrol edilir; IP:PORT girdileri için otomatik `adb connect` denenir.

## 🧵 Paralel Mantık ve Portlar

- Her cihaz için farklı bir Appium portu dinamik olarak atanır (`4730`dan başlar).
- `device_ports.json`, cihaz-adı → port eşleşmesini testler ile paylaşır.
- `ParallelRunner`, tag ve platforma göre Cucumber’ı thread bazında çağırır.

## 📌 Gereksinimler

- Java 17+
- Maven 3.8+
- Appium
- ADB (Android Debug Bridge)
- Android cihazlar veya emülatörler
