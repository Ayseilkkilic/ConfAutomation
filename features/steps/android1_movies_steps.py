from behave import given, when, then
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from pages.android1_movies_page import Android1_MoviesPage



@given('Chrome tarayıcısı açık')
def step_impl(context):
    assert context.driver is not None

@when('Planet Thy Adresine gidilir')
def steps_open_planet_thy(context):
    context.android1Page = Android1_MoviesPage(context.driver)
    context.android1Page.open_planet_web()