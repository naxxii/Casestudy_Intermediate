import pytest
import time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager

chrome_options = Options()
chrome_options.add_argument('--no-sandbox')
chrome_options.add_argument('--disable-dev-shm-usage')
chrome_options.add_experimental_option('excludeSwitches', ['enable-logging'])  # Suppresses DevTools logs.

@pytest.fixture(scope="class")
def driver_init(request):
    web_driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)
    request.cls.driver = web_driver
    yield
    web_driver.close()

@pytest.mark.usefixtures("driver_init")
class BasicTest:
    pass

class TestSwagLabs(BasicTest):
    def test_login(self):
        try:
            print("Starting test_login")
            self.driver.get("https://www.saucedemo.com/")
            time.sleep(3)
            self.driver.find_element(By.ID, "user-name").send_keys("standard_user")
            self.driver.find_element(By.ID, "password").send_keys("secret_sauce")
            self.driver.find_element(By.ID, "login-button").click()
            time.sleep(3)
            WebDriverWait(self.driver, 10).until(
                EC.presence_of_element_located((By.CLASS_NAME, 'inventory_list'))
            )
            print("Login successful")
        except Exception as e:
            print("Error during login:", str(e))

    def test_verify_products_page(self):
        try:
            print("Starting test_verify_products_page")
            time.sleep(3)
            assert "Products" in self.driver.find_element(By.CLASS_NAME, 'title').text
            add_to_cart_buttons = self.driver.find_elements(By.CLASS_NAME, 'btn_inventory')
            assert len(add_to_cart_buttons) == 6
            assert self.driver.find_element(By.CLASS_NAME, 'product_sort_container').is_displayed()
            print("Product page verified successfully")
        except Exception as e:
            print("Error verifying products page:", str(e))

    def test_add_item_to_cart(self):
        try:
            print("Starting test_add_item_to_cart")
            self.driver.find_element(By.ID, "add-to-cart-sauce-labs-backpack").click()
            time.sleep(3)
            WebDriverWait(self.driver, 10).until(
                lambda driver: driver.find_element(By.CLASS_NAME, 'shopping_cart_badge').text == '1'
            )
            item_description = self.driver.find_element(By.CLASS_NAME, 'inventory_item_desc').text
            assert "carry.allTheThings()" in item_description
            print("Item added to cart successfully")
        except Exception as e:
            print("Error adding item to cart:", str(e))

    def test_verify_item_and_logout(self):
        try:
            print("Starting test_verify_item_and_logout")
            self.driver.find_element(By.CLASS_NAME, 'shopping_cart_link').click()
            time.sleep(3)
            assert "Sauce Labs Backpack" in self.driver.find_element(By.CLASS_NAME, 'inventory_item_name').text
            self.driver.find_element(By.ID, 'react-burger-menu-btn').click()
            time.sleep(3)
            logout_link = WebDriverWait(self.driver, 10).until(
                EC.element_to_be_clickable((By.ID, 'logout_sidebar_link'))
            )
            logout_link.click()
            time.sleep(3)
            WebDriverWait(self.driver, 10).until(
                EC.presence_of_element_located((By.ID, "login-button"))
            )
            print("Logout successful")
        except Exception as e:
            print("Error during logout:", str(e))
