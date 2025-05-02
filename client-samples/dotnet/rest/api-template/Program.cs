// Initialize the configuration service to load application settings
var configService = new ConfigService();

// Load settings from the configuration service
var settings = configService.loadSettings();

// Initialize the authentication token service with the loaded settings
var authTokenService = new AuthTokenService(settings);
authTokenService.initalize();

// Retrieve the authentication token synchronously
var token = authTokenService.GetAuthToken().GetAwaiter().GetResult();

// Call the API using the settings, authentication token, and proxy settings
ApiService.callApi(
    settings.url,
    token,
    configService.loadProxySettings()
);
