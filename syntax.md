## Basics
```yml
message: "Hello, world!"

# Result: 'Hello, Jab!'
message_player: "Hello, %player%!"
```
- **message:** This is the most basic use of LangPack. This is a message without any need to format in order to use.
- **message_player:** This is a basic example of placeholders in LangPack. The **player** placeholder would be provided at the time of the call.
> ###### NOTE: If **player** is not defined, the message would be "Hello, player!", using the variable's name instead as a fallback. If the **player** field is defined elsewhere (either in the same file or another), that definition is used.

## Groups
```yml
group:

  header: '[Group]'

  # Result: '[Group] This is a group message.'
  message: '%group.header% This is a group message.'
```
Groups are YAML sections that can store grouped entries and nest sub-groups. Groups can also import from other files with metadata definitions.

## Pools
```yml
flip_coin:
  type: pool
  mode: random
  pool: ['Heads', 'Tails']
```
Pools enable random or sequenced picking of items from a list.
- **mode**
  - **random** - Picks a random item.
  - **sequence** - Picks the next item going from top to bottom.
  - **reverse_sequence** - Picks the next item going bottom to top.
    

- **pool** - A list of items to pick from.

## Action
```yml
coin_action:
  type: action
  text: 'Click this text to flip a coin!'
  command: '/say %player% flipped a coin. It landed %flip_coin%!'
```
Actions allow for entries to utilize hover-text and command-execution resources in Minecraft's chat.
- **text** - The text to display in chat.
- **hover** - The text to display when the main text is hovered.
- **command** - The command to execute when the main text is clicked.

## Metadata
```yml
__metadata__:
  import: 'imported_file'
  # OR
  imports:
    - 'C:/events.yml'
    - 'commands'
```
### Syntax
The metadata section allows for users of the library to manage how files load. Users can organize the contents of lang-packs to their preference. Imports are allowed for any file or group.

Import declaration variants are mutually exclusive. Only one can be used in a metadata definition.
- **import**: import only imports a single file as a string.
- **imports**: imports can import multiple files as a list.

#### extra_en.yml
```yml
header: '[Extra]'
```
#### example_en.yml
```yml
__metadata__:
import: 'extra'
  
# Result: '[Extra] Hello, World!'
message: '%header% Hello, World!'
```
#### example_group_en.yml
```yml
group:
  __metadata__:
    import: 'extra'

  # Import fields are shadowed when defined in the group.
  header: '[Example]'
  # Result: '[Example] Hello, World!'
  message: '%group.header% Hello, World!'
```
Import definitions are below the priority of the group or file that imports. If an identical definition exists, the definition in the import is shadowed. Definitions for groups concatenates the query for the group and the imported definitions.
- Imported to file: ``imported_def``
- Imported to group: ``group.imported_def``

## Custom & Complex
```yml
my_complex_object:
  type: complex_id
  ..
```
Custom objects are allowed, and are implementable via code. This allows for complex solutions for specific needs for plugins while still allowing access to the benefits of LangPack.