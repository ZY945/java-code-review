# JMeter Test Plan for Spring Boot JVM Performance Testing

This directory contains JMeter test plans for evaluating the performance of the Spring Boot application under different JVM configurations.

## Test Plans

1. `qps-test-plan.jmx` - Tests Queries Per Second (QPS) using read-only operations
2. `tps-test-plan.jmx` - Tests Transactions Per Second (TPS) using write operations
3. `mixed-load-test-plan.jmx` - Tests a mix of read and write operations

## Test Scenarios

Each test plan includes the following thread groups:

1. **Low Load** - 50 concurrent users
2. **Medium Load** - 200 concurrent users
3. **High Load** - 500 concurrent users
4. **Extreme Load** - 1000 concurrent users

## How to Run

1. Install JMeter (version 5.4+ recommended)
2. Open the desired test plan in JMeter
3. Configure the server hostname and port in the "User Defined Variables" section
4. Run the test and analyze the results

## Test Endpoints

The test plans target the following endpoints:

- `/api/users/qps-test` - Simple read operation for QPS testing
- `/api/users/tps-test` - Write operation for TPS testing
- `/api/users/recent` - Complex query for mixed load testing
- `/api/users/metrics` - For monitoring request counts and performance

## Analyzing Results

Each test plan includes listeners for:
- Summary Report
- Aggregate Report
- Response Time Graph
- Transaction Per Second Graph

## JVM Configuration Comparison

After running tests with different JVM configurations, compare the results to determine the optimal settings for your application's workload.
