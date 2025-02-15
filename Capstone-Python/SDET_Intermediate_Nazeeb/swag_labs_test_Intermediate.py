import pytest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager

# Setup Chrome options
chrome_options = Options()
chrome_options.add_argument("--headless")  # Runs Chrome in headless mode.
chrome_options.add_argument('--no-sandbox')
chrome_options.add_argument('--disable-dev-shm-usage')

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
    # Launch the URL and perform login
    def test_login(self):
        self.driver.get("https://www.saucedemo.com/")
        self.driver.find_element(By.ID, "user-name").send_keys("standard_user")
        self.driver.find_element(By.ID, "password").send_keys("secret_sauce")
        self.driver.find_element(By.ID, "login-button").click()
        assert "inventory" in self.driver.current_url

    # Verify multiple UI elements on the Products page
    def test_verify_products_page(self):
        assert "PRODUCTS" in self.driver.find_element(By.CLASS_NAME, 'title').text
        # Validate the presence of all add-to-cart buttons
        add_to_cart_buttons = self.driver.find_elements(By.CLASS_NAME, 'btn_inventory')
        assert len(add_to_cart_buttons) == 6  # Expect 6 items on the page
        # Validate sorting functionality is visible
        assert self.driver.find_element(By.CLASS_NAME, 'product_sort_container').is_displayed()

    # Add item to cart and validate the item's description
    def test_add_item_to_cart(self):
        self.driver.find_element(By.ID, "add-to-cart-sauce-labs-backpack").click()
        cart_items = self.driver.find_element(By.CLASS_NAME, 'shopping_cart_badge').text
        assert cart_items == '1'
        # Validate the description of the added item
        item_description = self.driver.find_element(By.CLASS_NAME, 'inventory_item_desc').text
        assert "carry.allTheThings()" in item_description

    # Verify item in cart and perform logout
    def test_verify_item_and_logout(self):
        # Verify cart item
        self.driver.find_element(By.CLASS_NAME, 'shopping_cart_link').click()
        assert "Sauce Labs Backpack" in self.driver.find_element(By.CLASS_NAME, 'inventory_item_name').text
        # Navigate to the menu to logout
        self.driver.find_element(By.ID, 'react-burger-menu-btn').click()
        self.driver.find_element(By.ID, 'logout_sidebar_link').click()
        assert "login" in self.driver.current_url

