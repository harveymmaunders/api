import json
from typing import Dict
import sys
import tty
import termios


def move_cursor(pos):
    """Move the cursor to a specific position."""
    sys.stdout.write(f"\r\033[{pos+1}G")
    sys.stdout.flush()


def get_char():
    """Capture a single character from user input without requiring Enter."""
    fd = sys.stdin.fileno()
    old_settings = termios.tcgetattr(fd)
    try:
        tty.setraw(fd)
        return sys.stdin.read(1)
    finally:
        termios.tcsetattr(fd, termios.TCSADRAIN, old_settings)


def get_user_input(fixed_part, template):
    """Allow user to type over 'x' characters and submit only when Enter is pressed."""
    input_list = list(fixed_part + template)
    start_idx = len(fixed_part)
    idx = start_idx

    sys.stdout.write(f"\r{''.join(input_list)}")
    move_cursor(idx)

    while True:
        char = get_char()

        if char in "\r\n":  # Enter to submit
            break
        elif char == "\x7f" and idx > start_idx:  # Backspace
            while idx > start_idx and template[idx - start_idx] != "x":
                idx -= 1
            if idx > start_idx:
                input_list[idx] = "x"
                idx -= 1
        elif (
            char.isalnum()
            and idx < len(input_list)
            and template[idx - start_idx] == "x"
        ):  # Typing
            input_list[idx] = char
            idx += 1
            while idx < len(input_list) and template[idx - start_idx] != "x":
                idx += 1

        sys.stdout.write(f"\r{''.join(input_list)}")
        move_cursor(idx)

    return "".join(input_list)


def get_yes_no(question: str) -> bool:
    """
    Prompt the user with a yes/no question and return the response as a boolean.
    """
    prompt = f"\n{question}\n 1 -> Yes\n 2 -> No\n Choice: "
    option = ""

    while option not in {"1", "2"}:
        option = input(prompt).strip()

    return option == "1"


def get_client_id() -> str:
    """
    Prompt the user for the client ID.
    """
    print("Please enter your client ID")
    print("e.g. 12345678-abcd-1234-efgh-1234567890ab")

    return get_user_input("Client ID: ", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")


def get_api_scope() -> str:
    """
    Prompt the user for the API scope.
    """
    print("\n\nPlease enter your app scope")
    print("e.g. https://api-uat.morganstanley.com/hello-world/.default")

    return input("App Scope: ")


def get_thumbprint() -> str:
    """
    Prompt the user for the certificate thumbprint.
    """
    print("\nPlease enter your certificate thumbprint")
    print("e.g. AB48C0D31F95EBF8425AECF3E7E6FA92B34C8D47")
    print(len("AB48C0D31F95EBF8425AECF3E7E6FA92B34C8D47"))

    return get_user_input("Thumbprint: ", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")


def get_private_key_file() -> str:
    """
    Prompt the user for the private key file path.
    """
    private_key_file = ""
    while not private_key_file.endswith(".pem"):
        private_key_file = input(
            "\n\nPlease enter your private key path (must end with .pem): "
        ).strip()
        if not private_key_file.endswith(".pem"):
            print("Error: The file path must end with .pem")
    return private_key_file


def get_proxies() -> Dict[str, str]:
    """
    Prompt the user for proxy settings if applicable.
    """
    if not get_yes_no("\nWould you like to add a proxy config? "):
        return {}

    proxy_config = {"proxy_host": input("Please enter the proxy host: ").strip()}

    while True:
        try:
            proxy_port = int(input("Please enter the proxy port: ").strip())
            break
        except ValueError:
            print("Error: The proxy port must be an integer.\n")
    proxy_config["proxy_port"] = proxy_port
    return proxy_config


def create_python_config(config: Dict):
    """
    Write the given configuration dictionary to a JSON file.
    """
    try:
        with open("./config.json", "w") as config_file:
            json.dump(config, config_file, indent=4)
        print("\n✅ Config file 'config.json' created successfully!")
    except Exception as e:
        print(f"\n❌ Error writing config file: {e}")


def main():
    """
    Gather user input and generate the configuration file.
    """
    config = {
        "client_id": get_client_id(),
        "scopes": [get_api_scope()],
        "thumbprint": get_thumbprint(),
        "private_key_file": get_private_key_file(),
        "tenant": "api.morganstanley.com",
        "url": input("\nPlease enter your API URL: ").strip(),
        "retry_bad_handshake_status": True,
    }

    config.update(get_proxies())
    create_python_config(config)


if __name__ == "__main__":
    main()
