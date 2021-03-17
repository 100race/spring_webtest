package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/*
 * 실행순서
 * BeforeClass ->
 *  Before -> Test1 -> After ->
 *  Before -> Test2 -> After -> 
 *  AfterClass
 */
public class TerminalTest {
	private static Terminal term;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		term = new Terminal();
		term.netConnect();
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		term.netDisConnect();
	}

	@Before
	public void setUp() throws Exception {
		term.logon("user1", "1234");
	}

	@After
	public void tearDown() throws Exception {
		term.logoff();
	}

	@Test
	public void terminalConnected() {
		assertTrue(term.isLogon()); //로그인이 되어있는지 확인
		System.out.println("== logon test");
	}

	@Test
	public void getReturnMsg() { //어노테이션만 있으면 test로 인식
		term.setMessage("hello");
		assertEquals("hello", term.getReturnMsg());
		System.out.println("== messsage test");
	}

}
