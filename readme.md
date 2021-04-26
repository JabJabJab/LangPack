## LangPack

LangPack is a text externalizer and language library written in Kotlin for Minecraft server plugins. This plugin is a
solution to externalization of texts for other plugins in a way that gives the plugin developers, the administrators of
servers, and contributors a standard to follow. The core of the plugin is a separate module to allow for developers to
add support for other server platforms.

### Locale support

As of Minecraft 1.16.5, all locales listed on https://minecraft.gamepedia.com/Language are supported. Player locales are
interpreted in the API for queries, sending messages, broadcasts.

### Externalize

This library aids in accessibility of modification of displayed text through YAML files associated with locales. Each
file can be accessed and modified according to both the plugin, and the administrator's needs. Both files and groupings
of text can import from other files for further organization options.

### Dynamic text

Text definitions can refer to other definitions using a processor. The processor implements the percentage placeholder
syntax. `%field%` Calls to the API can also provide arguments for definitions to include when processing text.

Minecraft color codes are supported using the '&' character. `&<color_code>`

Text definitions can be inside group definitions. Group definitions and files can import from other files, allowing for
both plugin developers and administrators to manage their language files with more options.

Complex definitions are included in LangPack where pools and actions can be implemented from Bungeecord's BaseComponent
API. Complex implementations can be authored and loaded in packs by third-parties.

### License

The source-code License for LangPack is MIT. LangPack will not force copy-left so that authors can apply this solution
both privately and commercially.

