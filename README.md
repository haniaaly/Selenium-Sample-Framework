# Acceptance Tests

These UI tests are configured via a local properties file and can be run on macOS, Linux, or Windows using the Maven Wrapper.

## Prerequisites

- **Java JDK 17+** installed (`java -version`)
- A supported browser installed locally (**Chrome**, **Firefox**, or **Edge**)
- Internet access (downloads Maven/dependencies)

## Setup (Required)

1. Copy the committed example config file:
   - From: `config/test.properties.example`
   - To: `config/test.properties`

2. Edit `config/test.properties` and adjust values.

### Example `config/test.properties`

```properties
url=https://example.com
username=test@gmail.com
password=testPassword
portfolioValue=0.00
# Optional
browsers=chrome
headless=false
currency=€
```

## Configuration reference

### Required keys:

- `url` – Application base URL
- `username` – Login username/email
- `password` – Login password
- `portfolioValue` – Expected numeric value (example: `0.00`)

### Optional keys (remove if unwanted):

- `browsers` – Browser(s) to run. You can run more than one browser by providing a comma-separated list, for example:
  browsers=chrome,firefox
- `headless` – `true` or `false`. If omitted, it should default to `true` (headless).
- `currency` – Currency symbol used in UI comparisons (example: `€`).


## Running the tests

### macOS / Linux:

1. From the project root run:
   ```bash
   ./mvnw test
   ```

2. If you get "Permission denied":
   ```bash
   chmod +x mvnw
   ./mvnw test
   ```

### Windows (PowerShell / cmd):

- From the project root run:
  ```bat
  mvnw.cmd test
  ```

### Important:
- For optional parameters: **REMOVE the entire line** from `config/test.properties` (don't leave an empty value like `headless=`).
