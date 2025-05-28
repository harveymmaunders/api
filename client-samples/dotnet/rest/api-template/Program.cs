// Initialize the configuration service to load application settings
var configService = new ConfigService();

// Load settings from the configuration service
var settings = configService.LoadSettings();

// Initialize the authentication token service with the loaded settings
var authTokenService = new AuthTokenService(settings);

// Retrieve the authentication token synchronously
var token = authTokenService.GetAuthToken();

// Call the API using the settings, authentication token, and proxy settings
ApiService.CallApi(
    settings.Url,
    token,
    configService.LoadProxySettings()
);
