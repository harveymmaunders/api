using System.Security.Cryptography.X509Certificates;
using NSubstitute;

namespace api_template.tests.services;

public class TestAuthTokenService
{
    private readonly ClientCredentialSettings clientCredentialSettings;
    private AuthTokenService authTokenServiceMock;
    public TestAuthTokenService()
    {
        clientCredentialSettings = new ClientCredentialSettings
        {
            clientId = "your-client-id",
            privateKeyFile = "path/to/privateKey.pem",
            publicKeyFile = "path/to/publicKey.pem",
            scopes = new List<string> { "scope1", "scope2" },
            tenant = "your-tenant-id",
            url = "https://your-auth-url.com"
        };
        authTokenServiceMock = Substitute.For<AuthTokenService>(clientCredentialSettings);
    }

    [Fact]
    public void TestInitializeConfiguresApp()
    {
        authTokenServiceMock.When(mock => mock.CreateConfidentialClient(
            Arg.Any<string>(),
            Arg.Any<string>(),
            Arg.Any<X509Certificate2>()
        )).DoNotCallBase();

        authTokenServiceMock.When(mock => mock.GetCertificate(
            Arg.Any<string>(),
            Arg.Any<string>()
        )).DoNotCallBase();

        authTokenServiceMock.initalize();

        // Assert
        authTokenServiceMock.Received(1).GetCertificate(
            clientCredentialSettings.publicKeyFile,
            clientCredentialSettings.privateKeyFile
        );

        authTokenServiceMock.Received(1).CreateConfidentialClient(
            clientCredentialSettings.clientId,
            $"https://login.microsoftonline.com/{clientCredentialSettings.tenant}",
            Arg.Any<X509Certificate2>()
        );
    }
}
    