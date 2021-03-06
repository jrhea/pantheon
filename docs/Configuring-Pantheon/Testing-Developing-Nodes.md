description: Pantheon Clique Proof-of-Authority (PoA) consensus protocol implementation
path: blob/master/ethereum/core/src/main/resources/
source: rinkeby.json
<!--- END of page meta data -->

# Testing and Developing Nodes

## Bootnodes

Bootnodes are used to initially discover peers. 

### Mainnet and Public Testnets

For mainnet, Rinkeby, Ropsten, and Görli, Pantheon predefines a list of enode URLs.  

### Private Networks

To start a bootnode for a private network:

1.  Export the public key to a file:

    !!! example
        ```bash
        pantheon --genesis=privateNetworkGenesis.json --datadir=nodeDataDir export-pub-key bootnode
        ```
        Where `privateNetworkGenesis.json` and `nodeDataDir` are changed to the relevant values for 
        your private network. 
        
        The node public key is exported to the `bootnode` file.
    
2. Start the bootnode, specifying:

    * Empty string for the [`--bootnodes`](../Reference/Pantheon-CLI-Syntax.md#bootnodes) option because this is the bootnode. 
    * Network ID for your private network.
    * Genesis file and data directory as in the previous step. 
    
    !!! example
        ```
        pantheon --bootnodes="" --genesis=privateNetworkGenesis.json --datadir=nodeDataDir --network-id 123 
         ```
     
To specify this bootnode for another node, the enode URL for the `--bootnodes` option is `enode://<id>@<host:port>` where:

* `<id>` is the node public key written to the specified file (`bootnode` in the above example) excluding the initial 0x. 
* `<host:port>` is the host and port the bootnode is listening on for P2P peer discovery. Specified by the `--p2p-listen` option for the bootnode (default is `127.0.0.1:30303`).

!!! example
    If the `--p2p-listen` option is not specified and the node public key exported is `0xc35c3ec90a8a51fd5703594c6303382f3ae6b2ecb9589bab2c04b3794f2bc3fc2631dabb0c08af795787a6c004d8f532230ae6e9925cbbefb0b28b79295d615f`
    
    The enode URL is:
    `enode://c35c3ec90a8a51fd5703594c6303382f3ae6b2ecb9589bab2c04b3794f2bc3fc2631dabb0c08af795787a6c004d8f532230ae6e9925cbbefb0b28b79295d615f@127.0.0.1:30303` 

!!! info
    The default host and port for P2P peer discovery is `127.0.0.1:30303`. Use the `--p2p-listen` option to specify a host and port. 

To start a node specifying the bootnode for P2P discovery:

!!! example
    ```bash
    pantheon --genesis=privateNetworkGenesis.json --datadir=nodeDataDir --p2p-listen=127.0.0.1:30301 --network-id=123 --bootnodes=enode://c35c3ec90a8a51fd5703594c6303382f3ae6b2ecb99bab2c04b3794f2bc3fc2631dabb0c08af795787a6c004d8f532230ae6e9925cbbefb0b28b79295d615f@127.0.0.1:30303
    ``` 