using Microsoft.Identity.Client;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;

public class AuthTokenService
{
    // Private field to hold the client credential settings
    private readonly ClientCredentialSettings _clientCredentialSettings;

    // Static property to hold the scopes for the token request
    public static string[]? Scopes { get; private set; }

    // Constructor to initialize the AuthTokenService with client credential settings
    public AuthTokenService(ClientCredentialSettings settings)
    {
        // Store the client credential settings
        _clientCredentialSettings = settings;
        Scopes = _clientCredentialSettings.Scopes.ToArray();
    }

    // Method to get the authority URL
    public string GetAuthority(string tenant)
    {
        return $"https://login.microsoftonline.com/{tenant}";
    }

    // Method to create a ConfidentialClientApplication instance
    public virtual IConfidentialClientApplication CreateConfidentialClient(string clientId, string authority, X509Certificate2 certificate)
    {
        return ConfidentialClientApplicationBuilder
            .Create(clientId)
            .WithAuthority(authority)
            .WithCertificate(certificate)
            .Build();
    }

    // Method to load and return an X509Certificate2 object using public and private key files
    public virtual X509Certificate2 GetCertificate(string publicKeyFile, string privateKeyFile)
    {
        using var rsa = RSA.Create();
        // Import the private key from the PEM file
        rsa.ImportFromPem(File.ReadAllText(privateKeyFile));

        // Load the certificate from the public key file and associate it with the private key
        var cert = X509CertificateLoader.LoadCertificateFromFile(publicKeyFile).CopyWithPrivateKey(rsa);
        return cert;
    }

    // Asynchronous method to acquire an authentication token
    public virtual async Task<string> GetToken(IConfidentialClientApplication app)
    {
        var authResult = await app.AcquireTokenForClient(Scopes).ExecuteAsync();
        Console.WriteLine($"AuthResult: {authResult}");
        return authResult.AccessToken;
    }

    // Asynchronous method to acquire an authentication token
    public string GetAuthToken()
    {
        var token = string.Empty;
        // Acquire the token for the client using the configured scopes
        using (X509Certificate2 certificate = GetCertificate(_clientCredentialSettings.PublicKeyFile, _clientCredentialSettings.PrivateKeyFile))
        {
            var app = CreateConfidentialClient(_clientCredentialSettings.ClientId, GetAuthority(_clientCredentialSettings.Tenant), certificate);
            token = GetToken(app).Result;
        }
        Console.WriteLine("Token acquired successfully.");
        return token; // Return the access token
    }
}
