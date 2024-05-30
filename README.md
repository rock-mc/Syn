# Syn

<p align="center">
  <img width="40%" src="https://raw.githubusercontent.com/rock-mc/Syn/main/images/logo.png">
</p>

**Syn** is a Minecraft plugin designed to manage player access to the server.  
Before each new player logs in for the first time, they need to obtain a verification code and enter it in the game to
be added to the allowlist.  
It has a guest mode that allows unverified players to enter the server, except for players on the banlist.

Supports Bukkit, Spigot, Paper, and Folia.

## Installation

1. Download the latest version of Syn plugin
2. Place the plugin file in `plugins` folder of your server
3. Restart your server

## Commands

* `/syn` - Show help information for Syn
* `/syn verify <code/player>` - The new player input the verification code to verify themselves, or OPs inputs the
  player's name to verify the Online player
* `/syn gencode [number]` - Generate verification codes of the number
* `/syn info [player]` - Show the status of Syn plugin or the player
* `/syn ban <player> [day hour min sec]` - Ban the player, default time is permanent
* `/syn unban <player>` - Unban the player
* `/syn guest` - If on, it allows everyone to enter the server, except for players on the ban list.
  If off, it only allows the player in the allowlist to come into the server.
* `/syn log [time] [player] [page]` - Show the log since the time or the last time the server was opened

### Usage

#### The first time the player logs in

1. OPs generate a verification code by `/syn gencode`
2. The new player inputs the verification code by `/syn verify <code>`
3. The player is added to the allowlist
4. The player can log in to the server normally
5. Otherwise, the player is kicked from the server

#### Ban the player

1. OPs ban the player by `/syn ban <player> [reason] [time]`. For example, `/syn ban Notch` or `/syn ban Notch 1d` or `/syn ban Notch 1y2d3h4m5s` or `/syn ban Notch "attack other players" 6m`
2. The banned player is kicked from the server
3. The banned player cannot log in to the server and can see the ban reason and time left

#### Unban the player

1. OPs unban the player by `/syn unban <player>`
2. The unbanned player can log in to the server normally

#### Guest mode

1. OPs open the server by `/syn guest`
2. Everyone can enter the server, except for players on the ban list
3. OPs close the server by `/syn guest`
4. Only players in the allowlist can enter the server
5. The server is closed by default

#### Check the info of Syn plugin or the player

1. OPs check the info of Syn plugin by `/syn info`
2. OPs check the info of the player by `/syn info <player>`

## Permissions

* `syn.*` - Grants access to all Syn-related permissions
    * `syn.gencode` - Allows generating verification codes (default: OP)
    * `syn.ban` - Allows banning players (default: OP)
    * `syn.unban` - Allows unbanning players (default: OP)
    * `syn.guest` - Allows opening/closing the server (default: OP)
    * `syn.info` - Allows checking the info of Syn system or the player (default: OP)
    * `syn.log` - Allows checking the log of Syn system (default: OP)
    * `syn.verify` - Allows verifying the player (default: everyone)

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