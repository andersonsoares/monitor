package dao;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class KeywordDAOTest {

	@Before
	public void beforeTest() {
		System.out.println("Alou");
	}
	
	@After
	public void afterTest() {
		System.out.println("Alou Alou");
		System.out.println(new Date());
	}
	
	@Test
	public void testAdd() {
		assertThat("Anderson").isEqualTo("Soares");
	}
	
}
