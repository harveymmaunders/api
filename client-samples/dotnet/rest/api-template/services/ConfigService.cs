using Microsoft.Extensions.Configuration;

public class ConfigService
{
    // Static property to hold the JSON configuration file name or path
    public static string jsonFile { get; set; } = "appsettings.json";

    // Constructor to initialize the JSON configuration file path
    public ConfigService(string jsonFilePath = "appsettings.json")
    {
        jsonFile = jsonFilePath;
    }

    // Method to load client credential settings from the configuration file
    public ClientCredentialSettings loadSettings()
    {
        // Build the configuration object using the JSON file and environment variables
        IConfigurationRoot config = new ConfigurationBuilder()
            .AddJsonFile(jsonFile)
            .AddEnvironmentVariables()
            .Build();

        // Retrieve and return the ClientCredentials section from the configuration
        ClientCredentialSettings clientCredentialSettings = config.GetRequiredSection("ClientCredentials").Get<ClientCredentialSettings>()
            ?? throw new InvalidOperationException("ClientCredentials section is missing or invalid in the configuration.");
        return clientCredentialSettings;
    }

    // Method to load proxy settings from the configuration file
    public string loadProxySettings()
    {
        // Build the configuration object using the JSON file and environment variables
        IConfigurationRoot config = new ConfigurationBuilder()
            .AddJsonFile(jsonFile)
            .AddEnvironmentVariables()
            .Build();

        // Retrieve the ProxySettings section from the configuration
        ProxySettings? proxySettings = config.GetSection("ProxySettings").Get<ProxySettings>();
        if (proxySettings != null)
        {
            // Return the proxy URL if proxy settings are found
            return $"https://{proxySettings.proxyHost}:{proxySettings.proxyPort}";
        }
        // Return an empty string if no proxy settings are found
        return "";
    }
}
