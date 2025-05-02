public class ClientCredentialSettings
{
    public required string clientId { get; set; }
    public required string privateKeyFile { get; set; }
    public required string publicKeyFile { get; set; }
    public required List<string> scopes { get; set; }
    public required string tenant { get; set; }
    public required string url { get; set; }
}
