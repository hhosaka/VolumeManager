package com.nag.android.ringmanager.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(LocationSettingTest.class);
		suite.addTestSuite(RingManagerTest.class);
		//$JUnit-END$
		return suite;
	}

}
