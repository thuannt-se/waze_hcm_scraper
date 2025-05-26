# Waze Data Scraper

This project is a data scraper that collects traffic and route information from Waze based on specified origin and destination coordinates. It utilizes configuration files in YAML format for easy setup and allows you to save the responses in JSON format for later processing.

## Features

- **Data Scraping**: Harvests real-time traffic data and routes from Waze.
- **YAML Configuration**: Easily configure origin and destination coordinates using YAML files.
- **JSON Output**: Save the scraped data in JSON format for further analysis and processing.
- **Easy to Extend**: The scheduler is designed for easy integration and extension for future features.

## Requirements

- Java 8 or higher
- Maven for dependency management (if applicable)

## Installation

1. Clone the repository:

2. Navigate to the project directory:

   ```bash
   cd waze-data-scraper
   ```

3. If you're using Maven, build the project:

   ```bash
   mvn clean install
   ```

## Configuration

Configure the origin and destination coordinates using a YAML file. Create a file named `config.yml` in the root of the project with the following structure:

```yaml
routes:
    name: <route_name>
    origin: 
      latitude: <origin_latitude>
      longitude: <origin_longitude>
    destination:
      latitude: <destination_latitude>
      longitude: <destination_longitude>
```

### Example `config.yml`

```yaml
routes:
   - name: CMT8
     origin:
      lat: 10.792846
      lon: 106.653536
     destination:
      lat: 10.771577
      lon: 106.693071
```

## Usage

To run the scraper and fetch data from Waze, use the following command:

```bash
java -jar target/waze-data-scraper.jar
```
Or if you are prefer containerization, you can build a Docker image and run it:
```bash
docker compose up --build
```

```bash
The program will read the coordinates specified in the `config.yml`, scrape the data from Waze, and save the responses to a JSON file (`output.json`) in the project directory.

## Output

The output will be stored in a JSON file with a name as `<route_name>_timestamp.json`, where `<route_name>` is the name specified in your `config.yml`. The JSON file will contain the traffic and route information retrieved from Waze.
```

## Error Handling

Ensure to handle any exceptions related to network issues or incorrect configuration values. The application includes basic error handling which can be extended to suit your error recovery strategies.

## Contributing

Contributions are welcome! If you have suggestions for improvements or want to report issues, please open an issue or submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).

## Acknowledgements

- Thanks to the Waze developers for providing useful traffic routing data.
- Open-source libraries that assist with JSON and YAML parsing.
