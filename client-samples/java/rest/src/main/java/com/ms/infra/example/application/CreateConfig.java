package com.ms.infra.example.application;

import java.util.HashMap;

import java.util.Map;
import java.util.Scanner;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateConfig {
    private static Scanner scanner;

    public static void main(String... args) throws Exception {
        scanner = new Scanner(System.in);
        createPropertiesFile(
                getEnv(),
                getClientID(),
                getAppScope(),
                getPrivateKeyFile(),
                getPublicKeyFile(),
                getProxyConfig()
        );
        scanner.close();
    }

    public static void createPropertiesFile(String env, String clientId, String clientAppScope, String privateKeyFile, String publicKeyFile, Map<String, String> proxyConfig) {
        String uatOption = env.equals("UAT") ? "-uat" : "";

        Properties properties = new Properties();
        properties.setProperty(
                "morgan-stanley-oauth2-token-uri",
                String.format("https://login.microsoftonline.com/api%s.morganstanley.com/oauth2/v2.0/token", uatOption)
        );
        properties.setProperty("client-app-id", clientId);
        properties.setProperty("client-app-scope", clientAppScope);
        properties.setProperty("private-key-file", privateKeyFile);
        properties.setProperty("public-certificate-file", publicKeyFile);
        properties.setProperty(
                "ms-url-api-domain",
                String.format("https://api%s.morganstanley.com/", uatOption)
        );

        if (!proxyConfig.isEmpty()) {
            properties.setProperty("proxy-host", proxyConfig.get("proxy_host"));
            properties.setProperty("proxy-port", proxyConfig.get("proxy_port"));
        }

        Path configPath = Path.of(String.format("src/main/resources/META-INF/microprofile-config%s.properties", uatOption));

        try {
            Files.createDirectories(configPath.getParent()); // Ensure directory exists
            try (FileOutputStream out = new FileOutputStream(configPath.toFile())) {
                properties.store(out, "MicroProfile Configuration File");
            }
            System.out.println("Config file created: " + configPath.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getClientID() {
        System.out.println("Please enter your client ID");
        System.out.println("e.g. 12345678-abcd-1234-efgh-1234567890ab");

        System.out.print("Client ID: ");
        return scanner.nextLine().trim();
    }

    public static String getAppScope() {
        System.out.println("\nPlease enter your app scope");
        System.out.println("e.g. https://api-uat.morganstanley.com/hello-world/.default");

        System.out.print("App Scope: ");
        return scanner.nextLine().trim();
    }

    public static String getPrivateKeyFile() {
        System.out.println("\nPlease enter your private key file (should end in .der)");
        String privateKeyFile = "";
        while (!privateKeyFile.endsWith(".der")) {
            System.out.print("Private Key File: ");
            privateKeyFile = scanner.nextLine().trim();

            if (privateKeyFile.endsWith(".pem")) {
                System.out.println("You need to have a der encoded file, you can create one by using:");
                System.out.println("openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt\n");
            }
            else if (!privateKeyFile.endsWith(".der")) {
                System.out.println("Your file should end in .der\n");
            }
        }
        return privateKeyFile;
    }

    public static String getPublicKeyFile() {
        System.out.println("\nPlease enter your public key file (should end in .cer)");
        String publicKeyFile = "";
        while (!publicKeyFile.endsWith(".cer")) {
            System.out.print("Public Key File: ");
            publicKeyFile = scanner.nextLine().trim();

            if (!publicKeyFile.endsWith(".cer")) {
                System.out.println("Your file should end in .cer\n");
            }
        }
        return publicKeyFile;
    }

    public static Map<String, String> getProxyConfig() {
        Map<String, String> proxyConfig = new HashMap<>();

        String option = "";

        while (!option.equals("y") && !option.equals("n")) {
            System.out.println("\nWould you like to add a proxy config?");
            System.out.print("\n y -> Yes\n n -> No\n Choice: ");
            option = scanner.nextLine().trim().toLowerCase();
        }

        if (option.equals("n")) {
            return proxyConfig;
        };

        System.out.print("Please enter the proxy host: ");
        String proxyHost = scanner.nextLine().trim();
        proxyConfig.put("proxy_host", proxyHost);

        String proxyPort;
        while (true) {
            try {
                System.out.print("Please enter the proxy port: ");
                proxyPort = scanner.nextLine().trim();
                Integer.parseInt(proxyPort);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error: The proxy port must be an integer.\n");
            }
        }
        proxyConfig.put("proxy_port", proxyPort);

        return proxyConfig;
    }

    public static String getEnv() {
        String option = "";

        while (!option.equals("u") && !option.equals("p")) {
            System.out.println("\nWhich environment is this for?");
            System.out.print("\n u -> UAT\n p -> Production\n Choice: ");
            option = scanner.nextLine().trim().toLowerCase();
        }

        if (option.equals("u")) {
            return "UAT";
        }
        return "prod";
    }
}
