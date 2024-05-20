# Syn

**Syn** is a Minecraft plugin designed to manage player access to the server.  
Before each new player logs in for the first time, they need to obtain a verification code and enter it in the game to
be added to the allowlist.  
It has a guest mode that allows unverified players to enter the server, except for players on the banlist.

## Installation

1. Download the latest version of Syn plugin
2. Place the plugin file in `plugins` folder of your server
3. Restart your server

## Usage

* `/syn` - Show help information for Syn
* `/syn verify <code/player>` - The player input the verification code to verify themselves, or OPs inputs the
  player's name to verify the Online player
* `/syn gencode [number]` - Generate a specified number of verification codes
* `/syn info [player]` - Show the status of Syn plugin or the player
* `/syn ban <player> [day hour min sec]` - Ban the player
* `/syn unban <player>` - Unban the player
* `/syn guest` - If on, it allows everyone to enter the server, except for players on the ban list.
  If off, it only allows the player in the allowlist to come into the server.
* `/syn log [time] [page]` - Show the log since the specified time or the last time the server was opened

## Permissions

* `syn.*` - Grants access to all Syn-related permissions
    * `syn.gencode` - Allows generating verification codes
    * `syn.ban` - Allows banning players
    * `syn.unban` - Allows unbanning players
    * `syn.guest` - Allows opening/closing the server
* `syn.gencode` - Allows generating verification codes (default: OP)
* `syn.ban` - Allows banning players (default: OP)
* `syn.unban` - Allows unbanning players (default: OP)
* `syn.guest` - Allows opening/closing the server (default: OP)
* `syn.info` - Allows checking the info of Syn system or the player

## Configuration

The configuration file for the plugin is located at `plugins/Syn/config.yml`. You can modify it according to your
needs.

## Support and Reporting Issues

If you encounter any issues or have any suggestions while using Syn, please submit them on GitHub Issues. We will
respond and resolve the issues as soon as possible.

## Contributing

We welcome contributions from the community. If you would like to contribute to Syn, please submit a pull
request.

### Testing

1. Write test cases for the new feature or bug fix
2. Run the test cases to ensure they pass
3. Test on a local server to ensure the feature or bug fix works as expected
4. Submit a pull request
5. Wait for the pull request to be reviewed and merged

## License

Syn is licensed under the MIT License.

## Acknowledgements

Thank you to all the developers and users who have contributed to Syn.