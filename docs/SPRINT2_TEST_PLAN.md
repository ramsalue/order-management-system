# Sprint 2 Test Plan

## Overview

This document outlines the testing strategy for Sprint 2 profile configuration.

## Test Objectives

1. Verify all three profiles (dev, test, prod) start successfully
2. Validate profile-specific configurations are loaded
3. Test profile switching between environments
4. Verify environment variables work correctly
5. Validate configuration validation catches errors
6. Test database connections for each profile
7. Verify API endpoints work in all profiles
8. Test security configurations per profile

## Test Environments

### Development (dev)
- Profile: dev
- Database: meli_order_db (local)
- Port: 8080
- Expected behavior: Detailed logging, CORS enabled, DevTools active

### Testing (test)
- Profile: test
- Database: meli_order_db_test (local)
- Port: 8081
- Expected behavior: Moderate logging, transaction rollback, isolated data

### Production (prod)
- Profile: prod
- Database: meli_order_db (simulated locally)
- Port: 8080
- Expected behavior: Minimal logging, secure configuration, environment variables required

## Test Cases

| Test Case | Objective |
|---|---|
| TC-01 | Verify dev profile starts correctly |
| TC-02 | Verify test profile starts correctly |
| TC-03 | Verify prod profile starts correctly |
| TC-04 | Verify can switch between profiles |
| TC-05 | Verify validation passes with correct config |
| TC-06 | Verify validation catches missing variables |
| TC-07 | Verify database connection in dev |
| TC-08 | Verify test database isolation |
| TC-09 | Verify prod uses environment variables |
| TC-10 | Verify API works in all profiles |
| TC-11 | Verify CORS works per profile |
| TC-12 | Verify logging is correct per profile |
| TC-13 | Verify error details per profile |
| TC-14 | Verify environment info is correct |
| TC-15 | Verify actuator exposure per profile |

---

**Tested By:** Luis E Ramírez
**Date:** October 19, 2025  
**Status:** In Progress