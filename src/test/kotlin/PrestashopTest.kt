import com.codeborne.selenide.Condition.*
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.Selectors.byText
import com.codeborne.selenide.Selenide.*
import com.codeborne.selenide.WebDriverRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.interactions.Actions
import java.time.Duration
import java.util.UUID
import kotlin.random.Random
import com.codeborne.selenide.Configuration
import org.openqa.selenium.By

class PrestashopTest {

    @BeforeEach
    fun setUp() {
        open("https://demo.prestashop.com")
        switchTo().frame("framelive")
        Configuration.timeout = 20000
    }

    @Test
    fun prestashopTestActions() {
        val randomEmail = "user_${UUID.randomUUID()}@email.com"

        // Register an account, check you're logged in
            // Wait until Create account page is loaded
        `$`(By.cssSelector("a[title='Create account']"))
            .shouldBe(visible, Duration.ofSeconds(10))
            .click()
        `$`(By.ById("field-id_gender-1"))
            .click()
        `$`(By.ById("field-firstname"))
            .value  = "Mister"
        `$`(By.ById("field-lastname"))
            .value = "Tester"
        `$`(By.ById("field-email"))
            .value = randomEmail
        `$`(By.ById("field-password"))
            .value = "supeRP!ssword123"
        `$`(By.ById("field-birthday"))
            .value = "01/01/1980"
            // Click all checkboxes
        `$`(By.name("optin"))
            //.shouldBe(visible)
            .click()
        `$`(By.name("psgdpr"))
            //.shouldBe(visible)
            .click()
        `$`(By.name("newsletter"))
            //.shouldBe(visible)
            .click()
        `$`(By.name("customer_privacy"))
            //.shouldBe(visible)
            .click()
            // Save customer
        `$`(By.cssSelector("button.btn.btn-primary.form-control-submit.float-xs-right[data-link-action='save-customer']"))
            .click()
        //`$x`("//*[@id='_desktop_user_info']/div/a[2]/span").shouldBe(visible)
            // check you're logged in, Element will be reused
        val signOutButton = `$`(By.cssSelector("a.logout.hidden-sm-down"))
            .shouldBe(visible,Duration.ofSeconds(10))
        // Open "Accessories" section
        `$`(By.ById("category-6"))
            .click()
        // Open "Home Accessories" section
        `$`(By.ById("left-column"))

        // Filter items in "Home Accessories" within price range 18-23
        fun filterMinMaxPriceAndCheckFilter() {

            val minPriceHandle =
                `$x`("//div[@id='search_filters']//a[contains(@class, 'ui-slider-handle')][1]")
                    .shouldBe(visible, Duration.ofSeconds(20))
            val maxPriceHandle =
                `$x`("//div[@id='search_filters']//a[contains(@class, 'ui-slider-handle')][2]")
                    .shouldBe(visible, Duration.ofSeconds(20))
            val actions = Actions(WebDriverRunner.getWebDriver())
            sleep(15000)
            actions.dragAndDropBy(minPriceHandle, 30, 0).perform()
            sleep(15000)
            element("#search_filters").find(byText("€18.00 - €42.00")).shouldBe(visible, Duration.ofSeconds(15))
            sleep(10000)
            actions.dragAndDropBy(maxPriceHandle, -150, 0).perform()
            sleep(10000)
            val filteredItems =
                element("#search_filters").find(byText("€18.00 - €23.00")).shouldBe(visible, Duration.ofSeconds(10))

            // Check items are correctly filtered
            val minPrice = 18.0
            val maxPrice = 23.0
            val productPrices = `$$`(".product-price").texts().map { it.replace("€", "").toDouble() }
            for (price in productPrices) {
                require(price in minPrice..maxPrice) { "Product price $price is not within the range $minPrice - $maxPrice" }
            }
        }
        filterMinMaxPriceAndCheckFilter()


        // Randomly choose an item
        fun selectRandomProduct() {
            val products: ElementsCollection = `$$x`("//*[@id='js-product-list']//article")

            if (products.count() > 0) {
                val randomProduct = products[Random.nextInt(products.count())]
                randomProduct.click()

            } else {
                println("No products found.")
            }

    }
        selectRandomProduct()
        // Increase product quantity by 1 clicking the up button
        `$`(".btn.btn-touchspin.js-touchspin.bootstrap-touchspin-up")
            .click()

        // Add products to cart
        val addProductToCart = `$`(".btn.btn-primary.add-to-cart")
        addProductToCart.click()

        sleep(10000)
        // Extract price values:
        val productPriceInCartText = `$x`("//*[@id='blockcart-modal']/div/div/div[2]/div/div[1]/div/div[2]/p")
            .text()
        println("Product price in cart text: $productPriceInCartText")
        val productQuantityInCartText = `$x`("//*[@id='blockcart-modal']/div/div/div[2]/div/div[1]/div/div[2]/span[2]/strong")
            .text()
        println("Product quantity in cart text: $productQuantityInCartText")
        val totalPriceText = `$x`("//*[@id='blockcart-modal']/div/div/div[2]/div/div[2]/div/p[4]/span[2]")
            .text()
        println("Total price text: $totalPriceText")
        //Check a price is correctly calculated
        var productPriceInCart = `$x`("//*[@id='blockcart-modal']/div/div/div[2]/div/div[1]/div/div[2]/p")
            .text().replace("[^\\d.]".toRegex(), "").toDouble()
        val productQuantityInCart = `$x`("//*[@id='blockcart-modal']/div/div/div[2]/div/div[1]/div/div[2]/span[2]/strong")
            .text().toInt()
        var totalPrice = `$x`("//*[@id='blockcart-modal']/div/div/div[2]/div/div[2]/div/p[4]/span[2]")
            .text().replace("[^\\d.]".toRegex(), "").toDouble()

        // Assert the total price
        assert(totalPrice == productPriceInCart * productQuantityInCart) {
            "Total price $totalPrice does not equal product price $productPriceInCart multiplied by quantity $productQuantityInCart"
        }

        //Go back to filtered list of items, choose one more item
        `$x`("//*[@id='blockcart-modal']/div/div/div[2]/div/div[2]/div/div/button")
            .shouldBe(visible, Duration.ofSeconds(10))
            .click()
        sleep(10000)

        `$x`("//*[@id='wrapper']/div/nav/ol/li[3]/a/span")
            .click()

        // Use same filter function
        filterMinMaxPriceAndCheckFilter()
        // Select random product
        selectRandomProduct()
       sleep(10000)
        // Save additional product price and quantity
        var additionalProductPrice = `$`(".current-price-value").text().replace("[^\\d.]".toRegex(), "").toDouble()
        println("Additional product price: $additionalProductPrice")
        //val additionalProductQuantity = `$`("#quantity_wanted").value?.toInt() //?.toInt() ?: 0
        val additionalProductQuantity = 1
        println("Additional product quantity: $additionalProductQuantity")
        // Add product in cart
        addProductToCart.click()
        sleep(10000)
        // Safe new Total price with additional products
        var totalPriceWithAdditionalProduct = `$x`("//*[@id='blockcart-modal']/div/div/div[2]/div/div[2]/div/p[4]/span[2]")
            .text().replace("[^\\d.]".toRegex(), "").toDouble()
        println("Total price with additional product: $totalPriceWithAdditionalProduct")

        //sleep(10000)
        // Go to cart
        `$x`("//*[@id='blockcart-modal']/div/div/div[2]/div/div[2]/div/div/a").click()
        //Check a price is correctly calculated
        val finalPrice = additionalProductPrice + totalPrice
        val finalPriceFormatted = String.format("%.2f", finalPrice).toDouble()
        assert(totalPriceWithAdditionalProduct == finalPriceFormatted)  {
            "Total price $totalPriceWithAdditionalProduct does not equal all products price"
        }

        println(totalPriceWithAdditionalProduct)
        // Checkout
        `$x`("//*[@id='main']/div/div[2]/div[1]/div[2]/div/a").click()

        // Fill out the form
            //Address field:
        `$x`("//*[@id='field-address1']")
            .value = "Somewhere st 1000"
            // City
        `$x`("//*[@id='field-city']")
            .value = "Boston"
            // State (dropdown menu)
        `$`(By.id("field-id_state"))
            .selectOption(1)
            // Zip code
        `$`(By.id("field-postcode"))
            .value = "10034"
            // Click Continue button
        `$`("button.continue.btn.btn-primary.float-xs-right[name='confirm-addresses'][value='1']")
            .click()

        sleep(10000)

        // Choose a shipping method - as only option is preselected than continue to next step
        `$`("button.continue.btn.btn-primary.float-xs-right[name='confirmDeliveryOption'][value='1']")
            .shouldBe(visible, Duration.ofSeconds(10))
            .click()

        //Choose "payment by Check", check the total price
            // Click checkbox
        `$x`("//*[@id='conditions_to_approve[terms-and-conditions]']").click()
            // Check the total price

        // Confirm your order and check order details
            // Unable to confirm, as proceed order option is disabled
        // Logout, check you've been successfully logged out
            // Click on MyStore logo to return on Homepage
        `$`(By.cssSelector("img.logo.img-fluid")).click()
            // Click Sign Out element to logout
        signOutButton.click()
            // Locate and assert Sign in element is visible
        `$`(By.cssSelector("span.hidden-sm-down")).shouldBe(visible)



        println("Test finished successfully.")

    }
}