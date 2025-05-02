namespace api_template.tests.services;

public class TestConfigService
{
    [Fact]
    public void TestAppSettingsFileExists()
    {
        Assert.True(File.Exists("appsettings.json"), "appsettings.json file does not exist.");
    }

    [Fact]
    public void TestLoadSettings()
    {
        ConfigService configService = new ConfigService("test-appsettings.json");
        var _settings = configService.loadSettings();

        Assert.IsType<ClientCredentialSettings>(_settings);
        Assert.NotNull(_settings);

        Assert.Equal("client-id", _settings.clientId);
        Assert.Equal("C:/test/path/keys/private_key.pem", _settings.privateKeyFile);
        Assert.Equal("C:/test/path/keys/public_key.cer", _settings.publicKeyFile);
        Assert.Equal(new[] { "https://api.example.com/hello-world/.default" }, _settings.scopes);
        Assert.Equal("api.example.com", _settings.tenant);
        Assert.Equal("https://api.example.com/", _settings.url);

        Assert.Equal("", configService.loadProxySettings());
    }

    [Fact]
    public void TestSetsCorrectProxy()
    {
        ConfigService configService = new ConfigService("test-appsettings-with-proxy.json");
        Assert.Equal("https://proxy.example.com:8080", configService.loadProxySettings());
    }
}
