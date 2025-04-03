# Gatling Load Testing for JWT/RBAC Authentication  

## Overview  
This project is a **Gatling load test** designed to evaluate the performance of authentication and authorization mechanisms in a Spring Boot application using **JWT & RBAC**.  

It simulates **500 concurrent login attempts** (90% invalid, 10% valid) to test how the system handles unauthorized access and measures response times.  

## Test Scenarios  
- **Valid Login Test**: Sends correct credentials and expects a **200 OK** response with a JWT token.  
- **Invalid Login Test**: Sends incorrect credentials and expects a **401 Unauthorized** response.  
- **Load Testing**: Runs with **500 virtual users** ramping up over **30 seconds**.  

## Setup & Requirements  
- Java 17+  
- Gatling (Open Source)  
- Morpheus (for metrics scraping)  
- Grafana (for visualization)  

### **Running the Tests**  
1. Clone the repository:  
   ```bash
   git clone https://github.com/your-username/gatling-jwt-rbac.git
   cd gatling-jwt-rbac 