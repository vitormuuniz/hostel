package br.com.hostel.e2e.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import br.com.hostel.e2e.ChromeConnection;

@RunWith(JUnitPlatform.class)
@TestMethodOrder(OrderAnnotation.class)
class ReservationTest {

	private final ChromeConnection chromeConnection = new ChromeConnection();
	private final WebDriver driver = chromeConnection.Connection();

	@BeforeEach
	void init() throws InterruptedException {
		driver.get("http://localhost:3000/");
		driver.manage().window().maximize();
		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/section/form/input[1]")).sendKeys("daniel@email.com");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/section/form/input[2]")).sendKeys("123456");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/section/form/button")).click();
		Thread.sleep(3000);
	}

	@Test
	@Order(1)
	void registerANewReservation() throws InterruptedException {

		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/a")).click();
		Thread.sleep(3000);
		driver.findElement(By.xpath("//*[@id=\"check-in\"]")).sendKeys("16022027");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"check-out\"]")).sendKeys("19022027");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"numberOfGuests\"]")).sendKeys("3");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"maxDailyRate\"]")).sendKeys("900");
		Thread.sleep(4000);

		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/form/button")).click();
		Thread.sleep(3000);

		assertEquals("Quartos Disponiveis", driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/h1")).getText());

		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/ul/li[1]/button")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/ul/li[2]/button")).click();
		Thread.sleep(1000);

		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/a")).click();
		Thread.sleep(3000);

		assertEquals("Selecione a forma de pagamento",
				driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/section/h1")).getText());

		driver.findElement(By.xpath("//*[@id=\"payment\"]")).click();

		Thread.sleep(3000);
		driver.findElement(By.xpath("//*[@id=\"react-select-3-option-0\"]")).click();

		Thread.sleep(3000);
		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/form/button")).click();
		Thread.sleep(5000);

		driver.close();
	}

	@Test
	@Order(2)
	void updateReservation() throws InterruptedException {

		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/ul/li/button[2]")).click();
		Thread.sleep(3000);
		
		assertEquals("Atualizar reserva",
				driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/section/h1")).getText());

		driver.findElement(By.xpath("//*[@id=\"check-in\"]")).sendKeys("1122029");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"check-out\"]")).sendKeys("1422029");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"numberOfGuests\"]")).sendKeys(Keys.BACK_SPACE);
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"numberOfGuests\"]")).sendKeys("2");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/form/button")).click();
		Thread.sleep(3000);

		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/ul/li[2]/button")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/a")).click();

		assertEquals("Selecione a forma de pagamento",
				driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/section/h1")).getText());

		driver.findElement(By.xpath("//*[@id=\"payment\"]")).click();
		Thread.sleep(3000);
		
		driver.findElement(By.xpath("//*[@id=\"react-select-3-option-0\"]")).click();
		Thread.sleep(3000);
		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/form/button")).click();
		Thread.sleep(3000);

		driver.close();
	}

	@Test
	@Order(3)
	void deleteReservation() throws InterruptedException {

		driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/ul/li/button[1]")).click();
		Thread.sleep(3000);
		Alert firstAlert = driver.switchTo().alert();
		firstAlert.accept();
		Thread.sleep(3000);

		driver.close();
	}

}
