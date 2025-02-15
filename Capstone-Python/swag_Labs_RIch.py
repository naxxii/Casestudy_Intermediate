import pytest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager

chrome_options = Options()
# chrome_options.add_argument("--headless")
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
    def test_login(self):
        self.driver.get("https://www.saucedemo.com/")
        self.driver.find_element(By.ID, "user-name").send_keys("standard_user")
        self.driver.find_element(By.ID, "password").send_keys("secret_sauce")
        self.driver.find_element(By.ID, "login-button").click()
        assert "inventory" in self.driver.current_url

    def test_verify_products_page(self):
        assert "products" in self.driver.find_element(By.CLASS_NAME, 'title').text.lower()

    def test_add_item_to_cart(self):
        self.driver.find_element(By.ID, "add-to-cart-sauce-labs-backpack").click()
        cart_items = self.driver.find_element(By.CLASS_NAME, 'shopping_cart_badge').text
        assert cart_items == '1'

    def test_verify_item_and_logout(self):
        self.driver.find_element(By.CLASS_NAME, 'shopping_cart_link').click()
        assert "Sauce Labs Backpack" in self.driver.find_element(By.CLASS_NAME, 'inventory_item_name').text
        self.driver.find_element(By.ID, 'react-burger-menu-btn').click()
        logout_link = WebDriverWait(self.driver, 10).until(
            EC.element_to_be_clickable((By.ID, 'logout_sidebar_link'))
        )
        logout_link.click()
        assert "login" in self.driver.current_url

if __name__ == "__main__":
    pytest.main(['-v', '-s', '--html=report.html', __file__])
