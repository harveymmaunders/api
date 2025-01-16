from websocket_server import WebsocketServer
import logging
import time

API_HOST = "localhost"
API_PORT = 13254
API_URL = f"ws://{API_HOST}:{API_PORT}"


def new_client(client, server):
    server.send_message_to_all("Message")
    time.sleep(3)
    server.shutdown_gracefully()


def main():
    server = WebsocketServer(host=API_HOST, port=API_PORT, loglevel=logging.INFO)
    server.set_fn_new_client(new_client)
    server.run_forever()


if __name__ == "__main__":
    main()
