using System.Security.Cryptography.X509Certificates;
using NSubstitute;

namespace api_template.tests.services;

public class TestAuthTokenService
{
    private readonly ClientCredentialSettings _clientCredentialSettings;
    private readonly AuthTokenService _authTokenServiceMock;

    public TestAuthTokenService()
    {
        _clientCredentialSettings = new ClientCredentialSettings
        {
            ClientId = "your-client-id",
            PrivateKeyFile = "path/to/privateKey.pem",
            PublicKeyFile = "path/to/publicKey.pem",
            Scopes = new() { "scope1", "scope2" },
            Tenant = "your-tenant-id",
            Url = "https://your-auth-url.com"
        };
        _authTokenServiceMock = Substitute.For<AuthTokenService>(_clientCredentialSettings);
    }

    [Fact]
    public void TestInitializeConfiguresApp()
    {
        _authTokenServiceMock.When(mock => mock.CreateConfidentialClient(
            Arg.Any<string>(),
            Arg.Any<string>(),
            Arg.Any<X509Certificate2>()
        )).DoNotCallBase();

        _authTokenServiceMock.When(mock => mock.GetCertificate(
            Arg.Any<string>(),
            Arg.Any<string>()
        )).DoNotCallBase();

        _authTokenServiceMock.Initialize();

        // Assert
        _authTokenServiceMock.Received(1).GetCertificate(
            _clientCredentialSettings.PublicKeyFile,
            _clientCredentialSettings.PrivateKeyFile
        );

        _authTokenServiceMock.Received(1).CreateConfidentialClient(
            _clientCredentialSettings.ClientId,
            $"https://login.microsoftonline.com/{_clientCredentialSettings.Tenant}",
            Arg.Any<X509Certificate2>()
        );
    }
}
