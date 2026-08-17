# Render Chest

This is a simple rendering library that currently only provides a custom glow feature that does not show through walls consisting of solid or cutout blocks. The scope of the project may eventually expand to include my world rendering system found in Skyblocker and Aaron's Mod.

The name is a play on the "Render Pearl" name of Minecraft's own system with the chest part being fitting of a library.

## Getting Started

For the list of versions please see the [maven](https://maven.azureaaron.net/#/), for changelogs look at the commit history as that will best showcases the changes given this library covers more advanced modding concepts.

The library should be JiJ'd into mods using Loom's `include`, shading is highly frowned upon as it will increase the performance cost notably through additional compute/render passes being performed.

The documentation for each part of the API can be found in the respective classes.

## Support

The library will only ever receive changes for the two absolute latest content drops for Minecraft. New features or significant changes may only target the latest Minecraft version due to the library being highly dependent on the game's internals.

Breaking changes to the API will only be made if necessary during ports to new Minecraft versions, the API will remain stable for a Minecraft version upon a stable release being provided for it. The current API design has been in use by myself for a long time and I don't expect it to need many changes.

This support guarantee will not be provided for the underlying implementation which may change at any time without notice. 

## Contributions

This project enforces a code formatting style via the Spotless and Checkstyle plugins, many common formatting issues can likely be fixed by simply running `./gradlew spotlessApply`. For any documentation or code please spell things according to Canadian English.

The work of porting to new Minecraft versions should be started by me so that I have a good understanding of how the rendering systems work since they are frequently evolving thus I need know how they work in order to maintain them.

## Licence

The project is available under the Apache 2.0 licence and I ask you to respect that. While it may not appear so, this project is the result of tens of hours poured into experimenting with the game's rendering system.
