package com.akvasoft.dental_scrape;

import com.akvasoft.dental_scrape.common.CreateExcelFile;
import com.akvasoft.dental_scrape.common.DateTimeUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Controller
public class Dental implements InitializingBean {
    private static FirefoxDriver driver = null;
    private static String url[] = {"http://www.dciindia.org.in/DentistDetails.aspx"};
    private static String codes[] = {"CALCIO"};
    private static HashMap<String, String> handlers = new HashMap<>();
    int nextPage = 1035;
    @Autowired
    private Repo repo;

    public void initialise() throws Exception {

        System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(false);

        driver = new FirefoxDriver(options);

        for (int i = 0; i < url.length - 1; i++) {
            driver.executeScript("window.open()");
        }

        ArrayList<String> windowsHandles = new ArrayList<>(driver.getWindowHandles());

        for (int i = 0; i < url.length; i++) {
            handlers.put(codes[i], windowsHandles.get(i));
        }
        List<DentalContent> dataList = new ArrayList<>();

        while (true) {
            try {
                dataList = browse();
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.err.println("finished..");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("RESTARTING------");

            }
        }
    }

    private List<DentalContent> browse() throws Exception {
        List<DentalContent> list = new ArrayList<>();
        try {
            JavascriptExecutor webdriver = (JavascriptExecutor) driver;
            driver.get("http://www.dciindia.org.in/DentistDetails.aspx");
            WebElement table = null;


            List<WebElement> elements = null;

            int pageCount = 1;
            boolean pageFound = false;
            for (int i = 0; i < 2354; i++) {

                System.out.println("Running main loop count " + i);
                Thread.sleep(20000);
                WebElement navigator = driver.findElementByXPath("/html/body/form/table/tbody/tr[1]/td/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[3]/td/fieldset/table/tbody/tr/td/div/table/tbody/tr[102]/td/table/tbody/tr");

                int count = 0;
                for (WebElement page : navigator.findElements(By.xpath("./*"))) {
                    count++;
                    try {
                        try {
                            if (Integer.parseInt(page.getAttribute("innerText")) == nextPage) {
                                webdriver.executeScript("arguments[0].scrollIntoView();", page);
                                page.click();
                                nextPage++;
                                pageFound = true;
                                break;
                            }

                        } catch (NumberFormatException c) {
                            if (count > 10) {
                                webdriver.executeScript("arguments[0].scrollIntoView();", page);
                                page.click();
                                pageFound = false;
                                break;
                            }
                        }
                    } catch (StaleElementReferenceException k) {
                        pageFound = false;
                    }
                }

                if (!pageFound) {
                    continue;
                }
                boolean isLoaded = false;
                boolean isPageBack = false;
                while (!isLoaded) {
                    try {
                        try {
                            table = driver.findElementByXPath("/html/body/form/table/tbody/tr[1]/td/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[3]/td/fieldset/table/tbody/tr/td/div/table/tbody");
                            isLoaded = true;
                        } catch (NoSuchElementException g) {
                            isLoaded = false;
                            driver.navigate().back();
                            driver.navigate().refresh();
                            System.err.println("refreshing..");
                        }
                    } catch (UnhandledAlertException c) {
                        Thread.sleep(5000);
                    }
                }

                int rows = 0;
                DentalContent content = null;
                List<WebElement> tRows = table.findElements(By.xpath("./*"));
                for (int j = 0; j < tRows.size(); j++) {

                    if (rows == 0 || rows > 100) {
                        System.out.println("skipped row " + rows);
                        rows++;
                        continue;
                    }

                    content = new DentalContent();
                    try {
                        List<WebElement> data = tRows.get(j).findElements(By.xpath("./*"));
                        content.setNo(data.get(0).getAttribute("innerText"));
                        content.setName(data.get(1).getAttribute("innerText"));
                        content.setRegistration(data.get(2).getAttribute("innerText"));
                        content.setCouncil(data.get(3).getAttribute("innerText"));

                        if (null != repo.getTopByRegistrationEquals(content.getRegistration())) {
                            System.err.println("duplicate found in database.....");
                            rows++;
                            continue;
                        }

                        repo.save(content);
                    } catch (StaleElementReferenceException e) {
                        boolean loaded = false;
                        while (!loaded) {
                            driver.navigate().refresh();
                            try {
                                table = driver.findElementByXPath("/html/body/form/table/tbody/tr[1]/td/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[3]/td/fieldset/table/tbody/tr/td/div/table/tbody");
                                tRows = table.findElements(By.xpath("./*"));
                                j--;
                                loaded = true;
                                continue;
                            } catch (UnhandledAlertException p) {
                                Thread.sleep(5000);
                                table = driver.findElementByXPath("/html/body/form/table/tbody/tr[1]/td/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[3]/td/fieldset/table/tbody/tr/td/div/table/tbody");
                                tRows = table.findElements(By.xpath("./*"));
                                j--;
                                loaded = true;
                                continue;
                            }
                        }
                    }

                    System.out.println(content.getNo() + "==" + content.getName() + "==" + content.getRegistration() + "==" + content.getCouncil());
                    rows++;
                }

                pageCount++;
                System.out.println("count of i === " + i);
                if (i == 500) {
                    break;
                }
            }


        } catch (NoAlertPresentException e) {

        } catch (NoSuchElementException m) {
            driver.navigate().refresh();
        }
        return list;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.initialise();
    }
}
