package net.petafuel.styx.updater;

import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class MainTest {
	static File DUMMY_CONFIG = new File("liquibase.properties");

	@BeforeAll
	public static void prepare() throws IOException {
		DUMMY_CONFIG.createNewFile();
	}

	@AfterAll
	public static void tearDown() {
		DUMMY_CONFIG.delete();
	}

	@Test
	public void testIsOperationRequested() {
		String args[] = {
				"command=dump",
				"COMMAND=InIt",
				"command= update"
		};
		Assert.assertTrue("command=dump was in parameter list and supposed to be found",
				Main.isOperationRequested(args, "command=dump"));
		Assert.assertTrue("command=init with random upper and lower caser supposed to be found",
				Main.isOperationRequested(args, "command=init"));
		Assert.assertFalse("Parameter list contains space and not supposued to be found",
				Main.isOperationRequested(args, "command=update"));
	}

	@Test
	public void testSelectLiquibaseFile() {
		Assert.assertEquals("liquibase/master-init.xml", Main.selectLiquibaseFile("command=init"));
		Assert.assertEquals("liquibase/master-update.xml", Main.selectLiquibaseFile("command=update"));
	}

	@Test()
	public void testSelectLiquibaseFileThrowsException() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Main.selectLiquibaseFile("command=unknown");
		}, "Unknown command expected to produce IllegalArgumentException");
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Main.selectLiquibaseFile();
		}, "No parameters expected to produce IllegalArgumentException");
	}

}
