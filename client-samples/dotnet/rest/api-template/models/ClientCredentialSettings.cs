public class ClientCredentialSettings
{
    public required string ClientId { get; set; }
    public required string PrivateKeyFile { get; set; }
    public required string PublicKeyFile { get; set; }
    public required List<string> Scopes { get; set; }
    public required string Tenant { get; set; }
    public required string Url { get; set; }
}
