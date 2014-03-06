package com.nag.android.volumemanager.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(LocationSettingTest.class);
		suite.addTestSuite(VolumeManagerTest.class);
		//$JUnit-END$
		return suite;
	}

}
