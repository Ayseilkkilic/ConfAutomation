from behave import given, when, then
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from pages.movies_pages import MoviesPage



@given('Chrome tarayıcısı açık')
def step_impl(context):
    assert context.driver is not None

@when('Planet Thy Adresine gidilir')
def steps_open_planet_thy(context):
    context.moviesPage = MoviesPage(context.driver)
    context.moviesPage.open_planet_web()

@then('User should see "{text}" section')
def step_user_should_see(context,text):
    context.moviesPage = MoviesPage(context.driver)
    context.moviesPage.user_should_see(text)