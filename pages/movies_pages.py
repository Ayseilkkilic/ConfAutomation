# pages/login_pages.py
import time
from locators.common_locators import CommonLocators
from appium.webdriver.common.appiumby import By
from common.base import Base



class MoviesPage(Base):
    def __init__(self, driver):
        self.driver = driver
        self.base = Base(driver)
        self.commonlocators = CommonLocators()

    def open_planet_web(self):
        self.driver.get("https://www.google.com")
    
    def user_should_see(context, text):
        context.base.get_element(By.XPATH, context.commonlocators.TEXT_XPATH.format(text))