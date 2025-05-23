from behave.__main__ import main as behave_main
import sys
import subprocess
import time
import requests
import threading
import shutil
import os


# Allure sonuçlarını temizle
if os.path.exists("allure-results"):
    shutil.rmtree("allure-results")

def start_appium(port):
    """Belirtilen portta Appium sunucusunu başlatır."""
    print(f"Appium sunucusu {port} portunda başlatılıyor...")
    process = subprocess.Popen(['appium', '--port', str(port)], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    
    # Appium'un başarılı bir şekilde başlamasını bekle
    for _ in range(30):
        try:
            response = requests.get(f'http://localhost:{port}/status', timeout=2)
            if response.status_code == 200:
                print(f"✅ Appium sunucusu {port} portunda başarıyla başlatıldı!")
                return process
        except requests.RequestException:
            time.sleep(1)
    
    print(f"❌ Appium sunucusu {port} portunda başlatılamadı!")
    return None

def determine_platforms_from_tags(tags):
    """Tag'lerden platformları belirler."""
    if not tags or not tags.startswith('--tags='):
        return []
    
    tag_value = tags.replace('--tags=', '')
    platforms = []
    
    if 'web_safari' in tag_value:
        platforms.append('safari')
    if 'android2' in tag_value:  # İki Android cihazını çalıştırmak için yeni tag
        platforms.extend(['android2'])
    if 'android1' in tag_value:  # Tek Android cihazı için
        platforms.append('android1')  # Varsayılan olarak android1
    if 'ios' in tag_value:
        platforms.append('ios')

    return platforms

def run_behave(tags, platform):
    result_dir = f"allure-results-{platform}"
    # Clean the result directory before running behave
    if os.path.exists(result_dir):
        shutil.rmtree(result_dir)
    behave_command = [
        "behave",
        tags,
        f"--define=platform={platform}",
        "--format=allure_behave.formatter:AllureFormatter",
        f"--out={result_dir}"
    ]
    print(f"🔎 {platform.upper()} için Behave Komutu: {' '.join(behave_command)}")
    try:
        subprocess.run(behave_command, check=True)
    except subprocess.CalledProcessError as e:
        print(f"❌ {platform.upper()} için hata: {e}")

if __name__ == '__main__':
    platform_tags = {}

    print("Birden fazla tag ve cihaz girebilirsiniz. Örnek: android1 için @login,@search gibi virgül ile ayırarak yazın.")
    while True:
        user_input = input("Platform ve tag(ler)ini girin (örnek: android1=@login,@search), bitirmek için boş bırakın: ").strip()
        if not user_input:
            break
        if "=" in user_input:
            platform, tags = user_input.split("=", 1)
            platform_tags[platform.strip()] = [tag.strip() if tag.strip().startswith("@") else "@" + tag.strip() for tag in tags.split(",")]

    if not platform_tags:
        print("Platform ve tag bilgisi girilmedi. Çıkılıyor.")
        sys.exit(1)

    appium_processes = []
    for platform in platform_tags:
        if platform == 'android1':
            appium_android1 = start_appium(4723)
            if not appium_android1:
                sys.exit("❌ Android1 için Appium başlatılamadı!")
            appium_processes.append(appium_android1)
        elif platform == 'android2':
            appium_android2 = start_appium(4724)
            if not appium_android2:
                sys.exit("❌ Android2 için Appium başlatılamadı!")
            appium_processes.append(appium_android2)
        elif platform == 'ios':
            appium_ios = start_appium(4725)
            if not appium_ios:
                sys.exit("❌ iOS için Appium başlatılamadı!")
            appium_processes.append(appium_ios)
        elif platform == 'safari':
            print("Safari testi seçildi, driver environment.py'da başlatılacak.")

    behave_threads = []
    for platform, tags in platform_tags.items():
        for tag in tags:
            t = threading.Thread(target=run_behave, args=(f"--tags={tag}", platform))
            behave_threads.append(t)
            t.start()

    for t in behave_threads:
        t.join()

    for process in appium_processes:
        process.terminate()
        print("🚪 Appium sunucusu kapatıldı!")

    print("\n📊 Tüm platformlar için Allure raporları oluşturuluyor.")
    for platform in platform_tags:
        result_dir = f"allure-results-{platform}"
        report_dir = f"allure-report-{platform}"
        try:
            subprocess.run(f"allure generate {result_dir} -o {report_dir} --clean", shell=True, check=True)
            subprocess.Popen(["allure", "open", report_dir])
            print(f"✅ {platform} için rapor oluşturuldu ve açıldı: {report_dir}")
        except subprocess.CalledProcessError:
            print(f"❌ {platform} için rapor oluşturulamadı.")
