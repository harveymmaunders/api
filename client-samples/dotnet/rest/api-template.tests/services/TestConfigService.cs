namespace ApiTemplate.Tests.Services;

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
        var configService = new ConfigService("test-appsettings.json");
        var settings = configService.LoadSettings();

        Assert.IsType<ClientCredentialSettings>(settings);
        Assert.NotNull(settings);

        Assert.Equal("client-id", settings.ClientId);
        Assert.Equal("C:/test/path/keys/private_key.pem", settings.PrivateKeyFile);
        Assert.Equal("C:/test/path/keys/public_key.cer", settings.PublicKeyFile);
        Assert.Equal(new[] { "https://api.example.com/hello-world/.default" }, settings.Scopes);
        Assert.Equal("api.example.com", settings.Tenant);
        Assert.Equal("https://api.example.com/", settings.Url);

        Assert.Equal("", configService.LoadProxySettings());
    }

    [Fact]
    public void TestSetsCorrectProxy()
    {
        var configService = new ConfigService("test-appsettings-with-proxy.json");
        Assert.Equal("http://proxy.example.com:8080", configService.LoadProxySettings());
    }
}
