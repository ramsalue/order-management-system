package com.meli.ordermanagementsystem.integration;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Integration Test Suite
 * Runs all integration tests together
 */
@Suite
@SelectClasses({
        ClientControllerIntegrationTest.class,
        ItemControllerIntegrationTest.class,
        OrderControllerIntegrationTest.class,
        ErrorHandlingIntegrationTest.class
})
public class IntegrationTestSuite {
    // Test suite runner
    // All tests will be executed when this suite is run
}