# features/environment.py
import json
from appium import webdriver as appium_webdriver
from appium.options.android import UiAutomator2Options
from selenium import webdriver as selenium_webdriver

def before_all(context):
    """ Runner'dan gelen platformları al ve uygun driver'ı başlat. """
    platform = context.config.userdata.get('platform', 'android')  # Varsayılan Android
    context.drivers = {}  # Birden fazla driver'ı desteklemek için
    context.driver = None  # Varsayılan olarak driver None

    # Eğer cihaz listesi içindeki bir platformsa, ona göre başlat
    with open("deviceList.json", "r") as f:
        device_list = json.load(f)
    with open("device_ports.json", "r") as pf:
        port_map = json.load(pf)

    for name, udid in device_list.items():
        if platform == name or platform == 'allDevice':
            capabilities = {
                'platformName': 'Android',
                'automationName': 'UiAutomator2',
                'udid': udid,
                'appPackage': 'com.android.chrome',
                'appActivity': 'com.google.android.apps.chrome.Main',
                'noReset': True
            }
            options = UiAutomator2Options()
            options.load_capabilities(capabilities)
            port = port_map.get(name, 4723)
            appium_server_url = f'http://localhost:{port}'
            try:
                android_driver = appium_webdriver.Remote(command_executor=appium_server_url, options=options)
                context.drivers[name] = android_driver
                if context.driver is None:
                    context.driver = android_driver
                print(f"✅ Appium {name} driver başlatıldı.")
            except Exception as e:
                print(f"❌ {name} driver başlatılamadı: {e}")
                raise

    if not context.drivers:
        raise ValueError(f"❌ Bilinmeyen platform: {platform}")

def after_all(context):
    """ Testler bitince tüm driver'ları kapat. """
    for platform, driver in context.drivers.items():
        if driver:
            driver.quit()
            print(f"🚪 {platform.upper()} driver kapatıldı.")