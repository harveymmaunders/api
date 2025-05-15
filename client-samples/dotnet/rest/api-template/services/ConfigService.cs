using Microsoft.Extensions.Configuration;

public class ConfigService
{
    // Static property to hold the JSON configuration file name or path
    public string JsonFile { get; set; } = "appsettings.json";

    // Constructor to initialize the JSON configuration file path
    public ConfigService(string jsonFilePath = "appsettings.json")
    {
        JsonFile = jsonFilePath;
    }

    // Method to load client credential settings from the configuration file
    public ClientCredentialSettings LoadSettings()
    {
        // Build the configuration object using the JSON file and environment variables
        IConfigurationRoot config = new ConfigurationBuilder()
            .AddJsonFile(JsonFile)
            .AddEnvironmentVariables()
            .Build();

        // Retrieve and return the ClientCredentials section from the configuration
        ClientCredentialSettings clientCredentialSettings = config.GetRequiredSection("ClientCredentials").Get<ClientCredentialSettings>()
            ?? throw new InvalidOperationException("ClientCredentials section is missing or invalid in the configuration.");
        return clientCredentialSettings;
    }

    // Method to load proxy settings from the configuration file
    public string LoadProxySettings()
    {
        // Build the configuration object using the JSON file and environment variables
        IConfigurationRoot config = new ConfigurationBuilder()
            .AddJsonFile(JsonFile)
            .AddEnvironmentVariables()
            .Build();

        // Retrieve the ProxySettings section from the configuration
        ProxySettings? proxySettings = config.GetSection("ProxySettings").Get<ProxySettings>();
        if (proxySettings != null)
        {
            // Return the proxy URL if proxy settings are found
            return $"http://{proxySettings.ProxyHost}:{proxySettings.ProxyPort}";
        }
        // Return an empty string if no proxy settings are found
        return "";
    }
}
