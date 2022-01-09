/*
Автор Садыков Александр
Тест
Ввод данных в личном кабинете otus.ru
Проверка вводимых данных
сайт/логин/пароль берется из файла config.properties(вариант из environment variables)
log file logs.log
 */
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import static java.lang.System.getenv;


public class CheckerRows {
    private  String host = "";
    private  String logSite = "";
    private  String pasSite = "";

    protected static WebDriver driver;
    private final Logger logger = LogManager.getLogger(CheckerRows.class);

    //чтение переменных из файла
    public void Vars() throws IOException {
        FileInputStream fis;
        Properties property = new Properties();
        fis = new FileInputStream("src/main/resources/config.properties");
        property.load(fis);

        host = property.getProperty("site.host");
        logSite = property.getProperty("site.login");
        pasSite = property.getProperty("site.pass");
    }

    @Before
    public void StartUp() throws IOException {
        /*
      переменные окружениия можно и так
        logSite = System.getenv("siteLogin");
       pasSite = System.getenv("sitePas");
      */

        Vars();

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        logger.info("Драйвер запущен");
    }

    @After
    public void End(){
        if (driver!=null)
            driver.quit();
        logger.info("Драйвер закрыт");
    }
    private void Auth() {

        String locator = "button.header2__auth.js-open-modal";
        driver.findElement(By.cssSelector(locator)).click();
        driver.findElement(By.cssSelector("div.new-input-line_slim:nth-child(3) > input:nth-child(1)")).clear();
        driver.findElement(By.cssSelector("div.new-input-line_slim:nth-child(3) > input:nth-child(1)")).sendKeys(logSite);
        driver.findElement(By.cssSelector(".js-psw-input")).clear();
        driver.findElement(By.cssSelector(".js-psw-input")).sendKeys(pasSite);
        driver.findElement(By.xpath("//button[contains(.,'Войти')]")).click();
        logger.info("Успешная авторизация");

    }
    public void  enterLC() throws InterruptedException {
        Thread.sleep(1000);//без него на chrome не запускается(раньше запускался) на firefox работает.
        driver.get(host+"/lk/biography/personal/");
        logger.info("Вход в личный кабинет");
    }
    @Test
    public void openPage() throws InterruptedException {
        //1. Открыть otus.ru
        driver.get(host);
        logger.info("Открыта главная страница отус");

        //2. Авторизоваться на сайте
        Auth();

        //3. Войти в личный кабинет

        enterLC();

        //4. Заполнить контакты

        driver.findElement(By.id("id_fname")).clear();
        driver.findElement(By.id("id_fname_latin")).clear();
        driver.findElement(By.id("id_lname")).clear();
        driver.findElement(By.id("id_lname_latin")).clear();
        driver.findElement(By.cssSelector(".input-icon > input:nth-child(1)")).clear();
        driver.findElement(By.cssSelector(".input-icon > input:nth-child(1)")).clear();
        driver.findElement(By.cssSelector("#id_contact-0-value")).clear();

        driver.findElement(By.id("id_fname")).sendKeys("Александр");
        driver.findElement(By.id("id_fname_latin")).sendKeys("Alexandr");
        driver.findElement(By.id("id_lname")).sendKeys("Садыков");
        driver.findElement(By.id("id_lname_latin")).sendKeys("Sadykov");
        driver.findElement(By.cssSelector(".input-icon > input:nth-child(1)")).sendKeys("09.01.1979");

        //Страна
        if(!driver.findElement(By.cssSelector(".js-lk-cv-dependent-master > label:nth-child(1) > div:nth-child(2)")).getText().contains("Россия"))
        {
            driver.findElement(By.cssSelector(".js-lk-cv-dependent-master > label:nth-child(1) > div:nth-child(2)")).click();
            driver.findElement(By.xpath("//*[contains(text(), 'Россия')]")).click();
        }
        //Город
        if(!driver.findElement(By.cssSelector(".js-lk-cv-dependent-slave-city > label:nth-child(1) > div:nth-child(2)")).getText().contains("Тюмень"))
        {
            driver.findElement(By.cssSelector(".js-lk-cv-dependent-slave-city > label:nth-child(1) > div:nth-child(2)")).click();
            driver.findElement(By.xpath("//*[contains(text(), 'Тюмень')]")).click();
        }
        //уровень англ.
        if(!driver.findElement(By.cssSelector("div.container__col_12:nth-child(3) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(3) > div:nth-child(2) > div:nth-child(1) > label:nth-child(1) > div:nth-child(2)")).getText().contains("Средний (Intermediate)"))
        {
            driver.findElement(By.cssSelector("div.container__col_12:nth-child(3) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(3) > div:nth-child(2) > div:nth-child(1) > label:nth-child(1) > div:nth-child(2)")).click();
            driver.findElement(By.xpath("//*[contains(text(), 'Средний (Intermediate)')]")).click();
        }
        driver.findElement(By.cssSelector(".radio:nth-child(1)")).click();

        if(!driver.findElement(By.cssSelector(".input_straight-top-right.input_no-border-right.lk-cv-block__input_fake.lk-cv-block__input_select-fake.js-custom-select-presentation")).getText().contains("Viber"));
        {
            driver.findElement(By.cssSelector(".input_straight-top-right.input_no-border-right.lk-cv-block__input_fake.lk-cv-block__input_select-fake.js-custom-select-presentation")).click();
            driver.findElement(By.cssSelector(".lk-cv-block__select-scroll_service > .lk-cv-block__select-option:nth-child(6)")).click();
            driver.findElement(By.id("id_contact-0-value")).sendKeys("+123456789");
        }

        //5. Нажать сохранить
        driver.findElement(By.xpath("//*[contains(text(), 'Сохранить и продолжить')]")).click();


        //6. Открыть https://otus.ru в “чистом браузере”
        driver.quit();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        logger.info("Драйвер запущен");
        logger.info("Запущена проверка полей");
        driver.get(host);
        //7. Авторизоваться на сайте
        Auth();
        //8. Войти в личный кабинет
        enterLC();
        //9. Проверить, что в разделе о себе отображаются указанные ранее данные
        Assert.assertEquals("Александр", driver.findElement(By.id("id_fname")).getAttribute("value"));
        Assert.assertEquals("Alexandr", driver.findElement(By.id("id_fname_latin")).getAttribute("value"));
        Assert.assertEquals("Садыков", driver.findElement(By.id("id_lname")).getAttribute("value"));
        Assert.assertEquals("Sadykov", driver.findElement(By.id("id_lname_latin")).getAttribute("value"));
        Assert.assertEquals("09.01.1979", driver.findElement(By.cssSelector(".input-icon > input:nth-child(1)")).getAttribute("value"));
        Assert.assertEquals("Россия", driver.findElement(By.cssSelector(".js-lk-cv-dependent-master > label:nth-child(1) > div:nth-child(2)")).getText());
        Assert.assertEquals("Тюмень", driver.findElement(By.cssSelector(".js-lk-cv-dependent-slave-city > label:nth-child(1) > div:nth-child(2)")).getText());
        Assert.assertEquals("Средний (Intermediate)", driver.findElement(By.cssSelector("div.container__col_12:nth-child(3) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(3) > div:nth-child(2) > div:nth-child(1) > label:nth-child(1) > div:nth-child(2)")).getText());
        Assert.assertEquals("Viber", driver.findElement(By.cssSelector(".input_straight-top-right.input_no-border-right.lk-cv-block__input_fake.lk-cv-block__input_select-fake.js-custom-select-presentation")).getText());
        Assert.assertEquals("stopper79@gmail.com", driver.findElement(By.id("id_email")).getAttribute("value"));
        Assert.assertEquals("+123456789", driver.findElement(By.id("id_contact-0-value")).getAttribute("value"));
        logger.info("Все проверки пройдены");
    }

}