# SecureDoors

SecureDoors is a Minecraft plugin designed to manage player access to the server. The plugin requires new players to enter a verification code when joining the server for the first time. Once verified, they will be automatically added to the whitelist, ensuring secure access control for the server.

## Features

* New players must enter a verification code on their first server join
* Verified players are automatically added to the whitelist
* Simple and user-friendly command system for administrators to manage verification codes and the whitelist
* Customizable verification code length and expiration time
* Multi-language support with localized messages

## Installation

1. Download the latest version of the SecureDoors plugin from the Releases page.
2. Place the downloaded SecureDoors.jar file into the plugins folder of your server.
3. Restart the server to load the plugin.

## Usage

1. When a new player joins the server for the first time, they will receive a message asking them to enter a verification code.
2. The player must obtain a valid verification code from an administrator.
3. The player enters the /verify <code> command in the chat, replacing <code> with the actual verification code.
4. If the verification code is correct, the player will be automatically added to the whitelist and can access the server normally.

## Commands

* `/sd reload` - Reloads the plugin configuration
* `/sd generate` [amount] - Generates the specified number of verification codes
* `/sd list` - Lists all generated verification codes
* `/sd remove` <code> - Removes the specified verification code

## Permissions

* `securedoors.admin` - Grants administrator permissions to use all commands
* `securedoors.bypass` - Allows players to bypass the verification process

## Configuration

The plugin's configuration file, config.yml, is located in the plugins/SecureDoors folder. You can customize settings such as verification code length, expiration time, and localized messages.

## Support and Feedback

If you encounter any issues while using the SecureDoors plugin or have any suggestions or feedback, please submit them on the Issues page.

## License

The SecureDoors plugin is licensed under the MIT License. You are free to use, modify, and distribute this plugin, but please retain the original author's copyright notice.