from appium import webdriver
from selenium.webdriver import ActionChains
#from appium.webdriver.common.touch_action import TouchAction
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

class Base:
    """Base class for driver actions like click, get element
    """
    def __init__(self,driver):
        self.driver = driver
            
    def get_element(self,by,selector,*args):
        if (len(args) >=1):
            WebDriverWait(self.driver, args[0]).until(EC.visibility_of_element_located((by,selector)))
        else:
            WebDriverWait(self.driver, 30).until(EC.visibility_of_element_located((by,selector)))
        return self.driver.find_element(by=by,value=selector)
   
    def get_elements(self,by,selector):
        WebDriverWait(self.driver, 30).until(EC.visibility_of_element_located((by,selector)))
        return self.driver.find_elements(by=by, value=selector)   
    
    def click_element(self,by,selector):
        self.get_element(by,selector).click() 
    
    def click_element_with_wait(self,by,selector,wait):
        self.get_element(by,selector,wait).click()                
        
    def enter_text(self,by,selector,text):
        element = self.driver.find_element(by=by,value=selector)
        element.clear()
        element.send_keys(text)  
    
    def send_keys(self,by,selector,text):
        element = self.driver.find_element(by=by,value=selector)
        element.send_keys(text)   
        
    def click_enter(self):  
        self.driver.press_keycode(66) #click enter
        
    def close_keyboard(self):
        if(self.driver.is_keyboard_shown()):
               self.driver.hide_keyboard()  
               
    def get_text(self,by,selector):
        element = self.get_element(by,selector)
        return element.text   
   
    def check_element_visibility(self,by,selector):
        try:
            return self.get_element(by,selector).is_displayed() 
        except:
            return False    
    
    def wait_element_visibility(self,seconds,by,selector):
        WebDriverWait(self.driver, seconds).until(EC.visibility_of_element_located((by,selector)))
           
    def scroll_until_visible_text(self,text):
        self.driver.find_element(AppiumBy.ANDROID_UIAUTOMATOR,"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"" + text + "\").instance(0))")
     
    def scroll_horizontal_until_visible_text(self,text):
        self.driver.find_element(AppiumBy.ANDROID_UIAUTOMATOR,'new UiScrollable(new UiSelector().scrollable(true).instance(0)).setAsHorizontalList().scrollIntoView(new UiSelector().text("{}").instance(0))'.format(text))
      
    def scroll_down(self):
        size = self.driver.get_window_size()
        x = size['width'] * 0.7
        y_start = size['height'] * 0.65
        y_end =  size['height'] * 0.32
        self.driver.swipe(x,y_start,x,y_end,2000)  
        
    def scroll_down_end(self,iteration):
        """scroll down to end of the page by iterating 

        Args:
            iteration (int): specify how many times to scroll
        """
        for i in range(iteration):
            self.scroll_down()
            
    def scroll_to_end_of_page(self):
        # Get the initial page source
        initial_page_source = self.driver.page_source
        start_x = self.driver.get_window_size()['width'] // 3
        start_y = self.driver.get_window_size()['height'] * 0.75
        end_y = self.driver.get_window_size()['height'] * 0.25  
        while True:
            self.driver.swipe(start_x, start_y, start_x, end_y, duration=200)
            updated_page_source = self.driver.page_source  #Get the updated page source
            if updated_page_source == initial_page_source:
                break
            initial_page_source = updated_page_source  #Update the initial page source for the next iteration
        
    def swipe_to_point(self,x1,y1,x2,y2,milliseconds):
        self.driver.swipe(x1,y1,x2,y2,milliseconds)   
        
    #def tap_coordinate(self,x,y):
        #action = TouchAction(self.driver)
        #action.tap(None,x,y,1).perform()
        
    def get_attribute_value(self,by,selector,attribute):
        return self.get_element(by,selector).get_attribute(attribute)    
    
    def go_back(self):
        self.driver.back()
        
    def scroll_to_text(self, target_text, max_swipes=10):
        """
        Scroll until the target text is visible on the screen.

        Args:
            target_text (str): The text you want to find.
            max_swipes (int): Maximum number of swipes to attempt before giving up.

        Returns:
            bool: True if the target text is found, False otherwise.
        """
        screen_width = self.driver.get_window_size()['width']
        screen_height = self.driver.get_window_size()['height']
        
        for _ in range(max_swipes):
            try:
                element = self.driver.find_element(AppiumBy.XPATH, f"//*[contains(@text, '{target_text}')]")
                if element.is_displayed():
                    return True
            except:
                pass
            
            # Calculate scroll positions dynamically based on screen dimensions
            start_x = screen_width // 3
            start_y = int(screen_height * 0.75)
            end_y = int(screen_height * 0.55)
            
            # Perform a dynamic swipe action
            self.driver.swipe(start_x, start_y, start_x, end_y, duration=500)
        
        return False  # Target text not found after maximum swipes    
    
    def scroll_up_to_text(self, target_text, max_swipes=10):
        """
        Scroll until the target text is visible on the screen.

        Args:
            target_text (str): The text you want to find.
            max_swipes (int): Maximum number of swipes to attempt before giving up.

        Returns:
            bool: True if the target text is found, False otherwise.
        """
        screen_width = self.driver.get_window_size()['width']
        screen_height = self.driver.get_window_size()['height']
        
        for _ in range(max_swipes):
            try:
                element = self.driver.find_element(AppiumBy.XPATH, f"//*[contains(@text, '{target_text}')]")
                if element.is_displayed():
                    return True
            except:
                pass
            
            # Calculate scroll positions dynamically based on screen dimensions
            start_x = screen_width // 2
            start_y = int(screen_height * 0.6)
            end_x = start_x
            end_y = int(screen_height * 0.8)
            
            # Perform a dynamic swipe action
            self.driver.swipe(start_x, start_y, end_x, end_y, duration=500)
        
        return False  # Target text not found after maximum swipes  
    
    def check_text_not_visible(self,text):
        try:
            self.get_element(AppiumBy.XPATH,self.commonlocators.TEXT_XPATH.format(text),5)
        except:
            assert True  
    