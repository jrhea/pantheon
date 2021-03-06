description: Run Pantheon using the official docker image
<!--- END of page meta data -->

# Running Pantheon from Docker Image

A Docker image is provided to run a Pantheon node in a Docker container. 

Use this Docker image to run a single Pantheon node without installing Pantheon. 

## Prerequisites

To run Pantheon from the Docker image, you must have [Docker](https://docs.docker.com/install/) installed.  

## Quickstart

To run a Pantheon node in a container connected to the Ethereum mainnet: 

```bash
docker run pegasyseng/pantheon:latest
```

## Command Line Options 
 
!!!attention
    You cannot use the following Pantheon command line options when running Pantheon from the Docker image:
    
    * [`--datadir`](../Reference/Pantheon-CLI-Syntax.md#datadir), see [Persisting Data](#persisting-data)
    * [`--config`](../Reference/Pantheon-CLI-Syntax.md#config), see [Custom Configuration File](#custom-configuration-file)
    * [`--genesis`](../Reference/Pantheon-CLI-Syntax.md#genesis), see [Custom Genesis File](#custom-genesis-file).
    * [`--rpc-listen`](../Reference/Pantheon-CLI-Syntax.md#rpc-listen), [`--p2plisten`](../Reference/Pantheon-CLI-Syntax.md#p2plisten), [`--ws-listen`](../Reference/Pantheon-CLI-Syntax.md#ws-listen), see [Exposing Ports](#exposing-ports)
    
    All other [Pantheon command line options](/Reference/Pantheon-CLI-Syntax) work in the same way as when Pantheon is installed locally.

### Persisting Data 

Specify a Docker volume to persist data between stopping and restarting the container. This is the equivalent of specifying the [`--datadir`](../Reference/Pantheon-CLI-Syntax.md#datadir) option. 

If a Docker volume is not specified, all data saved to the data directory is removed each time the container is stopped. 

To run Pantheon specifying a volume for the data directory: 

```bash
docker run --mount type=bind,source=/<pantheonDataDir>,target=/var/lib/pantheon pegasyseng/pantheon:latest

``` 

Where `<pantheonDataDir>` is the volume to which the data is saved.  

### Custom Configuration File 

Specify a custom configuration file to provide a file containing key/value pairs for command line options. This is the equivalent of specifying the [`--config`](../Reference/Pantheon-CLI-Syntax.md#config) option. 

To run Pantheon specifying a custom configuration file: 
```bash
docker run --mount type=bind,source=/<path/myconf.toml>,target=/etc/pantheon/pantheon.conf pegasyseng/pantheon:latest

```

Where `myconf.toml` is your custom configuration file and `path` is the absolute path to the file.
!!!example
    ```bash
    docker run --mount type=bind,source=/Users/username/pantheon/myconf.toml,target=/etc/pantheon/pantheon.conf pegasyseng/pantheon:latest
    ```

### Custom Genesis File 

Specify a custom genesis file to configure the blockchain. This is equivalent to specifying the `--genesis` option.

To run Pantheon specifying a custom genesis file: 
```bash
docker run --mount type=bind,source=</path/mygenesis.json>,target=/etc/pantheon/genesis.json pegasyseng/pantheon:latest
```

Where `mygenesis.json` is your custom configuration file and `path` is the absolute path to the file.

!!!example
    ```bash
    docker run --mount type=bind,source=/Users/username/pantheon/mygenesis.json,target=/etc/pantheon/genesis.json pegasyseng/pantheon:latest
    ```

### Exposing Ports

Expose ports for P2P peer discovery, JSON-RPC service, and WebSockets. This is required to use the 
defaults ports or specify different ports (the equivalent of specifying the [`--rpc-listen`](../Reference/Pantheon-CLI-Syntax.md#rpc-listen), 
[`--p2p-listen`](../Reference/Pantheon-CLI-Syntax.md#p2p-listen), [`--ws-listen`](../Reference/Pantheon-CLI-Syntax.md#ws-listen) options).

To run Pantheon exposing local ports for access: 
```bash
$ docker run -p <localportJSON-RPC>:8545 -p <localportWS>:8546 -p <localportP2P>:30303 pegasyseng/pantheon:latest --rpc-enabled --ws-enabled
```

!!!example
    To enable RPC calls to http://127.0.0.1:8545 and P2P discovery on http://127.0.0.1:13001:
    ```bash
    docker run -p 8545:8545 -p 13001:30303 pegasyseng/pantheon:latest --rpc-enabled
    ```
 
## Starting Pantheon 

### Run a Node on Ethereum Mainnet 

To run a node on the Ethereum mainnet: 

```bash
docker run -p 30303:30303 --mount type=bind,source=/<myvolume/pantheon>,target=/var/lib/pantheon pegasyseng/pantheon:latest
```

To run a node on mainnet with the HTTP JSON-RPC service enabled: 
```bash
docker run -p 8545:8545 -p 30303:30303 --mount type=bind,source=/<myvolume/pantheon>,target=/var/lib/pantheon pegasyseng/pantheon:latest --rpc-enabled
```

## Run a Node on Ropsten Testnet 

Save a local copy of the [Ropsten genesis file](https://github.com/PegaSysEng/pantheon/blob/master/config/src/main/resources/ropsten.json). 

To run a node on Ropsten: 
```bash
docker run -p 30303:30303 --mount type=bind,source=/<myvolume/pantheon/ropsten>,target=/var/lib/pantheon --mount type=bind,source=/<path>/ropsten.json,target=/etc/pantheon/genesis.json pegasyseng/pantheon:latest --network-id=3 --bootnodes=enode://6332792c4a00e3e4ee0926ed89e0d27ef985424d97b6a45bf0f23e51f0dcb5e66b875777506458aea7af6f9e4ffb69f43f3778ee73c81ed9d34c51c4b16b0b0f@52.232.243.152:30303,enode://94c15d1b9e2fe7ce56e458b9a3b672ef11894ddedd0c6f247e0f1d3487f52b66208fb4aeb8179fce6e3a749ea93ed147c37976d67af557508d199d9594c35f09@192.81.208.223:30303
```

## Run a Node on Rinkeby Testnet 

To run a node on Rinkeby: 
```bash
docker run -p 30303:30303 --mount type=bind,source=/<myvolume/pantheon/rinkeby>,target=/var/lib/pantheon pegasyseng/pantheon:latest --rinkeby
```

## Run a Node for Testing 

To run a node that mines blocks at a rate suitable for testing purposes with WebSockets enabled: 
```bash
docker run -p 8546:8546 --mount type=bind,source=/<myvolume/pantheon/testnode>,target=/var/lib/pantheon pegasyseng/pantheon:latest --dev-mode --bootnodes= --miner-enabled --miner-coinbase fe3b557e8fb62b89f4916b721be55ceb828dbd73 --rpc-cors-origins "all" --ws-enabled
```

## Stopping Pantheon and Cleaning up Resources

When you're done running nodes, you can shut down the node container without deleting resources. Alternatively, you can delete the container (after stopping it) and its associated volume. Run `docker container ls` and `docker volume ls` to obtain the container and volume names. Then run the following commands:

To stop a container:
```bash
docker stop <container-name>
```

To delete a container:
```bash
docker rm <container-name>
```

To delete a container volume (optional):
```bash
docker volume rm <volume-name>
```