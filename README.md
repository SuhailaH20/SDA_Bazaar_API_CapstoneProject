
# SDA Bazaar API Test Automation Framework

## Framework Overview

This is a TestNG + Rest Assured + Java + Maven test automation framework with:
- Modular test design organized by user stories (US01, US05, etc.)
- Reusable base specification (`apiBazaar`) for authentication and headers
- ConfigReader for centralized configuration management
- ObjectMapperUtils for JSON parsing and test data handling
- Positive and negative test coverage for API endpoints
- Smoke suite execution via TestNG XML
- Extent Reports and Allure integration for reporting
- JavaFaker for dynamic test data generation

---

## Project Structure

````
SDA_Bazaar_API_Capstone/
├── java/
│   ├── baseurls/
│   │   └── apiBazaar.java          # Base URL + token generation
│   ├── tests/
│   │   ├── US01_CreateAccount.java
│   │   ├── US05_AddToCartTests.java
│   │   ├── US17_BrowseAllStores.java
│   │   └── ...
│   └── utilities/
│       ├── ApiUtil.java
│       ├── ApiUtilities.java
│       ├── ConfigReader.java
│       └── ObjectMapperUtils.java
│
├── resources/
│   └── json_data/
│       └── add_to_cart.json
│
├── pom.xml
└── Smoke_suite.xml
````


---

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- IntelliJ IDEA recommended

### Setup
1. Clone or download the framework  
2. Import as Maven project  
3. Update `ConfigReader.properties` with:
   - `api.base.url`
   - `customer.email`
   - `default.password`
4. Run:
   ```bash
   mvn clean install


---

## Maven Commands

### Execute All Tests

```bash
mvn clean test
```

### Execute Specific Suite

```bash
mvn test -DsuiteXmlFile=Smoke_suite.xml
```

### Execute Specific Test Class

```bash
mvn -Dtest=US05_AddToCartTests test
```

---

## Available Suites

| Suite            | Description                                                |
| ---------------- | ---------------------------------------------------------- |
| Smoke_suite.xml  | Runs critical smoke tests across modules                   |
| regression_suite | Full regression coverage (user, store, cart, product APIs) |

---

## Framework Features

### 1. Base Specification (`apiBazaar`)

Handles:

* Base URI setup
* Content type
* Headers (Accept, Authorization)
* Token generation via `/login`

```java
spec = apiBazaar.spec(
    ConfigReader.getCustomerEmail(),
    ConfigReader.getDefaultPassword()
);
```

---

### 2. Utilities

| Utility           | Purpose                     |
| ----------------- | --------------------------- |
| ConfigReader      | Reads environment configs   |
| ObjectMapperUtils | JSON parsing utilities      |
| ApiUtilities      | Common API helper functions |

---

## TestNG Integration

* Suite management via XML
* Method-level control
* Parallel execution supported

---

## Reporting

* Extent HTML Report
* Allure TestNG Report
* Surefire Reports

---

## Example Test (Add to Cart)

```java
@Test
public void TC001_addProduct_defaultQuantity() {
    JsonNode payload = data.get("valid_product");

    Response response = given()
            .spec(spec)
            .body(payload.toString())
            .post("/cart/add");

    Assert.assertEquals(response.statusCode(), 200);
    Assert.assertTrue(response.jsonPath().getBoolean("success"));
    Assert.assertEquals(
        response.jsonPath().getString("message"),
        "Product added to cart successfully"
    );
}
```

---

## Test Reports

Reports are generated in:

* target/surefire-reports
* allure-results
* extent-report.html

---

## Example Test Execution Flow

1. Maven command triggers TestNG suite
2. @BeforeClass initializes spec and token
3. Test execution runs positive and negative scenarios
4. Assertions validate status codes and response data
5. Reporting generates Extent and Allure reports

---

## Contributing

### Adding New Tests

1. Create a new test class under `tests/`
2. Add JSON payloads under `resources/json_data/`
3. Update `Smoke_suite.xml` to include or exclude tests

### Best Practices

* Keep test data externalized in JSON
* Use ObjectMapperUtils for parsing
* Validate both status codes and response body
* Separate positive and negative cases
* Use ConfigReader for environment variables

---

## Support & Resources

* Application URL: [https://bazaarstores.com/api](https://bazaarstores.com/api)
* Swagger API: [https://bazaarstores.com/api/documentation](https://bazaarstores.com/api/documentation)
* Framework: TestNG + Rest Assured
* Build Tool: Maven
* Language: Java 21

---
