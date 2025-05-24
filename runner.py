from behave.__main__ import main as behave_main
import sys
import subprocess
import time
import requests
import threading
import shutil
import os
import json

sys.path.append(os.path.dirname(os.path.abspath(__file__)))


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

    with open("deviceList.json", "r") as f:
        device_list = json.load(f)
    
    for name in device_list:
        if name in tag_value:
            platforms.append(name)
    
    if 'allDevice' in tag_value:
        platforms.append('allDevice')
    
    return platforms

def run_behave(tags, platform):
    behave_command = [
        "behave",
        tags,
        f"--define=platform={platform}"
    ]
    print(f"🔎 {platform.upper()} için Behave Komutu: {' '.join(behave_command)}")
    try:
        subprocess.run(behave_command, check=True)
    except subprocess.CalledProcessError as e:
        print(f"❌ {platform.upper()} için hata: {e}")

if __name__ == '__main__':
    platform_tags = {}

    # Cihaz listesini yükle
    with open("deviceList.json", "r") as f:
        device_list = json.load(f)

    print("Birden fazla tag ve cihaz girebilirsiniz. Örnek: android1 için @login,@search gibi virgül ile ayırarak yazın.")
    while True:
        user_input = input("Platform ve tag(ler)ini girin (örnek: ekran1=@login,@search), bitirmek için boş bırakın: ").strip()
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
        if platform == 'allDevice':
            for i, (device_name, device_ip) in enumerate(device_list.items()):
                port = 4730 + i
                if ":" in device_ip and device_ip.replace(".", "").replace(":", "").isdigit():
                    subprocess.run(["adb", "connect", device_ip])
                appium_proc = start_appium(port)
                if not appium_proc:
                    sys.exit(f"❌ {device_name} için Appium başlatılamadı!")
                appium_processes.append(appium_proc)
        elif platform in device_list:
            device_id = device_list[platform]
            if ":" in device_id and device_id.replace(".", "").replace(":", "").isdigit():
                subprocess.run(["adb", "connect", device_id])
            port = 4723
            appium_proc = start_appium(port)
            if not appium_proc:
                sys.exit(f"❌ {platform} için Appium başlatılamadı!")
            appium_processes.append(appium_proc)

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
