# SecureDoors

SecureDoors is a Minecraft plugin designed to manage player whitelist verification and banning functionality.

## Features

* Player Verification: Players are required to input a verification code to enter the server when they first log in
* Generate Verification Codes: OPs can generate verification codes for players to use
* Ban Players: OPs can ban specific players for a certain period of time
* Unban Players: OPs can unban players
* View System Information: OPs can view the status and information of the SecureDoors system
* Open/Close Server: OPs can open or close the server to control player access

## Installation

1. Download the latest version of SecureDoors plugin
2. Place the plugin file in `plugins` folder of your server
3. Restart your server

## Usage

* `/sd` - Show help information for SecureDoors
* `/sd verify <code>` - Input the verification code to verify the player
* `/sd gencode [number]` - Generate a specified number of verification codes
* `/sd info` - Show the status and information of the SecureDoors system
* `/sd ban <player> [day hour min sec]` - Ban a specific player for a certain period of time
* `/sd unban <player>` - Unban a specific player
* `/sd open` - Allow everyone to enter the server, except for players on the ban list
* `/sd close` - Allow the player in the allowlist to come into the server.

## Permissions

* `sd.*` - Grants access to all SecureDoors-related permissions
  * `sd.gencode` - Allows generating verification codes
  * `sd.block` - Allows banning players
  * `sd.unblock` - Allows unbanning players
  * `sd.door` - Allows opening/closing the server
* `sd.gencode` - Allows generating verification codes (default: OP)
* `sd.block` - Allows banning players (default: OP)
* `sd.unblock` - Allows unbanning players (default: OP)
* `sd.door` - Allows opening/closing the server (default: OP)

## Configuration

The configuration file for the plugin is located at plugins/SecureDoors/config.yml. You can modify it according to your needs.

## Support and Reporting Issues

If you encounter any issues or have any suggestions while using SecureDoors, please submit them on GitHub Issues. We will respond and resolve the issues as soon as possible.

## License

SecureDoors is licensed under the MIT License.

## Acknowledgements

Thank you to all the developers and users who have contributed to SecureDoors.