# pages/login_pages.py
import time
from locators.login_locators import LoginpageLocators
from appium.webdriver.common.appiumby import By
from common.base import Base



class Android1_MoviesPage(Base):
    def __init__(self, driver):
        self.driver = driver
        self.locators = LoginpageLocators()

    def open_planet_web(self):
        self.driver.get("https://planet.thy.com")