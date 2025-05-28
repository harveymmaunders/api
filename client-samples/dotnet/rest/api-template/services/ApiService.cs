using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;

public class ApiService
{
    // Method to call an API with a given URL, token, and optional proxy
    public static HttpResponseMessage GetResponse(string url, string token, string proxy)
    {
        Console.WriteLine($"Calling API at {url}.\n");
        var httpClientHandler = new HttpClientHandler();

        // Configure proxy if provided
        if (!string.IsNullOrEmpty(proxy))
        {
            httpClientHandler.Proxy = new WebProxy(proxy);
            httpClientHandler.UseProxy = true;
        }

        // Create an HttpClient instance with the configured handler
        using var httpClient = new HttpClient(httpClientHandler);

        // Set the Authorization header with the provided token
        httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", token);
        
        // Make a GET request
        return httpClient.GetAsync(url).Result;
    }

    public static void CallApi(string url, string token, string proxy)
    {
        // Get the response from the API
        var response = GetResponse(url, token, proxy);
        
        // Check if the response is successful
        if (response.IsSuccessStatusCode)
        {
            // Read and format the JSON response content
            var content = response.Content.ReadAsStringAsync().Result;
            var formattedJson = System.Text.Json.JsonSerializer.Serialize(
                System.Text.Json.JsonDocument.Parse(content).RootElement,
                new System.Text.Json.JsonSerializerOptions { WriteIndented = true }
            );

            // Print the formatted JSON response
            Console.WriteLine($"API Response: {formattedJson}");
        }
        else
        {
            // Print an error message if the API call fails
            Console.WriteLine($"API call failed with status code: {response.StatusCode}");
        }
    }
}
