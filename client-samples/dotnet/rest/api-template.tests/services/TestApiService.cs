using WireMock.Server;
using WireMock.RequestBuilders;
using WireMock.ResponseBuilders;

namespace ApiTemplate.Tests.Services;

public class TestApiService
{
    [Fact]
    public async Task TestGetResponseAsync()
    {
        // Arrange: Start the mock server
        var server = WireMockServer.Start();

        // Define the mock server's URL
        string endpoint = "/api/test";
        var mockUrl = $"{server.Urls[0]}{endpoint}";

        // Configure the mock server to respond to a GET request
        server.Given(
            Request.Create()
                .WithPath(endpoint)
                .UsingGet()
        ).RespondWith(
            Response.Create()
                .WithStatusCode(200)
                .WithBody("Mock Response")
        );

        // Act: Call the ApiService.GetResponse method
        var token = "mock-token";
        var proxy = string.Empty; // No proxy for this test
        var response = ApiService.GetResponse(mockUrl, token, proxy);

        // Assert: Verify the response
        Assert.NotNull(response);
        Assert.Equal(200, (int)response.StatusCode);
        var responseBody = await response.Content.ReadAsStringAsync();
        Assert.Equal("Mock Response", responseBody);

        // Verify the mock server received the request
        var logEntries = server.LogEntries;
        Assert.Single(logEntries);
        Assert.Contains(logEntries, log => log.RequestMessage.Path == endpoint && log.RequestMessage.Method == "GET");

        // Assert: Verify the request had the expected Authorization header
        Assert.Contains(logEntries, log => 
            log.RequestMessage.Headers.ContainsKey("Authorization") &&
            log.RequestMessage.Headers["Authorization"].Contains($"Bearer {token}")
        );

        // Stop the server
        server.Stop();
    }
}
