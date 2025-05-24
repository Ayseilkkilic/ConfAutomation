# pages/login_pages.py
import time
from locators.common_locators import CommonLocators
from appium.webdriver.common.appiumby import By
from common.base import Base



class Android1_MoviesPage(Base):
    def __init__(self, driver):
        self.driver = driver
        self.locators = CommonLocators()

    def open_planet_web(self):
        self.driver.get("https://www.google.com")