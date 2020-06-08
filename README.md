# Telny Chat

A simple TCP Chat Server written in JAVA.

## Build Image

Use [Docker](https://www.docker.com) to build a Container image of TelnyChat.
Build process will do:
1. Download and install OpenJDK : The JAVA environment to execute the Chat.
2. Download and install Maven   : To compile and build source code.
3. Copy locale source into docker image.
4. Do Maven Build, executing Integration Test Suite, and generating the executable Java archive.
5. Configure Launch Command and Container local Port.

```bash
docker build -f Dockerfile -t telnychat:1.0.0 .
```
## Run Container

Use [Docker](https://www.docker.com) to run builded image as Container
Run process will start new Container, binding 10000 host port to 10000 Container port.

```bash
docker run --name telnychat -i -d --rm -p 10000:10000 telnychat:1.0.0
```
## Usage

Server listens on port 10000.

1. Connect using a telnet Cient, like Putty (Select RAW mode)
2. Entar a nickname
3. Chat (Don't use special char "|", because it is a reserved char).

After logging in, you will be subscripted on a Default Channel (Topic), where clients
can share their messages. So, your messages will be sended to all connected clients.

You can also submit to the server some simple commands.
Commands supported are:
1. **___GET_CLIENTS**        : Get a list of all connected clients.
2. **___GET_TOPICS**         : Get a list of all active Channels (Topics).
3. **___BROADCAST_TO_TOPIC** : Send a message to all Clients with a subscription on a Topic. *
4. **___DISCONNECT**         : Disconnect from the Chat

About **___BROADCAST_TO_TOPIC** command:
The message's format to use with this Command is: COMMAND|TOPIC|MESSAGE (Es.: ___BROADCAST_TO_TOPIC|HOBBY|Do you like to play football?)
To Send a message on the Default Channel, you can simply write the MESSAGE,
without specifying the ___BROADCAST_TO_TOPIC command and it's format.

![picture alt](https://pasteboard.co/JccKHbc.png "Chat Preview")

## License
[MIT](https://choosealicense.com/licenses/mit/)

