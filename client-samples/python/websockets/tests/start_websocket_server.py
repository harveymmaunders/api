from websocket_server import WebsocketServer
import logging
from time import sleep

API_HOST = "localhost"
API_PORT = 13254
API_URL = f"ws://{API_HOST}:{API_PORT}"


def new_client(client, server):
    server.send_message(client, "Message from the server")
    sleep(2)
    server.shutdown_gracefully()


def main():
    server = WebsocketServer(host=API_HOST, port=API_PORT, loglevel=logging.INFO)
    server.set_fn_new_client(new_client)
    server.run_forever()


if __name__ == "__main__":
    main()
