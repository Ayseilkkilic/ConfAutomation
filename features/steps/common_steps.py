from behave import given, when, then
from appium import webdriver
from common.base import Base
from locators.common_locators import CommonLocators
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.support import expected_conditions as EC

@given('User click on "{text}" button')
def step_click_on_button_with_text(context, text):
    context.base = Base(context.driver)
    context.commonlocators = CommonLocators()
    context.base.click_element(AppiumBy.XPATH, context.commonlocators.TEXT_XPATH.format(text))

@then('"{text}" section should be displayed')
def step_should_be_displayed(context, text):
    context.base = Base(context.driver)
    context.commonlocators = CommonLocators()
    context.base.get_element(AppiumBy.XPATH, context.commonlocators.TEXT_XPATH.format(text))

@then('"{text}" should be visible on the screen')
def step_text_should_be_visible(context, text):
    context.base = Base(context.driver)
    context.commonlocators = CommonLocators()
    element = context.base.get_element(AppiumBy.XPATH, context.commonlocators.TEXT_XPATH.format(text, text))
    assert element.is_displayed(), f'"{text}" is not visible on the screen.'
